package edu.sc.csce740;

import edu.sc.csce740.ProgressCheckerIntf;
import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.StudentRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paladin on 11/5/15.
 */
public class ProgressCheckerBase implements ProgressCheckerIntf {
    @Override
    public List<RequirementCheckResult> CheckProgress(StudentRecord studentRecord) throws Exception {
        currentStudentRecord = studentRecord;
        List<RequirementCheckResult> result = new ArrayList<RequirementCheckResult>();

        RequirementCheckResult checkingCoreCourcesResult = CheckCoreCourses();
        if (checkingCoreCourcesResult != null)
            result.add(checkingCoreCourcesResult);
        /// TODO: add all the other Check* functions results to the list
        return result;
    }

    RequirementCheckResult CheckCoreCourses() {
        return null;
    }

    RequirementCheckResult CheckAdditionalCredits() {
        return null;
    }

    RequirementCheckResult CheckDegreeBasedCredits() {
        return null;
    }

    RequirementCheckResult CheckThesisCredits() {
        return null;
    }

    RequirementCheckResult CheckTimeLimit() {
        return null;
    }

    RequirementCheckResult CheckGPA() {
        return null;
    }

    RequirementCheckResult CheckMilestones() {
        return null;
    }

    RequirementCheckResult CheckExperience() {
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
