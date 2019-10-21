package at.wrk.coceso.radio.api.dto;

import static java.util.Objects.requireNonNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Port {

    private final String path, name;

    public Port(String path, String name) {
        this.path = requireNonNull(path, "Port path must not be null");
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
        return new ToStringBuilder(this)
                .append("path", path)
                .append("name", name)
                .toString();
    }
}
