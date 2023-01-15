package com.dezso.varga.pokerfoci.converters;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AddNewAccountDto;

import java.util.List;

public interface AccountConverter {

    AccountForAdminDto fromAccountToAccountForAdminDto(Account account);

    List<AccountForAdminDto> fromAccountListToAccountForAdminDtoList(List<Account> accountList);

    Account fromAddNewAccountDtoToAccount(AddNewAccountDto addNewAccountDto);
}
