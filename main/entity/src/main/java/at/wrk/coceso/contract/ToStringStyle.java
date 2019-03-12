package at.wrk.coceso.contract;

public class ToStringStyle extends org.apache.commons.lang3.builder.ToStringStyle {
    public static final ToStringStyle STYLE = new ToStringStyle();

    private ToStringStyle() {
        setUseShortClassName(true);
        setUseIdentityHashCode(false);
    }
}
