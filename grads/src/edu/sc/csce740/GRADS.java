package edu.sc.csce740;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import edu.sc.csce740.exception.*;
import edu.sc.csce740.model.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import java.util.stream.Collectors;


/**
 * Created by paladin on 10/31/15.
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


    public enum RequestType
    {
        GET_STUDENT_IDS,
        GET_TRANSCRIPT,
        UPDATE_TRANSCRIPT,
        ADD_NOTE,
        GENERATE_PROGRESS_SUMMARY,
        SIMULATE_COURSES
    }

    public GRADS()
    {
        programOfStudyProgressCheckers = new HashMap<>();
        programOfStudyProgressCheckers.put(Degree.Type.MENG, new ProgressCheckerForMENG());
        programOfStudyProgressCheckers.put(Degree.Type.PHD, new ProgressCheckerForPHD());
        programOfStudyProgressCheckers.put(Degree.Type.MS, new ProgressCheckerForMS());
        programOfStudyProgressCheckers.put(Degree.Type.MSE, new ProgressCheckerForMSE());
        graduateCertificateProgressChecker = new ProgressCheckerForINFAS();

        SetCurrentTerm(new Term(2015, Term.Season.FALL));
    }

    public void SetCurrentTerm(Term currentTerm) {
        graduateCertificateProgressChecker.SetCurrentTerm(currentTerm);
        programOfStudyProgressCheckers.values().stream().forEach(x -> x.SetCurrentTerm(currentTerm));
    }

    @Override
    public void loadUsers(String usersFileName) throws Exception
    {
        try {
            users = new Gson().fromJson(new FileReader(new File(getClass().getClassLoader().getResource(usersFileName).getFile())), new TypeToken<List<User>>() {
            }.getType());
        } catch (Exception ex) {
            throw new DBIsNotAvailableOrCorruptedException(ex);
        }
    }

    @Override
    public void loadCourses(String coursesFileName) throws Exception {
        try {
            allCourses = new Gson().fromJson(new FileReader(new File(getClass().getClassLoader().getResource(coursesFileName).getFile())), new TypeToken<List<Course>>() {
            }.getType());
        } catch (Exception ex) {
            throw new DBIsNotAvailableOrCorruptedException(ex);
        }
    }


    @Override
    public void loadRecords(String studentRecordsFileName) throws Exception
    {
        try {
            studentRecords = new Gson().fromJson(new FileReader(new File(getClass().getClassLoader().getResource(studentRecordsFileName).getFile())), new TypeToken<List<StudentRecord>>() {
            }.getType());
            this.studentRecordsFileName = studentRecordsFileName;
        } catch (Exception ex) {
            throw new DBIsNotAvailableOrCorruptedException(ex);
        }
    }

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

    @Override
    public void setUser(String userId) throws Exception
    {
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

    @Override
    public void clearSession() throws Exception
    {
        this.loggedUser = null;
        temporaryStudentRecord = null;
    }

    @Override
    public String getUser()
    {
        // Question: Who is accepting? Shall we provide this info to anyone? Is it safe?
        return this.loggedUser.id;
    }

    @Override
    public List<String> getStudentIDs() throws Exception
    {
        checkAuthorization(RequestType.GET_STUDENT_IDS, null);
  		return studentRecords.stream().filter(x -> x.department != null && x.department.equals(this.loggedUser.department)).
                map(x -> x.student.id).collect(Collectors.toList());
    }

    /// Get raw transcript, we do not copy it now. We use this method to have a student record to modify, for instance.
    private StudentRecord getRawTranscript(String userId) throws Exception
    {
        try {
            StudentRecord result = studentRecords.stream().filter(x -> x.student.id.equals(userId)).findFirst().get();
            return result;
        } catch (NoSuchElementException ex) {
            throw new InvalidDataRequestedException(ex);
        }
    }

    @Override
    /// We return copy of the object it is safe to do any operations to the object, it is not gonna affect our DB.
    public StudentRecord getTranscript(String userId) throws Exception
    {
     	checkAuthorization(RequestType.GET_TRANSCRIPT, userId);
        return (StudentRecord) cloneSerializableObject(getRawTranscript(userId));
    }

    @Override
    public void updateTranscript(String userId, StudentRecord transcript, Boolean permanent) throws Exception
    {
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

    @Override
    public void addNote(String userId, String note, Boolean permanent) throws Exception
    {
    	checkAuthorization(RequestType.ADD_NOTE, userId);
        if (permanent == true) {
            StudentRecord currentStudentRecord = getRawTranscript(userId);
            if (currentStudentRecord.notes == null) {
                currentStudentRecord.notes = new ArrayList<>();
            }
            currentStudentRecord.notes.add(note);
            saveRecords();
        }
        /// Do nothing if note is not permanent. It is not going to influence anything.
    }

    @Override
    public ProgressSummary generateProgressSummary(String userId) throws Exception
    {
        checkAuthorization(RequestType.GENERATE_PROGRESS_SUMMARY, userId);
        StudentRecord studentRecord = getTranscript(userId);
        return generateProgressSummaryImpl(studentRecord);
    }

    @Override
    public ProgressSummary simulateCourses(String userId, List<CourseTaken> courses) throws Exception
    {
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
    private void checkAuthorization(RequestType requestType, String studentID) throws Exception
    {
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

            if (loggedUser.role.equals(User.Role.GRADUATE_PROGRAM_COORDINATOR) && (requestType != null) && requestType.equals(RequestType.GET_STUDENT_IDS)) {
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

    ProgressSummary generateProgressSummaryImpl(StudentRecord studentRecord) throws Exception
    {
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

    private List<RequirementCheckResult> calculateMilestones(StudentRecord record) throws Exception
    {
        List<RequirementCheckResult> progress =
                programOfStudyProgressCheckers.get(record.degreeSought.name).CheckProgress(record);
        if (record.certificateSought != null) {
            progress.addAll(graduateCertificateProgressChecker.CheckProgress(record));
        }

        return progress;
    }

    private Object cloneSerializableObject(Object object) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(object);
        return gson.fromJson(jsonString, object.getClass());
    }
}
