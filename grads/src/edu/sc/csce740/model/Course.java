package edu.sc.csce740.model;

/**
 *  Class that accumulate Course information. It uses only to store the data and can check different properties of
 *  the course based on its name.
 */
public class Course
{
    public String name;
    public String id;
    public String numCredits;

    /**
     * Checks of the class is CSCE class based on its name.
     * @return true if the class is CSCE one.
     */
    public boolean IsCSCE() {
        return id.startsWith("csce");
    }

    /**
     * Checks of the class is above 7 hundred based on its name.
     * @return true if the class is above 7 hundred.
     */
    public boolean Is7xx() {
        int i = 0;
        while (!Character.isDigit(id.charAt(i)))
            i++;

        return id.charAt(i) == '7' || id.charAt(i) == '8';
    }

    /**
     * Checks of the class is graduate class based on its name.
     * @return true if the class is graduate one.
     */
    public boolean IsGraduate() {
        int i = 0;
        while (!Character.isDigit(id.charAt(i)))
            i++;

        return id.charAt(i) == '7' || id.charAt(i) == '8' || id.charAt(i) == '5' || id.charAt(i) == '6';
    }
}
