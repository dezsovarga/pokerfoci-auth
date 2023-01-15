package com.dezso.varga.pokerfoci.controller;

import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AddNewAccountDto;
import com.dezso.varga.pokerfoci.services.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping("admin")
@AllArgsConstructor
@RolesAllowed("ROLE_ADMIN")
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
}
