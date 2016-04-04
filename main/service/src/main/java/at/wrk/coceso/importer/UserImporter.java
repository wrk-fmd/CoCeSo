package at.wrk.coceso.importer;

import at.wrk.coceso.entity.User;
import java.util.Collection;

public interface UserImporter {

  public Collection<User> updateUsers(String data, Collection<User> existing) throws ImportException;

}
