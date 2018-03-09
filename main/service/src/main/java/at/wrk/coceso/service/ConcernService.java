package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface ConcernService {

  Concern getById(int id);

  List<Concern> getAll();

  Concern getByName(String name);

  Concern update(Concern concern, User user);

  void setClosed(int concern_id, boolean close, User user);

  void addSection(String section, int concernId);

  void removeSection(String section, int concernId);

  boolean isClosed(Integer concernId);

  boolean isClosed(Concern concern);

}
