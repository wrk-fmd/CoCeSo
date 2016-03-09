package at.wrk.coceso.entity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserTest {

    private User t;

    @Before
    public void setUp() {
        this.t = new User();
    }

    @Test
    public void validatePasswordTest() {
        this.t.setPassword("12345");
        Assert.assertEquals(true, this.t.validatePassword("12345"));
    }

    @Test
    public void validateWrongPasswordTest() {
        this.t.setPassword("-%43456");
        Assert.assertEquals(false, this.t.validatePassword("ä24ß12345"));
    }

    @Test
    public void validateEmptyPasswordTest() {
        this.t.setPassword("lsdfjao234_sf");
        Assert.assertEquals(false, this.t.validatePassword(""));
    }
}
