package com.dezso.varga.pokerfoci.repository;

import com.dezso.varga.pokerfoci.domain.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by dezso on 02.07.2017.
 */
public interface AccountRepository extends CrudRepository<Account, Long> {

    Account findByEmail(String email);

    List<Account> findAll();

    Account findByUsername(String username);
}
