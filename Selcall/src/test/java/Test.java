import at.wrk.selcall.IllegalMessageException;
import at.wrk.selcall.SelcallManager;

/**
 * Created by Robert on 12.06.2014.
 */
public class Test {
    public static void main(String[] args) {
        SelcallManager sel = SelcallManager.getInstance();

        try {
            Thread.sleep(2000);

            try {
                sel.sendMessage("12345");
            } catch (IllegalMessageException e) {
                e.printStackTrace();
            }

            Thread.sleep(2000);
            try {
                sel.sendMessage("blabl");
            } catch (IllegalMessageException e) {
                e.printStackTrace();
            }

            Thread.sleep(2000);
            try {
                sel.sendMessage(null);
            } catch (IllegalMessageException e) {
                e.printStackTrace();
            }

            Thread.sleep(60000);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }
}
