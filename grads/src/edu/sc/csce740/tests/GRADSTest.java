package edu.sc.csce740.tests;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import java.io.FileWriter;
import java.io.File;
import java.io.FileReader;

import org.junit.Assert;
import org.junit.Test;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GRADSTest extends TestCase {

    @Test
    public void testLoadUsers() throws Exception {

    }

    @Test
    public void testLoadCourses() throws Exception {

    }

    @Test
    public void testLoadRecords() throws Exception {

    }

    @Test
    public void testSetUser() throws Exception {

    }

    @Test
    public void testClearSession() throws Exception {

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

	// Error user not in db
	grads.setUser(testUser2);
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
    public void testAddNote() throws Exception {

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
	Professor professor = new Professor();
    	professor.department = "COMPUTER_SCIENCE";
    	professor.firstName = "Gregory";
      	professor.lastName = "Gay";
      	studentRecord.advisors.add (professor);

	// Committee
      	studentRecord.committee = new ArrayList<>();

	// Committe member #1
      	studentRecord.committee.add (professor);

	// Committe member #2
       	professor.department = "COMPUTER_SCIENCE";
    	professor.firstName = "Duncan";
      	professor.lastName = "Buell";
	studentRecord.committee.add (professor);

	// Committe member #3
       	professor.department = "COMPUTER_SCIENCE";
    	professor.firstName = "Caroline";
      	professor.lastName = "Eastman";
	studentRecord.committee.add (professor);

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
	//ProgressSummary progressSummary =  new ProgressSummary();
	///progressSummary.student = studentRecord.student;
	//progressSummary.department = studentRecord.department;
	//progressSummary.termBegan = studentRecord.termBegan;
	//progressSummary.degreeSought = studentRecord.degreeSought;
	//progressSummary.advisors = studentRecord.advisors;
	//progressSummary.committee = studentRecord.committee;

	GRADS grads = new GRADS();

	grads.loadRecords("students.txt");
	grads.loadUsers("users.txt");
	grads.loadCourses("courses.txt");

	grads.setUser("mmatthews");

	ProgressSummary progressSummary = grads.generateProgressSummary("hsmith");

	List<RequirementCheckResult> requirementCheckResults = progressSummary.requirementCheckResults;

	for (int i = 0; i < requirementCheckResults.size(); i++)
	{
		RequirementCheckResult result = requirementCheckResults.get(i);

	    	if (result.name.equals("CORE_COURSES_" + degreeSought.name))
	    	{
			Assert.assertEquals(result.passed, false);
		}
		else if (result.name.equals("ADDITIONAL_CREDITS_" + degreeSought.name))
		{
			Assert.assertEquals(result.passed, false);
		}
		else if (result.name.equals("DEGREE_BASED_CREDITS_" + degreeSought.name))
		{
			Assert.assertEquals(result.passed, false);
		}
		else if (result.name.equals("THESIS_CREDITS_" + degreeSought.name))
		{
			Assert.assertEquals(result.passed, false);
		}
		else if (result.name.equals("TIME_LIMIT_" + degreeSought.name))
		{
			Assert.assertEquals(result.passed, true);
		}
		else if (result.name.equals("MILESTONES_" + degreeSought.name))
		{
			Assert.assertEquals(result.passed, false);
		}
		else if (result.name.equals("GPA"))
		{
			Assert.assertEquals(result.passed, true);
		}

	}

    }

    @Test
    public void testSimulateCourses() throws Exception {

    }
}
