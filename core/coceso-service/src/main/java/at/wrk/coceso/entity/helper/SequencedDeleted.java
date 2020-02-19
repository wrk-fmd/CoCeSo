package at.wrk.coceso.entity.helper;

public class SequencedDeleted {
  private final int hver;
  private final int seq;
  private final int delete;

  public SequencedDeleted(int hver, int seq, int id) {
    this.hver = hver;
    this.seq = seq;
    this.delete = id;
  }

  public int getHver() {
    return hver;
  }

  public int getSeq() {
    return seq;
  }

  public int getDelete() {
    return delete;
  }
}
