package edu.sc.csce740;

import edu.sc.csce740.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by paladin on 11/3/15.
 */
public class ProgressCheckerForPHD extends ProgressCheckerBase
{
	Map<String,Integer> thesisClassesIds = null;

	private int degreeBasedCreditsWithMasters = 24;
	private int degreeBasedCreditsWithoutMasters = 48;

	public ProgressCheckerForPHD()
	{
		degreeName = "PHD";
		additionalCredits = 20;

		requiredClassesIds = new HashSet<>();
		requiredClassesIds.add("csce513");
		requiredClassesIds.add("csce531");
		requiredClassesIds.add("csce551");
		requiredClassesIds.add("csce750");
	    requiredClassesIds.add("csce791");

		// Class ID followed by maximum # of credits for class
		// Enter a value greater then zero to use this feature
		// If need to limit a class to 6 credits (2 classes) enter 6
		// If zero is entered class can not be taken at all to
		// meet the additional credits requirement. thesis check seperate.
		excludedClassesIds = new HashSet<>();
		excludedClassesIds.add("csce799");
		excludedClassesIds.add("csce899");

		// Class ID followed by minimum # of credits for class
		// Enter a value greater then zero to use this feature
		// If need to take a class like 899 for 12 credits total enter 12
		thesisClassesIds = new HashMap<>();
		thesisClassesIds.put("csce899",12);

	} // End of ProgressCheckerForPHD constructor

    @Override
    RequirementCheckResult CheckDegreeBasedCredits()
    {
		/// TODO: Implement it. See implementation in Base class as a template.
//    	RequirementCheckResult result = null;
//
//		//Map<String,Integer> allExcluded = excludedClassesIds;
//		//allExcluded.putAll(requiredClassesIds); // Add the required core courses to the excluded list
//
//      	ProgressCheckerCommon checkerCommon = new ProgressCheckerCommon();
//
//		RequirementCheckInput requirementCheckInput = new RequirementCheckInput();
//		//requirementCheckInput.excludedCourseIds = allExcluded;
//		requirementCheckInput.coursesTaken = currentStudentRecord.coursesTaken;
//		requirementCheckInput.yearsToFinishClasses = yearsToFinishClasses;
//		requirementCheckInput.minNbrCredits = degreeBasedCreditsWithMasters;
//		requirementCheckInput.csce700Level = true; // Count only CSCE courses 700 and above
//
//        result = checkerCommon.CheckCoursesByExclusion(requirementCheckInput);
//		result.name = "DEGREE_BASED_CREDITS_PHD";
//
//		List<Degree.Type> listDegrees = null;
//		listDegrees = currentStudentRecord.previousDegrees.stream().map(x -> x.name).collect(Collectors.toList());
//
//		// Check to see if the candidate has a masters degree already. If so then we can return current result
//		if (listDegrees != null)
//		{
//			if ((listDegrees.contains(Degree.Type.MS)) ||
//                (listDegrees.contains(Degree.Type.MENG)) ||
//                (listDegrees.contains(Degree.Type.MSE)))
//        	{
//				return result;
//			}
//		} // End of if for having a previous degree
//
//		// No masters so we need to check for total graduate courses. Keep the result of the 1st test as either result
//		// will cause the overall test to fail
//		boolean firstTest = false;
//		if (result.passed == true)
//			firstTest = true;
//
//		requirementCheckInput.minNbrCredits = degreeBasedCreditsWithoutMasters;
//		requirementCheckInput.csce700Level = false;
//		requirementCheckInput.graduateLevel = true; // Count all graduate courses
//
//		result = checkerCommon.CheckCoursesByExclusion(requirementCheckInput);
//		result.name = "DEGREE_BASED_CREDITS_PHD";
//
//		if ((result.passed == false) || (!firstTest))
//			result.passed = false;
//
//        return result;
		return null;

    } // End of CheckDegreeBasedCredits method

    @Override
    RequirementCheckResult CheckThesisCredits()
    {
		RequirementCheckResult result = null;

		ProgressCheckerCommon checkerCommon = new ProgressCheckerCommon();

		RequirementCheckInput requirementCheckInput = new RequirementCheckInput();
		requirementCheckInput.includedCourseIds = thesisClassesIds;
		requirementCheckInput.coursesTaken = currentStudentRecord.coursesTaken;
		requirementCheckInput.yearsToFinishClasses = yearsToFinishClasses;

		result = checkerCommon.CheckCoursesByInclusion(requirementCheckInput);
		result.name = "THESIS_CREDITS_PHD";

 		return result;
    }

} // End of ProgressCheckerForPHD class
