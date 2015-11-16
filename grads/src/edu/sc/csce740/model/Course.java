package edu.sc.csce740.model;

/**
 * Created by paladin on 11/1/15.
 */
public class Course
{
    public String name;
    public String id;
    public String numCredits;

    public boolean IsCSCE() {
        return id.startsWith("csce");
    }

    public boolean Is7xx() {
        int i = 0;
        while (!Character.isDigit(id.charAt(i)))
            i++;

        return id.charAt(i) == '7' || id.charAt(i) == '8';
    }
}
