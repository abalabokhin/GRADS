package edu.sc.csce740.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequirementCheckInput
{
	public Map<String,Integer> includedCourseIds;
	public Map<String,Integer> excludedCourseIds;
	public List<CourseTaken> coursesTaken;
	public int yearsToFinishClasses;
	public int minNbrCredits;
	public boolean graduateLevel = false;
	public boolean csce700Level = false;
}
