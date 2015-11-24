package edu.sc.csce740;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import edu.sc.csce740.exception.DBIsNotAvailableOrCorruptedException;
import edu.sc.csce740.exception.DBIsNotLoadedException;
import edu.sc.csce740.exception.InvalidDataRequestedException;
import edu.sc.csce740.exception.NoUsersAreLoggedIn;
import edu.sc.csce740.exception.UserHasInsufficientPrivilegeException;
import edu.sc.csce740.model.Course;
import edu.sc.csce740.model.CourseTaken;
import edu.sc.csce740.model.Degree;
import edu.sc.csce740.model.ProgressSummary;
import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.Student;
import edu.sc.csce740.model.StudentRecord;
import edu.sc.csce740.model.Term;
import edu.sc.csce740.model.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


/**
 *
 */
public class GRADS implements GRADSIntf
{
   	private List<User> users = null;
	private List<Course> allCourses = null;
	private List<StudentRecord> studentRecords = null;

	private StudentRecord temporaryStudentRecord = null;
	private User loggedUser = null;

	private Map<Degree.Type, ProgressCheckerIntf> programOfStudyProgressCheckers;

	ProgressCheckerIntf graduateCertificateProgressChecker;

    private String studentRecordsFileName;

/**
* This enumeration lists the valid request types accepted by GRADS from all valid users roles configured to use GRADS.
*/

    public enum RequestType {
        GET_STUDENT_IDS,
        GET_TRANSCRIPT,
        UPDATE_TRANSCRIPT,
        ADD_NOTE,
        GENERATE_PROGRESS_SUMMARY,
        SIMULATE_COURSES
    }


    public GRADS() {
        programOfStudyProgressCheckers = new HashMap<>();
        programOfStudyProgressCheckers.put(Degree.Type.MENG, new ProgressCheckerForMENG());
        programOfStudyProgressCheckers.put(Degree.Type.PHD, new ProgressCheckerForPHD());
        programOfStudyProgressCheckers.put(Degree.Type.MS, new ProgressCheckerForMS());
        programOfStudyProgressCheckers.put(Degree.Type.MSE, new ProgressCheckerForMSE());
        graduateCertificateProgressChecker = new ProgressCheckerForINFAS();

        SetCurrentTerm(new Term(2015, Term.Season.FALL));
    }


    /**
     * This method specifies the current academic term applicable for all student progress verification criteria within GRADS.
     * @param currentTerm involves current semester and current year enrolled
     */

    public void SetCurrentTerm(Term currentTerm) {
        graduateCertificateProgressChecker.SetCurrentTerm(currentTerm);
        programOfStudyProgressCheckers.values().stream().forEach(x -> x.SetCurrentTerm(currentTerm));
    }


    /**
     * This method refers to the list of valid users permitted to interact with GRADS at any given instance.
     * @param usersFileName file containing a list of users
     * @throws Exception if usersFile not found
     */
    @Override
    public void loadUsers(String usersFileName) throws Exception {
        try {
            users = new Gson().fromJson(new FileReader(new File(getClass().getClassLoader().getResource(usersFileName).getFile())), new TypeToken<List<User>>() {
            }.getType());
        } catch (Exception ex) {
            throw new DBIsNotAvailableOrCorruptedException(ex);
        }
    }


    /**
     * This method refers to the list of valid courses permitted to be listed within GRADS at any given instance.

     * @param coursesFileName file containing a list of courses
     * @throws Exception if coursesFile not found
     */
    @Override
    public void loadCourses(String coursesFileName) throws Exception {
        try {
            allCourses = new Gson().fromJson(new FileReader(new File(getClass().getClassLoader().getResource(coursesFileName).getFile())), new TypeToken<List<Course>>() {
            }.getType());
        } catch (Exception ex) {
            throw new DBIsNotAvailableOrCorruptedException(ex);
        }
    }


    /**
     * This method refers to the student information accessible with GRADS at any given instance.
     * @param studentRecordsFileName file containing a list of student records
     * @throws Exception if studentRecordsFileName is not found
     */
    @Override
    public void loadRecords(String studentRecordsFileName) throws Exception {
        try {
            studentRecords = new Gson().fromJson(new FileReader(new File(getClass().getClassLoader().getResource(studentRecordsFileName).getFile())), new TypeToken<List<StudentRecord>>() {
            }.getType());
            this.studentRecordsFileName = studentRecordsFileName;
        } catch (Exception ex) {
            throw new DBIsNotAvailableOrCorruptedException(ex);
        }
    }


    /**
     * This method is designed to persist data into a desired data store permanently.
     * @throws Exception if DB is not available
     */
    private void saveRecords() throws Exception {
        try {
            FileWriter writer = new FileWriter(studentRecordsFileName);
            JsonWriter jsonWriter = new JsonWriter(writer);
            jsonWriter.setIndent("    ");
            new Gson().toJson(studentRecords, studentRecords.getClass(), jsonWriter);
            writer.close();
        } catch (IOException ex) {
            throw new DBIsNotAvailableOrCorruptedException(ex);
        }
    }

