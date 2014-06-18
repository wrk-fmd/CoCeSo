package at.wrk.coceso.controller.data;

import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.List;

public interface IEntityController<E> {

    public List<E> getAll(String caseId);

    public E getByPost(int id);

    public E getByGet(int id);

    public String update(E e, BindingResult result, String case_id, Principal user);
}
