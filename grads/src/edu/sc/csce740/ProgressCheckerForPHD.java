package edu.sc.csce740;

import edu.sc.csce740.model.Course;
import edu.sc.csce740.model.RequirementCheckInput;
import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.StudentRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Created by paladin on 11/3/15.
 */
public class ProgressCheckerForPHD extends ProgressCheckerBase
{
    Map<String,Integer> requiredClassesIds = null;
	Map<String,Integer> excludedClassesIds = null;
	Map<String,Integer> thesisClassesIds = null;

	private int yearsToFinishClasses = 6;
	private int yearsToFinishProgram = 6;
	private int additionalCredits = 20;
	private int degreeBasedCreditsWithMasters = 24;
	private int degreeBasedCreditsWithoutMasters = 48;

	public ProgressCheckerForPHD()
	{
		// Class ID followed by minimum # of credits for class
		// Enter a value greater then zero to use this feature
		// If need to take a class like 899 for 12 credits total enter 12
		requiredClassesIds = new HashMap<>();
		requiredClassesIds.put("csce513",0);
		requiredClassesIds.put("csce531",0);
		requiredClassesIds.put("csce551",0);
		requiredClassesIds.put("csce750",0);
	    requiredClassesIds.put("csce791",0);

		// Class ID followed by maximum # of credits for class
		// Enter a value greater then zero to use this feature
		// If need to limit a class to 6 credits (2 classes) enter 6
		// If zero is entered class can not be taken at all to
		// meet the additional credits requirement. thesis check seperate.
		excludedClassesIds = new HashMap<>();
		excludedClassesIds.put("csce799",0);
		excludedClassesIds.put("csce899",0);

		// Class ID followed by minimum # of credits for class
		// Enter a value greater then zero to use this feature
		// If need to take a class like 899 for 12 credits total enter 12
		thesisClassesIds = new HashMap<>();
		thesisClassesIds.put("csce899",12);

	} // End of ProgressCheckerForPHD constructor

	@Override
	RequirementCheckResult CheckCoreCourses()
 	{
		RequirementCheckResult result = null;

		Map<String,Integer> copyRequiredClassesIds = new HashMap<>();
		copyRequiredClassesIds.putAll(requiredClassesIds);

		ProgressCheckerCommon checkerCommon = new ProgressCheckerCommon();

		RequirementCheckInput requirementCheckInput = new RequirementCheckInput();
		requirementCheckInput.includedCourseIds = copyRequiredClassesIds;
		requirementCheckInput.coursesTaken = currentStudentRecord.coursesTaken;
		requirementCheckInput.yearsToFinishClasses = yearsToFinishClasses;

		result = checkerCommon.CheckCoursesByInclusion(requirementCheckInput);
		result.name = "CORE_COURSES_PHD";

 		return result;

 	} // End of CheckCoreCourses method


    @Override
    RequirementCheckResult CheckAdditionalCredits()
    {
    	RequirementCheckResult result = null;

		Map<String,Integer> copyExcludedClassesIds = new HashMap<>();
		copyExcludedClassesIds.putAll(excludedClassesIds);
		copyExcludedClassesIds.putAll(requiredClassesIds);

      	ProgressCheckerCommon checkerCommon = new ProgressCheckerCommon();

		RequirementCheckInput requirementCheckInput = new RequirementCheckInput();
		requirementCheckInput.excludedCourseIds = copyExcludedClassesIds;
		requirementCheckInput.coursesTaken = currentStudentRecord.coursesTaken;
		requirementCheckInput.yearsToFinishClasses = yearsToFinishClasses;
		requirementCheckInput.minNbrCredits = additionalCredits;
		requirementCheckInput.csce700Level = true;

        result = checkerCommon.CheckCoursesByExclusion(requirementCheckInput);
		result.name = "ADDITIONAL_CREDITS_PHD";

        return result;

    } // End of CheckAdditionalCredits method

    @Override
    RequirementCheckResult CheckDegreeBasedCredits()
    {
    	RequirementCheckResult result = null;

		Map<String,Integer> allExcluded = excludedClassesIds;
		allExcluded.putAll(requiredClassesIds); // Add the required core courses to the excluded list

      	ProgressCheckerCommon checkerCommon = new ProgressCheckerCommon();

		RequirementCheckInput requirementCheckInput = new RequirementCheckInput();
		requirementCheckInput.excludedCourseIds = allExcluded;
		requirementCheckInput.coursesTaken = currentStudentRecord.coursesTaken;
		requirementCheckInput.yearsToFinishClasses = yearsToFinishClasses;
		requirementCheckInput.minNbrCredits = degreeBasedCreditsWithMasters;
		requirementCheckInput.csce700Level = true; // Count only CSCE courses 700 and above

        result = checkerCommon.CheckCoursesByExclusion(requirementCheckInput);
		result.name = "DEGREE_BASED_CREDITS_PHD";

		List<Degree.Type> listDegrees = null;
		listDegrees = currentStudentRecord.previousDegrees.stream().map(x -> x.name).collect(Collectors.toList());

		// Check to see if the candidate has a masters degree already. If so then we can return current result
		if (listDegrees != null)
		{
			if ((listDegrees.contains(Degree.Type.MS)) ||
                (listDegrees.contains(Degree.Type.MENG)) ||
                (listDegrees.contains(Degree.Type.MSE)))
        	{
				return result;
			}
		} // End of if for having a previous degree

		// No masters so we need to check for total graduate courses. Keep the result of the 1st test as either result
		// will cause the overall test to fail
		boolean firstTest = false;
		if (result.passed == true)
			firstTest = true;

		requirementCheckInput.minNbrCredits = degreeBasedCreditsWithoutMasters;
		requirementCheckInput.csce700Level = false;
		requirementCheckInput.graduateLevel = true; // Count all graduate courses

		result = checkerCommon.CheckCoursesByExclusion(requirementCheckInput);
		result.name = "DEGREE_BASED_CREDITS_PHD";

		if ((result.passed == false) || (!firstTest))
			result.passed = false;

        return result;

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
