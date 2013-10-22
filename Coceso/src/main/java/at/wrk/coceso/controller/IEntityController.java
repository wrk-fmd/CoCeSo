package at.wrk.coceso.controller;

import org.springframework.validation.BindingResult;

import java.util.List;

public interface IEntityController<E> {

    public List<E> getAll(int caseId);

    public E getByPost(int id);

    public E getByGet(int id);

    public String update(E e, BindingResult result);
}
