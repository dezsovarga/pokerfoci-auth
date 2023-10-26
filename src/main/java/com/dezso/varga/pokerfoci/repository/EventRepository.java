package com.dezso.varga.pokerfoci.repository;

import com.dezso.varga.pokerfoci.domain.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface EventRepository extends CrudRepository<Event, Long> {

    List<Event> findAll();

    @Query(
            value = "SELECT * FROM EVENT order by date desc limit 1",
            nativeQuery = true)
    Event findLatestEvent();
}
