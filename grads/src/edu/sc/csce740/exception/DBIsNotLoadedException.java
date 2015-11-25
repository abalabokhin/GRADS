package edu.sc.csce740.exception;

/**
 *  Exception that shows that DB is not loaded but some data is requested form this DB.
 */
public class DBIsNotLoadedException extends GRADSException {
    public DBIsNotLoadedException(Throwable var1) {
        super(var1);
    }

    public DBIsNotLoadedException(String what) {
        super(what);
    }
}
