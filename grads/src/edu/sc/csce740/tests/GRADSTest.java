package edu.sc.csce740.tests;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;


import edu.sc.csce740.exception.DBIsNotAvailableOrCorruptedException;
import edu.sc.csce740.exception.DBIsNotLoadedException;
import edu.sc.csce740.exception.InvalidDataRequestedException;
import edu.sc.csce740.exception.NoUsersAreLoggedIn;
import edu.sc.csce740.exception.UserHasInsufficientPrivilegeException;
import edu.sc.csce740.GRADS;
import edu.sc.csce740.model.Term;
import edu.sc.csce740.model.Student;
import edu.sc.csce740.model.StudentRecord;
import edu.sc.csce740.model.Degree;
import edu.sc.csce740.model.Professor;
import edu.sc.csce740.model.Course;
import edu.sc.csce740.model.CourseTaken;
import edu.sc.csce740.model.ProgressSummary;
import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.RequirementDetails;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.*;

public class GRADSTest {

	@Test
	public void testSetUser() throws Exception
	{
		String testUser1 = "mmatthews";

		GRADS grads = new GRADS();

		try
		{
			grads.setUser(testUser1);
			fail();
		}
		catch (DBIsNotLoadedException e)
		{
			Assert.assertTrue(e.toString().contains("DBIsNotLoadedException"));
		}

		grads.loadUsers("users.txt");
		grads.setUser(testUser1);

		Assert.assertEquals(grads.getUser().equals(testUser1), true);
	}

	@Test
	public void testClearSession() throws Exception
	{
		String testUser1 = "mmatthews";

		GRADS grads = new GRADS();

		grads.loadUsers("users.txt");
		grads.setUser(testUser1);

		Assert.assertEquals(grads.getUser().equals(testUser1), true);

		grads.clearSession();

		Assert.assertEquals(grads.getUser() == null, true);
	}

	@Test
	public void testGetUser() throws Exception
	{
		String testUser1 = "mmatthews";
		String testUser2 = "notpresent";

		GRADS grads = new GRADS();

		grads.loadUsers("users.txt");
		grads.setUser(testUser1);

		Assert.assertEquals(grads.getUser().equals(testUser1), true);
		Assert.assertEquals(grads.getUser().equals(testUser2), false);
	}

	@Test
	public void testGetStudentIDs() throws Exception {

	}

	@Test
	public void testGetTranscript() throws Exception {

	}

	@Test
	public void testUpdateTranscript() throws Exception {

	}

	@Test
	public void testAddNote() throws Exception
	{
		String note = "test note to be added";
		String studentId = "mhunt";
		String GPCid = "mmatthews";
		boolean noteExists = false;

		GRADS grads = new GRADS();

		grads.loadUsers("users.txt");
		grads.loadRecords("students.txt");

		grads.setUser(GPCid);

		// Get the current notes and check to ensure note is NOT present
		List<String> notes = grads.getTranscript(studentId).notes;
		Assert.assertEquals(notes.contains(note), false);

		// Add the note
		grads.addNote(studentId,note,true);

		// Get the current notes and check to ensure note IS present
		notes = grads.getTranscript(studentId).notes;
		Assert.assertEquals(notes.contains(note), true);
	}

	@Test
	public void testGenerateProgressSummary() throws Exception
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

		// Create a progress summary object
		ProgressSummary progressSummary1 =  new ProgressSummary();
		progressSummary1.student = studentRecord.student;
		progressSummary1.department = studentRecord.department;
		progressSummary1.termBegan = studentRecord.termBegan;
		progressSummary1.degreeSought = studentRecord.degreeSought;
		progressSummary1.advisors = studentRecord.advisors;
		progressSummary1.committee = studentRecord.committee;

		List<RequirementCheckResult> requirementCheckResults = new ArrayList<>();
		RequirementDetails details = new RequirementDetails();
		List<String> notes = new ArrayList<>();

		// Add core courses
		RequirementCheckResult result = new RequirementCheckResult();
		result.name = "CORE_COURSES_" + degreeSought.name;
		result.passed = false;
		details.notes = new ArrayList<>();
		details.notes.add("Core courses [csce791, csce551, csce750, csce531, csce513] are left to be taken.");
		result.details = details;
		requirementCheckResults.add (result);

		// Add Additional credits
		result = new RequirementCheckResult();
		details = new RequirementDetails();
		details.notes = new ArrayList<>();
		result.name = "ADDITIONAL_CREDITS_" + degreeSought.name;
		result.passed = false;
		details.notes.add("Must pass 17 more hours of CSCE courses numbered above 700 that are not core courses.");
		result.details = details;
		requirementCheckResults.add (result);

		// Add degree based credits
		result = new RequirementCheckResult();
		details = new RequirementDetails();
		details.notes = new ArrayList<>();
		result.name = "DEGREE_BASED_CREDITS_" + degreeSought.name;
		result.passed = false;
		details.courses.add(courseTaken);
		details.notes.add("Must pass 45 more hours of graduate courses.");
		details.notes.add("Must pass 21 more hours of CSCE courses numbered above 700.");
		result.details = details;
		requirementCheckResults.add (result);

		// Add thesis credits
		result = new RequirementCheckResult();
		details = new RequirementDetails();
		details.notes = new ArrayList<>();
		result.name = "THESIS_CREDITS_" + degreeSought.name;
		result.passed = false;
		details.notes.add("Must pass 12 more hours of csce899.");
		result.details = details;
		requirementCheckResults.add (result);

		// Add time limit
		result = new RequirementCheckResult();
		details = new RequirementDetails();
		details.notes = new ArrayList<>();
		result.name = "TIME_LIMIT_" + degreeSought.name;
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
		result.name = "MILESTONES_" + degreeSought.name;
		result.passed = false;
		details.notes.add("Missing milestone DISSERTATION_ADVISOR_SELECTED");
		details.notes.add("Missing milestone QUALIFYING_EXAM_PASSED");
		details.notes.add("Missing milestone DISSERTATION_SUBMITTED");
		details.notes.add("Missing milestone COMPREHENSIVE_EXAM_PASSED");
		details.notes.add("Missing milestone PROGRAM_OF_STUDY_SUBMITTED");
		details.notes.add("Missing milestone DISSERTATION_DEFENSE_PASSED");
		details.notes.add("Missing milestone DISSERTATION_COMMITTEE_FORMED");
		details.notes.add("Missing milestone DISSERTATION_DEFENSE_SCHEDULED");
		details.notes.add("Missing milestone DISSERTATION_PROPOSAL_SCHEDULED");
		result.details = details;
		requirementCheckResults.add (result);

		// Add the requirement check result
		progressSummary1.requirementCheckResults = requirementCheckResults;

		// Wrrite the progress summary to a string for comparison
		String outProgressSummary1 = new GsonBuilder().setPrettyPrinting().create().toJson(progressSummary1);

		GRADS grads = new GRADS();

		grads.loadRecords("students.txt");
		grads.loadUsers("users.txt");
		grads.loadCourses("courses.txt");

		grads.setUser("mmatthews");

		ProgressSummary progressSummary2 = grads.generateProgressSummary("hsmith");

		String outProgressSummary2 = new GsonBuilder().setPrettyPrinting().create().toJson(progressSummary2);

		Assert.assertEquals(outProgressSummary1.equals(outProgressSummary2),true);

	}

    @Test
    public void testSimulateCourses() throws Exception {

    }
}
