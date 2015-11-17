package edu.sc.csce740;

import edu.sc.csce740.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by paladin on 11/3/15.
 */
public class ProgressCheckerForPHD extends ProgressCheckerBase
{
	int degreeBasedCreditsWithMasters = 24;
	int degreeBasedCreditsWithoutMasters = 48;

	public ProgressCheckerForPHD()
	{
		degreeName = "PHD";
		additionalCredits = 20;
		thesisCredits = 12;
		thesisClassId = "csce899";
		yearsToFinishDegree = 8;

		requiredClassesIds = new HashSet<>();
		requiredClassesIds.add("csce513");
		requiredClassesIds.add("csce531");
		requiredClassesIds.add("csce551");
		requiredClassesIds.add("csce750");
	    requiredClassesIds.add("csce791");

		excludedClassesIds = new HashSet<>();
		excludedClassesIds.add("csce799");
		excludedClassesIds.add("csce899");

		milestones = new HashSet<>();
		milestones.add(Milestone.MilestoneType.DISSERTATION_ADVISOR_SELECTED);
		milestones.add(Milestone.MilestoneType.PROGRAM_OF_STUDY_SUBMITTED);
		milestones.add(Milestone.MilestoneType.DISSERTATION_COMMITTEE_FORMED);
		milestones.add(Milestone.MilestoneType.QUALIFYING_EXAM_PASSED);
		milestones.add(Milestone.MilestoneType.DISSERTATION_PROPOSAL_SCHEDULED);
		milestones.add(Milestone.MilestoneType.COMPREHENSIVE_EXAM_PASSED);
		milestones.add(Milestone.MilestoneType.DISSERTATION_SUBMITTED);
		milestones.add(Milestone.MilestoneType.DISSERTATION_DEFENSE_SCHEDULED);
		milestones.add(Milestone.MilestoneType.DISSERTATION_DEFENSE_PASSED);
	} // End of ProgressCheckerForPHD constructor

    @Override
    RequirementCheckResult CheckDegreeBasedCredits()
    {
		/// TODO: Implement it. See implementation in Base class as a template.
		return null;
    } // End of CheckDegreeBasedCredits method

} // End of ProgressCheckerForPHD class
