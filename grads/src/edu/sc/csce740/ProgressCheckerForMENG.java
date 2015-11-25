package edu.sc.csce740;

import edu.sc.csce740.model.Milestone;
import edu.sc.csce740.model.RequirementCheckResult;

import java.util.HashSet;

/**
 * This class covers requirements related to the 'MENG' program of study.
 */
public class ProgressCheckerForMENG extends ProgressCheckerBase {

	/**
	 * Default constructor, set all the necessary data to the exact program of study.
	 */
	public ProgressCheckerForMENG() {
		degreeName = "MENG";
		additionalCredits = 11;
		degreeBasedCredits = 30;
		nonCsceCredits = 6;
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
		milestones.add(Milestone.MilestoneType.PROGRAM_OF_STUDY_SUBMITTED);
		milestones.add(Milestone.MilestoneType.COMPREHENSIVE_EXAM_PASSED);
	}

    @Override
    RequirementCheckResult CheckThesisCredits() {
		return null;
    }
}
