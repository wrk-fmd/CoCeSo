package at.wrk.coceso.entity.helper;

public interface JsonViews {

  public interface Always {
  }

  public interface Home extends Always {
  }

  public interface Edit extends Always {
  }

  public interface Main extends Always {
  }

  public interface Client extends Always {

  }

  public interface ClientList extends Client {
  }

  public interface ClientDetail extends Client {
  }

  public interface ClientUnit extends Client {

  }

  public interface ClientUnitList extends ClientList, ClientUnit {

  }

  public interface ClientUnitDetail extends ClientDetail, ClientUnit {

  }

  public interface Patadmin extends Always {
  }

}
