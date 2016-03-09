package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Integer> {

  public Point findByInfo(String name);

}
