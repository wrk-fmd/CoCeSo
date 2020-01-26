package at.wrk.geocode.autocomplete;

import org.junit.Test;

import java.util.Optional;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AutocompleteKeyParserTest {

    public static final String EXPECTED_PREFIX = "Nottendorfer Gasse # ";

    @Test
    public void intersectionSearch_returnPrefix() {
        String query = "Nottendorfer Gasse # ";

        Optional<String> prefix = AutocompleteKeyParser.getPrefixForIntersectionSearch(query);

        assertThat(prefix, isPresentAnd(equalTo(EXPECTED_PREFIX)));
    }

    @Test
    public void multilineIntersectionSearch_returnPrefix() {
        String query = "Nottendorfer Gasse # \n1030 Wien";

        Optional<String> prefix = AutocompleteKeyParser.getPrefixForIntersectionSearch(query);

        assertThat(prefix, isPresentAnd(equalTo(EXPECTED_PREFIX)));
    }

    @Test
    public void intersectionSearchWithFilter_returnPrefix() {
        String query = "Nottendorfer Gasse # filter\n1030 Wien";

        Optional<String> prefix = AutocompleteKeyParser.getPrefixForIntersectionSearch(query);

        assertThat(prefix, isPresentAnd(equalTo(EXPECTED_PREFIX)));
    }

    @Test
    public void notAnIntersectionSearch_returnEmpty() {
        String query = "Nottendorfer Gasse \n1030 Wien";

        Optional<String> prefix = AutocompleteKeyParser.getPrefixForIntersectionSearch(query);

        assertThat(prefix, isEmpty());
    }

    @Test
    public void notAnIntersectionSearchWithHashOnSecondLine_returnEmpty() {
        String query = "Nottendorfer Gasse \n1030 Wien # foobar";

        Optional<String> prefix = AutocompleteKeyParser.getPrefixForIntersectionSearch(query);

        assertThat(prefix, isEmpty());
    }
}
