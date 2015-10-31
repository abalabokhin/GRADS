package tests;

import main.HelloWorld;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by paladin on 10/31/15.
 */
public class HelloWorldTest {

    @Test
    public void testAdd() throws Exception {
        HelloWorld hw = new HelloWorld();

        assertEquals(4, hw.add(1, 2));
    }
}