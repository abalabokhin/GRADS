package edu.sc.csce740.model;

import java.util.List;
import java.util.ArrayList;

/**
 *  Class that accumulate RequirementDetails information. It uses only to store the data and does not have any methods.
 */
public class RequirementDetails
{
    public Float gpa;
    public List<CourseTaken> courses = new ArrayList<>();
    public List<Milestone> milestones;
    public List<String> notes;
}
