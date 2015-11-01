package edu.sc.csce740.tests;

import edu.sc.csce740.GRADS;
import edu.sc.csce740.GRADSIntf;

import static org.testng.Assert.*;

/**
 * Created by paladin on 10/31/15.
 */
public class GRADSTest {

    @org.testng.annotations.Test
    public void testLoadUsers() throws Exception {
        GRADSIntf grads = new GRADS();
        grads.loadUsers("DB/users.txt");
    }
}