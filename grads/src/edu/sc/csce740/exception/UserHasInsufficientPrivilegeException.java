package edu.sc.csce740.exception;

/**
 * Created by paladin on 11/8/15.
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
