package com.dezso.varga.pokerfoci.converters;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Participation;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountWithSkillDto;

import java.util.List;

public interface AccountConverter {

    AccountForAdminDto fromAccountToAccountForAdminDto(Account account);

    List<AccountForAdminDto> fromAccountListToAccountForAdminDtoList(List<Account> accountList);

    Account fromAddNewAccountDtoToAccount(AccountDto accountDto);

    Account fromUpdateAccountDtoToAccount(AccountForAdminDto updateAccountDto, Account account);

    List<Participation> fromAccountNameListToEventParticipationList(List<String> accountNames);

    AccountWithSkillDto fromAccountToAccountWithSkillDto(Account account);

    AccountWithSkillDto fromParticipationToAccountWithSkillDto(Participation participation);

    List<AccountWithSkillDto> fromParticipationListToAccountWithSkillDtoList(List<Participation> participationList);

    List<AccountWithSkillDto> fromAccountListToAccountWithSkillDtoList(List<Account> accountList);

}
