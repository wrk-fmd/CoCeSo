package at.wrk.cocesoprototype.dao.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import at.wrk.cocesoprototype.entities.Einheit;


public class EinheitRowMapper implements RowMapper<Einheit> {

	@Override
	public Einheit mapRow(ResultSet resultSet, int line) throws SQLException {
		
		EinheitExtractor einheitExtractor = new EinheitExtractor();
		
		return einheitExtractor.extractData(resultSet);
	}

}
