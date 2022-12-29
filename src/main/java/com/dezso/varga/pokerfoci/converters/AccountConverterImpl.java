package com.dezso.varga.pokerfoci.converters;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.RoleEnum;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountConverterImpl implements AccountConverter {
    @Override
    public AccountForAdminDto fromAccountToAccountForAdminDto(Account account) {
        AccountForAdminDto accountForAdminDto =
                AccountForAdminDto.builder()
                        .username(account.getUsername())
                        .email(account.getEmail())
                        .isActive(account.isActive()).build();
        accountForAdminDto.setAdmin(account.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ROLE_ADMIN.name())));
        return accountForAdminDto;
    }

    @Override
    public List<AccountForAdminDto> fromAccountListToAccountForAdminDtoList(List<Account> accountList) {
        return accountList.stream().map(account -> fromAccountToAccountForAdminDto(account)).collect(Collectors.toList());
    }
}
