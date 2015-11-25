package edu.sc.csce740.model;

/**
 * models the relevant aspects of the data structure that stores course information
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

    public boolean IsGraduate() {
        int i = 0;
        while (!Character.isDigit(id.charAt(i)))
            i++;

        return id.charAt(i) == '7' || id.charAt(i) == '8' || id.charAt(i) == '5' || id.charAt(i) == '6';
    }
}
