package at.wrk.coceso.form;

import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.UnitState;

public class Group {

  private int id;
  private String call;
  private String imgsrc;
  private int capacity;
  private boolean active;

  public Group() {
  }

  public Group(Unit u) {
    this.id = u.getId();
    this.call = u.getCall();
    this.imgsrc = u.getImgsrc();
    this.capacity = u.getCapacity() == null ? 0 : u.getCapacity();
    this.active = (u.getState() == UnitState.EB);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getCall() {
    return call;
  }

  public String getImgsrc() {
    return imgsrc;
  }

  public void setImgsrc(String imgsrc) {
    this.imgsrc = imgsrc;
  }

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
