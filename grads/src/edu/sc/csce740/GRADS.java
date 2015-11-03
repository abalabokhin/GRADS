package edu.sc.csce740;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.sc.csce740.model.*;

import java.io.File;
import java.io.FileReader;
import java.util.List;

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

    @Override
    public void loadUsers(String usersFile) throws Exception {
        users = new Gson().fromJson( new FileReader( new File(usersFile)), new TypeToken<List<User>>(){}.getType());
        int fg = 67;
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
        /// TODO: implement it.
        return null;
    }

    @Override
    public ProgressSummary simulateCourses(String userId, List<CourseTaken> courses) throws Exception {
        checkAuthorization(RequestType.SIMULATE_COURCES, userId);
        /// TODO: implement it.
        return null;
    }

    /// userId might be "" if it is not required for the request.
    private boolean checkAuthorization(RequestType requestType, String userId) throws Exception {
        // TODO: implement checking logged user id + user id + role + type request matching
        return true;
    }

    private float calculateGPA(List<CourseTaken> classes) throws Exception {
        /// TODO: implement, if GPA cannot be calculated, throw exception
        return 0;
    }

    private RequirementCheckResult calculateMilestones(StudentRecord record) throws Exception {
        /// TODO: implement, it is not clear so far, it might be the list of some structures. Don't rush with implementing it.
        return null;
    }

    private List<User> users = null;
    private List<Course> allCourses = null;
    private List<StudentRecord> studentRecords = null;

    StudentRecord temporaryStudentRecord = null;
    private String loggedUserId = "";
}
