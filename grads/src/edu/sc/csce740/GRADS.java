package edu.sc.csce740;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.sc.csce740.exception.*;
import edu.sc.csce740.model.*;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Collections;
import java.util.*;


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
    public void loadUsers(String usersFile) throws Exception
    {
        try {
            users = new Gson().fromJson(new FileReader(new File(usersFile)), new TypeToken<List<User>>() {
            }.getType());
        } catch (Exception ex) {
            throw new DBIsNotAvailableOrCorruptedException();
        }
    }

    @Override
    public void loadCourses(String coursesFile) throws Exception {
        try {
            allCourses = new Gson().fromJson(new FileReader(new File(coursesFile)), new TypeToken<List<Course>>() {
            }.getType());
        } catch (Exception ex) {
            throw new DBIsNotAvailableOrCorruptedException();
        }
    }

    @Override
    public void loadRecords(String recordsFile) throws Exception
    {
        try {
            studentRecords = new Gson().fromJson(new FileReader(new File(recordsFile)), new TypeToken<List<StudentRecord>>() {
            }.getType());
        } catch (Exception ex) {
            throw new DBIsNotAvailableOrCorruptedException();
        }
    }

    @Override
    public void setUser(String userId) throws Exception
    {
        clearSession();
        if (users == null)
            throw new DBIsNotLoadedException();
        try {
            this.loggedUser = users.stream().filter(x -> x.id.equals(userId)).findFirst().get();
        } catch (NoSuchElementException exception)
        {
            throw new InvalidDataRequestedException();
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
  		return studentRecords.stream().filter(x -> x.department.equals(this.loggedUser.department)).
                map(x -> x.student.id).collect(Collectors.toList());
    }

    @Override
    public StudentRecord getTranscript(String userId) throws Exception
    {
     	checkAuthorization(RequestType.GET_TRANSCRIPT, userId);
		return studentRecords.stream().filter(x -> x.student.id.equals(userId)).findFirst().get();
    }

    @Override
    public void updateTranscript(String userId, StudentRecord transcript, Boolean permanent) throws Exception
    {
     	checkAuthorization(RequestType.UPDATE_TRANSCRIPT, userId);
        if (permanent) {
            /// TODO: throw exception if fields cannot be updated by logged user type.
            /// TODO: change db if updating is permanent
        } else {
            temporaryStudentRecord = transcript;
        }
    }

    @Override
    public void addNote(String userId, String note, Boolean permanent) throws Exception
    {
    	checkAuthorization(RequestType.ADD_NOTE, userId);
        /// TODO: add note and save DB.
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
        /// TODO: update studentRecord with temporary student record (temporaryStudentRecord)
        /// TODO: add courses variable to studentRecord
        return generateProgressSummaryImpl(studentRecord);
    }

    /// userId might be null if it is not required for the request.
    private void checkAuthorization(RequestType requestType, String studentID) throws Exception
    {
        if (loggedUser == null)
            throw new NoUsersAreLoggedIn();

        if (studentRecords == null)
            throw new DBIsNotLoadedException();

        try {
            String studentDepartment = "";
            if (studentID != null) {
                studentDepartment = studentRecords.stream().filter(x -> x.student.id.equals(studentID)).findFirst().get().department;
            }

            if (requestType.equals(RequestType.GET_STUDENT_IDS) && loggedUser.role.equals(User.Role.GRADUATE_PROGRAM_COORDINATOR))
                return;

            if (loggedUser.role.equals(User.Role.GRADUATE_PROGRAM_COORDINATOR) && studentDepartment.equals(loggedUser.department))
                return;

            if (loggedUser.role.equals(User.Role.STUDENT) && studentID.equals(loggedUser.id) &&
                    requestType.equals(RequestType.GET_TRANSCRIPT) && requestType.equals(RequestType.UPDATE_TRANSCRIPT) &&
                    requestType.equals(RequestType.GENERATE_PROGRESS_SUMMARY) && requestType.equals(RequestType.SIMULATE_COURSES))
                return;

            throw new UserHasInsufficientPrivilegeException();
        } catch (NoSuchElementException ex) {
            throw new InvalidDataRequestedException();
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

    /// TODO: add requirementCheck to result and data from studentRecord
        return result;
    }

    private List<RequirementCheckResult> calculateMilestones(StudentRecord record) throws Exception
    {
        List<RequirementCheckResult> progress =
                programOfStudyProgressCheckers.get(record.degreeSought.name).CheckProgress(record);
        if (record.degreeSought != null)
            progress.addAll(graduateCertificateProgressChecker.CheckProgress(record));

        return progress;
    }
}
