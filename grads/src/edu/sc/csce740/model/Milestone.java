package edu.sc.csce740.model;

/**
<<<<<<< HEAD
 *  Class that accumulate Milestone information. It uses only to store the data and does not have any methods.
=======
 * models the relevant aspects of the data structure that stores milestone information
 * related to the student’s progress
>>>>>>> aa3e80255b4c0e5fdef3754f09e81655c7beaca9
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
