package com.dezso.varga.pokerfoci.controller;

import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AddNewAccountDto;
import com.dezso.varga.pokerfoci.services.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin")
@AllArgsConstructor
@Secured( "ROLE_ADMIN" )
public class AdminController {

    private AdminService adminService;

    @GetMapping("/accounts")
    public List<AccountForAdminDto> listAccounts() {
        return adminService.listAccounts();
    }

    @PostMapping("/account")
    public AccountForAdminDto addAccount(@RequestBody AddNewAccountDto newAccountDtoRequest) {
        return adminService.addNewAccount(newAccountDtoRequest);
    }

    @PutMapping("/account")
    public AccountForAdminDto updateAccount(@RequestBody AccountForAdminDto updateAccountDtoRequest) throws Exception{

        return adminService.updateAccount(updateAccountDtoRequest);
    }
}
