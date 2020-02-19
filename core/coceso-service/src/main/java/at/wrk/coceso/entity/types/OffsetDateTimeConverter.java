package at.wrk.coceso.entity.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Converter(autoApply = true)
public class OffsetDateTimeConverter implements AttributeConverter<OffsetDateTime, Timestamp> {

  @Override
  public Timestamp convertToDatabaseColumn(final OffsetDateTime offsetDateTime) {
    return offsetDateTime == null ? null : Timestamp.valueOf(offsetDateTime
            .atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
  }

  @Override
  public OffsetDateTime convertToEntityAttribute(final Timestamp sqlTimestamp) {
    return sqlTimestamp == null ? null : sqlTimestamp.toLocalDateTime().atOffset(ZoneOffset.UTC);
  }
}
