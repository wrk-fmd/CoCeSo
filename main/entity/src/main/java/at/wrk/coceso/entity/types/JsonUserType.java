package at.wrk.coceso.entity.types;

import at.wrk.coceso.entity.helper.JsonViews;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public abstract class JsonUserType<T> implements UserType, Serializable {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final ObjectWriter writer = mapper.writerWithView(JsonViews.Database.class);

  @Override
  public abstract Class<T> returnedClass();

  @Override
  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return this.deepCopy(cached);
  }

  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    try {
      return serialize(value);
    } catch (IOException e) {
      throw new HibernateException("unable to disassemble object", e);
    }
  }

  @Override
  public T nullSafeGet(final ResultSet rs, final String[] names, final SharedSessionContractImplementor session, final Object owner)
          throws HibernateException, SQLException {
    try {
      return deserialize(rs.getString(names[0]));
    } catch (IOException e) {
      throw new HibernateException("unable to read object from result set", e);
    }
  }

  @Override
  public void nullSafeSet(final PreparedStatement st,  final Object value, final int index, final SharedSessionContractImplementor session)
          throws HibernateException, SQLException {
    if (value == null) {
      st.setNull(index, Types.OTHER);
    } else {
      try {
        st.setObject(index, serialize(value), Types.OTHER);
      } catch (IOException e) {
        throw new HibernateException("unable to set object to result set", e);
      }
    }
  }

  private String serialize(Object obj) throws JsonProcessingException {
    return writer.writeValueAsString(obj);
  }

  private T deserialize(String data) throws IOException {
    return data == null ? null : mapper.readValue(data, returnedClass());
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    return Objects.equals(x, y);
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    return Objects.hashCode(x);
  }

  @Override
  public boolean isMutable() {
    return true;
  }

  @Override
  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return this.deepCopy(original);
  }

  @Override
  public int[] sqlTypes() {
    return new int[]{Types.JAVA_OBJECT};
  }
}
