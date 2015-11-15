package edu.sc.csce740;

import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.StudentRecord;
import edu.sc.csce740.model.Term;

import java.util.List;

/**
 * Created by paladin on 11/3/15.
 */
public interface ProgressCheckerIntf
{
    List<RequirementCheckResult> CheckProgress(StudentRecord studentRecord) throws Exception;
    void SetCurrentTerm(Term currentTerm);
}
