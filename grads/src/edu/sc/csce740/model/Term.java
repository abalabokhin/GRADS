package edu.sc.csce740.model;


/**
 * This class calculates the duration(number of years) spent by a student in a given degree
 */
public class Term
{
    public enum Season {FALL, SPRING, SUMMER}

    public Term(int year, Season season)
    {
        this.semester = season;
        this.year = year;
    }

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
