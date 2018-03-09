package at.wrk.coceso.entity.helper;

import at.wrk.coceso.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JS logging functionality
 *
 */
public class ClientLog {

  private static final Logger LOG = LoggerFactory.getLogger(ClientLog.class);

  private String msg;

  private String url;

  private int line;

  private int col;

  private String stack;

  public void log() {
    LOG.error("in file {}:{}:{}\n{}\nStacktrace:\n{}", url, line, col, msg, stack);
  }

  public void log(User user) {
    LOG.error("in file {}:{}:{} with user {}\n{}\nStacktrace:\n{}", url, line, col, user.getUsername(), msg, stack);
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

}
