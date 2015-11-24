package edu.sc.csce740.exception;

/**
 * Created by paladin on 11/8/15.
 */
public class InvalidDataRequestedException extends GRADSException
{
    public InvalidDataRequestedException(Throwable var1) {
        super(var1);
    }

    public InvalidDataRequestedException(String what) {
        super(what);
    }
}
