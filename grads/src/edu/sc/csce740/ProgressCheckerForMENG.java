package edu.sc.csce740;

import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.StudentRecord;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by paladin on 11/3/15.
 */
public class ProgressCheckerForMENG extends ProgressCheckerBase
{
    Map<String,Integer> requiredClassesIds = null;
	Map<String,Integer> excludedClassesIds = null;

	private int yearsToFinishClasses = 0;
	private int yearsToFinishProgram = 6;
	private int additionalCredits = 11;

	private int degreeBasedCreditsGraduate = 30;
	private int degreeBasedCreditsCSCE = 24;

	public ProgressCheckerForMENG()
	{
		// Class ID followed by minimum # of credits for class
		// Enter a value greater then zero to use this feature
		// If need to take a class like 899 for 12 credits total enter 12
		// If zero is entered class can be taken once
		// If desired the actual credits for the course could be
		// entered and result would be the same
		requiredClassesIds = new HashMap<>();
		requiredClassesIds.put("csce513",0);
		requiredClassesIds.put("csce531",0);
		requiredClassesIds.put("csce750",0);
	    requiredClassesIds.put("csce791",0);

		// Class ID followed by maximum # of credits for class
		// Enter a value greater then zero to use this feature
		// If need to limit a class to 6 credits (2 classes) enter 6
		// If zero is entered class can not be taken at all to
		// meet the additional credits requirement. thesis check seperate.
		excludedClassesIds = new HashMap<>();
		excludedClassesIds.put("csce797",0);
		excludedClassesIds.put("csce798",3);
		excludedClassesIds.put("csce799",0);
	}

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
		result.name = "CORE_COURSES_MENG";

 		return result;
 	}

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

        result = checkerCommon.CheckCoursesByExclusion(requirementCheckInput);
		result.name = "ADDITIONAL_CREDITS_MENG";

        return result;

    } // End of CheckAdditionalCredits method



    @Override
    RequirementCheckResult CheckDegreeBasedCredits()
    {
    	RequirementCheckResult result = null;

		Map<String,Integer> allExcluded = excludedClassesIds;
		allExcluded.putAll(requiredClassesIds);

      	ProgressCheckerCommon checkerCommon = new ProgressCheckerCommon();

		RequirementCheckInput requirementCheckInput = new RequirementCheckInput();
		requirementCheckInput.excludedCourseIds = allExcluded;
		requirementCheckInput.coursesTaken = currentStudentRecord.coursesTaken;
		requirementCheckInput.yearsToFinishClasses = yearsToFinishClasses;
		requirementCheckInput.minNbrCredits = degreeBasedCreditsCSCE;
		requirementCheckInput.csce700Level = true;

        result = checkerCommon.CheckCoursesByExclusion(requirementCheckInput);
		result.name = "DEGREE_BASED_CREDITS_MENG";

		boolean firstTest = false;
		if (result.passed == true)
			firstTest = true;

		requirementCheckInput.minNbrCredits = degreeBasedCreditsGraduate;
		requirementCheckInput.csce700Level = false;
		requirementCheckInput.graduateLevel = true;

		result = checkerCommon.CheckCoursesByExclusion(requirementCheckInput);
		result.name = "DEGREE_BASED_CREDITS_MENG";

		if ((result.passed == false) || (!firstTest))
			result.passed = false;

        return result;

     } // End of CheckDegreeBasedCredits method

    @Override
    RequirementCheckResult CheckThesisCredits()
    {
        return null;
    }
}
