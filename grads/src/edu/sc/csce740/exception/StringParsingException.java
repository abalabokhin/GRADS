package edu.sc.csce740.exception;

/**
 * Created by paladin on 11/15/15.
 */
public class StringParsingException extends GRADSException {

    public StringParsingException(Throwable var1) {
        super(var1);
    }

    public StringParsingException(String what) {
        super(what);
    }
}
