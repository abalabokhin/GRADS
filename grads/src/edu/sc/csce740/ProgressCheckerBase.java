package edu.sc.csce740;

import edu.sc.csce740.ProgressCheckerIntf;
import edu.sc.csce740.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by paladin on 11/5/15.
 */
public class ProgressCheckerBase implements ProgressCheckerIntf
{
    @Override
    public List<RequirementCheckResult> CheckProgress(StudentRecord studentRecord) throws Exception
    {
 		currentStudentRecord = studentRecord;
        List<RequirementCheckResult> result = new ArrayList<RequirementCheckResult>();

        RequirementCheckResult checkingCoreCoursesResult = CheckCoreCourses();
        if (checkingCoreCoursesResult != null)
            result.add(checkingCoreCoursesResult);

        RequirementCheckResult checkingAdditionalCreditsResult = CheckAdditionalCredits();
        if (checkingAdditionalCreditsResult != null)
            result.add(checkingAdditionalCreditsResult);

        RequirementCheckResult checkingDegreeBasedCreditsResult = CheckDegreeBasedCredits();
        if (checkingDegreeBasedCreditsResult != null)
            result.add(checkingDegreeBasedCreditsResult);

        RequirementCheckResult checkingThesisCreditsResult = CheckThesisCredits();
        if (checkingThesisCreditsResult != null)
            result.add(checkingThesisCreditsResult);

        RequirementCheckResult checkingTimeLimitResult = CheckTimeLimit();
        if (checkingTimeLimitResult != null)
            result.add(checkingTimeLimitResult);

        RequirementCheckResult checkingGPAResult = CheckGPA();
        if (checkingGPAResult != null)
            result.add(checkingGPAResult );

        RequirementCheckResult checkingMilestonesResult = CheckMilestones();
        if (checkingMilestonesResult != null)
             result.add(checkingMilestonesResult);

        RequirementCheckResult checkingExperienceResult = CheckExperience();
        if (checkingExperienceResult != null)
            result.add(checkingExperienceResult);

        return result;
    }

    @Override
    public void SetCurrentTerm(Term currentTerm) {
        this.currentTerm = currentTerm;
    }

    RequirementCheckResult CheckCoreCourses()
    {
        RequirementCheckResult result = new RequirementCheckResult();

        List<CourseTaken> takenRequiredCourses = Arrays.asList(currentStudentRecord.coursesTaken.stream().filter(
                x -> requiredClassesIds.contains(x.course.id) && x.term.isExpired(currentTerm, yearsToFinishClasses)).
                toArray(CourseTaken[]::new));

        result.details.courses = takenRequiredCourses;
        Set<String> uniqueTakenRequiredCourses =
                takenRequiredCourses.stream().map(x -> x.course.id).collect(Collectors.toSet());

        if (uniqueTakenRequiredCourses.size() == requiredClassesIds.size()) {
            result.passed = true;
        } else {
            result.passed = false;
            Set<String> leftClasses = new HashSet<>(requiredClassesIds);
            leftClasses.removeAll(uniqueTakenRequiredCourses);
            result.details.notes.add("Core courses [" + String.join(", ", leftClasses) + "] are left to be taken.");
        }

        result.name = "CORE_COURSES_" + degreeName;
        return result;
    }

    RequirementCheckResult CheckAdditionalCredits()
    {
        return null;
    }

    RequirementCheckResult CheckDegreeBasedCredits()
    {
        return null;
    }

    RequirementCheckResult CheckThesisCredits()
    {
        return null;
    }

    RequirementCheckResult CheckTimeLimit()
    {
        return null;
    }

    RequirementCheckResult CheckGPA()
    {
        return null;
    }

    RequirementCheckResult CheckMilestones()
    {
        return null;
    }

    RequirementCheckResult CheckExperience()
    {
        return null;
    }

    protected Term currentTerm;
    protected String degreeName;
    protected Set<String> requiredClassesIds;
    int yearsToFinishClasses = 6;
    /// number of additional classes (not included in required classes) of 7 hundred and above
    int additionalCredits;
    int degreeBasedCredits;
    List<String> excludedClassesIds;
    int thesisCredits;
    String thesisClassId;
    int yearsToFinishDegree;
    List<String> milestones;
    int nonCSCEcredits;

    StudentRecord currentStudentRecord;
}
