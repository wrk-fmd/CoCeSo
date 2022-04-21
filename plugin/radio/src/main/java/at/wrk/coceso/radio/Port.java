package at.wrk.coceso.radio;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class Port {

    private final String path;
    private final String name;

    public Port(String path, String name) {
        this.path = Objects.requireNonNull(path, "Port path must not be null!");
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name == null ? path : name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("path", path)
                .append("name", name)
                .toString();
    }
}
