package edu.sc.csce740.model;

/**
 * Created by paladin on 10/31/15.
 */
public class CourseTaken {
    public enum Grade {
        A, B, C, D, F, P, _
    }

    public Course course;
    public Term term;
    public Grade grade;
}
