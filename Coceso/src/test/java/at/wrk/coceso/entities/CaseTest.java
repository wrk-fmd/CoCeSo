package at.wrk.coceso.entities;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CaseTest {

    private Case t;
    private Case u;

    @Before
    public void setUp() {
        this.t = new Case();
        this.u = new Case();
    }

    @Test
    public void prepareNotNullTest() {
        /* Prepare */
        this.t.name = null;
        this.t.organiser = null;

        /* Execute */
        this.t.prepareNotNull();

        /* Check */
        Assert.assertEquals("", this.t.name);
        Assert.assertEquals("", this.t.organiser);
    }

    @Test
    public void prepareAnotherNotNullTest() {
        /* Prepare */
        this.t.name = "f00";
        this.t.organiser = null;

        /* Execute */
        this.t.prepareNotNull();

        /* Check */
        Assert.assertEquals("f00", this.t.name);
        Assert.assertEquals("", this.t.organiser);
    }

    @Test
    public void equalsTest() {
        /* Check */
        Assert.assertEquals(true, this.t.equals(this.u));
    }
}
