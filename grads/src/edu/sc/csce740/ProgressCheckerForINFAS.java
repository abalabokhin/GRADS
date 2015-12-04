package edu.sc.csce740;

import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.RequirementDetails;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class covers requirements related to the Information Assurance Certificate program of study.
 */
public class ProgressCheckerForINFAS extends ProgressCheckerBase {
    int additional7xxCredits = 9;

    /**
     * Default constructor, set all the necessary data to the exact certificate.
     */
    public ProgressCheckerForINFAS() {
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
    RequirementCheckResult CheckAdditionalCredits(){
        RequirementCheckResult result = new RequirementCheckResult();
        /// collect all non expired csce classes above 7 hundred excluding requiredClassesIds and excludedClassesIds.
        int CSCE7xxCourses = currentStudentRecord.coursesTaken.stream().filter(
                x -> x.course.Is7xx() && x.course.IsCSCE() &&
                        !requiredClassesIds.contains(x.course.id) && !excludedClassesIds.contains(x.course.id) &&
                        !x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
                y -> Integer.parseInt(y.course.numCredits)).sum();

        int nonCSCECourses = currentStudentRecord.coursesTaken.stream().filter(
                x -> !x.course.IsCSCE() && x.course.IsGraduate() &&
                        !requiredClassesIds.contains(x.course.id) && !excludedClassesIds.contains(x.course.id) &&
                        !x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
                y -> Integer.parseInt(y.course.numCredits)).sum();

        int CSCE5xxCourses = currentStudentRecord.coursesTaken.stream().filter(
                x -> !x.course.Is7xx() && x.course.IsCSCE() && x.course.IsGraduate() &&
                        !requiredClassesIds.contains(x.course.id) && !excludedClassesIds.contains(x.course.id) &&
                        !x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
                y -> Integer.parseInt(y.course.numCredits)).sum();

        nonCSCECourses = Math.max(6, nonCSCECourses);

        int totalHours = CSCE5xxCourses + nonCSCECourses + CSCE7xxCourses;

        if (totalHours >= additionalCredits && CSCE7xxCourses >= additional7xxCredits) {
            result.passed = true;
        } else {
            result.passed = false;
            result.details = new RequirementDetails();
            result.details.notes = new ArrayList<>();
            if (totalHours < additionalCredits) {
                result.details.notes.add("Must pass " +
                        String.valueOf(additionalCredits - totalHours) +
                        " more hours that are not core courses.");
            }
            if (CSCE7xxCourses < additional7xxCredits) {
                result.details.notes.add("Must pass " +
                        String.valueOf(additional7xxCredits - CSCE7xxCourses) +
                        " more hours of CSCE courses numbered above 700 that are not core courses.");
            }
        }
        result.name = "ADDITIONAL_CREDITS_" + degreeName;
        return result;
    }

    @Override
    RequirementCheckResult CheckThesisCredits() {
        return null; }

    @Override
    RequirementCheckResult CheckTimeLimit() {
        RequirementCheckResult result = super.CheckTimeLimit();
        result.name = "TIME_LIMIT_" + degreeName;
        return result;
    }

    @Override
    RequirementCheckResult CheckMilestones() {
        return null; }
}
