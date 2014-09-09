package at.wrk.coceso.entity.helper;

import at.wrk.coceso.entity.Operator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * JS logging functionality
 *
 */
public class ClientLog {

  private static final Logger logger = Logger.getLogger(ClientLog.class);

  private String msg;

  private String url;

  private int line;

  private int col;

  private String stack;

  private Level level = Level.ERROR;

  public void log() {
    logger.log(level, "in file " + url + ":" + line + ":" + col + "\n" + msg + (stack != null ? "\nStacktrace:\n" + stack : ""));
  }

  public void log(Operator user) {
    logger.log(level, "in file " + url + ":" + line + ":" + col + " with user " + user.getUsername() + "\n" + msg + (stack != null ? "\nStacktrace:\n" + stack : ""));
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getLine() {
    return line;
  }

  public void setLine(int line) {
    this.line = line;
  }

  public int getCol() {
    return col;
  }

  public void setCol(int col) {
    this.col = col;
  }

  public String getStack() {
    return stack;
  }

  public void setStack(String stack) {
    this.stack = stack;
  }

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }

}
