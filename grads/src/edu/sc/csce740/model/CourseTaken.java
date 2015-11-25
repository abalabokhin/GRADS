package edu.sc.csce740.model;

/**
 *  Class that accumulate Course Taken data. It uses only to store the data does not have any data.
 */
public class CourseTaken
{
    /**
     *  Enum to represent all the possible grades. It can convert different grades into scores for GPA calculating.
     */
    public enum Grade {
        A(4),
        B(3),
        C(2),
        D(1),
        F(0),
        P(null),
        _(null);

        private final Integer factor;

        /**
         *  Constructor
         */
        Grade(Integer factor) {
            this.factor = factor;
        }

        /**
         *  return grade factor for the exact grade.
         */
        public Integer getFactor() {
            return factor;
        }
    }

    public Course course;
    public Term term;
    public Grade grade;
}
