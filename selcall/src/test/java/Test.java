import org.apache.log4j.Logger;

/**
 * Created by Robert on 12.06.2014.
 */
public class Test {
    public final static Logger LOG = Logger.getLogger(Test.class);

    public static void main(String[] args) {

        try {
            /*Thread.sleep(2000);

            try {
                sel.sendMessage("12345");
            } catch (IllegalMessageException e) {
                LOG.warn("", e);
            }

            Thread.sleep(2000);
            try {
                sel.sendMessage("blabl");
            } catch (IllegalMessageException e) {
                LOG.warn("", e);
            }

            Thread.sleep(2000);
            try {
                sel.sendMessage(null);
            } catch (IllegalMessageException e) {
                LOG.warn("", e);
            }*/

            Thread.sleep(60000);
        } catch (InterruptedException e) {
            LOG.warn("", e);
        }
    }
}
