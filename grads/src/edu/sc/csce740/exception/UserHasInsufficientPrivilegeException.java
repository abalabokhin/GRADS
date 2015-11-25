package edu.sc.csce740.exception;

/**
 *  Exception that shows user has no privileges fro the request.
 */
public class UserHasInsufficientPrivilegeException extends GRADSException
{
    public UserHasInsufficientPrivilegeException(Throwable var1) {
        super(var1);
    }

    public UserHasInsufficientPrivilegeException(String what) {
        super(what);
    }
}
