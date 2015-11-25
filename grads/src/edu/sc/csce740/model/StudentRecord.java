package edu.sc.csce740.model;

import java.util.List;

/**
 * model the relevant aspects of the data structure that stores a student’s information
 */
public class StudentRecord
{
    public Student student;
    public String department;
    public Term termBegan;
    public Degree degreeSought;
    public Certificate certificateSought;
    public List<Degree> previousDegrees;
    public List<Professor> advisors;
    public List<Professor> committee;
    public List<CourseTaken> coursesTaken;
    public List<Milestone> milestonesSet;
    public List<String> notes;
    public float gpa;

}
