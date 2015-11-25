package edu.sc.csce740.exception;

/**
 *  Based class for all GRADS Exceptions.
 */
public class GRADSException extends Exception
{
    public GRADSException(Throwable var1)
    {
        super(var1);
    }
    public GRADSException(String what)
    {
        super(what);
    }
}
