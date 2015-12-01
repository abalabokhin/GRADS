package edu.sc.csce740.tests;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import edu.sc.csce740.GRADSIntf;
import edu.sc.csce740.exception.DBIsNotLoadedException;
import edu.sc.csce740.exception.NoUsersAreLoggedIn;
import edu.sc.csce740.GRADS;
import edu.sc.csce740.model.Certificate;
import edu.sc.csce740.model.Course;
import edu.sc.csce740.model.CourseTaken;
import edu.sc.csce740.model.Degree;
import edu.sc.csce740.model.Milestone;
import edu.sc.csce740.model.Professor;
import edu.sc.csce740.model.ProgressSummary;
import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.RequirementDetails;
import edu.sc.csce740.model.Student;
import edu.sc.csce740.model.StudentRecord;
import edu.sc.csce740.model.Term;

import java.io.FileReader;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class GRADSTest  {

	GRADSIntf grads;
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
	public void Initialize()
	{
        grads = new GRADS();
	}

	@Test
	public void testSetUserSuccess() throws Exception
	{
        String testUser1 = "mmatthews";
		grads.loadUsers("users.txt");
		grads.setUser(testUser1);

		Assert.assertEquals(grads.getUser(), testUser1);
	}

	@Test
	public void testSetUserFail() throws Exception
	{
		String testUser1 = "mmatthews";
		exception.expect(DBIsNotLoadedException.class);
		grads.setUser(testUser1);
	}

	@Test
	public void testClearSessionSuccess() throws Exception
	{
		grads.loadUsers("users.txt");
		grads.setUser("mmatthews");
		grads.clearSession();

        Assert.assertEquals(grads.getUser(), null);

		exception.expect(NoUsersAreLoggedIn.class);

        grads.getStudentIDs();
        grads.getTranscript("hsmith");
        grads.updateTranscript("hsmith", null, true);
		grads.addNote("hsmith", "I am a note", false);
        grads.generateProgressSummary("hsmith");
        grads.simulateCourses("hsmith", null);
	}

	@Test
	public void testGetUserSuccess() throws Exception
	{
		String testUser1 = "mmatthews";
		grads.loadUsers("users.txt");
		grads.setUser(testUser1);

		Assert.assertEquals(grads.getUser(), testUser1);
	}

    @Test
    public void testGetUserFail() throws Exception
    {
        String testUser = "notpresent";
        grads.loadUsers("users.txt");
        try {
            grads.setUser(testUser);
        } catch (Exception ex) {}

        Assert.assertEquals(grads.getUser(), null);
    }

	@Test
	public void testGetStudentIDsSuccess() throws Exception {
        /// TODO: implement
	}

	@Test
	public void testGetStudentIDsFail() throws Exception {
        /// TODO: implement
	}

	@Test
	public void testGetTranscriptSuccess() throws Exception
	{
		StudentRecord studentRecord1 = createStudentRecord();
        String studentId = studentRecord1.student.id;
		addStudentRecordToDB(studentRecord1);

		grads.loadRecords("students.txt");
		grads.loadUsers("users.txt");
		grads.setUser("mmatthews");

		StudentRecord studentRecord2 = grads.getTranscript(studentId);
        assertReflectionEquals(studentRecord1, studentRecord2, ReflectionComparatorMode.LENIENT_ORDER);
	}

    @Test
    public void testGetTranscriptFail() throws Exception {
        /// TODO: implement
    }

	@Test
	public void testUpdateTranscriptSuccess() throws Exception
	{
		StudentRecord originalStudentRecord = createStudentRecord();
        String studentId = originalStudentRecord.student.id;
		addStudentRecordToDB(originalStudentRecord);

		grads.loadRecords("students.txt");
		grads.loadUsers("users.txt");
		grads.setUser("mmatthews");

		// get the inital date for the term began
        StudentRecord studentRecordFromDB = grads.getTranscript(studentId);

	 	// update term began and save it the db
        studentRecordFromDB.termBegan.year = 2014;
        // not permanent update
		grads.updateTranscript("hsmith", studentRecordFromDB, false);
        studentRecordFromDB = grads.getTranscript(studentId);
        // check that DB version is the same as original version, not updated one.
        assertReflectionEquals(originalStudentRecord, studentRecordFromDB, ReflectionComparatorMode.LENIENT_ORDER);

        studentRecordFromDB.termBegan.year = 2014;
        // permanent update
        grads.updateTranscript("hsmith", studentRecordFromDB, true);
		grads.loadRecords("students.txt");

		// get the final date for the term began
        studentRecordFromDB = grads.getTranscript("hsmith");
        originalStudentRecord.termBegan.year = 2014;
        // check that DB version is the same as updated original version.
        assertReflectionEquals(originalStudentRecord, studentRecordFromDB, ReflectionComparatorMode.LENIENT_ORDER);
	}

    @Test
    public void testUpdateTranscriptFail() throws Exception
    {
        /// TODO: implement
    }

	@Test
	public void testAddNoteSuccess() throws Exception
	{
		String note = "test note to be added";
		String GPCid = "mmatthews";

        StudentRecord studentRecord = createStudentRecord();
        String studentId = studentRecord.student.id;
        addStudentRecordToDB(studentRecord);

		grads.loadUsers("users.txt");
		grads.loadRecords("students.txt");

		grads.setUser(GPCid);

		// Get the current notes and check to ensure note is NOT present
		List<String> notes = grads.getTranscript(studentId).notes;
		Assert.assertFalse(notes.contains(note));

		// Add the note
		grads.addNote(studentId, note, true);

		// Get the current notes and check to ensure note IS present
		notes = grads.getTranscript(studentId).notes;
		Assert.assertTrue(notes.contains(note));
	}

    @Test
    public void testAddNoteFail() throws Exception
    {
        /// TODO: implement
    }

	@Test
	public void testGenerateProgressSummaryPHDSuccess() throws Exception
	{
        /// TODO: implement
	}

    @Test
    public void testGenerateProgressSummaryPHDFail() throws Exception
    {
        StudentRecord studentRecord1 = createStudentRecord();
        String studentId = studentRecord1.student.id;
        String degreeSought = studentRecord1.degreeSought.name.toString();

        addStudentRecordToDB(studentRecord1);

        // Create a progress summary object
        ProgressSummary progressSummary1 =  new ProgressSummary();
        progressSummary1.student = studentRecord1.student;
        progressSummary1.department = studentRecord1.department;
        progressSummary1.termBegan = studentRecord1.termBegan;
        progressSummary1.degreeSought = studentRecord1.degreeSought;
        progressSummary1.advisors = studentRecord1.advisors;
        progressSummary1.committee = studentRecord1.committee;

        List<RequirementCheckResult> requirementCheckResults = new ArrayList<>();
        RequirementDetails details = new RequirementDetails();
        List<String> notes = new ArrayList<>();

        // Add core courses
        RequirementCheckResult result = new RequirementCheckResult();
        result.name = "CORE_COURSES_" + degreeSought;
        result.passed = false;
        details.notes = new ArrayList<>();
        details.notes.add("Core courses [csce791, csce551, csce750, csce531, csce513] are left to be taken.");
        result.details = details;
        requirementCheckResults.add (result);

        // Add Additional credits
        result = new RequirementCheckResult();
        details = new RequirementDetails();
        details.notes = new ArrayList<>();
        result.name = "ADDITIONAL_CREDITS_" + degreeSought;
        result.passed = false;
        details.notes.add("Must pass 17 more hours of CSCE courses numbered above 700 that are not core courses.");
        result.details = details;
        requirementCheckResults.add (result);

        // Add degree based credits
        result = new RequirementCheckResult();
        details = new RequirementDetails();
        details.notes = new ArrayList<>();
        result.name = "DEGREE_BASED_CREDITS_" + degreeSought;
        result.passed = false;
        details.courses = studentRecord1.coursesTaken;
        details.notes.add("Must pass 45 more hours of graduate courses.");
        details.notes.add("Must pass 21 more hours of CSCE courses numbered above 700.");
        result.details = details;
        requirementCheckResults.add (result);

        // Add thesis credits
        result = new RequirementCheckResult();
        details = new RequirementDetails();
        details.notes = new ArrayList<>();
        result.name = "THESIS_CREDITS_" + degreeSought;
        result.passed = false;
        details.notes.add("Must pass 12 more hours of csce899.");
        result.details = details;
        requirementCheckResults.add (result);

        // Add time limit
        result = new RequirementCheckResult();
        details = new RequirementDetails();
        details.notes = new ArrayList<>();
        result.name = "TIME_LIMIT_" + degreeSought;
        result.passed = true;
        requirementCheckResults.add (result);

        // Add GPA
        result = new RequirementCheckResult();
        details = new RequirementDetails();
        result.name = "GPA";
        result.passed = true;
        details.gpa = (float) 4.0;
        result.details = details;
        requirementCheckResults.add (result);

        // Add milestones
        result = new RequirementCheckResult();
        details = new RequirementDetails();
        details.notes = new ArrayList<>();
        result.name = "MILESTONES_" + degreeSought;
        result.passed = false;
        details.notes.add("Missing milestone QUALIFYING_EXAM_PASSED");
        details.notes.add("Missing milestone PROGRAM_OF_STUDY_SUBMITTED");
        details.notes.add("Missing milestone DISSERTATION_DEFENSE_PASSED");
        details.notes.add("Missing milestone DISSERTATION_ADVISOR_SELECTED");
        details.notes.add("Missing milestone DISSERTATION_PROPOSAL_SCHEDULED");
        details.notes.add("Missing milestone DISSERTATION_SUBMITTED");
        details.notes.add("Missing milestone COMPREHENSIVE_EXAM_PASSED");
        details.notes.add("Missing milestone DISSERTATION_DEFENSE_SCHEDULED");
        details.notes.add("Missing milestone DISSERTATION_COMMITTEE_FORMED");

        result.details = details;
        requirementCheckResults.add (result);

        // Add the requirement check result
        progressSummary1.requirementCheckResults = requirementCheckResults;

        grads.loadRecords("students.txt");
        grads.loadUsers("users.txt");
        grads.loadCourses("courses.txt");

        grads.setUser("mmatthews");

        ProgressSummary progressSummary2 = grads.generateProgressSummary(studentId);

        assertReflectionEquals(progressSummary1, progressSummary2, ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void testGenerateProgressSummaryMSESuccess() throws Exception
    {
        /// TODO: implement
    }

    @Test
    public void testGenerateProgressSummaryMSEFail() throws Exception
    {
        /// TODO: implement
    }

    @Test
    public void testGenerateProgressSummaryMSSuccess() throws Exception
    {
        /// TODO: implement
    }

    @Test
    public void testGenerateProgressSummaryMSFail() throws Exception
    {
        /// TODO: implement
    }

    @Test
    public void testGenerateProgressSummaryMENGSuccess() throws Exception
    {
        /// TODO: implement
    }

    @Test
    public void testGenerateProgressSummaryMENGFail() throws Exception
    {
        /// TODO: implement
    }

    @Test
    public void testGenerateProgressSummaryINFASSuccess() throws Exception
    {
        /// TODO: implement
    }

    @Test
    public void testGenerateProgressSummaryINFASFail() throws Exception
    {
        /// TODO: implement
    }

    @Test
    public void testSimulateCoursesSuccess() throws Exception {
        /// TODO: implement
    }

    @Test
    public void testSimulateCoursesFail() throws Exception {
        /// TODO: implement
    }

	public void addStudentRecordToDB(StudentRecord studentRecord) throws Exception
	{
		// Read in the current student records
		List<StudentRecord> studentRecords = null;
		FileReader reader = new FileReader(getClass().getClassLoader().getResource("students.txt").getFile());
		studentRecords = new Gson().fromJson(reader, new TypeToken<List<StudentRecord>>(){}.getType());

		boolean studentExists = studentRecords.stream().anyMatch(x -> x.student.id.equals(studentRecord.student.id));

		// Remove the record we are going to add if it already exists
		if (studentExists)
		{
			StudentRecord result = studentRecords.stream().filter(x -> x.student.id.equals(studentRecord.student.id)).findFirst().get();
			studentRecords.remove(result);
		}

		// Add the new student record
		studentRecords.add(studentRecord);

		// Write all records back to the db
		FileWriter writer = new FileWriter(getClass().getClassLoader().getResource("students.txt").getFile());
		JsonWriter jsonWriter = new JsonWriter(writer);
		jsonWriter.setIndent("    ");
		new Gson().toJson(studentRecords, studentRecords.getClass(), jsonWriter);
		writer.close();
	}


    public StudentRecord createStudentRecord()
    {
		StudentRecord studentRecord = new StudentRecord();

		// Student
		Student student = new Student();
		student.id = "hsmith";
		student.firstName = "Harold";
		student.lastName = "Smith";
		studentRecord.student = student;

		// Department
		studentRecord.department = "COMPUTER_SCIENCE";

		// Term began
		Term term = new Term(2015, Term.Season.FALL);
		studentRecord.termBegan = term;

		// Degree sought
		Degree degreeSought = new Degree();
		degreeSought.name = Degree.Type.PHD;
		degreeSought.graduation = new Term(2018, Term.Season.FALL);
		studentRecord.degreeSought = degreeSought;

		// Previous Degree
		Degree previousDegree = new Degree();
		previousDegree.name = Degree.Type.BS;
		previousDegree.graduation = new Term(2015, Term.Season.SPRING);
		studentRecord.previousDegrees = new ArrayList<>();
		studentRecord.previousDegrees.add (previousDegree );

		// Advisor
		studentRecord.advisors = new ArrayList<>();
		Professor advisor = new Professor();
		advisor.department = "COMPUTER_SCIENCE";
		advisor.firstName = "Gregory";
		advisor.lastName = "Gay";
		studentRecord.advisors.add (advisor);

		// Committee
		studentRecord.committee = new ArrayList<>();

		// Committe member #1
		studentRecord.committee.add (advisor);

		// Committe member #2
		Professor committe2 = new Professor();
		committe2.department = "COMPUTER_SCIENCE";
		committe2.firstName = "Duncan";
		committe2.lastName = "Buell";
		studentRecord.committee.add (committe2);

		// Committe member #3
		Professor committe3 = new Professor();
		committe3.department = "COMPUTER_SCIENCE";
		committe3.firstName = "Caroline";
		committe3.lastName = "Eastman";
		studentRecord.committee.add (committe3);

		// Courses
		studentRecord.coursesTaken = new ArrayList<>();
		CourseTaken courseTaken = new CourseTaken();
		Course course = new Course();

		// Add csce740
		course.name = "Software Engineering";
		course.id = "csce740";
		course.numCredits = "3";
		courseTaken.course = course;
		courseTaken.term = new Term(2015, Term.Season.FALL);
		courseTaken.grade = CourseTaken.Grade.A;
		studentRecord.coursesTaken.add(courseTaken);

		studentRecord.notes = new ArrayList<>();
		studentRecord.notes.add("Bad student");

		return studentRecord;
	}
}
