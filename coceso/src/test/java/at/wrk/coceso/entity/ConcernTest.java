package at.wrk.coceso.entity;

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
        this.t.setName(null);
        this.t.setInfo(null);

        /* Execute */
        this.t.prepareNotNull();

        /* Check */
        Assert.assertEquals("", this.t.getName());
        Assert.assertEquals("", this.t.getInfo());
    }

    @Test
    public void prepareAnotherNotNullTest() {
        /* Prepare */
        this.t.setName("f00");
        this.t.setInfo(null);

        /* Execute */
        this.t.prepareNotNull();

        /* Check */
        Assert.assertEquals("f00", this.t.getName());
        Assert.assertEquals("", this.t.getInfo());
    }

    @Test
    public void equalsTest() {
        /* Check */
        Assert.assertEquals(true, this.t.equals(this.u));
    }
}
