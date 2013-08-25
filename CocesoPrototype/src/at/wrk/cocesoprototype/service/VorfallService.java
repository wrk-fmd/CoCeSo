package at.wrk.cocesoprototype.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import at.wrk.cocesoprototype.dao.VorfallDao;
import at.wrk.cocesoprototype.entities.Vorfall;

public class VorfallService implements IVorfallService {
	
	@Autowired
	VorfallDao vorfallDao;

	@Override
	public void insert(Vorfall vorfall) {
		vorfallDao.insert(vorfall);
		
	}

	@Override
	public List<Vorfall> getVorfallList() {
		
		return vorfallDao.getVorfallList();
	}

	@Override
	public void update(Vorfall vorfall) {
		vorfallDao.update(vorfall);
		
	}

	@Override
	public void delete(int id) {
		vorfallDao.delete(id);
		
	}

	@Override
	public Vorfall getVorfall(int id) {
		
		return vorfallDao.getVorfall(id);
	}

}
