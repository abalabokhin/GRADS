package edu.sc.csce740;

import edu.sc.csce740.model.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by paladin on 11/3/15.
 */
public class ProgressCheckerForMSE extends ProgressCheckerBase
{
	Set<String> optionalClassesIds = null;
	String workExperienceEquivalentClassId = "csce793";

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
        /*
        Students​ must pass a minimum of 15 credit hours in graduate courses
        (excluding required courses).
        */

		RequirementCheckResult result = new RequirementCheckResult();

		/// collect all non expired csce grad classes excluding excludedClassesIds and csce798
		int specialCoursesHours = currentStudentRecord.coursesTaken.stream().filter(
				x -> specialCourse.equals(x.course.id) &&
						!x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
				y -> Integer.parseInt(y.course.numCredits)).sum();

		/// collect all non expired csce grad classes excluding excludedClassesIds and csce798
		int graduateScseCoursesHours = currentStudentRecord.coursesTaken.stream().filter(
				x -> x.course.IsGraduate() && x.course.IsCSCE() && !specialCourse.equals(x.course.id) &&
						!excludedClassesIds.contains(x.course.id) &&
						!x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
				y -> Integer.parseInt(y.course.numCredits)).sum();

		/// collect all non expired non csce grad classes.
		int graduateNonScseCoursesHours = currentStudentRecord.coursesTaken.stream().filter(
				x -> x.course.IsGraduate() && !x.course.IsCSCE() &&
						!x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
				y -> Integer.parseInt(y.course.numCredits)).sum();

		int totalHours = Math.min(specialCoursesHours, specialCourseMaxCredits) + graduateScseCoursesHours +
				Math.min(nonCsceCredits, graduateNonScseCoursesHours);

		if (totalHours >= degreeBasedCredits)
			result.passed = true;
		else {
			result.passed = false;
			result.details = new RequirementDetails();
			result.details.notes = new ArrayList<>();
			result.details.notes.add("Must pass " +
					String.valueOf(additionalCredits - totalHours) +
					" more hours of graduate courses.");
		}

		List<CourseTaken> takenGradCourses = Arrays.asList(currentStudentRecord.coursesTaken.stream().filter(
				x -> x.course.IsGraduate() && !x.term.isExpired(currentTerm, yearsToFinishClasses)).
				toArray(CourseTaken[]::new));

		result.details.courses = takenGradCourses;
		result.name = "ADDITIONAL_CREDITS_" + degreeName;

		return result;
	}





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
		RequirementCheckResult result = new RequirementCheckResult()

			if(currentStudentRecord.coursesTaken.stream().filter(x -> x.course.id.equals(workExperienceEquivalentClassId)
					&& !x.term.isExpired(currentTerm, yearsToFinishClasses)).findFirst().isPresent()){
				result.passed = true;

			} else {
				result.passed = false;
				result.details = new RequirementDetails();
				result.details.notes = new ArrayList<>();
				result.details.notes.add("Must pass " +
						String.valueOf(workExperienceEquivalentClassId));
			}

		return result;
		}



}
