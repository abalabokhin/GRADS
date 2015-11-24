package edu.sc.csce740;

import edu.sc.csce740.model.RequirementCheckInput;
import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.RequirementDetails;
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
    RequirementCheckResult CheckAdditionalCredits(){
            RequirementCheckResult result = new RequirementCheckResult();

            /// collect all non expired csce classes above 7 hundred excluding requiredClassesIds and excludedClassesIds.


            int CSCEcourses = currentStudentRecord.coursesTaken.stream().filter(
                    x -> x.course.Is7xx() && x.course.IsCSCE() &&
                            !requiredClassesIds.contains(x.course.id) && !excludedClassesIds.contains(x.course.id) &&
                            !x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
                    y -> Integer.parseInt(y.course.numCredits)).sum();

            int nonCSCEcourses = currentStudentRecord.coursesTaken.stream().filter(
                    x -> !x.course.Is7xx() && !x.course.IsCSCE() &&
                            !requiredClassesIds.contains(x.course.id) && !excludedClassesIds.contains(x.course.id) &&
                            !x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
                    y -> Integer.parseInt(y.course.numCredits)).sum();
            int additionalcourses = currentStudentRecord.coursesTaken.stream().filter(
                    x -> !requiredClassesIds.contains(x.course.id) && !excludedClassesIds.contains(x.course.id) &&
                            !x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
                    y -> Integer.parseInt(y.course.numCredits)).sum();
            additionalcourses = additionalcourses - (CSCEcourses + nonCSCEcourses);
            int allCourseCredits = additionalcourses + CSCEcourses + nonCSCEcourses ;
            if (CSCEcourses >=9 && nonCSCEcourses <=6 && allCourseCredits >= additionalCredits){
                result.passed = true;
            }
            else {
                result.passed = false;
                result.details = new RequirementDetails();
                result.details.notes = new ArrayList<>();
                result.details.notes.add("Must pass " +
                        String.valueOf(additionalCredits - allCourseCredits) +
                        " more credit hours that are not core courses is still required.");
            }
            result.name = "ADDITIONAL_CREDITS_" + degreeName;
            return result;

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
