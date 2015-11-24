package edu.sc.csce740;

import edu.sc.csce740.model.Milestone;

import java.util.*;

/**
 * This class covers requirements related to the 'MS' program of study.
 */
public class ProgressCheckerForMS extends ProgressCheckerBase
{
	public ProgressCheckerForMS()
	{
		degreeName = "MS";
		additionalCredits = 8;
		degreeBasedCredits = 24;
		nonCsceCredits = 6;
		thesisCredits = 6;
		thesisClassId = "csce799";
		yearsToFinishDegree = 6;

		requiredClassesIds = new HashSet<>();
		requiredClassesIds.add("csce513");
		requiredClassesIds.add("csce531");
		requiredClassesIds.add("csce750");
	    requiredClassesIds.add("csce791");

		excludedClassesIds = new HashSet<>();
		excludedClassesIds.add("csce797");
		excludedClassesIds.add("csce799");

		milestones = new HashSet<>();
		milestones.add(Milestone.MilestoneType.ACADEMIC_ADVISOR_APPOINTED);
		milestones.add(Milestone.MilestoneType.THESIS_ADVISOR_SELECTED);
		milestones.add(Milestone.MilestoneType.PROGRAM_OF_STUDY_SUBMITTED);
		milestones.add(Milestone.MilestoneType.THESIS_COMMITTEE_FORMED);
		milestones.add(Milestone.MilestoneType.THESIS_PROPOSAL_SCHEDULED);
		milestones.add(Milestone.MilestoneType.THESIS_PROPOSAL_APPROVED);
		milestones.add(Milestone.MilestoneType.THESIS_SUBMITTED);
		milestones.add(Milestone.MilestoneType.THESIS_DEFENSE_SCHEDULED);
		milestones.add(Milestone.MilestoneType.THESIS_DEFENSE_PASSED);
	}
}
