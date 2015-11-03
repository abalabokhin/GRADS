package edu.sc.csce740.model;

/**
 * Created by paladin on 11/1/15.
 */
public class Degree {
    public enum DegreeType {
        BS, MS, MENG, MSE, PHD
    }

    public DegreeType name;
    public Term graduation;
}
