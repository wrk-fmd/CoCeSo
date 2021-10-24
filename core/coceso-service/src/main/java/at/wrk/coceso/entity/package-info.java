@TypeDef(typeClass = PostgresJsonType.class, defaultForType = PointDto.class)
@TypeDef(typeClass = PostgresEnumType.class, defaultForType = IncidentClosedReason.class)
@TypeDef(typeClass = PostgresEnumType.class, defaultForType = IncidentType.class)
@TypeDef(typeClass = PostgresEnumType.class, defaultForType = JournalEntryType.class)
@TypeDef(typeClass = PostgresEnumType.class, defaultForType = Sex.class)
@TypeDef(typeClass = PostgresEnumType.class, defaultForType = TaskState.class)
@TypeDef(typeClass = PostgresEnumType.class, defaultForType = UnitState.class)
@TypeDef(typeClass = PostgresEnumType.class, defaultForType = UnitType.class)
package at.wrk.coceso.entity;

import at.wrk.coceso.entity.enums.IncidentClosedReason;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.JournalEntryType;
import at.wrk.coceso.entity.enums.Sex;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitState;
import at.wrk.coceso.entity.enums.UnitType;
import at.wrk.fmd.mls.geocoding.api.dto.PointDto;
import at.wrk.fmd.mls.hibernate.enums.PostgresEnumType;
import at.wrk.fmd.mls.hibernate.json.PostgresJsonType;
import org.hibernate.annotations.TypeDef;
