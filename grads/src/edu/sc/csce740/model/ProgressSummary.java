package edu.sc.csce740.model;

import java.util.List;

/**
 *  Class that accumulate ProgressSummary information. It uses only to store the data and does not have any methods.
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
