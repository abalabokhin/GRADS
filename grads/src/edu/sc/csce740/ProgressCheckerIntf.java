package edu.sc.csce740;

import edu.sc.csce740.model.RequirementCheckResult;
import edu.sc.csce740.model.StudentRecord;
import edu.sc.csce740.model.Term;

import java.util.List;

/**
 * This interface is accessible to the calling methods to perform verification of
 * the eligibility checks applicable to the program of study linked to the student record.
 */
public interface ProgressCheckerIntf
{
    List<RequirementCheckResult> CheckProgress(StudentRecord studentRecord) throws Exception;
    void SetCurrentTerm(Term currentTerm);
}
