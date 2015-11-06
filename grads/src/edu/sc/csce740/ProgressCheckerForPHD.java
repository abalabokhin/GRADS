package edu.sc.csce740;

import edu.sc.csce740.model.Course;
import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.StudentRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paladin on 11/3/15.
 */
public class ProgressCheckerForPHD extends ProgressCheckerBase {
    ProgressCheckerForPHD() {
//        yearsToFinishClasses = 6;
//
//        requiredClassesIds.add("csce513");
//        requiredClassesIds.add("csce531");
//        requiredClassesIds.add("csce551");
//        requiredClassesIds.add("csce750");
//        requiredClassesIds.add("csce791");
//
//        additionalCredits = 20;
    }

    @Override
    RequirementCheckResult CheckDegreeBasedCredits() {
        return null;
    }
}
