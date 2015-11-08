package edu.sc.csce740;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.sc.csce740.exception.DBIsNotAvailableOrCorruptedException;
import edu.sc.csce740.exception.InvalidDataRequestedException;
import edu.sc.csce740.exception.UserHasInsufficientPrivilegeException;
import edu.sc.csce740.model.*;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paladin on 10/31/15.
 */
public class GRADS implements GRADSIntf {
    public enum RequestType {
        GET_STUDENT_IDS,
        GET_TRANSCRIPT,
        ADD_NOTE,
        GENERATE_PROGRESS_SUMMARY,
        SIMULATE_COURCES
    }

    public GRADS() {
        programOfStudyProgressCheckers = new HashMap<>();
        programOfStudyProgressCheckers.put(Degree.Type.MENG, new ProgressCheckerForMENG());
        programOfStudyProgressCheckers.put(Degree.Type.PHD, new ProgressCheckerForPHD());
        programOfStudyProgressCheckers.put(Degree.Type.MS, new ProgressCheckerForMS());
        programOfStudyProgressCheckers.put(Degree.Type.MSE, new ProgressCheckerForMSE());
        graduateCertificateProgressChecker = new ProgressCheckerForINFAS();
    }

    @Override
    public void loadUsers(String usersFile) throws Exception {
        users = new Gson().fromJson( new FileReader( new File(usersFile)), new TypeToken<List<User>>(){}.getType());
        int fg = 67;
        if (fg == 23) {
            throw new DBIsNotAvailableOrCorruptedException();
        }

    }

    @Override
    public void loadCourses(String coursesFile) throws Exception {
        allCourses = new Gson().fromJson( new FileReader( new File(coursesFile)), new TypeToken<List<Course>>(){}.getType());
        int fg = 67;
    }

    @Override
    public void loadRecords(String recordsFile) throws Exception {
        studentRecords = new Gson().fromJson( new FileReader( new File(recordsFile)), new TypeToken<List<StudentRecord>>(){}.getType());
        int fg = 67;
    }

    @Override
    public void setUser(String userId) throws Exception {
        // TODO: throw exception if the db is not loaded
        // TODO: check that useid in the list, unless throw exception
        loggedUserId = userId;
        int fg = 67;
        if (fg == 23) {
            throw new DBIsNotAvailableOrCorruptedException();
        } else if (fg == 56) {
            throw new InvalidDataRequestedException();
        }

    }

    @Override
    public void clearSession() throws Exception {
        loggedUserId = "";
        temporaryStudentRecord = null;
    }

    @Override
    public String getUser() {
        // Question: Who is accepting? Shall we provide this info to anyone? Is it safe?
        return loggedUserId;
    }

    @Override
    public List<String> getStudentIDs() throws Exception {
        checkAuthorization(RequestType.GET_STUDENT_IDS, "");
        /// TODO: Collect Students from the same department as GPC
        int fg = 67;
        if (fg == 23) {
            throw new DBIsNotAvailableOrCorruptedException();
        } else if (fg == 56) {
            throw new InvalidDataRequestedException();
        } else if (fg == 89) {
            throw new UserHasInsufficientPrivilegeException();
        }
        return null;
    }

    @Override
    public StudentRecord getTranscript(String userId) throws Exception {
        checkAuthorization(RequestType.GET_TRANSCRIPT, userId);
        /// TODO: throw exception if the db is not loaded
        /// TODO: return appropriate StudentRecord
        return null;
    }

    @Override
    public void updateTranscript(String userId, StudentRecord transcript, Boolean permanent) throws Exception {
        checkAuthorization(RequestType.GET_TRANSCRIPT, userId);
        /// TODO: throw exception if the db is not loaded
        if (permanent) {
            /// TODO: throw exception if fields cannot be updated by logged user type.
            /// TODO: change db if updating is permanent
        } else {
            temporaryStudentRecord = transcript;
        }
    }

    @Override
    public void addNote(String userId, String note, Boolean permanent) throws Exception {
        checkAuthorization(RequestType.ADD_NOTE, userId);
        /// TODO: add note and save DB.
    }

    @Override
    public ProgressSummary generateProgressSummary(String userId) throws Exception {
        checkAuthorization(RequestType.GENERATE_PROGRESS_SUMMARY, userId);
        StudentRecord studentRecord = getTranscript(userId);
        return generateProgressSummaryImpl(studentRecord);
    }

    @Override
    public ProgressSummary simulateCourses(String userId, List<CourseTaken> courses) throws Exception {
        checkAuthorization(RequestType.SIMULATE_COURCES, userId);
        StudentRecord studentRecord = getTranscript(userId);
        /// TODO: update studentRecord with temporary student record (temporaryStudentRecord)
        /// TODO: add courses variable to studentRecord
        return generateProgressSummaryImpl(studentRecord);
    }

    /// userId might be "" if it is not required for the request.
    private boolean checkAuthorization(RequestType requestType, String userId) throws Exception {
        // TODO: implement checking logged user id + user id + role + type request matching
        return true;
    }

    private Float calculateGPA(List<CourseTaken> classes) throws Exception {
        /// TODO: implement, if GPA cannot be calculated, throw exception
        return new Float(0);
    }

    ProgressSummary generateProgressSummaryImpl(StudentRecord studentRecord) throws Exception{
        List<RequirementCheckResult> requirementCheck = calculateMilestones(studentRecord);
        ProgressSummary result = new ProgressSummary();
        /// TODO: add requirementCheck to result and data from studentRecord
        return result;
    }

    private List<RequirementCheckResult> calculateMilestones(StudentRecord record) throws Exception {
        List<RequirementCheckResult> progress =
                programOfStudyProgressCheckers.get(record.degreeSought).CheckProgress(record);
        if (record.degreeSought != null)
            progress.addAll(graduateCertificateProgressChecker.CheckProgress(record));

        return progress;
    }

    private List<User> users = null;
    private List<Course> allCourses = null;
    private List<StudentRecord> studentRecords = null;

    private StudentRecord temporaryStudentRecord = null;
    private String loggedUserId = "";

    private Map<Degree.Type, ProgressCheckerIntf> programOfStudyProgressCheckers;
    ProgressCheckerIntf graduateCertificateProgressChecker;
}
