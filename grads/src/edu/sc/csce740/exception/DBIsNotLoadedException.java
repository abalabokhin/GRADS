package edu.sc.csce740.exception;

/**
 * Created by paladin on 11/15/15.
 */
public class DBIsNotLoadedException extends GRADSException {
    public DBIsNotLoadedException(Throwable var1) {
        super(var1);
    }

    public DBIsNotLoadedException(String what) {
        super(what);
    }
}
