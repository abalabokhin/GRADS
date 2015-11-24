package edu.sc.csce740.tests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.GsonBuilder;

import edu.sc.csce740.GRADS;
import edu.sc.csce740.GRADSIntf;
import edu.sc.csce740.model.ProgressSummary;
import edu.sc.csce740.model.StudentRecord;
import edu.sc.csce740.model.Term;
import edu.sc.csce740.model.User;
import org.testng.Assert;

import java.io.File;
import java.io.FileReader;
import java.util.List;

//import static org.testng.Assert.*;

/**
 * Created by paladin on 10/31/15.
 */
public class GRADSTest
{
    @org.testng.annotations.Test
    public static void test() throws Exception {
		GRADSIntf grads = new GRADS();
		grads.loadUsers("DB/users.txt");

		grads.setUser("mmatthews");
		grads.loadCourses("DB/courses.txt");
		grads.loadRecords("DB/students.txt");

		//grads.addNote("mhunt", "New Note", true);

		// Print the student IDs
//		List<String> studentIDs = grads.getStudentIDs();
//		String representation = new GsonBuilder().setPrettyPrinting().create().toJson(studentIDs);
//		System.out.println(representation);

/*

		// Print the transcript
		System.out.println("=========================================");
		System.out.println("Print the transcript");
		System.out.println("=========================================");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		StudentRecord sr = grads.getTranscript("mhunt");
		String json = gson.toJson(sr);
		System.out.println(json);

		// Print the progress summary
		System.out.println("=========================================");
		System.out.println("Print the progress summary");
		System.out.println("=========================================");
		Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
		ProgressSummary ps = grads.generateProgressSummary("mhunt");
		String json2 = gson2.toJson(ps);
		System.out.println(json2);

*/
	}

	@org.testng.annotations.Test
	public static void testExpiration() throws Exception {
		Term pastTerm1 = new Term(2011, Term.Season.FALL);
		Term pastTerm2 = new Term(2011, Term.Season.SPRING);
		Term pastTerm3 = new Term(2011, Term.Season.SUMMER);

		Term currentTerm1 = new Term(2012, Term.Season.FALL);
		Term currentTerm2 = new Term(2017, Term.Season.SPRING);
		Term currentTerm3 = new Term(2017, Term.Season.SUMMER);
		Term currentTerm4 = new Term(2018, Term.Season.SUMMER);
		Term currentTerm5 = new Term(2017, Term.Season.FALL);

		Assert.assertEquals(pastTerm1.isExpired(currentTerm1, 6), false);
		Assert.assertEquals(pastTerm2.isExpired(currentTerm2, 6), true);
		Assert.assertEquals(pastTerm3.isExpired(currentTerm3, 6), true);
		Assert.assertEquals(pastTerm3.isExpired(currentTerm4, 6), true);
		Assert.assertEquals(pastTerm1.isExpired(currentTerm5, 6), true);

	}
}

