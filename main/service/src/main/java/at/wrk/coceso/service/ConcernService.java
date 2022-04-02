package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.List;

@Service
@Transactional
public interface ConcernService {

  @Nullable
  Concern getById(int id);

  List<Concern> getAll();

  List<Concern> getAllOpen();

  Concern getByName(String name);

  Concern update(Concern concern);

  void setClosed(int concern_id, boolean close);

  void addSection(String section, int concernId);

  void removeSection(String section, int concernId);

  boolean isClosed(Integer concernId);

  boolean isClosed(Concern concern);

}
