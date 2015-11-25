package edu.sc.csce740.exception;

/**
 *  Exception that shows that DB is not available or it has the wrong format, eg JSON is not valid, etc.
 */
public class DBIsNotAvailableOrCorruptedException extends GRADSException
{
    public DBIsNotAvailableOrCorruptedException(Throwable var1) {
        super(var1);
    }

    public DBIsNotAvailableOrCorruptedException(String what) {
        super(what);
    }
}
