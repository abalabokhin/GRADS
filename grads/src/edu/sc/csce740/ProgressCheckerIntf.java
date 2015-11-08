package edu.sc.csce740;

import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.StudentRecord;

import java.util.List;

/**
 * Created by paladin on 11/3/15.
 */
public interface ProgressCheckerIntf {
    public List<RequirementCheckResult> CheckProgress(StudentRecord studentRecord) throws Exception;
}