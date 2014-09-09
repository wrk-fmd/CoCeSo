package at.wrk.coceso.entity.helper;

import at.wrk.coceso.entity.Unit;

/**
 * Unit with additional "locked" switch
 *
 */
public class UnitWithLocked extends Unit {

  private boolean locked;

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

}
