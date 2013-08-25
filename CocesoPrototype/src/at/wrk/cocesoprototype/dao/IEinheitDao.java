package at.wrk.cocesoprototype.dao;

import java.util.List;

import at.wrk.cocesoprototype.entities.Einheit;

public interface IEinheitDao {
	
	public void insert(Einheit einheit);

	public List<Einheit> getEinheitList();

	public void update(Einheit einheit);

	public void delete(int id);

	public Einheit getEinheit(int id);

}
