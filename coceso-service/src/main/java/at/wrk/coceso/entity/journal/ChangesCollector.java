package at.wrk.coceso.entity.journal;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collection;
import java.util.LinkedList;

public class ChangesCollector {

    private final String type;
    private final Collection<Change> data;

    public ChangesCollector(String type) {
        this.type = type;
        this.data = new LinkedList<>();
    }

    public <T> void put(String key, T newValue) {
        put(key, null, newValue);
    }

    public <T> void put(String key, T oldValue, T newValue) {
        data.add(new Change(type + "." + key, oldValue, newValue));
    }

    public Collection<Change> getData() {
        return data;
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
                .append("data", data)
                .toString();
    }
}
