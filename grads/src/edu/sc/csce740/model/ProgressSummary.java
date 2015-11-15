package edu.sc.csce740.model;

import java.util.List;

/**
 * Created by paladin on 10/31/15.
 */
public class ProgressSummary
{
    public Student student;
    public String department;
    public Term termBegan;
    public Degree degreeSought;
    public Certificate certificateSought;
    public List<Professor> advisors;
    public List<Professor> committee;
    public List<RequirementCheckResult> requirementCheckResults;
}
