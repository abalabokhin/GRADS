package edu.sc.csce740.tests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.GsonBuilder;

import edu.sc.csce740.GRADS;
import edu.sc.csce740.GRADSIntf;
import edu.sc.csce740.model.ProgressSummary;
import edu.sc.csce740.model.StudentRecord;
import edu.sc.csce740.model.User;

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
    public static void test() throws Exception
    {
        GRADSIntf grads = new GRADS();
        grads.loadUsers("/Users/razrl3u1/Downloads/GRADS_Materials/data/users.txt");

        grads.setUser("mmatthews");
		grads.loadCourses("/Users/razrl3u1/Downloads/GRADS_Materials/data/courses.txt");
		grads.loadRecords("/Users/razrl3u1/Downloads/GRADS_Materials/data/students.txt");

		// Print the student IDs
		List<String> studentIDs = grads.getStudentIDs();
		String representation = new GsonBuilder().setPrettyPrinting().create().toJson(studentIDs);
		System.out.println(representation);

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
}