package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;

import java.util.Collection;

public interface PointService {

  Collection<String> autocomplete(String filter, Concern concern);
}
