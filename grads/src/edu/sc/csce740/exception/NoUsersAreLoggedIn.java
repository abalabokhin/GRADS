package edu.sc.csce740.exception;

/**
 *  Exception that shows that no users are logged in, but such a user is required to apply the request.
 */
public class NoUsersAreLoggedIn extends GRADSException {
    public NoUsersAreLoggedIn(Throwable var1) {
        super(var1);
    }

    public NoUsersAreLoggedIn(String what) {
        super(what);
    }
}
