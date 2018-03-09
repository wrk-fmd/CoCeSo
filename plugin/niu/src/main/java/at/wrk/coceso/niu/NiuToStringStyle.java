package at.wrk.coceso.niu;

import org.apache.commons.lang3.builder.ToStringStyle;

public class NiuToStringStyle extends ToStringStyle {
    public static final NiuToStringStyle STYLE = new NiuToStringStyle();

    private NiuToStringStyle() {
        setUseShortClassName(true);
        setUseIdentityHashCode(false);
    }
}
