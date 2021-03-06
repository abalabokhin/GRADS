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


/** GRADS class implements main functionality of GRADS system. It has methods:
 * SetCurrentTerm - sets the current term
 * loadUsers - loads the file containing the list of users
 * loadCourses - loads the file containing the list of courses
 * loadRecords - loads the file containing the list of records
 * saveRecords - saves the changes made to a student record permanently in the same file that was used to load Records.
 * setUser - sets the user ID
 * clearSession - clear user id and temporary student record
 * getUser - gets the user that is currently logged in
 * getStudentIDs - gets the list of student IDs
 * getRawTranscript - gets the pointer to the actual transcript of the student. The record can be changed via the pointer
 * getTranscript - gets the copy of student transcript. It is safe to change anything in this structure
 * updateTranscript - updates the transcript of the student, temporary or permanently
 * addNote - appends notes to a student's record
 * ProgressSummary - analyzes the progress of a student in the degree enrolled in
 * simulateCourses - simulates the progress made by the student if the courses added will be taken in the future
 * checkAuthorization - checks the authorization of the user to requested student by request, uses internally.
 * generateProgressSummaryImpl - implementation of generation the progress summary of the student
 * calculateMilestones - calculates the milestones for the degree the student is enrolled in
 * cloneSerializableObject - clones any java object by json serialization.
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

    /**
     * Default constructor, set progress checkers for different programs and default current term.
     */
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
     * @param currentTerm includes the current semester and current year enrolled
     */
    public void SetCurrentTerm(Term currentTerm) {
        graduateCertificateProgressChecker.SetCurrentTerm(currentTerm);
        programOfStudyProgressCheckers.values().stream().forEach(x -> x.SetCurrentTerm(currentTerm));
    }


    /**
     * This method refers to the list of valid users permitted to interact with GRADS at any given instance.
     * @param usersFileName file containing a list of users
     * @throws DBIsNotAvailableOrCorruptedException if usersFile is not found
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
     * @throws DBIsNotAvailableOrCorruptedException if coursesFile is not found
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
     * @param studentRecordsFileName file containing a list of student records, it is saves inside class for saveRecords method.
     * @throws DBIsNotAvailableOrCorruptedException if studentRecordsFileName is not found
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
     * This method is designed to store all student records into the same file that was used in loadRecords method.
     * @throws DBIsNotAvailableOrCorruptedException if the DB file cannot be written.
     */
    private void saveRecords() throws Exception {
        try {
            FileWriter writer = new FileWriter(new File(getClass().getClassLoader().getResource(studentRecordsFileName).getFile()));
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
     * @throws DBIsNotLoadedException if users are not loaded by calling loadUsers
     * @throws InvalidDataRequestedException if userId cannot be found in DB.
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
     * @throws Exception No exception can be thrown from the method. But it is required from the interface.
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
        return loggedUser == null ? null : loggedUser.id;
    }


    /**
     * This method returns the list of student ID's requested from GRADS by GPC.
     * @return returns student records for the department of logged GPC user.
     * @throws UserHasInsufficientPrivilegeException if user tole is STUDENT, not GPC.
     */
    @Override
    public List<String> getStudentIDs() throws Exception {
        checkAuthorization(RequestType.GET_STUDENT_IDS, null);
  		return studentRecords.stream().filter(x -> x.department != null && x.department.equals(this.loggedUser.department)).
                map(x -> x.student.id).collect(Collectors.toList());
    }


    /**
     * This method is used to retrieve raw pointer to StudentRecord by studentId. It uses internally and it is not
     * returned to the user, because it can be used to change data into the record.
     * @param userId id of the user whose raw transcript is requested
     * @return returns the raw transcript of the student
     * @throws InvalidDataRequestedException if the student raw transcript does not exist in DB
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
     * @throws InvalidDataRequestedException if the student raw transcript does not exist in DB
     */
    @Override
    public StudentRecord getTranscript(String userId) throws Exception {

        checkAuthorization(RequestType.GET_TRANSCRIPT, userId);
        return (StudentRecord) cloneSerializableObject(getRawTranscript(userId));
    }


    /**
     * This method is used to modify and persist the modified student information back to the permanent data store or
     * save provided StudentRecord temporary to use into simulateCourses method.
     * @param userId the student ID to overwrite.
     * @param transcript the new student record
     * @param permanent a status flag indicating whether (if false) to make a
     * temporary edit to the in-memory structure or (if true) a permanent edit into DB file.
     * @throws Exception if any error happens.
     */
    @Override
    public void updateTranscript(String userId, StudentRecord transcript, Boolean permanent) throws Exception {

        checkAuthorization(RequestType.UPDATE_TRANSCRIPT, userId);

        if (permanent) {
            StudentRecord currentStudentData = getRawTranscript(userId);
            //check to ensure if user is of role student
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
     * temporary edit to the in-memory structure or (if true) a permanent edit. Because temporary note influence nothing,
     *                   only permanently saving notes is implemented.
     * @throws Exception when unable to add note to the student record by any reasons.
     */
    @Override
    public void addNote(String userId, String note, Boolean permanent) throws Exception {

    	checkAuthorization(RequestType.ADD_NOTE, userId);

        if (permanent == true) {
            StudentRecord currentStudentRecord = getRawTranscript(userId);
            //if the current student record has no already existing notes
            if (currentStudentRecord.notes == null) {
                currentStudentRecord.notes = new ArrayList<>();
            }
            currentStudentRecord.notes.add(note);
            saveRecords();

        }else{
            // Do nothing if note is not permanent. It is not going to influence anything.
        }
    }


    /**
     * This method is used to generate a summary of student progress using various eligibility checks
     * relevant to their program of study. It finds student record data in DB and call generateProgressSummaryImpl
     * method for actual creating Summary Report.
     * @param userId id of the student whose progress summary is being analyzed
     * @return  student record to the method generateProgressSummaryImpl
     * @throws Exception when unable to return the record by any reasons.
     */
    @Override
    public ProgressSummary generateProgressSummary(String userId) throws Exception {

        checkAuthorization(RequestType.GENERATE_PROGRESS_SUMMARY, userId);
        StudentRecord studentRecord = getTranscript(userId);

        return generateProgressSummaryImpl(studentRecord);
    }


    /**
     * This method provides a summary of the student progress based on the submitted list of prospective courses.
     * If temporary StudentRecord is saved after calling updateTranscript method, this one is used to create the report.
     * It finds student record data in DB or use temporary data, add new courses and call generateProgressSummaryImpl
     * method for actual creating the report.
     * @param userId the student to generate the record for.
     * @param courses a list of the prospective courses.
     * @return student record of the user whose progress was analyzed by simulating courses to be taken in the future terms
     * @throws Exception unable to simulate courses and return student record
     */
    @Override
    public ProgressSummary simulateCourses(String userId, List<CourseTaken> courses) throws Exception {

     	checkAuthorization(RequestType.SIMULATE_COURSES, userId);
        StudentRecord studentRecord = getTranscript(userId);
        //check to ensure that the temporary student record is not empty
        if (temporaryStudentRecord != null && temporaryStudentRecord.student.id.equals(userId)) {
            studentRecord = temporaryStudentRecord;
        }
        //check to ensure that the student has taken zero courses
        if (studentRecord.coursesTaken == null) {
            studentRecord.coursesTaken = new ArrayList<>();
        }
        studentRecord.coursesTaken.addAll(courses);

        return generateProgressSummaryImpl(studentRecord);
    }


    /**
     * This method is used to authenticate requester user ID, the submitted request type and the logged user role.
     * @param requestType
     * @param studentID id of the student whose record is being requested. It might be null if it is not required for the request.
     * @throws NoUsersAreLoggedIn if no users are logged in.
     * @throws DBIsNotLoadedException if student records DB is not loaded.
     * @throws UserHasInsufficientPrivilegeException if the user does not have enough privileges for this request
     * @throws InvalidDataRequestedException if there are no records with studentID in DB.
     */
    private void checkAuthorization(RequestType requestType, String studentID) throws Exception {
        //check to ensure that a user is logged in
        if (loggedUser == null) {
            throw new NoUsersAreLoggedIn("");
        }

        if (studentRecords == null) {
            throw new DBIsNotLoadedException("Student record DB is not loaded.");
        }

        try {
            String studentDepartment = "";
            // check to ensure that there exists a student ID
            if (studentID != null) {
                studentDepartment = studentRecords.stream().filter(x -> x.student.id.equals(studentID)).findFirst().get().department;
            }
            //checks to ensure that the role of the user logged in is GPC
            if (requestType.equals(RequestType.GET_STUDENT_IDS)) {
                if (loggedUser.role.equals(User.Role.GRADUATE_PROGRAM_COORDINATOR)) {
                    return;
                } else {
                    throw new UserHasInsufficientPrivilegeException("");
                }
            }

            //check to ensure that a GPC of another department cannot access student records of the CSCE students
            if (loggedUser.role.equals(User.Role.GRADUATE_PROGRAM_COORDINATOR) && studentDepartment.equals(loggedUser.department)){
                return;
            }
            //check to ensure the student is requesting one's own transcript, progress summary and course simulation
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
     * This method is used to generate the required information items for building a progress summary based on provided StudentRecord
     * @param studentRecord it has all the info to generate the report.
     *                      It should be safe to change this object (it must be cloned before providing to this method).
     * @return returns a copy of the student record now stored as result
     * @throws Exception if unable to return result
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
     * Different implementations of ProgressCheckerIntf interface is called for different program of study or INFAS.
     * @param record
     * @return returns the progress made by a student
     * @throws Exception when unable to calculate milestones
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
     * @param object any one of the modifications made to a student record
     * @return returns a copy of the object modified
     */
    private Object cloneSerializableObject(Object object) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(object);
        return gson.fromJson(jsonString, object.getClass());
    }
}
