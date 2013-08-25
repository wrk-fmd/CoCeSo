package at.wrk.cocesoprototype.dao.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import at.wrk.cocesoprototype.entities.Vorfall;

public class VorfallExtractor implements ResultSetExtractor<Vorfall> {

	public Vorfall extractData(ResultSet resultSet) throws SQLException, DataAccessException {
		
		Vorfall vorfall = new Vorfall();
		
		vorfall.setId(resultSet.getInt(1));
		vorfall.setStart(resultSet.getString(2));
		vorfall.setEnd(resultSet.getString(3));
		vorfall.setTyp(resultSet.getString(4));
		vorfall.setText(resultSet.getString(5));
		vorfall.setStatus(resultSet.getString(6));
		
		return vorfall;
	}

}