    /**
     * This method provides the driver method an ability to specify the interacting user's identity to GRADS.
     * @param userId  the id of the user to log in.
     * @throws Exception if invalid data is requested
     */
    @Override
    public void setUser(String userId) throws Exception {
        clearSession();
        if (users == null) {
            throw new DBIsNotLoadedException("");
        }
        try {
            this.loggedUser = users.stream().filter(x -> x.id.equals(userId)).findFirst().get();
        } catch (NoSuchElementException ex)
        {
            throw new InvalidDataRequestedException(ex);
        }
    }


    /**
     * This method provides the calling function to reset its user and student information stored during each
     * interaction initiated by the driver method. This is expected to be called after any user session termination.
     * @throws Exception
     */
    @Override
    public void clearSession() throws Exception {
        this.loggedUser = null;
        temporaryStudentRecord = null;
    }


    /**
     * This method returns the userID of the user currently interacting with GRADS by means of a driver method.
     * @return returns the id of the logged user
     */
    @Override
    public String getUser() {
        // Question: Who is accepting? Shall we provide this info to anyone? Is it safe?
        return this.loggedUser.id;
    }


    /**
     * This method returns the list of student ID's requested from GRADS by means of a driver method.
     * @return returns student records for the given department (here CSCE)
     * @throws Exception
     */
    @Override
    public List<String> getStudentIDs() throws Exception {
        checkAuthorization(RequestType.GET_STUDENT_IDS, null);
  		return studentRecords.stream().filter(x -> x.department != null && x.department.equals(this.loggedUser.department)).
                map(x -> x.student.id).collect(Collectors.toList());
    }


    /**
     * This method is used to retrieve all the Student information associated with the provided userID from the data store.
     * @param userId id of the user whose raw transcript is requested
     * @return returns the raw transcript of the student
     * @throws Exception if the student raw transcript does not exist in DB
     */
    private StudentRecord getRawTranscript(String userId) throws Exception {
        try {
            StudentRecord result = studentRecords.stream().filter(x -> x.student.id.equals(userId)).findFirst().get();
            return result;
        } catch (NoSuchElementException ex) {
            throw new InvalidDataRequestedException(ex);
        }
    }


    /**
     * This method is used to make a copy of all the Student information associated
     * with the provided userID from the getRawTranscript() method.
     * This copy of the student record object may be utilized for any data manipulation
     * without impacting the corresponding data from the data store. This is designed
     * to maintain disparate objects of the same type for various verification criteria in the calling methods.
     * @param userId  the identifier of the student.
     * @return returns the student's record
     * @throws Exception if transcript is not available
     */
    @Override
    public StudentRecord getTranscript(String userId) throws Exception {

        checkAuthorization(RequestType.GET_TRANSCRIPT, userId);
        return (StudentRecord) cloneSerializableObject(getRawTranscript(userId));
    }


    /**
     * This method is used to modify and persist the modified student information back to the permanent data store.
     * @param userId the student ID to overwrite.
     * @param transcript  the new student record
     * @param permanent  a status flag indicating whether (if false) to make a
     * temporary edit to the in-memory structure or (if true) a permanent edit.
     * @throws Exception
     */
    @Override
    public void updateTranscript(String userId, StudentRecord transcript, Boolean permanent) throws Exception {

        checkAuthorization(RequestType.UPDATE_TRANSCRIPT, userId);

        if (permanent) {
            StudentRecord currentStudentData = getRawTranscript(userId);
            if (loggedUser.role.equals(User.Role.STUDENT)) {
                if (currentStudentData.student == null) {
                    currentStudentData.student = new Student();
                }
                if (transcript.student.firstName != null) {
                    currentStudentData.student.firstName = transcript.student.firstName;
                }
                if (transcript.student.lastName != null) {
                    currentStudentData.student.lastName = transcript.student.lastName;
                }
            } else {
                if (transcript.department == null || !transcript.department.equals(currentStudentData.department)) {
                    throw new UserHasInsufficientPrivilegeException("");
                }
                currentStudentData.student = transcript.student;
                currentStudentData.termBegan = transcript.termBegan;
                currentStudentData.degreeSought = transcript.degreeSought;
                currentStudentData.certificateSought = transcript.certificateSought;
                currentStudentData.previousDegrees = transcript.previousDegrees;
                currentStudentData.advisors = transcript.advisors;
                currentStudentData.committee = transcript.committee;
                currentStudentData.coursesTaken = transcript.coursesTaken;
                currentStudentData.milestonesSet = transcript.milestonesSet;
                currentStudentData.notes = transcript.notes;
                currentStudentData.gpa = transcript.gpa;
            }

            saveRecords();

        } else {
            temporaryStudentRecord = transcript;
        }
    }


