package at.wrk.cocesoprototype.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import at.wrk.cocesoprototype.dao.EinheitDao;
import at.wrk.cocesoprototype.entities.Einheit;

public class EinheitService implements IEinheitService {

	@Autowired
	EinheitDao einheitDao;
	
	@Override
	public void insert(Einheit einheit) {
		einheitDao.insert(einheit);
		
	}

	@Override
	public List<Einheit> getEinheitList() {
		
		return einheitDao.getEinheitList();
	}

	@Override
	public void update(Einheit einheit) {
		einheitDao.update(einheit);
		
	}

	@Override
	public void delete(int id) {
		einheitDao.delete(id);
		
	}

	@Override
	public Einheit getEinheit(int id) {
		
		return einheitDao.getEinheit(id);
	}

}
