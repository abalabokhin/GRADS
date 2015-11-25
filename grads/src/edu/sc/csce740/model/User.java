package edu.sc.csce740.model;

/**
 * models the relevant aspects of the data structure that stores
 * the user information associated with the department.
 */
public class User extends Human
{
    public enum Role {STUDENT, GRADUATE_PROGRAM_COORDINATOR}

    public String id;
    public Role role;
    public String department;
}
