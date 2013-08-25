package at.wrk.cocesoprototype.dao.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import at.wrk.cocesoprototype.entities.Vorfall;

public class VorfallRowMapper implements RowMapper<Vorfall> {

	@Override
	public Vorfall mapRow(ResultSet resultSet, int line) throws SQLException {
		
		VorfallExtractor vorfallExtractor = new VorfallExtractor();
		
		return vorfallExtractor.extractData(resultSet);
	}

}
