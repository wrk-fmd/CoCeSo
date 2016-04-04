package at.wrk.coceso.entity.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

public class EnumUserType implements UserType, ParameterizedType {

  private Class<? extends Enum> enumClass;

  @Override
  public void setParameterValues(Properties parameters) {
    String enumClassName = parameters.getProperty("enumClass");
    try {
      enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
    } catch (ClassNotFoundException cfne) {
      throw new HibernateException("Enum class not found", cfne);
    }
  }

  @Override
  public Class<? extends Enum> returnedClass() {
    return enumClass;
  }

  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
    String value = rs.getString(names[0]);
    if (rs.wasNull()) {
      return null;
    }
    try {
      return Enum.valueOf(enumClass, value);
    } catch (Exception e) {
      throw new HibernateException("Exception while invoking valueOf method for enum class " + enumClass, e);
    }
  }

  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
    if (value == null) {
      st.setNull(index, Types.OTHER);
    } else if (value instanceof Enum) {
      st.setObject(index, ((Enum) value).name(), Types.OTHER);
    } else {
      throw new HibernateException("Value of class " + value.getClass() + "is not an enum");
    }
  }

  @Override
  public int[] sqlTypes() {
    return new int[]{Types.OTHER};
  }

  @Override
  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return cached;
  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable) value;
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    return x == y;
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    return x.hashCode();
  }

  @Override
  public boolean isMutable() {
    return false;
  }

  @Override
  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }

}
