package com.dezso.varga.pokerfoci.repository;

import com.dezso.varga.pokerfoci.domain.EventLog;
import org.springframework.data.repository.CrudRepository;

public interface EventLogRepository extends CrudRepository<EventLog, Long> {
}
