package at.wrk.coceso.entity.types;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OffsetDateTimeConverter implements AttributeConverter<OffsetDateTime, Timestamp> {

  @Override
  public Timestamp convertToDatabaseColumn(OffsetDateTime offsetDateTime) {
    return offsetDateTime == null ? null : Timestamp.valueOf(offsetDateTime
            .atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
  }

  @Override
  public OffsetDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
    return sqlTimestamp == null ? null : sqlTimestamp.toLocalDateTime().atOffset(ZoneOffset.UTC);
  }
}
