package at.wrk.coceso.entity.helper;

public interface JsonViews {

  interface Always {
  }

  interface Web extends Always, PointMinimal {
  }

  interface Database extends Always {
  }

  interface PointMinimal extends Always {
  }

  interface PointFull extends PointMinimal {
  }

  interface Home extends Web {
  }

  interface Edit extends Web {
  }

  interface UserFull extends Edit {
  }

  interface Main extends Web {
  }

  interface Client extends Web {
  }

  interface ClientList extends Client {
  }

  interface ClientDetail extends Client {
  }

  interface ClientUnit extends Client {
  }

  interface ClientUnitList extends ClientList, ClientUnit {
  }

  interface ClientUnitDetail extends ClientDetail, ClientUnit {
  }

  interface Patadmin extends Web {
  }

}
