package at.wrk.fmd.coceso.plugin.geobroker.token;

public interface TokenGenerator {
    /**
     * Calculates a token for the given unitId. With the same salt, the token is is the same, iff the unitId is the same.
     */
    String calculateToken(String unitId, String salt);
}
