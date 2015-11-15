package edu.sc.csce740;

import edu.sc.csce740.ProgressCheckerIntf;
import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.StudentRecord;

import java.util.ArrayList;
import java.util.List;

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

        RequirementCheckResult checkingCoreCourcesResult = CheckCoreCourses();
        if (checkingCoreCourcesResult != null)
            result.add(checkingCoreCourcesResult);

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

        /// TODO: add all the other Check* functions results to the list
        return result;
    }

    RequirementCheckResult CheckCoreCourses()
    {
        return null;
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

    String degreeName;
    List<String> requiredClassesIds;
    int yearsToFinishClasses;
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
