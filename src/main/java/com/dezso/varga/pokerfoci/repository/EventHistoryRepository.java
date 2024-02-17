package com.dezso.varga.pokerfoci.repository;

import com.dezso.varga.pokerfoci.domain.EventHistory;
import org.springframework.data.repository.CrudRepository;

public interface EventHistoryRepository extends CrudRepository<EventHistory, Long> {
}
