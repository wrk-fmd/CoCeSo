package at.wrk.coceso.shell;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.service.ConcernService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.table.Table;
import org.springframework.shell.support.table.TableHeader;
import org.springframework.stereotype.Component;

@Component
public class ConcernCommands implements CommandMarker {

  @Autowired
  private ConcernService concernService;

  private Concern concern;

  @CliAvailabilityIndicator({"concern list", "concern select"})
  public boolean isAvailable() {
    return true;
  }

  @CliCommand(value = "concern list", help = "List all open concerns")
  public Table list() {
    Table table = new Table();
    table.addHeader(1, new TableHeader("ID"));
    table.addHeader(2, new TableHeader("Name"));

    concernService.getAll().stream()
        .filter(c -> !c.isClosed())
        .forEachOrdered(c -> table.addRow(c.getId() + "", c.getName()));

    return table;
  }

  @CliCommand(value = "concern select", help = "Select an open concern to use subsequently")
  public String select(@CliOption(key = {"concern"}, mandatory = true, help = "The id of the concern to choose") final int concernId) {
    if (concernId == 0) {
      concern = null;
      return "Concern was unset";
    }

    Concern chosen = concernService.getById(concernId);
    if (Concern.isClosed(chosen)) {
      return "Concern missing or closed";
    }
    concern = chosen;
    return String.format("Concern %s was selected", concern);
  }

  public Concern getConcern() {
    return concern;
  }

}
