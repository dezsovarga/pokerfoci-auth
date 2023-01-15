package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AddNewAccountDto;

import java.util.List;

public interface AdminService {

    List<AccountForAdminDto> listAccounts();
    AccountForAdminDto addNewAccount(AddNewAccountDto newAccountDtoRequest);
}
