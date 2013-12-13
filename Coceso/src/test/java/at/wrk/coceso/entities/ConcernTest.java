package at.wrk.coceso.entities;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConcernTest {

    private Concern t;
    private Concern u;

    @Before
    public void setUp() {
        this.t = new Concern();
        this.u = new Concern();
    }

    @Test
    public void prepareNotNullTest() {
        /* Prepare */
        this.t.name = null;
        this.t.info = null;

        /* Execute */
        this.t.prepareNotNull();

        /* Check */
        Assert.assertEquals("", this.t.name);
        Assert.assertEquals("", this.t.info);
    }

    @Test
    public void prepareAnotherNotNullTest() {
        /* Prepare */
        this.t.name = "f00";
        this.t.info = null;

        /* Execute */
        this.t.prepareNotNull();

        /* Check */
        Assert.assertEquals("f00", this.t.name);
        Assert.assertEquals("", this.t.info);
    }

    @Test
    public void equalsTest() {
        /* Check */
        Assert.assertEquals(true, this.t.equals(this.u));
    }
}
