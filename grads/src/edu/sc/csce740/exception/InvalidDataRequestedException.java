package edu.sc.csce740.exception;

/**
 *  Exception that shows that DB does not have that information that is requested. Usually it is userID.
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
