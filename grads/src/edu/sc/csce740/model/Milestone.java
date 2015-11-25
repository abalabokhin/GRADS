package edu.sc.csce740.model;

/**
 *  Class that accumulate Milestone information. It uses only to store the data and does not have any methods.
 */
public class Milestone
{
    /**
     *  Enum to represent all the possible milestones.
     */
    public enum MilestoneType {
        DISSERTATION_ADVISOR_SELECTED,
        PROGRAM_OF_STUDY_SUBMITTED,
        DISSERTATION_COMMITTEE_FORMED,
        QUALIFYING_EXAM_PASSED,
        DISSERTATION_PROPOSAL_SCHEDULED,
        COMPREHENSIVE_EXAM_PASSED,
        DISSERTATION_SUBMITTED,
        DISSERTATION_DEFENSE_SCHEDULED,
        DISSERTATION_DEFENSE_PASSED,

        ACADEMIC_ADVISOR_APPOINTED,
        THESIS_ADVISOR_SELECTED,
        THESIS_COMMITTEE_FORMED,
        THESIS_PROPOSAL_SCHEDULED,
        THESIS_PROPOSAL_APPROVED,
        THESIS_SUBMITTED,
        THESIS_DEFENSE_SCHEDULED,
        THESIS_DEFENSE_PASSED,

        REPORT_SUBMITTED,
        REPORT_APPROVED,
        }

    public MilestoneType milestone;
    public Term term;
}
