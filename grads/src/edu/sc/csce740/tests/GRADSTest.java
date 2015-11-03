package edu.sc.csce740.tests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.sc.csce740.GRADS;
import edu.sc.csce740.GRADSIntf;
import edu.sc.csce740.model.ProgressSummary;
import edu.sc.csce740.model.User;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by paladin on 10/31/15.
 */
public class GRADSTest {

    @org.testng.annotations.Test
    public void test() throws Exception {
        GRADSIntf grads = new GRADS();
        grads.loadUsers("DB/users.txt");
        grads.loadCourses("DB/courses.txt");
        grads.loadRecords("DB/students.txt");
        List<ProgressSummary> reports = new Gson().fromJson( new FileReader( new File("DB/progress.txt")), new TypeToken<List<ProgressSummary>>(){}.getType());
        int fg = 78;
    }
}