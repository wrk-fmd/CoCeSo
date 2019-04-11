package at.wrk.coceso.entity.point;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PoiPointTest {
    @Test
    public void createPointWithText_returnTextSeperatedByWhitespace() {
        PoiPoint poiPoint = new PoiPoint("some text", "additional text");

        String info = poiPoint.getInfo();

        assertThat(info, equalTo("some text\nadditional text"));
    }
}