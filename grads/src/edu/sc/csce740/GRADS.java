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
	private String loggedUserId = "";

	private Map<Degree.Type, ProgressCheckerIntf> programOfStudyProgressCheckers;
	ProgressCheckerIntf graduateCertificateProgressChecker;

    private String department;
    private String role;

    public enum RequestType
    {
        GET_STUDENT_IDS,
        GET_TRANSCRIPT,
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
    }

    @Override
    public void loadUsers(String usersFile) throws Exception
    {
        users = new Gson().fromJson( new FileReader( new File(usersFile)), new TypeToken<List<User>>(){}.getType());
        int fg = 69;
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
    public void loadRecords(String recordsFile) throws Exception
    {
        studentRecords = new Gson().fromJson( new FileReader( new File(recordsFile)), new TypeToken<List<StudentRecord>>(){}.getType());
        int fg = 67;
    }

    @Override
    public void setUser(String userId) throws Exception
    {
        // TODO: throw exception if the db is not loaded
        // TODO: check that useid in the list, unless throw exception
        this.loggedUserId = userId;

		User user = users.stream().filter(x -> x.id.equals(this.loggedUserId)).findFirst().get();

		this.department = user.department;
		this.role = user.role.toString();
    }

    @Override
    public void clearSession() throws Exception
    {
        this.loggedUserId = "";
        temporaryStudentRecord = null;
        this.department = "";
		this.role = "";
    }

    @Override
    public String getUser()
    {
        // Question: Who is accepting? Shall we provide this info to anyone? Is it safe?
        return this.loggedUserId;
    }

    @Override
    public List<String> getStudentIDs() throws Exception
    {
        if (!checkAuthorization(RequestType.GET_STUDENT_IDS, ""))
        	 throw new UserHasInsufficientPrivilegeException();

  		List<String> rv;

		rv = users.stream().filter(x -> x.department.equals(this.department) && x.role.equals(User.Role.STUDENT)).map(x -> x.id).collect(Collectors.toList());

        /// TODO: throw new DBIsNotAvailableOrCorruptedException();
        //  throw new InvalidDataRequestedException();

       return rv;

    }

    @Override
    public StudentRecord getTranscript(String userId) throws Exception
    {
     	StudentRecord rv = null;

     	if (!checkAuthorization(RequestType.GET_TRANSCRIPT, userId))
        	throw new UserHasInsufficientPrivilegeException();

		rv = studentRecords.stream().filter(x -> x.student.id.equals(userId)).findFirst().get();
        /// TODO: throw exception if the db is not loaded
        /// TODO: return appropriate StudentRecord
        return rv;
    }

    @Override
    public void updateTranscript(String userId, StudentRecord transcript, Boolean permanent) throws Exception
    {
     	if (!checkAuthorization(RequestType.GET_TRANSCRIPT, userId))
        	throw new UserHasInsufficientPrivilegeException();
        /// TODO: throw exception if the db is not loaded

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
    	if (!checkAuthorization(RequestType.ADD_NOTE, userId))
        	throw new UserHasInsufficientPrivilegeException();

        /// TODO: add note and save DB.
    }

    @Override
    public ProgressSummary generateProgressSummary(String userId) throws Exception
    {
      	if (!checkAuthorization(RequestType.GENERATE_PROGRESS_SUMMARY, userId))
        	throw new UserHasInsufficientPrivilegeException();


        StudentRecord studentRecord = getTranscript(userId);
        return generateProgressSummaryImpl(studentRecord);
    }

    @Override
    public ProgressSummary simulateCourses(String userId, List<CourseTaken> courses) throws Exception
    {
     	if (!checkAuthorization(RequestType.SIMULATE_COURSES, userId))
        	throw new UserHasInsufficientPrivilegeException();

        StudentRecord studentRecord = getTranscript(userId);
        /// TODO: update studentRecord with temporary student record (temporaryStudentRecord)
        /// TODO: add courses variable to studentRecord
        return generateProgressSummaryImpl(studentRecord);
    }

    /// userId might be "" if it is not required for the request.
    private boolean checkAuthorization(RequestType requestType, String userId) throws Exception
    {
        String studentDepartment = "";
		boolean rv = false;

        if (!userId.equals(""))
        {
			User studentUser = users.stream().filter(x -> x.id.equals(userId)).findFirst().get();
			studentDepartment = studentUser.department;
		}

		if (this.role.equals(User.Role.GRADUATE_PROGRAM_COORDINATOR.toString()))
		{
			if (requestType.equals(RequestType.GET_STUDENT_IDS))
        		rv = true;
        	else if ((requestType.equals(RequestType.GET_TRANSCRIPT)) && (this.department.equals(studentDepartment)))
         		rv = true;
        	else if ((requestType.equals(RequestType.ADD_NOTE)) && (this.department.equals(studentDepartment)))
        		rv = true;
        	else if ((requestType.equals(RequestType.GENERATE_PROGRESS_SUMMARY)) && (this.department.equals(studentDepartment)))
         		rv = true;
            else if ((requestType.equals(RequestType.SIMULATE_COURSES)) && (this.department.equals(studentDepartment)))
         		rv = true;
	 	}
	 	else
	 	{
			if ((requestType.equals(RequestType.GET_TRANSCRIPT)) && (this.loggedUserId.equals(userId)))
         		rv = true;
         	else if ((requestType.equals(RequestType.GENERATE_PROGRESS_SUMMARY)) && (this.loggedUserId.equals(userId)))
         		rv = true;
            else if ((requestType.equals(RequestType.SIMULATE_COURSES)) && (this.loggedUserId.equals(userId)))
         		rv = true;
		}

		return rv;

    } // End of checkAuthorization method

    private Float calculateGPA(List<CourseTaken> classes) throws Exception
    {
        float sumHours = 0;
        float sumGP = 0;

        for (CourseTaken courseTaken : classes)
        {
            Integer gradeFactor = courseTaken.grade.getFactor();
            if (gradeFactor != null) {
                try {
                    float numCredits = Float.parseFloat(courseTaken.course.numCredits);
                    sumGP += (numCredits * gradeFactor);
                    sumHours += numCredits;
                } catch (NumberFormatException ex)
                {
                    throw new DBIsNotAvailableOrCorruptedException();
                }
            }
		}

        if (sumHours == 0)
            return null;
		return sumGP / sumHours;
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
     // if (record.degreeSought != null)
     //       progress.addAll(graduateCertificateProgressChecker.CheckProgress(record));
        return progress;
    }


}
