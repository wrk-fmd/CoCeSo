package at.wrk.coceso.entity.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

public class Changes implements Iterable<Changes.Change> {

    private final String type;

    @JsonProperty("data")
    private final Collection<Change> data;

    public Changes() {
        this((String) null);
    }

    private Changes(Changes c) {
        type = c.type;
        data = new LinkedList<>(c.data);
    }

    public Changes(String type) {
        this.type = type;
        this.data = new LinkedList<>();
    }

    public String getType() {
        return type;
    }

    public void put(String key, Object oldValue, Object newValue) {
        data.add(new Change(key, oldValue, newValue));
    }

    @JsonIgnore
    public boolean isEmpty() {
        return data.isEmpty();
    }

    public Changes deepCopy() {
        return new Changes(this);
    }

    @Override
    public Iterator<Change> iterator() {
        return data.iterator();
    }

    @Override
    public void forEach(Consumer<? super Change> consumer) {
        data.forEach(consumer);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
                .append("data", data)
                .toString();
    }

    public static class Change {

        private final String key;
        private final Object oldValue;
        private final Object newValue;

        private Change() {
            this(null, null, null);
        }

        private Change(String key, Object oldValue, Object newValue) {
            this.key = key;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public String getKey() {
            return key;
        }

        public Object getOldValue() {
            return oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("key", key)
                    .append("oldValue", oldValue)
                    .append("newValue", newValue)
                    .toString();
        }
    }
}
