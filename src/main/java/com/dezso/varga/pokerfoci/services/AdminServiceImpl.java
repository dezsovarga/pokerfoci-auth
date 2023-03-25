package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.converters.AccountConverter;
import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AddNewAccountDto;
import com.dezso.varga.pokerfoci.exeptions.BgException;
import com.dezso.varga.pokerfoci.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private AccountRepository accountRepository;
    private AccountConverter accountConverter;

    @Override
    public List<AccountForAdminDto> listAccounts() {
        List<Account> allAccounts = accountRepository.findAll();
        return accountConverter.fromAccountListToAccountForAdminDtoList(allAccounts);
    }

    @Override
    public AccountForAdminDto addNewAccount(AddNewAccountDto newAccountDtoRequest) {
        //TODO: add validation
        Account account = accountConverter.fromAddNewAccountDtoToAccount(newAccountDtoRequest);
        Account savedAccount = accountRepository.save(account);
        return accountConverter.fromAccountToAccountForAdminDto(savedAccount);
    }

    @Override
    public AccountForAdminDto updateAccount(AccountForAdminDto updateAccountDtoRequest) throws Exception{
        if (updateAccountDtoRequest.getId() == null) {
            throw new BgException("Account id cannot be null for update account", HttpStatus.PRECONDITION_FAILED.value());
        }
        Optional<Account> existingAccount = accountRepository.findById(updateAccountDtoRequest.getId());
        if (existingAccount.isEmpty()) {
            throw new BgException("Invalid account for update", HttpStatus.PRECONDITION_FAILED.value());
        }
        Account updatedAccount = accountConverter.fromUpdateAccountDtoToAccount(updateAccountDtoRequest, existingAccount.get());
        updatedAccount = accountRepository.save(updatedAccount);
        return accountConverter.fromAccountToAccountForAdminDto(updatedAccount);
    }
}
