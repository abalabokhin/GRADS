package edu.sc.csce740.model;

/**
 *  Class that extends Human class with id, role and department.
 */
public class User extends Human
{
    /**
     *  Enum to represent all the possible roles.
     */
    public enum Role {STUDENT, GRADUATE_PROGRAM_COORDINATOR}

    public String id;
    public Role role;
    public String department;
}
