package at.wrk.coceso.form;

import java.util.Objects;

public class GroupIcon implements Comparable<GroupIcon> {
    private final String name;

    public GroupIcon(final String name) {
        this.name = Objects.requireNonNull(name, "Name of group icon must not be null.");
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(final GroupIcon o) {
        return name.compareTo(o.name);
    }
}
