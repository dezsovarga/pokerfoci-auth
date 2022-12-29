package integration;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Role;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class AdminControllerTest extends BaseControllerTest {

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Test
    public void getListOfAccountsForAdminPage() throws Exception {
        Account account = aTestAccount();
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( "email@varga.com","password");
        ResponseEntity<String> response = apiWrapper.getAccountsForAdmin(port, bearerToken);

        List<AccountForAdminDto> accountForAdminDtoList =
                mapper.readValue(response.getBody(), new TypeReference<>(){});

        assertFalse(accountForAdminDtoList.isEmpty());
        assertEquals(account.getEmail(), accountForAdminDtoList.get(0).getEmail());
        assertEquals(account.getUsername(), accountForAdminDtoList.get(0).getUsername());
        assertEquals(account.isActive(), accountForAdminDtoList.get(0).isActive());
        assertTrue(accountForAdminDtoList.get(0).isAdmin());
    }

    private Account aTestAccount() {
        return new Account(1L,
                "username",
                "firstname",
                "lastName",
                "email@varga.com",
                passwordEncoder.encode("password"),
                true,
                Set.of(new Role( "ROLE_ADMIN")));
    }
}