    /**
     * This method is used to add notes to the retrieved student record.
     * @param userId the student ID to add a note to.
     * @param note  the note to append
     * @param permanent  a status flag indicating whether (if false) to make a
     * temporary edit to the in-memory structure or (if true) a permanent edit.
     * @throws Exception when unable to add note to the student record
     */
    @Override
    public void addNote(String userId, String note, Boolean permanent) throws Exception {

    	checkAuthorization(RequestType.ADD_NOTE, userId);

        if (permanent == true) {
            StudentRecord currentStudentRecord = getRawTranscript(userId);
            if (currentStudentRecord.notes == null) {
                currentStudentRecord.notes = new ArrayList<>();
            }
            currentStudentRecord.notes.add(note);
            saveRecords();

        }else{
            // Do nothing if note is not permanent. It is not going to influence anything.
            System.out.println("Supplied Note is not added to the record permanently.");
        }

    }


    /**
     * This method is used to generate a summary of student progress using various eligibility checks relevant to their program of study.
     * @param userId the student to generate the record for.
     * @return progress summary 
     * @throws Exception
     */
    @Override
    public ProgressSummary generateProgressSummary(String userId) throws Exception {

        checkAuthorization(RequestType.GENERATE_PROGRESS_SUMMARY, userId);
        StudentRecord studentRecord = getTranscript(userId);

        return generateProgressSummaryImpl(studentRecord);
    }


    /**
     * This method provides a summary of the student progress based on the submitted list of prospective courses.
     * @param userId the student to generate the record for.
     * @param courses a list of the prospective courses.
     * @return
     * @throws Exception
     */
    @Override
    public ProgressSummary simulateCourses(String userId, List<CourseTaken> courses) throws Exception {

     	checkAuthorization(RequestType.SIMULATE_COURSES, userId);
        StudentRecord studentRecord = getTranscript(userId);

        if (temporaryStudentRecord != null) {
            studentRecord = temporaryStudentRecord;
        }
        if (studentRecord.coursesTaken == null) {
            studentRecord.coursesTaken = new ArrayList<>();
        }
        studentRecord.coursesTaken.addAll(courses);

        return generateProgressSummaryImpl(studentRecord);
    }


    /// userId might be null if it is not required for the request.
    /**
     * This method is used to authenticate requester user ID and the submitted request type.
     * @param requestType
     * @param studentID
     * @throws Exception
     */
    private void checkAuthorization(RequestType requestType, String studentID) throws Exception {
        if (loggedUser == null) {
            throw new NoUsersAreLoggedIn("");
        }

        if (studentRecords == null) {
            throw new DBIsNotLoadedException("Student record DB is not loaded.");
        }

        try {
            String studentDepartment = "";
            if (studentID != null) {
                studentDepartment = studentRecords.stream().filter(x -> x.student.id.equals(studentID)).findFirst().get().department;
            }

            if (loggedUser.role.equals(User.Role.GRADUATE_PROGRAM_COORDINATOR)
                    && (requestType != null)
                    && requestType.equals(RequestType.GET_STUDENT_IDS)) {
                return;
            }

            if (loggedUser.role.equals(User.Role.GRADUATE_PROGRAM_COORDINATOR) && studentDepartment.equals(loggedUser.department)){
                return;
            }

            if (loggedUser.role.equals(User.Role.STUDENT) && studentID.equals(loggedUser.id) && (requestType != null)
                    && (requestType.equals(RequestType.GET_TRANSCRIPT)
                    || requestType.equals(RequestType.GENERATE_PROGRESS_SUMMARY)
                    || requestType.equals(RequestType.SIMULATE_COURSES)
                    || requestType.equals(RequestType.UPDATE_TRANSCRIPT))) {
                return;
            }

            throw new UserHasInsufficientPrivilegeException("");
        } catch (NoSuchElementException ex) {
            throw new InvalidDataRequestedException(ex);
        }
    }


    /**
     * This method is used to generate the required information items for building a progress summary.
     * @param studentRecord
     * @return
     * @throws Exception
     */
    ProgressSummary generateProgressSummaryImpl(StudentRecord studentRecord) throws Exception {

        List<RequirementCheckResult> requirementCheck = calculateMilestones(studentRecord);
        ProgressSummary result = new ProgressSummary();

        result.requirementCheckResults = requirementCheck;
    	result.degreeSought = studentRecord.degreeSought;
    	result.student = studentRecord.student;
    	result.termBegan = studentRecord.termBegan;
    	result.department = studentRecord.department;
    	result.certificateSought = studentRecord.certificateSought;
    	result.advisors = studentRecord.advisors;
       	result.committee = studentRecord.committee;

        return result;
    }


    /**
     * This method is used to compute the various progress milestones associated with the program of study linked to a student record.
     * @param record
     * @return
     * @throws Exception
     */
    private List<RequirementCheckResult> calculateMilestones(StudentRecord record) throws Exception {

        List<RequirementCheckResult> progress =
                programOfStudyProgressCheckers.get(record.degreeSought.name).CheckProgress(record);

        if (record.certificateSought != null) {
            progress.addAll(graduateCertificateProgressChecker.CheckProgress(record));
        }

        return progress;
    }


    /**
     * This method is private to the calling function and creates a clone of the object passed as a parameter to it.
     * This is used to retain the integrity of the original object and perform all manipulations only to its cloned version.
     * @param object
     * @return
     */
    private Object cloneSerializableObject(Object object) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(object);

        return gson.fromJson(jsonString, object.getClass());
    }
}
