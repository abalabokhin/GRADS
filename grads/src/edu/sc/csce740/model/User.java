package edu.sc.csce740.model;

/**
 * Created by paladin on 10/31/15.
 */
public class User {
    public enum Role {
        STUDENT,
        GRADUATE_PROGRAM_COORDINATOR
    }

    public String id;
    public Role role;
    public String department;
}
