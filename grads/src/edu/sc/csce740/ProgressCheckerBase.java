package edu.sc.csce740;

import edu.sc.csce740.exception.StringParsingException;
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
        RequirementCheckResult result = new RequirementCheckResult();

        /// collect special class hours
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

        int tempDegreeBasedCredits = degreeBasedCredits;
        if (currentStudentRecord.certificateSought != null) {
            /// Add 9 hour if there is a certificate.
            tempDegreeBasedCredits += 9;
        }
        if (totalHours >= tempDegreeBasedCredits)
            result.passed = true;
        else {
            result.passed = false;
            result.details = new RequirementDetails();
            result.details.notes = new ArrayList<>();
            result.details.notes.add("Must pass " +
                            String.valueOf(tempDegreeBasedCredits - totalHours) +
                            " more hours of graduate courses.");
        }

        List<CourseTaken> takenGradCourses = Arrays.asList(currentStudentRecord.coursesTaken.stream().filter(
                x -> x.course.IsGraduate() && !x.term.isExpired(currentTerm, yearsToFinishClasses) && !excludedClassesIds.contains(x.course.id)).
                toArray(CourseTaken[]::new));

        result.details.courses = takenGradCourses;
        result.name = "DEGREE_BASED_CREDITS_" + degreeName;

        return result;
    }

    RequirementCheckResult CheckThesisCredits()
    {
        RequirementCheckResult result = new RequirementCheckResult();
        result.details = new RequirementDetails();
        /// collect all non expired thesis class hours
        int thesisCoursesHours = currentStudentRecord.coursesTaken.stream().filter(
                x -> thesisClassId.equals(x.course.id) &&
                        !x.term.isExpired(currentTerm, yearsToFinishClasses)).mapToInt(
                y -> Integer.parseInt(y.course.numCredits)).sum();

        if (thesisCoursesHours >= thesisCredits)
            result.passed = true;
        else {
            result.passed = false;
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

    RequirementCheckResult CheckGPA() throws Exception
    {
        List<CourseTaken> takenGradCourses = Arrays.asList(currentStudentRecord.coursesTaken.stream().filter(
                x -> x.course.IsGraduate() && !x.term.isExpired(currentTerm, yearsToFinishClasses)).
                toArray(CourseTaken[]::new));

        Float gradGPA = calculateGPA(takenGradCourses);

        List<CourseTaken> taken7xxCourses = Arrays.asList(currentStudentRecord.coursesTaken.stream().filter(
                x -> x.course.Is7xx() && !x.term.isExpired(currentTerm, yearsToFinishClasses)).
                toArray(CourseTaken[]::new));

        Float classes7GPA = calculateGPA(taken7xxCourses);

        RequirementCheckResult result = new RequirementCheckResult();
        if (gradGPA == null) {
            result.passed = true;
            result.details = new RequirementDetails();
            result.details.notes = new ArrayList<>();
            result.details.notes.add("Must have any graduate unexpired courses");
        } else if (gradGPA >= 3.0 && (classes7GPA == null || (classes7GPA != null && classes7GPA >= 3.0))) {
            result.passed = true;
            result.details = new RequirementDetails();
            result.details.gpa = gradGPA;
        } else {
            result.passed = false;
            result.details = new RequirementDetails();
            result.details.gpa = gradGPA;
            result.details.notes = new ArrayList<>();
            if (gradGPA < 3.0) {
                result.details.notes.add("Must have GPA >= 3.0");
            }
            if (classes7GPA != null && classes7GPA < 3.0) {
                result.details.notes.add("Must have 7xx classes GPA >= 3.0");
            }
        }

        result.name = "GPA";
        return result;
    }

    RequirementCheckResult CheckMilestones()
    {
        /// Copy all the milestones
        RequirementCheckResult result = new RequirementCheckResult();
        result.details = new RequirementDetails();
        result.details.notes = new ArrayList<>();
        result.details.milestones = currentStudentRecord.milestonesSet;
        Set<Milestone.MilestoneType> milestonesToPass = new HashSet<>(milestones);

        List<Milestone.MilestoneType> passedMilestones = new ArrayList();
        if (currentStudentRecord.milestonesSet != null) {
            passedMilestones.addAll(
                    currentStudentRecord.milestonesSet.stream().filter(x -> !x.term.isExpired(currentTerm, yearsToFinishDegree)).map(x -> x.milestone).collect(Collectors.toList()));
        }

        result.passed = false;
        milestonesToPass.removeAll(passedMilestones);
        if (milestonesToPass.isEmpty()) {
            result.passed = true;
        } else {
            milestonesToPass.stream().forEach(x -> result.details.notes.add("Missing milestone " + x.toString()));
        }

        result.name = "MILESTONES_" + degreeName;
        return result;
    }

    /// It uses only in one children, so basic implementation do nothing. This method overrides in MSE.
    RequirementCheckResult CheckExperience()
    {
        return null;
    }

    protected Float calculateGPA(List<CourseTaken> classes) throws Exception
    {
        float sumHours = 0;
        float sumGP = 0;

        for (CourseTaken courseTaken : classes)
        {
            Integer gradeFactor = courseTaken.grade.getFactor();
            if (gradeFactor != null) {
                try {
                    float numCredits = Float.parseFloat(courseTaken.course.numCredits);
                    sumGP += (numCredits * gradeFactor);
                    sumHours += numCredits;
                } catch (NumberFormatException ex)
                {
                    throw new StringParsingException(ex);
                }
            }
        }

        if (sumHours == 0)
            return null;
        return sumGP / sumHours;
    }

    protected Term currentTerm;
    protected String degreeName;
    protected Set<String> requiredClassesIds;
    protected int yearsToFinishClasses = 6;
    /// number of additional classes (not included in required classes) of 7 hundred and above
    protected int additionalCredits;
    /// These classes are excluded from additional_credits and degree_based_credits
    protected Set<String> excludedClassesIds;
    protected int degreeBasedCredits;
    protected int nonCsceCredits;
    protected int thesisCredits;
    protected String thesisClassId;
    protected int yearsToFinishDegree;
    Set<Milestone.MilestoneType> milestones;

    String specialCourse = "csce798";
    int specialCourseMaxCredits = 3;

    StudentRecord currentStudentRecord;
}
