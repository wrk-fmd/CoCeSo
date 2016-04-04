package at.wrk.coceso.radio;

public class Port {

  private final String path, name;

  public Port(String path, String name) {
    if (path == null) {
      throw new IllegalArgumentException("Port path must not be null!");
    }

    this.path = path;
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public String getName() {
    return name == null ? path : name;
  }

}
