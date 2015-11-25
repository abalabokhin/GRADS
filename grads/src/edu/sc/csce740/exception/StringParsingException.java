package edu.sc.csce740.exception;

/**
 *  Exception that shows that a String that must have a number cannot be parsed into number.
 */
public class StringParsingException extends GRADSException {

    public StringParsingException(Throwable var1) {
        super(var1);
    }

    public StringParsingException(String what) {
        super(what);
    }
}
