package edu.sc.csce740.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by paladin on 11/2/15.
 */
public class RequirementDetails
{
    public Float gpa;
    public List<CourseTaken> courses = new ArrayList<>();
    public List<Milestone> milestones;
    public List<String> notes;
}
