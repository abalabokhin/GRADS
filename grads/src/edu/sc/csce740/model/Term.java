package edu.sc.csce740.model;

/**
 * Created by paladin on 11/1/15.
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
        /// TODO: implement it.
        return false;
    }

    public Season semester;
    public int year;
}
