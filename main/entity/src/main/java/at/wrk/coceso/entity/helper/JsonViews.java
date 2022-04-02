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

  interface ClientDetail extends Client {
  }

  interface Patadmin extends Web {
  }

}
