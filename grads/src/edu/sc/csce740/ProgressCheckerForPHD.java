package edu.sc.csce740;

import edu.sc.csce740.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by paladin on 11/3/15.
 */
public class ProgressCheckerForPHD extends ProgressCheckerBase
{
	int degreeBasedCreditsWithMasters = 24;
	int degreeBasedCreditsWithoutMasters = 48;
	int degreeBasedCredits7xx = 24;
	Set<Degree.Type> mastersDegrees;

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

		mastersDegrees = new HashSet<>();
		mastersDegrees.add(Degree.Type.MENG);
		mastersDegrees.add(Degree.Type.MSE);
		mastersDegrees.add(Degree.Type.MS);
	} // End of ProgressCheckerForPHD constructor

    @Override
    RequirementCheckResult CheckDegreeBasedCredits()
    {
		RequirementCheckResult result = new RequirementCheckResult();
		result.name = "DEGREE_BASED_CREDITS_" + degreeName;
		result.details = new RequirementDetails();

		/// sum all credit hours from all non expired csce 7xx grad classes excluding excludedClassesIds.
		int graduate7xxScseCoursesHours = currentStudentRecord.coursesTaken.stream().filter(
				x -> x.course.Is7xx() && x.course.IsCSCE() && !excludedClassesIds.contains(x.course.id) &&
						!x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
				y -> Integer.parseInt(y.course.numCredits)).sum();

		/// collect all non expired non csce grad classes.
		int graduateScseCoursesHours = currentStudentRecord.coursesTaken.stream().filter(
				x -> x.course.IsGraduate() && x.course.IsCSCE() && !excludedClassesIds.contains(x.course.id) &&
						!x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
				y -> Integer.parseInt(y.course.numCredits)).sum();

		boolean hasMasterDegree = currentStudentRecord.previousDegrees.stream().filter(x -> mastersDegrees.contains(x.name)).findAny().isPresent();

		int graduateScseCourcesLeft = Math.max(0,
				hasMasterDegree ? degreeBasedCreditsWithMasters - graduateScseCoursesHours : degreeBasedCreditsWithoutMasters - graduateScseCoursesHours);
		int graduateScseCources7xxLeft = Math.max(0, degreeBasedCredits7xx - graduate7xxScseCoursesHours);

		if (graduateScseCourcesLeft == 0 && graduateScseCources7xxLeft == 0) {
			result.passed = true;
		} else {
			result.passed = false;
			result.details.notes = new ArrayList<>();
			/*"Must pass 25 more hours of graduate courses.",
                 "Must pass 11 more hours of CSCE courses numbered above 700."*/
			if (graduateScseCourcesLeft > 0) {
				result.details.notes.add("Must pass " + Integer.toString(graduateScseCourcesLeft) + " more hours of graduate courses.");
			}
			if (graduateScseCources7xxLeft > 0) {
				result.details.notes.add("Must pass " + Integer.toString(graduateScseCources7xxLeft) + " more hours of CSCE courses numbered above 700.");
			}
		}

		List<CourseTaken> takenGradCourses = Arrays.asList(currentStudentRecord.coursesTaken.stream().filter(
				x -> x.course.IsGraduate() && !x.term.isExpired(currentTerm, yearsToFinishClasses) && !excludedClassesIds.contains(x.course.id)).
				toArray(CourseTaken[]::new));

		result.details.courses = takenGradCourses;
		return result;
    } // End of CheckDegreeBasedCredits method

} // End of ProgressCheckerForPHD class
