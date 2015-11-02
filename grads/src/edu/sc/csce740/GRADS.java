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
    @Override
    public void loadUsers(String usersFile) throws Exception {
        users = new Gson().fromJson( new FileReader( new File(usersFile)), new TypeToken<List<User>>(){}.getType());
    }

    @Override
    public void loadCourses(String coursesFile) throws Exception {
        allCourses = new Gson().fromJson( new FileReader( new File(coursesFile)), new TypeToken<List<Course>>(){}.getType());
    }

    @Override
    public void loadRecords(String recordsFile) throws Exception {
        studentRecords = new Gson().fromJson( new FileReader( new File(recordsFile)), new TypeToken<List<StudentRecord>>(){}.getType());
    }

    @Override
    public void setUser(String userId) throws Exception {

    }

    @Override
    public void clearSession() throws Exception {

    }

    @Override
    public String getUser() {
        return null;
    }

    @Override
    public List<String> getStudentIDs() throws Exception {
        return null;
    }

    @Override
    public StudentRecord getTranscript(String userId) throws Exception {
        return null;
    }

    @Override
    public void updateTranscript(String userId, StudentRecord transcript, Boolean permanent) throws Exception {

    }

    @Override
    public void addNote(String userId, String note, Boolean permanent) throws Exception {

    }

    @Override
    public ProgressSummary generateProgressSummary(String userId) throws Exception {
        return null;
    }

    @Override
    public ProgressSummary simulateCourses(String userId, List<CourseTaken> courses) throws Exception {
        return null;
    }

    private List<User> users;
    private List<Course> allCourses;
    private List<StudentRecord> studentRecords;
}
