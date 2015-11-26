package edu.sc.csce740.tests;

import edu.sc.csce740.model.Term;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by paladin on 11/25/15.
 */
public class TermUnitTest {

    @Test
    public void testIsExpired() throws Exception {
        Term pastTerm1 = new Term(2011, Term.Season.FALL);
        Term pastTerm2 = new Term(2011, Term.Season.SPRING);
        Term pastTerm3 = new Term(2011, Term.Season.SUMMER);

        Term currentTerm1 = new Term(2012, Term.Season.FALL);
        Term currentTerm2 = new Term(2017, Term.Season.SPRING);
        Term currentTerm3 = new Term(2017, Term.Season.SUMMER);
        Term currentTerm4 = new Term(2018, Term.Season.SUMMER);
        Term currentTerm5 = new Term(2017, Term.Season.FALL);

        Assert.assertEquals(pastTerm1.isExpired(currentTerm1, 6), false);
        Assert.assertEquals(pastTerm2.isExpired(currentTerm2, 6), true);
        Assert.assertEquals(pastTerm3.isExpired(currentTerm3, 6), true);
        Assert.assertEquals(pastTerm3.isExpired(currentTerm4, 6), true);
        Assert.assertEquals(pastTerm1.isExpired(currentTerm5, 6), true);
    }
}