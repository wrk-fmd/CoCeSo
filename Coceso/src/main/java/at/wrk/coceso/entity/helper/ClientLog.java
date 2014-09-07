package at.wrk.coceso.entity.helper;

import at.wrk.coceso.entity.Operator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JS logging functionality
 *
 */
public class ClientLog {

  //TODO: Log to a separate file
  private static final Logger logger = Logger.getLogger("CoCeSo");

  private String msg;

  private String url;

  private int line;

  private int col;

  private String stack;

  private Level level = Level.SEVERE;

  public void log() {
    logger.log(level, "{0} in file {1} at line {2}:{3}{4}", new Object[]{msg, url, line, col, (stack != null) ? "\nStacktrace:\n"+stack : ""});
  }

  public void log(Operator user) {
    logger.log(level, "{0} in file {1} at line {2}:{3} with user {4}{5}", new Object[]{msg, url, line, col, user.getUsername(), (stack != null) ? "\nStacktrace:\n"+stack : ""});
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
