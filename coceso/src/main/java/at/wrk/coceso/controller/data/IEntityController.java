package at.wrk.coceso.controller.data;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface IEntityController<E> {

  public List<E> getAll(int concern_id);

  public E getById(int id);

  public String update(E e, BindingResult result, int concern_id, UsernamePasswordAuthenticationToken token);
}
