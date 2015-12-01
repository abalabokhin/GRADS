package edu.sc.csce740.tests;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import edu.sc.csce740.GRADSIntf;
import edu.sc.csce740.exception.DBIsNotAvailableOrCorruptedException;
import edu.sc.csce740.exception.DBIsNotLoadedException;
import edu.sc.csce740.exception.InvalidDataRequestedException;
import edu.sc.csce740.exception.NoUsersAreLoggedIn;
import edu.sc.csce740.exception.UserHasInsufficientPrivilegeException;
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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import static java.util.Arrays.asList;
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

	}

	@Test
	public void testGetStudentIDsFail() throws Exception {

	}

	@Test
	public void testGetTranscriptSuccess() throws Exception
	{
		StudentRecord studentRecord1 = createStudentRecord();

		addStudentRecordtoDB(studentRecord1);

		grads.loadRecords("students.txt");
		grads.loadUsers("users.txt");
		grads.setUser("mmatthews");

		StudentRecord studentRecord2 = grads.getTranscript("hsmith");
        assertReflectionEquals(studentRecord1, studentRecord2, ReflectionComparatorMode.LENIENT_ORDER);
	}

    @Test
    public void testGetTranscriptFail() throws Exception {



    }

	@Test
	public void testUpdateTranscriptSuccess() throws Exception
	{
		StudentRecord studentRecord = createStudentRecord();

		addStudentRecordtoDB(studentRecord);

		grads.loadRecords("students.txt");
		grads.loadUsers("users.txt");
		grads.setUser("mmatthews");

		// get the inital date for the term began
		studentRecord = grads.getTranscript("hsmith");
		String termBegan1 = studentRecord.termBegan.year + " " + studentRecord.termBegan.semester.toString();

	 	// update term began and save it the db
		studentRecord.termBegan.year = 2014;
		grads.updateTranscript("hsmith", studentRecord, true);

		grads.loadRecords("students.txt");

		// get the final date for the term began
		studentRecord = grads.getTranscript("hsmith");
		String termBegan2 = studentRecord.termBegan.year + " " + studentRecord.termBegan.semester.toString();

		// Record is NOT being saved
		Assert.assertEquals(termBegan1.equals(termBegan2), false);
	}

    @Test
    public void testUpdateTranscriptFail() throws Exception
    {




    }

	@Test
	public void testAddNoteSuccess() throws Exception
	{
		String note = "test note to be added";
		String GPCid = "mmatthews";

        StudentRecord studentRecord = createStudentRecord();
        String studentId = studentRecord.student.id;
        addStudentRecordtoDB(studentRecord);

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
		Assert.assertEquals(notes.contains(note), true);
	}

    @Test
    public void testAddNoteFail() throws Exception
    {


    }

	@Test
	public void testGenerateProgressSummaryPHDSuccess() throws Exception
	{
		StudentRecord studentRecord1 = createStudentRecord();
		String degreeSought = studentRecord1.degreeSought.name.toString();

		addStudentRecordtoDB(studentRecord1);

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

		// Wrrite the progress summary to a string for comparison
		String outProgressSummary1 = new GsonBuilder().setPrettyPrinting().create().toJson(progressSummary1);

		grads.loadRecords("students.txt");
		grads.loadUsers("users.txt");
		grads.loadCourses("courses.txt");

		grads.setUser("mmatthews");

		ProgressSummary progressSummary2 = grads.generateProgressSummary("hsmith");

		Assert.assertEquals(equalsStudent(progressSummary1.student,progressSummary2.student), true);
		Assert.assertEquals(progressSummary1.department.equals(progressSummary2.department), true);
		Assert.assertEquals(equalsTermBegan(progressSummary1.termBegan,progressSummary2.termBegan), true);
		Assert.assertEquals(equalsDegreeSought(progressSummary1.degreeSought,progressSummary2.degreeSought), true);
		Assert.assertEquals(equalsCertificateSought(progressSummary1.certificateSought,progressSummary2.certificateSought), true);
		Assert.assertEquals(equalsProfessors(progressSummary1.advisors,progressSummary2.advisors), true);
		Assert.assertEquals(equalsProfessors(progressSummary1.committee,progressSummary2.committee), true);
		Assert.assertEquals(equalsRequirementCheckResults(progressSummary1.requirementCheckResults,progressSummary2.requirementCheckResults), true);

		//String outProgressSummary1 = new GsonBuilder().setPrettyPrinting().create().toJson(progressSummary1);
		//String outProgressSummary2 = new GsonBuilder().setPrettyPrinting().create().toJson(progressSummary2);
		//Assert.assertEquals(outProgressSummary1.equals(outProgressSummary2),true);

	}

    @Test
    public void testGenerateProgressSummaryPHDFail() throws Exception
    {}

    @Test
    public void testGenerateProgressSummaryMSESuccess() throws Exception
    {}

    @Test
    public void testGenerateProgressSummaryMSEFail() throws Exception
    {}

    @Test
    public void testGenerateProgressSummaryMSSuccess() throws Exception
    {}

    @Test
    public void testGenerateProgressSummaryMSFail() throws Exception
    {}

    @Test
    public void testGenerateProgressSummaryMENGSuccess() throws Exception
    {}

    @Test
    public void testGenerateProgressSummaryMENGFail() throws Exception
    {}

    @Test
    public void testGenerateProgressSummaryINFASSuccess() throws Exception
    {}

    @Test
    public void testGenerateProgressSummaryINFASFail() throws Exception
    {}

    @Test
    public void testSimulateCoursesSuccess() throws Exception {

    }

    @Test
    public void testSimulateCoursesFail() throws Exception {

    }

	public void addStudentRecordtoDB(StudentRecord studentRecord) throws Exception
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

	public boolean equalsStudent(Student record1, Student record2)
	{
		if ((!record1.id.equals(record2.id)) ||
			(!record1.firstName.equals(record2.firstName)) ||
			(!record1.lastName.equals(record2.lastName)))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public boolean equalsTermBegan(Term term1, Term term2)
	{
		if ((term1.year != term2.year) ||
			(!term1.semester.equals(term2.semester)))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public boolean equalsCertificateSought(Certificate certSought1, Certificate certSought2)
	{
		if ((certSought1 != null) || (certSought1 != null))
		{
			if ((!certSought1.name.equals(certSought1.name)) ||
				(certSought1.graduation.year != certSought1.graduation.year) ||
				(!certSought1.graduation.semester.equals(certSought1.graduation.semester)))
			{
				return false;
			}
		}

		return true;
	}

	public boolean equalsDegreeSought(Degree degreeSought1, Degree degreeSought2)
	{
		if ((!degreeSought1.name.equals(degreeSought2.name)) ||
			(degreeSought1.graduation.year != degreeSought2.graduation.year) ||
			(!degreeSought1.graduation.semester.equals(degreeSought2.graduation.semester)))
		{
			return false;
		}
		else
		{
			return true;
		}

	}

	// Previous Degrees equals check
	public boolean equalsPreviousDegrees(List<Degree> previousDegrees1, List<Degree> previousDegrees2)
	{
		if ((previousDegrees1 != null) || (previousDegrees2 != null))
		{
			if (previousDegrees1.size() != previousDegrees2.size())
			{
				return false;
			}

			for (Degree degree1 : previousDegrees1)
			{
   				Degree degree2 = previousDegrees2.stream().filter(x -> x.name.equals(degree1.name) &&
   				                                                       x.graduation.year == degree1.graduation.year &&
   				                                                       x.graduation.semester.equals(degree1.graduation.semester)).findFirst().get();
   				previousDegrees2.remove(degree2);
			}

			if (previousDegrees2.size() != 0)
			{
				return false;
			}
		}

		return true;
	}


	// Committee members and Advisors check
	public boolean equalsProfessors(List<Professor> professors1,  List<Professor> professors2)
	{
		if ((professors1 != null) || (professors2 != null))
		{
			if (professors1.size() != professors2.size())
			{
				return false;
			}

			for (Professor member1 : professors1)
			{
   				Professor member2 = professors2.stream().filter(x -> x.firstName.equals(member1.firstName) &&
   				                                                     x.lastName.equals(member1.lastName) &&
   				                                                     x.department.equals(member1.department)).findFirst().get();
   				professors2.remove(member2);
			}

			if (professors2.size() != 0)
			{
				return false;
			}
		}

		return true;
	}

	// Courses taken equals check
	public boolean equalsCoursesTaken(List<CourseTaken> coursesTaken1, List<CourseTaken> coursesTaken2)
	{
		if ((coursesTaken1 != null) || (coursesTaken2 != null))
		{
			if (coursesTaken1.size() != coursesTaken2.size())
			{
				return false;
			}

			int listSize = coursesTaken1.size();

			for (CourseTaken courseTaken1 : coursesTaken1)
			{
   				CourseTaken courseTaken2 = coursesTaken2.stream().filter(x -> x.course.id.equals(courseTaken1.course.id) &&
   				                                                              x.term.year == courseTaken1.term.year &&
   				                                                              x.term.semester.equals(courseTaken1.term.semester) &&
   				                                                              x.grade.equals(courseTaken1.grade)).findFirst().get();
				if (courseTaken2 != null)
				{
					listSize --;
				}

			}

			if (listSize != 0)
			{
				return false;
			}
		}

		return true;
	}

	// Milestones equal check
	public boolean equalsMilestonesSet(List<Milestone> milestonesSet1, List<Milestone> milestonesSet2)
	{
		if ((milestonesSet1 != null) || (milestonesSet2 != null))
		{
			if (milestonesSet1.size() != milestonesSet2.size())
			{
				return false;
			}

			for (Milestone milestone1 : milestonesSet1)
			{
   				Milestone milestone2 = milestonesSet2.stream().filter(x -> x.milestone.equals(milestone1.milestone) &&
   				                                                           x.term.year == milestone1.term.year &&
   				                                                           x.term.semester.equals(milestone1.term.semester)).findFirst().get();
   				milestonesSet2.remove(milestone2);
			}

			if (milestonesSet2.size() != 0)
			{
				return false;
			}
		}

		return true;
	}

	// Notes equal check
	public boolean equalsNotes(List<String> notes1, List<String> notes2)
	{
		if ((notes1 != null) || (notes2 != null))
		{
			if (notes1.size() != notes2.size())
			{
				return false;
			}

			notes2.removeAll(notes1);

			if (notes2.size() != 0)
			{
				return false;
			}
		}

		return true;
	}

	// Requirements equal check
	public boolean equalsRequirementCheckResults(List<RequirementCheckResult> resultsSet1, List<RequirementCheckResult> resultsSet2)
	{
		if ((resultsSet1 != null) || (resultsSet2 != null))
		{
			if (resultsSet1.size() != resultsSet2.size())
			{
				return false;
			}

			for (RequirementCheckResult result1 : resultsSet1)
			{
			   	RequirementCheckResult result2 = resultsSet2.stream().filter(x -> x.name.equals(result1.name)).findFirst().get();

			   	if (result2.passed != result1.passed)
			   		return false;

			   	if ((result1.details != null) || (result2.details != null))
			   	{

					if ((result1.details.gpa != null) || (result2.details.gpa != null))
					{
						if (Math.abs(result1.details.gpa - result2.details.gpa) > .00001)
						{
							return false;
						}
					}

					if ((!equalsCoursesTaken(result1.details.courses,result2.details.courses)) ||
					    (!equalsMilestonesSet(result1.details.milestones,result2.details.milestones)) ||
					    (!equalsNotes(result1.details.notes,result2.details.notes)))
					{
						return false;
					}

				}

			   	resultsSet2.remove(result2);
			}

			if (resultsSet2.size() != 0)
			{
				return false;
			}
		}

		return true;
	}

}
