package rostyk.stupnytskiy.andromeda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rostyk.stupnytskiy.andromeda.dto.request.account.AccountLoginRequest;
import rostyk.stupnytskiy.andromeda.dto.request.account.AccountRegistrationRequest;
import rostyk.stupnytskiy.andromeda.dto.response.AuthenticationResponse;
import rostyk.stupnytskiy.andromeda.service.AccountService;

import javax.validation.Valid;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody AccountLoginRequest request) {
        return accountService.login(request);
    }

    @PostMapping("/register-user")
    public AuthenticationResponse registerUser(@Valid @RequestBody AccountRegistrationRequest request) throws IOException {
        return accountService.registerUser(request);
    }

    @PostMapping("/register-seller")
    public AuthenticationResponse registerSeller(@Valid @RequestBody AccountRegistrationRequest request) throws IOException {
        return accountService.registerSeller(request);
    }

    @GetMapping("/checkToken")
    public void checkToken() {
    }
}












