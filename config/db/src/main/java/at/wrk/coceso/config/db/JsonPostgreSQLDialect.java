package at.wrk.coceso.config.db;

import org.hibernate.dialect.PostgreSQL9Dialect;

import java.sql.Types;

public class JsonPostgreSQLDialect extends PostgreSQL9Dialect {

  public JsonPostgreSQLDialect() {
    super();
    this.registerColumnType(Types.JAVA_OBJECT, "json");
    this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    this.registerHibernateType(Types.JAVA_OBJECT, "json");
    this.registerHibernateType(Types.JAVA_OBJECT, "jsonb");
  }
}
