package edu.sc.csce740.tests;

import edu.sc.csce740.GRADS;
import edu.sc.csce740.GRADSIntf;
import org.junit.Test;

public class GRADSTest {

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
    public void testGetUser() throws Exception {

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
    public void testGenerateProgressSummary() throws Exception {
        GRADSIntf grads = new GRADS();
        grads.loadUsers("users.txt");

        grads.setUser("mmatthews");
        grads.loadCourses("courses.txt");
        grads.loadRecords("students.txt");

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

    @Test
    public void testSimulateCourses() throws Exception {

    }
}