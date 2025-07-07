package at.wrk.coceso.entity.enums;

public enum Ambulance {
  KTW_sitzend,
  KTW_liegend,
  RTW,
  RTW_mit_NEF;

  @Override
  public String toString() {
    return name().replace("_", " ");
  }
}
