package edu.sc.csce740.exception;

/**
 * Created by paladin on 11/8/15.
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
