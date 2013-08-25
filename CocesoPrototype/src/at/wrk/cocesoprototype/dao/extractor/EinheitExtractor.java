package at.wrk.cocesoprototype.dao.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import at.wrk.cocesoprototype.entities.Einheit;

public class EinheitExtractor implements ResultSetExtractor<Einheit> {

public Einheit extractData(ResultSet resultSet) throws SQLException, DataAccessException {
		
		Einheit einheit = new Einheit();
		
		einheit.setId(resultSet.getInt(1));
		einheit.setVorfallId(resultSet.getInt(2));
		einheit.setName(resultSet.getString(3));
		einheit.setTyp(resultSet.getString(4));
		einheit.setStatus(resultSet.getString(5));
		
		return einheit;
	}

}
