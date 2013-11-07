package at.wrk.coceso.entities;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PersonTest {

    private Person p;

    @Before
    public void setUp() {
        p = new Person();
    }

    @Test
    public void simpleTest() {
        Assert.assertEquals(1, 1);
    }

    @Test
    public void validatePasswordTest() {
        p.setPassword("12345");
        Assert.assertEquals(true, p.validatePassword("12345"));
    }

    @Test
    public void validateWrongPasswordTest() {
        p.setPassword("-%43456");
        Assert.assertEquals(false, p.validatePassword("ä24ß12345"));
    }
}
