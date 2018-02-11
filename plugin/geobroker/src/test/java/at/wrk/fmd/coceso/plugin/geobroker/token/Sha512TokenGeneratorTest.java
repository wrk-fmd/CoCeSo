package at.wrk.fmd.coceso.plugin.geobroker.token;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class Sha512TokenGeneratorTest {
    private TokenGenerator sut;

    @Before
    public void init() {
        sut = new Sha512TokenGenerator();
    }

    @Test
    public void sameIdAndSalt_sameToken() {
        String unitId = "unit-id";
        String salt = "Salt-1234";

        String token1 = sut.calculateToken(unitId, salt);
        String token2 = sut.calculateToken(unitId, salt);

        assertThat(token1, equalTo(token2));
    }

    @Test
    public void saltDiffers_differentToken() {
        String unitId = "unit-id";

        String token1 = sut.calculateToken(unitId, "Salt-1234");
        String token2 = sut.calculateToken(unitId, "another salt");

        assertThat(token1, not(equalTo(token2)));
    }

    @Test
    public void idDiffers_differentToken() {
        String salt = "Salt-1234";

        String token1 = sut.calculateToken("unitId1", salt);
        String token2 = sut.calculateToken("unitId2", salt);

        assertThat(token1, not(equalTo(token2)));
    }

    @Test
    public void idAndToken_bothNotInResultingString() {
        String unitId = "unit-id";
        String salt = "Salt-1234";

        String token1 = sut.calculateToken(unitId, salt);

        assertThat(token1, not(containsString(unitId)));
        assertThat(token1, not(containsString(salt)));
    }
}