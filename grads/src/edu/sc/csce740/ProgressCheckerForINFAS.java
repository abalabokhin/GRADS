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
    public ProgressCheckerForINFAS()
	{
		degreeName = "INFAS";
		additionalCredits = 18;
        yearsToFinishDegree = 6;

		requiredClassesIds = new HashSet<>();
		requiredClassesIds.add("csce522");
		requiredClassesIds.add("csce715");
		requiredClassesIds.add("csce727");

		excludedClassesIds = new HashSet<>();
		excludedClassesIds.add("csce799");
	}

    @Override
    RequirementCheckResult CheckAdditionalCredits()
    {
		/// TODO: implement it! See implementation in ProgressCheckerBase as a template
        return null;

    }

    @Override
    RequirementCheckResult CheckThesisCredits() { return null; }

    @Override
    RequirementCheckResult CheckMilestones()
    {
        return null;
    }
}
