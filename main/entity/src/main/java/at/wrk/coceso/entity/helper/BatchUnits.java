package at.wrk.coceso.entity.helper;

import at.wrk.coceso.entity.Point;

/**
 * Form data for the batch unit creation
 *
 */
public class BatchUnits {

  private String call;
  private int from;
  private int to;
  private boolean withDoc;
  private boolean portable;
  private boolean transportVehicle;
  private Point home;

  public String getCall() {
    return call;
  }

  public void setCall(String call) {
    this.call = call;
  }

  public int getFrom() {
    return from;
  }

  public void setFrom(int from) {
    this.from = from;
  }

  public int getTo() {
    return to;
  }

  public void setTo(int to) {
    this.to = to;
  }

  public boolean isWithDoc() {
    return withDoc;
  }

  public void setWithDoc(boolean withDoc) {
    this.withDoc = withDoc;
  }

  public boolean isPortable() {
    return portable;
  }

  public void setPortable(boolean portable) {
    this.portable = portable;
  }

  public boolean isTransportVehicle() {
    return transportVehicle;
  }

  public void setTransportVehicle(boolean transportVehicle) {
    this.transportVehicle = transportVehicle;
  }

  public Point getHome() {
    return home;
  }

  public void setHome(Point home) {
    this.home = home;
  }
}
