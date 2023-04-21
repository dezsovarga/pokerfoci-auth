package com.dezso.varga.pokerfoci.converters;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountDto;

import java.util.List;

public interface AccountConverter {

    AccountForAdminDto fromAccountToAccountForAdminDto(Account account);

    List<AccountForAdminDto> fromAccountListToAccountForAdminDtoList(List<Account> accountList);

    Account fromAddNewAccountDtoToAccount(AccountDto accountDto);

    Account fromUpdateAccountDtoToAccount(AccountForAdminDto updateAccountDto, Account account);

    List<Account> fromAccountNameListToAccountList(List<String> accountNames);

    AccountDto fromAccountToAccountDto(Account account);

    List<AccountDto> fromAccountListToAccountDtoList(List<Account> accountList);
}
