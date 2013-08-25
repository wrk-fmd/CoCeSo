package at.wrk.cocesoprototype.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import at.wrk.cocesoprototype.dao.extractor.VorfallRowMapper;
import at.wrk.cocesoprototype.entities.Vorfall;

public class VorfallDao implements IVorfallDao {
	
	@Autowired
	DataSource dataSource;

	@Override
	public void insert(Vorfall vorfall) {
		
		String sql = "INSERT INTO vorfall (vstart, vende, vtyp, vtext, vstatus) VALUES (?, ?, ?, ?, ?)";

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { vorfall.getStart(), vorfall.getEnd(), vorfall.getTyp(), vorfall.getText(), vorfall.getStatus() });
		
	}

	@Override
	public List<Vorfall> getVorfallList() {

		List<Vorfall> vorfallList = new ArrayList<Vorfall>();

		String sql = "SELECT * FROM vorfall ORDER BY vstart";

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		vorfallList = jdbcTemplate.query(sql, new VorfallRowMapper());
		
		return vorfallList;
	}

	@Override
	public void update(Vorfall vorfall) {

		String sql = "UPDATE vorfall SET vstart = ?, vende = ?, vtyp = ?, vtext = ?, vstatus = ? WHERE vid = ?";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { vorfall.getStart(), vorfall.getEnd(), vorfall.getTyp(), vorfall.getText(),
				vorfall.getStatus(), vorfall.getId() });
		
	}

	@Override
	public void delete(int id) {

		String sql = "DELETE FROM vorfall WHERE vid = " + id;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql);
		
	}

	@Override
	public Vorfall getVorfall(int id) {

		List<Vorfall> vorfallList = new ArrayList<Vorfall>();
		
		String sql = "SELECT * FROM vorfall WHERE vid = " + id;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		vorfallList = jdbcTemplate.query(sql, new VorfallRowMapper());
		
		return vorfallList.get(0);
	}
	
}
