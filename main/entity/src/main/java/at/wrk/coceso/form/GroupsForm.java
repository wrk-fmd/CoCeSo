package at.wrk.coceso.form;

import at.wrk.coceso.entity.Unit;
import java.util.List;
import java.util.stream.Collectors;

public class GroupsForm {

  private List<Group> groups;

  public GroupsForm() {
  }

  public GroupsForm(List<Unit> units) {
    groups = units.stream().map(Group::new).collect(Collectors.toList());
  }

  public List<Group> getGroups() {
    return groups;
  }

  public void setGroups(List<Group> groups) {
    this.groups = groups;
  }

}
