package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    List<Unit> findByIdIn(Collection<Long> id);

    List<Unit> findByConcern(Concern concern);

    List<Unit> findByConcern(Concern concern, Sort sort);

//    Unit findFirstByCallIgnoreCaseAndConcernAndTypeIn(String call, Concern concern, Collection<UnitType> type);
//
//    List<Unit> findByConcernAndTypeIn(Concern concern, Collection<UnitType> type);

    // TODO Update
//  @Query("SELECT DISTINCT u FROM Unit u INNER JOIN u.crew c WHERE c = :user AND u.type IN :types AND u.concern.closed = false")
//  List<Unit> findByUser(@Param("user") User user, @Param("types") Collection<UnitType> types);
//
//  @Query("SELECT DISTINCT u FROM Unit u INNER JOIN u.crew c WHERE c = :user AND u.concern = :concern")
//  List<Unit> findByConcernUser(@Param("concern") Concern concern, @Param("user") User user);

    @Query("SELECT u FROM Unit u WHERE u.concern = :concern AND u.container IS NULL")
    List<Unit> findWithoutContainer(@Param("concern") Concern concern);

    @Query("SELECT l.unit.id FROM JournalEntry l WHERE l.incident = :incident AND l.unit IS NOT NULL GROUP BY l.unit")
    List<Long> findRelated(@Param("incident") Incident incident);
}
