package at.wrk.coceso.entityevent.impl;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class NotifyListExecutor {
    private final ObjectFactory<NotifyList> notifyListProvider;

    @Autowired
    public NotifyListExecutor(final ObjectFactory<NotifyList> notifyListProvider) {
        this.notifyListProvider = notifyListProvider;
    }

    public <T> T execute(final Function<NotifyList, T> function) {
        NotifyList notify = notifyListProvider.getObject();
        T ret = function.apply(notify);
        notify.sendNotifications();
        return ret;
    }

    public void executeVoid(final Consumer<NotifyList> function) {
        NotifyList notify = notifyListProvider.getObject();
        function.accept(notify);
        notify.sendNotifications();
    }
}
