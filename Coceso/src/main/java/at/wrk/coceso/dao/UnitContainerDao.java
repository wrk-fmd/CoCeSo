package at.wrk.coceso.dao;

import at.wrk.coceso.entity.helper.SlimUnit;
import at.wrk.coceso.entity.helper.SlimUnitContainer;
import at.wrk.coceso.entity.helper.UnitContainer;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Repository
public class UnitContainerDao {

    private JdbcTemplate jdbc;

    @Autowired
    public UnitContainerDao(DataSource dataSource) {
        jdbc = new JdbcTemplate(dataSource);
    }

// READING

    /**
     * Returns all UnitContainer of the Concern, but subContainer will be empty
     * @param concernId Id of active Concern
     * @return List of all Containers, spare Units NOT Included
     */
    public List<UnitContainer> getByConcernSlim(int concernId) {
        UnitContainer top = createTopContainerIfNotExistent(concernId);
        if(top == null)
            return null;

        String q = "SELECT * FROM container WHERE concern_fk = ? ORDER BY ordering ASC";
        List<UnitContainer> ret = jdbc.query(q, new Object[]{concernId}, new ContainerMapper());
        for(UnitContainer uc : ret) {
            loadUnits(uc);
        }
        return ret;
    }

    /**
     * Returns top Container of Concern, Hierarchy included
     * @param concernId Id of active Concern
     * @return Top Container, Spare Units NOT in Top Container
     */
    public UnitContainer getByConcernId(int concernId) {
        UnitContainer top = createTopContainerIfNotExistent(concernId);
        if(top == null)
            return null;

        Queue<UnitContainer> queue = new LinkedList<UnitContainer>();
        queue.offer(top);

        while(queue.peek() != null) {
            UnitContainer current = queue.poll();

            loadUnits(current);

            current.setSubContainer(new LinkedList<UnitContainer>());

            for(UnitContainer container : getSubContainer(current.getId())) {
                current.getSubContainer().add(container);
                queue.offer(container);
            }

        }
        return top;
    }

    public List<SlimUnit> getSpareUnits(int concernId) {
        String q = "SELECT u.id AS unit_fk, -1 AS ordering, u.call FROM unit u " +
                "WHERE u.concern_fk = ? AND u.id NOT IN " +
                "(SELECT uc.unit_fk FROM unit_in_container uc JOIN container c ON uc.container_fk = c.id " +
                "WHERE c.concern_fk = ?)";

        return jdbc.query(q, new Object[] {concernId, concernId}, new ContainerUnitMapper());
    }

    private List<UnitContainer> getSubContainer(int headId) {
        String q = "SELECT * FROM container WHERE head = ? AND head != id ORDER BY ordering ASC";
        return jdbc.query(q, new Object[]{headId}, new ContainerMapper());
    }

    private void loadUnits(UnitContainer container) {
        if(container == null)
            return;

        String q = "SELECT uc.*, u.call FROM unit_in_container uc, unit u WHERE u.id = uc.unit_fk AND container_fk = ?";
        try {
            List<SlimUnit> ordered = jdbc.query(q, new Object[]{container.getId()}, new ContainerUnitMapper());
            Collections.sort(ordered);
            container.setUnits(ordered);
        } catch (DataAccessException dae) {
            Logger.error(dae.getMessage());
        }
    }

    private UnitContainer getByContainerId(int containerId) {
        String q = "SELECT * FROM container WHERE id = ?";

        UnitContainer container;

        try {
            container = jdbc.queryForObject(q, new Object[]{containerId}, new ContainerMapper());
        }
        catch(DataAccessException dae) {
            return null;
        }

        return container;
    }

    private UnitContainer getTopContainer(int concernId) {
        String q = "SELECT * FROM container WHERE head = id AND concern_fk = ?";

        UnitContainer container;

        try {
            container = jdbc.queryForObject(q, new Object[]{concernId}, new ContainerMapper());
        }
        catch(DataAccessException dae) {
            return null;
        }

        return container;
    }

// WRITING
    private UnitContainer createTopContainerIfNotExistent(int concernId) {
        UnitContainer top = getTopContainer(concernId);
        if(top == null) {
            addTopContainer(concernId, "Top");
            top = getTopContainer(concernId);
            if(top == null)
                return null;
        }
        return top;
    }

