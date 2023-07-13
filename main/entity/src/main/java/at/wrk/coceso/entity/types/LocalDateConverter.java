package at.wrk.coceso.entity.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.LocalDate;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

  @Override
  public Date convertToDatabaseColumn(LocalDate localDate) {
    return localDate == null ? null : Date.valueOf(localDate);
  }

  @Override
  public LocalDate convertToEntityAttribute(Date sqlDate) {
    return sqlDate == null ? null : sqlDate.toLocalDate();
  }
}
