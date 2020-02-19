package at.wrk.coceso.entity.enums;

import java.util.EnumSet;
import java.util.Set;

public enum UnitType {
  Portable, Triage, Treatment, Postprocessing, Info, Officer;

  public static final Set<UnitType> treatmentTypes = EnumSet.of(Treatment, Triage);

  public boolean isTreatment() {
    return treatmentTypes.contains(this);
  }

}
