package edu.sc.csce740;

import edu.sc.csce740.model.RequirementCheckInput;
import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.StudentRecord;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by paladin on 11/3/15.
 */
public class ProgressCheckerForMSE extends ProgressCheckerBase
{

    Map<String,Integer> requiredClassesIds = null;
	Map<String,Integer> optionalClassesIds = null;
	Map<String,Integer> experienceClassesIds = null;

	private int yearsToFinishClasses = 0;
	private int yearsToFinishProgram = 6;
	private int additionalCredits = 15;

	public ProgressCheckerForMSE()
	{
		// Class ID followed by minimum # of credits for class
		// Enter a value greater then zero to use this feature
		// If need to take a class like 899 for 12 credits total enter 12
		// If zero is entered class can be taken once
		// If desired the actual credits for the course could be
		// entered and result would be the same
		requiredClassesIds = new HashMap<>();
		requiredClassesIds.put("csce740",0);
		requiredClassesIds.put("csce741",0);
		requiredClassesIds.put("csce742",0);
		requiredClassesIds.put("csce743",0);
        requiredClassesIds.put("csce747",0);

		// Class ID followed by minimum # of credits for class
		// Enter a value greater then zero to use this feature
		// If need to take a class like 899 for 12 credits total enter 12
		// If zero is entered class can be taken once
		// If desired the actual credits for the course could be
		// entered and result would be the same
		// Class ID followed by minimum # of credits for class
        optionalClassesIds = new HashMap<>();
		optionalClassesIds.put("csce510",0);
		optionalClassesIds.put("csce512",0);
		optionalClassesIds.put("csce515",0);
		optionalClassesIds.put("csce516",0);
		optionalClassesIds.put("csce520",0);
		optionalClassesIds.put("csce522",0);
		optionalClassesIds.put("csce547",0);
		optionalClassesIds.put("csce721",0);
		optionalClassesIds.put("csce723",0);
		optionalClassesIds.put("csce725",0);
		optionalClassesIds.put("csce744",0);
		optionalClassesIds.put("csce745",0);
		optionalClassesIds.put("csce767",0);
		optionalClassesIds.put("csce782",0);
		optionalClassesIds.put("csce821",0);
		optionalClassesIds.put("csce822",0);
		optionalClassesIds.put("csce826",0);
		optionalClassesIds.put("csce846",0);
		optionalClassesIds.put("mgsc872",0);

		// Class ID followed by minimum # of credits for class
		experienceClassesIds = new HashMap<>();
		experienceClassesIds.put("csce793",0);
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
		result.name = "CORE_COURSES_MSE";

 		return result;
 	}

    @Override
    RequirementCheckResult CheckAdditionalCredits()
    {
		RequirementCheckResult result = null;

		ProgressCheckerCommon checkerCommon = new ProgressCheckerCommon();

		RequirementCheckInput requirementCheckInput = new RequirementCheckInput();
		requirementCheckInput.includedCourseIds = optionalClassesIds;
		requirementCheckInput.coursesTaken = currentStudentRecord.coursesTaken;
		requirementCheckInput.yearsToFinishClasses = yearsToFinishClasses;
		requirementCheckInput.minNbrCredits = additionalCredits;

		result = checkerCommon.CheckCoursesByInclusion(requirementCheckInput);
		result.name = "ADDITIONAL_CREDITS_MSE";

 		return result;

    } // End of CheckAdditionalCredits method


    @Override
    RequirementCheckResult CheckDegreeBasedCredits()
    {
        return null;
    }

    @Override
    RequirementCheckResult CheckThesisCredits()
    {
        return null;
    }

    @Override
    RequirementCheckResult CheckExperience()
    {
		RequirementCheckResult result = null;

		ProgressCheckerCommon checkerCommon = new ProgressCheckerCommon();

		RequirementCheckInput requirementCheckInput = new RequirementCheckInput();
		requirementCheckInput.includedCourseIds = experienceClassesIds;
		requirementCheckInput.coursesTaken = currentStudentRecord.coursesTaken;
		requirementCheckInput.yearsToFinishClasses = yearsToFinishClasses;

		result = checkerCommon.CheckCoursesByInclusion(requirementCheckInput);
		result.name = "EXPERIENCE";

 		return result;
    }
}
