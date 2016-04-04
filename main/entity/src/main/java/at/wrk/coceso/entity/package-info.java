@TypeDefs({
  @TypeDef(typeClass = ChangesUserType.class, defaultForType = Changes.class),
  @TypeDef(typeClass = EnumUserType.class, parameters = @Parameter(name = "enumClass", value = "at.wrk.coceso.entity.enums.Authority"), defaultForType = Authority.class),
  @TypeDef(typeClass = EnumUserType.class, parameters = @Parameter(name = "enumClass", value = "at.wrk.coceso.entity.enums.IncidentState"), defaultForType = IncidentState.class),
  @TypeDef(typeClass = EnumUserType.class, parameters = @Parameter(name = "enumClass", value = "at.wrk.coceso.entity.enums.IncidentType"), defaultForType = IncidentType.class),
  @TypeDef(typeClass = EnumUserType.class, parameters = @Parameter(name = "enumClass", value = "at.wrk.coceso.entity.enums.LogEntryType"), defaultForType = LogEntryType.class),
  @TypeDef(typeClass = EnumUserType.class, parameters = @Parameter(name = "enumClass", value = "at.wrk.coceso.entity.enums.TaskState"), defaultForType = TaskState.class),
  @TypeDef(typeClass = EnumUserType.class, parameters = @Parameter(name = "enumClass", value = "at.wrk.coceso.entity.enums.UnitState"), defaultForType = UnitState.class),
  @TypeDef(typeClass = EnumUserType.class, parameters = @Parameter(name = "enumClass", value = "at.wrk.coceso.entity.enums.UnitType"), defaultForType = UnitType.class),
  @TypeDef(typeClass = EnumUserType.class, parameters = @Parameter(name = "enumClass", value = "at.wrk.coceso.entity.enums.Naca"), defaultForType = Naca.class),
  @TypeDef(typeClass = EnumUserType.class, parameters = @Parameter(name = "enumClass", value = "at.wrk.coceso.entity.enums.Sex"), defaultForType = Sex.class)
})
package at.wrk.coceso.entity;

import at.wrk.coceso.entity.enums.*;
import at.wrk.coceso.entity.helper.Changes;
import at.wrk.coceso.entity.types.ChangesUserType;
import at.wrk.coceso.entity.types.EnumUserType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
