package edu.sc.csce740.model;

/**
 * Created by paladin on 11/1/15.
 */
public class Milestone
{
    enum MilestoneType {

        DISSERTATION_ADVISOR_SELECTED,
        DISSERTATION_COMMITTEE_FORMED

        /// TODO: Add more:
//        Program of Study Submitted
//        Dissertation Advisor Selected
//        Program of Study Submitted
//        Dissertation Committee Formed
//        Qualifying Exam Passed
//        Dissertation Proposal Scheduled
//        Comprehensive Exam Passed
//        Dissertation Submitted
//        Dissertation Defense Scheduled
//        Dissertation Defense Passed
//
//        Academic Advisor Appointed
//        Thesis Advisor Selected
//        Thesis Committee Formed
//        Thesis Proposal Scheduled
//        Thesis Proposal Approved
//        Thesis Submitted
//        Thesis Defense Scheduled
//        Thesis Defense Passed
//
//        Report Submitted
//        Report Approved
        }
    public String milestone;
    public Term term;
}
