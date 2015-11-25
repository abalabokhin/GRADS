package edu.sc.csce740.model;


/**
 * This class represents the time stamp in GRADS system - Term and year.
 * It can also calculate the expiration of the class by same other timestamp.
 */
public class Term
{
    /**
     * Enum of all the possible seasons to study.
     */
    public enum Season {FALL, SPRING, SUMMER}

    /**
     * Constructor
     */
    public Term(int year, Season season)
    {
        this.semester = season;
        this.year = year;
    }

    /**
     * Check is class is expired by some other term.
     * @param currentTime term to check expiration against
     * @param yearsToExpire how many years can be spent before class is expired
     * @return true if class is expired and false otherwise
     */
    public boolean isExpired(Term currentTime, int yearsToExpire) {

        if((currentTime.year - this.year) < yearsToExpire) {
            return false;
        }
        if((currentTime.year - this.year) == yearsToExpire) {
            if(this.semester == Season.FALL && currentTime.semester != Season.FALL) {
                return false;
            }
            if(this.semester == Season.SUMMER && currentTime.semester == Season.SPRING) {
                return false;
            }
        }
        return true;
    }

    public Season semester;
    public int year;
}
