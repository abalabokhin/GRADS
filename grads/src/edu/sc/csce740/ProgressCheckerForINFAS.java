package edu.sc.csce740;

import edu.sc.csce740.model.RequirementCheckInput;
import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.StudentRecord;

import java.util.*;

/**
 * Created by paladin on 11/3/15.
 */
public class ProgressCheckerForINFAS extends ProgressCheckerBase
{
	private int yearsToFinishProgram = 6;

	public ProgressCheckerForINFAS()
	{
		degreeName = "INFAS";
		additionalCredits = 18;
		// Class ID followed by minimum # of credits for class
		// Enter a value greater then zero to use this feature
		// If need to take a class like 899 for 12 credits total enter 12
		// If zero is entered class can be taken once
		// If desired the actual credits for the course could be
		// entered and result would be the same
		requiredClassesIds = new HashSet<>();
		requiredClassesIds.add("csce522");
		requiredClassesIds.add("csce715");
		requiredClassesIds.add("csce727");

		// Class ID followed by maximum # of credits for class
		// Enter a value greater then zero to use this feature
		// If need to limit a class to 6 credits (2 classes) enter 6
		// If zero is entered class can not be taken at all to
		// meet the additional credits requirement. thesis check seperate.
		excludedClassesIds = new HashSet<>();
		excludedClassesIds.add("csce799");
	}

    @Override
    RequirementCheckResult CheckAdditionalCredits()
    {
		/// TODO: implement it! See implementation in ProgressCheckerBase as a template
//    	RequirementCheckResult result = null;
//
//		Map<String,Integer> copyExcludedClassesIds = new HashMap<>();
//		copyExcludedClassesIds.putAll(excludedClassesIds);
//		//copyExcludedClassesIds.putAll(requiredClassesIds);
//
//      	ProgressCheckerCommon checkerCommon = new ProgressCheckerCommon();
//
//		RequirementCheckInput requirementCheckInput = new RequirementCheckInput();
//		requirementCheckInput.excludedCourseIds = copyExcludedClassesIds;
//		requirementCheckInput.coursesTaken = currentStudentRecord.coursesTaken;
//		requirementCheckInput.yearsToFinishClasses = yearsToFinishClasses;
//		requirementCheckInput.minNbrCredits = additionalCredits;
//
//        result = checkerCommon.CheckCoursesByExclusion(requirementCheckInput);
//		result.name = "ADDITIONAL_CREDITS_INFAS";
//
        return null;

    } // End of CheckAdditionalCredits method



    @Override
    RequirementCheckResult CheckThesisCredits()
    {
        return null;
    }

    @Override
    RequirementCheckResult CheckMilestones()
    {
        return null;
    }
}
