package at.wrk.coceso.plugins.vienna;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;

class AddressInfoList {

  private final AddressInfoEntry[] entries;

  public AddressInfoList(@JsonProperty("features") AddressInfoEntry[] entries) {
    Arrays.sort(entries);
    this.entries = entries;
  }

  public AddressInfoEntry[] getEntries() {
    return entries;
  }

  public AddressInfoEntry getFirst() {
    return entries[0];
  }

  public int count() {
    return (entries == null) ? 0 : entries.length;
  }

}
