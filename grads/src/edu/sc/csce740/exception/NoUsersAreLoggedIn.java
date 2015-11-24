package edu.sc.csce740.exception;

/**
 * Created by paladin on 11/15/15.
 */
public class NoUsersAreLoggedIn extends GRADSException {
    public NoUsersAreLoggedIn(Throwable var1) {
        super(var1);
    }

    public NoUsersAreLoggedIn(String what) {
        super(what);
    }
}
