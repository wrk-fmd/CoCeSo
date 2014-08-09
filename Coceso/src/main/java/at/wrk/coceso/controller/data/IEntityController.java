package at.wrk.coceso.controller.data;

import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.List;

public interface IEntityController<E> {

  public List<E> getAll(int concernId);

  public E getById(int id);

  public String update(E e, BindingResult result, int concernId, Principal user);
}
