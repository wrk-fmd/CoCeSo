package at.wrk.coceso.entity.helper;

import com.fasterxml.jackson.annotation.JsonView;

public class SequencedResponse<T> {

  @JsonView(JsonViews.Always.class)
  private final int hver;

  @JsonView(JsonViews.Always.class)
  private final int seq;

  @JsonView(JsonViews.Always.class)
  private final T data;

  public SequencedResponse(int hver, int seq, T data) {
    this.hver = hver;
    this.seq = seq;
    this.data = data;
  }

  public int getHver() {
    return hver;
  }

  public int getSeq() {
    return seq;
  }

  public T getData() {
    return data;
  }

}
