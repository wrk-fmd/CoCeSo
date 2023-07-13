package at.wrk.coceso.entity.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

  @Override
  public Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
    return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
  }

  @Override
  public LocalDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
    return sqlTimestamp == null ? null : sqlTimestamp.toLocalDateTime();
  }
}