    private int addTopContainer(final int concernId, final String name) {
        // Top Container already exists
        if(getTopContainer(concernId) != null)
             return -1;

        try {
            final String q = "INSERT INTO container (ordering, head, name, concern_fk) " +
                    "VALUES (0, currval('container_id_seq'),?,?)";

            KeyHolder holder = new GeneratedKeyHolder();

            jdbc.update(new PreparedStatementCreator() {

                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);

                    ps.setString(1, name == null ? "" : name);
                    ps.setInt(2, concernId);
                    return ps;
                }
            }, holder);

            return (Integer) holder.getKeys().get("id");

        } catch (DataAccessException dae) {
            Logger.warning(dae.getMessage());
            return -1;
        }
    }

    public int addContainer(final int headId, final String name, final double ordering) {

        final UnitContainer head = getByContainerId(headId);
        if(head == null)
            return -1;

        try {
            final String q = "INSERT INTO container (head, name, concern_fk, ordering) VALUES (?,?,?,?)";

            KeyHolder holder = new GeneratedKeyHolder();

            jdbc.update(new PreparedStatementCreator() {

                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);

                    ps.setInt(1, headId);
                    ps.setString(2, name == null ? "" : name);
                    ps.setInt(3, head.getConcernId());
                    ps.setDouble(4, ordering);
                    return ps;
                }
            }, holder);

            return (Integer) holder.getKeys().get("id");

        } catch (DataAccessException dae) {
            Logger.warning(dae.getMessage());
            return -1;
        }
    }

    public boolean updateContainer(UnitContainer container) {
        UnitContainer old = getByContainerId(container.getId());
        UnitContainer newHead = getByContainerId(container.getHead());

        // Check if existent, same Concern in new Container and same Concern as new Head-Container
        if(old == null || newHead.getConcernId() != old.getConcernId()) {
            return false;
        }

        String q = "UPDATE container SET name = ?, ordering = ?, head = ? WHERE id = ?";
        try {
            jdbc.update(q,
                    container.getName() == null ? "" : container.getName(),
                    container.getOrdering(),
                    container.getHead(),
                    container.getId());
            Logger.debug("container updated: "+container);
            return true;
        } catch (DataAccessException dae) {
            Logger.error(dae.getMessage());
            return false;
        }
    }

    public boolean removeContainer(int containerId) {
        String q = "DELETE FROM container WHERE id = ?";
        try {
            jdbc.update(q, containerId);
            return true;
        } catch (DataAccessException dae) {
            return false;
        }
    }

    public boolean addUnit(int containerId, int unitId, double ordering) {
        String q = "INSERT INTO unit_in_container (container_fk, unit_fk, ordering) VALUES (?,?,?)";
        try {
            jdbc.update(q, containerId, unitId, ordering);
        } catch (DataAccessException dae) {
            Logger.error(dae.getMessage());
            return false;
        }
        return true;
    }

    public void resetUnit(int unitId) {
        String q = "DELETE FROM unit_in_container WHERE unit_fk = ?";
        try {
            jdbc.update(q, unitId);
        } catch (DataAccessException dae) {
            Logger.error(dae.getMessage());
        }
    }

// HELPER
    private class ContainerMapper implements RowMapper<UnitContainer> {
        @Override
        public UnitContainer mapRow(ResultSet resultSet, int i) throws SQLException {
            UnitContainer ret = new UnitContainer();

            ret.setId(resultSet.getInt("id"));
            ret.setConcernId(resultSet.getInt("concern_fk"));
            ret.setName(resultSet.getString("name"));
            ret.setOrdering(resultSet.getDouble("ordering"));
            ret.setHead(resultSet.getInt("head"));

            return ret;
        }
    }

    private class ContainerUnitMapper implements RowMapper<SlimUnit> {
        @Override
        public SlimUnit mapRow(ResultSet resultSet, int i) throws SQLException {
            SlimUnit ret = new SlimUnit();
            ret.setId(resultSet.getInt("unit_fk"));
            ret.setOrdering(resultSet.getDouble("ordering"));
            ret.setCall(resultSet.getString("call"));

            return ret;
        }
    }
}
