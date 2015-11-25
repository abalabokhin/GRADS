package edu.sc.csce740.model;

/**
 * models the relevant aspects of the data structure that stores the completed course information related to
 * a student object such as name and number of credits.
 */
public class CourseTaken
{
    public enum Grade {
        A(4),
        B(3),
        C(2),
        D(1),
        F(0),
        P(null),
        _(null);

        private final Integer factor;

        Grade(Integer factor) {
            this.factor = factor;
        }

        public Integer getFactor() {
            return factor;
        }
    }

    public Course course;
    public Term term;
    public Grade grade;
}
