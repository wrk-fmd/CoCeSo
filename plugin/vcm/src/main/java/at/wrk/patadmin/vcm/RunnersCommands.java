package at.wrk.patadmin.vcm;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Medinfo;
import at.wrk.coceso.service.patadmin.MedinfoService;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.shell.ConcernCommands;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class RunnersCommands implements CommandMarker {

  @Autowired
  private ConcernCommands concernCommands;

  @Autowired
  private MedinfoService medinfoService;

  @Autowired
  private PatadminService patadminService;

  @CliAvailabilityIndicator({"runners import", "runners clear"})
  public boolean isAvailable() {
    return !Concern.isClosed(concernCommands.getConcern());
  }

  @CliCommand(value = "runners import", help = "Import runners data from CSV file")
  public String importRunners(@CliOption(key = {"filename"}, mandatory = true, help = "The location of the CSV file") final File file) {
    Concern concern = concernCommands.getConcern();
    if (Concern.isClosed(concern)) {
      return "Concern missing or closed";
    }

    long start = System.currentTimeMillis();

    VcmRunners runners;
    try {
      runners = new VcmRunners(file, concern);
    } catch (IOException e) {
      return "File not found or invalid!";
    }

    List<Medinfo> saved = medinfoService.save(runners);
    return String.format("%d runners were saved to concern %s, took %d seconds", saved.size(), concern, (System.currentTimeMillis() - start) / 1000);
  }

  @CliCommand(value = "runners clear", help = "Clear runners in concern")
  public String clearRunners(@CliOption(key = {"cascade"}, mandatory = false, unspecifiedDefaultValue = "false",
      help = "Also update patients referencing this runner") final boolean cascade) {
    Concern concern = concernCommands.getConcern();
    if (Concern.isClosed(concern)) {
      return "Concern missing or closed";
    }

    String patients = "";
    if (cascade) {
      patients = String.format("Removed runners from %d patients\n", patadminService.removeMedinfos(concern));
    }

    return String.format("%sRemoved %d runners from concern %s", patients, medinfoService.deleteAll(concern), concern);
  }

}
