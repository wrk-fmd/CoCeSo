package at.wrk.cocesoprototype.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import at.wrk.cocesoprototype.dao.extractor.EinheitRowMapper;
import at.wrk.cocesoprototype.entities.Einheit;

public class EinheitDao implements IEinheitDao {
	
	@Autowired
	DataSource dataSource;
	
	public EinheitDao () {
		
	}

	@Override
	public void insert(Einheit einheit) {
		String sql = "INSERT INTO einheit (vid, ename, etyp, estatus) VALUES (?, ?, ?, ?)";

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { einheit.getVorfallId(), einheit.getName(), einheit.getTyp(), einheit.getStatus() });
		
	}

	@Override
	public List<Einheit> getEinheitList() {
		
		List<Einheit> einheitList = new ArrayList<Einheit>();

		String sql = "SELECT * FROM einheit";

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		einheitList = jdbcTemplate.query(sql, new EinheitRowMapper());
		
		return einheitList;
	}

	@Override
	public void update(Einheit einheit) {
		
		String sql = "UPDATE einheit SET vid = ?, ename = ?, etyp = ?, estatus = ? WHERE eid = ?";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { einheit.getVorfallId(), einheit.getName(), einheit.getTyp(), 
				einheit.getStatus(), einheit.getId() });
		
	}

	@Override
	public void delete(int id) {
		
		String sql = "DELETE FROM einheit WHERE eid = " + id;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql);
		
	}

	@Override
	public Einheit getEinheit(int id) {
		
		List<Einheit> userList = new ArrayList<Einheit>();
		
		String sql = "SELECT * FROM einheit WHERE eid = " + id;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		userList = jdbcTemplate.query(sql, new EinheitRowMapper());
		
		return userList.get(0);
	}

}
