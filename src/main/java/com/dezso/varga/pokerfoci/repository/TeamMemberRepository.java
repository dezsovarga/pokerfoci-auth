package com.dezso.varga.pokerfoci.repository;

import com.dezso.varga.pokerfoci.domain.TeamMember;
import org.springframework.data.repository.CrudRepository;

public interface TeamMemberRepository extends CrudRepository<TeamMember, Long> {
}