package at.wrk.cocesoprototype.service;

import java.util.List;

import at.wrk.cocesoprototype.entities.Vorfall;

public interface IVorfallService {
	
	public void insert(Vorfall vorfall);

	public List<Vorfall> getVorfallList();

	public void update(Vorfall vorfall);

	public void delete(int id);

	public Vorfall getVorfall(int id);

}
