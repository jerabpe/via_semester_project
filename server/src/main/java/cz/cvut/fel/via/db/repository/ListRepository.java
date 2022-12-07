package cz.cvut.fel.via.db.repository;

import cz.cvut.fel.via.db.model.User;
import cz.cvut.fel.via.db.model.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListRepository extends JpaRepository<WatchList, Long> {
    List<WatchList> findAllByOwner(@Param("owner") User owner);
    WatchList findByTitle(@Param("title") String title);
}
