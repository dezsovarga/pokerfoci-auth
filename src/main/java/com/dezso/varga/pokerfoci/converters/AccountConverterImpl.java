package com.dezso.varga.pokerfoci.converters;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Role;
import com.dezso.varga.pokerfoci.domain.RoleEnum;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AddNewAccountDto;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class AccountConverterImpl implements AccountConverter {

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public AccountForAdminDto fromAccountToAccountForAdminDto(Account account) {
        AccountForAdminDto accountForAdminDto =
                AccountForAdminDto.builder()
                        .id(account.getId())
                        .username(account.getUsername())
                        .email(account.getEmail())
                        .isActive(account.isActive())
                        .skill(account.getSkill())
                        .build();
        accountForAdminDto.setIsAdmin(account.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ROLE_ADMIN.name())));
        return accountForAdminDto;
    }

    @Override
    public List<AccountForAdminDto> fromAccountListToAccountForAdminDtoList(List<Account> accountList) {
        return accountList.stream().map(account -> fromAccountToAccountForAdminDto(account)).collect(Collectors.toList());
    }

    @Override
    public Account fromAddNewAccountDtoToAccount(AddNewAccountDto addNewAccountDto) {
        return Account.builder()
                .username(addNewAccountDto.getUsername())
                .email(addNewAccountDto.getEmail())
                .password(bCryptPasswordEncoder.encode(addNewAccountDto.getPassword()))
                .skill(addNewAccountDto.getSkill())
                .active(true)
                .roles(Stream.of(new Role(RoleEnum.ROLE_USER.name())).collect(Collectors.toCollection(HashSet::new)))
                .build();
    }

    @Override
    public Account fromUpdateAccountDtoToAccount(AccountForAdminDto updateAccountDto, Account account) {
        Optional<Role> adminRole = account.getRoles().stream().filter(role -> role.getName().equals(RoleEnum.ROLE_ADMIN.name())).findFirst();
        if (updateAccountDto.getIsAdmin() != null) {
            if (updateAccountDto.getIsAdmin() && adminRole.isEmpty()) {
                account.getRoles().add(new Role(RoleEnum.ROLE_ADMIN.name()));
            }
            if (!updateAccountDto.getIsAdmin() && adminRole.isPresent()) {
                account.getRoles().remove(adminRole.get());
            }
        }
        return account;
    }
}
