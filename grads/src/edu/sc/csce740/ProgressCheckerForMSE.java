package edu.sc.csce740;

import edu.sc.csce740.model.Milestone;
import edu.sc.csce740.model.RequirementCheckInput;
import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.StudentRecord;

import java.util.*;

/**
 * Created by paladin on 11/3/15.
 */
public class ProgressCheckerForMSE extends ProgressCheckerBase
{
	Set<String> optionalClassesIds = null;
	String experienceClasseId = "csce793";

	public ProgressCheckerForMSE()
	{
		degreeName = "MSE";
		additionalCredits = 15;
		yearsToFinishDegree = 6;

		requiredClassesIds = new HashSet<>();
		requiredClassesIds.add("csce740");
		requiredClassesIds.add("csce741");
		requiredClassesIds.add("csce742");
		requiredClassesIds.add("csce743");
        requiredClassesIds.add("csce747");

		milestones = new HashSet<>();
		milestones.add(Milestone.MilestoneType.ACADEMIC_ADVISOR_APPOINTED);
		milestones.add(Milestone.MilestoneType.PROGRAM_OF_STUDY_SUBMITTED);
		milestones.add(Milestone.MilestoneType.COMPREHENSIVE_EXAM_PASSED);
		milestones.add(Milestone.MilestoneType.REPORT_SUBMITTED);
		milestones.add(Milestone.MilestoneType.REPORT_APPROVED);

        optionalClassesIds = new HashSet<>();
		optionalClassesIds.add("csce510");
		optionalClassesIds.add("csce512");
		optionalClassesIds.add("csce515");
		optionalClassesIds.add("csce516");
		optionalClassesIds.add("csce520");
		optionalClassesIds.add("csce522");
		optionalClassesIds.add("csce547");
		optionalClassesIds.add("csce721");
		optionalClassesIds.add("csce723");
		optionalClassesIds.add("csce725");
		optionalClassesIds.add("csce744");
		optionalClassesIds.add("csce745");
		optionalClassesIds.add("csce767");
		optionalClassesIds.add("csce782");
		optionalClassesIds.add("csce821");
		optionalClassesIds.add("csce822");
		optionalClassesIds.add("csce826");
		optionalClassesIds.add("csce846");
		optionalClassesIds.add("mgsc872");
	}

    @Override
    RequirementCheckResult CheckAdditionalCredits()
    {
		/// TODO: implement it! See implementation in ProgressCheckerBase as a template
 		return null;
    }

    @Override
    RequirementCheckResult CheckDegreeBasedCredits() { return null; }

    @Override
    RequirementCheckResult CheckThesisCredits()
    {
        return null;
    }

    @Override
    RequirementCheckResult CheckExperience()
    {
		/// TODO. Implement it.
		return null;
    }
}
