package edu.sc.csce740.model;

import java.util.List;
import java.util.ArrayList;

/**
 *models the relevant aspects of the data structure that stores
 *  program requirements such as gpa, courses completed and milestones.
 */
public class RequirementDetails
{
    public Float gpa;
    public List<CourseTaken> courses = new ArrayList<>();
    public List<Milestone> milestones;
    public List<String> notes;
}
