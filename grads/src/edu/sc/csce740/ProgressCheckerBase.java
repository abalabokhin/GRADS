package edu.sc.csce740;

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
                x -> requiredClassesIds.contains(x.course.id) && !x.term.isExpired(currentTerm, yearsToFinishClasses)).
                toArray(CourseTaken[]::new));

        result.details = new RequirementDetails();
        result.details.courses = takenRequiredCourses;
        Set<String> uniqueTakenRequiredCourses =
                takenRequiredCourses.stream().map(x -> x.course.id).collect(Collectors.toSet());

        if (uniqueTakenRequiredCourses.size() == requiredClassesIds.size()) {
            result.passed = true;
        } else {
            result.passed = false;
            Set<String> leftClasses = new HashSet<>(requiredClassesIds);
            leftClasses.removeAll(uniqueTakenRequiredCourses);
            result.details.notes = new ArrayList<>();
            result.details.notes.add("Core courses [" + String.join(", ", leftClasses) + "] are left to be taken.");
        }

        result.name = "CORE_COURSES_" + degreeName;
        return result;
    }

    RequirementCheckResult CheckAdditionalCredits()
    {
        RequirementCheckResult result = new RequirementCheckResult();

        /// collect all non expired csce classes above 7 hundred excluding requiredClassesIds and excludedClassesIds.
        int additionalCreditHoursTaken = currentStudentRecord.coursesTaken.stream().filter(
                x -> x.course.Is7xx() && x.course.IsCSCE() &&
                        !requiredClassesIds.contains(x.course.id) && !excludedClassesIds.contains(x.course.id) &&
                        !x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
                y -> Integer.parseInt(y.course.numCredits)).sum();

        if (additionalCreditHoursTaken >= additionalCredits)
            result.passed = true;
        else {
            result.passed = false;
            result.details = new RequirementDetails();
            result.details.notes = new ArrayList<>();
            result.details.notes.add("Must pass " +
                    String.valueOf(additionalCredits - additionalCreditHoursTaken) +
                    " more hours of CSCE courses numbered above 700 that are not core courses.");
        }
        result.name = "ADDITIONAL_CREDITS_" + degreeName;
        return result;
    }

    RequirementCheckResult CheckDegreeBasedCredits()
    {
        /*
        Students​ must pass a minimum of 24 credit hours in graduate courses
        (excluding CSCE 799).
        Students may count a maximum of 6 hours in non­CSCE courses
        At most, 3 hours of CSCE 798 may be applied toward the degree.
        CSCE 797 may not be applied toward the degree.
        */

        String specialCourse = "csce798";
        int specialCourseMaxCredits = 3;

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

    RequirementCheckResult CheckThesisCredits()
    {
        RequirementCheckResult result = new RequirementCheckResult();
        /// collect all non expired thesis class hours
        int thesisCoursesHours = currentStudentRecord.coursesTaken.stream().filter(
                x -> thesisClassId.equals(x.course.id) &&
                        !x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
                y -> Integer.parseInt(y.course.numCredits)).sum();

        if (thesisCoursesHours >= thesisCredits)
            result.passed = true;
        else {
            result.passed = false;
            result.details = new RequirementDetails();
            result.details.notes = new ArrayList<>();
            result.details.notes.add("Must pass " +
                    String.valueOf(thesisCredits - thesisCoursesHours) +
                    " more hours of " + thesisClassId + ".");
        }

        List<CourseTaken> takenGradCourses = Arrays.asList(currentStudentRecord.coursesTaken.stream().filter(
                x -> thesisClassId.equals(x.course.id) && !x.term.isExpired(currentTerm, yearsToFinishClasses)).
                toArray(CourseTaken[]::new));

        result.details.courses = takenGradCourses;
        result.name = "THESIS_CREDITS_" + degreeName;
        return result;
    }

    RequirementCheckResult CheckTimeLimit()
    {
        RequirementCheckResult result = new RequirementCheckResult();
        if (!currentStudentRecord.termBegan.isExpired(currentTerm, yearsToFinishDegree))
            result.passed = true;
        else
            result.passed = false;

        result.name = "TIME_LIMIT_" + degreeName;
        return result;
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
    protected int yearsToFinishClasses = 6;
    /// number of additional classes (not included in required classes) of 7 hundred and above
    protected int additionalCredits;
    /// These classes are excluded from additional_credits and degree_based_credits
    Set<String> excludedClassesIds;
    protected int degreeBasedCredits;
    protected int nonCsceCredits;
    int thesisCredits;
    String thesisClassId;
    int yearsToFinishDegree;
    List<String> milestones;

    StudentRecord currentStudentRecord;
}
