package rostyk.stupnytskiy.andromeda.service.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rostyk.stupnytskiy.andromeda.dto.request.account.AccountDataRequest;
import rostyk.stupnytskiy.andromeda.dto.request.account.AccountLoginRequest;
import rostyk.stupnytskiy.andromeda.dto.response.account.AccountResponse;
import rostyk.stupnytskiy.andromeda.dto.response.AuthenticationResponse;
import rostyk.stupnytskiy.andromeda.entity.account.seller_account.goods_seller.GoodsSellerSettings;
import rostyk.stupnytskiy.andromeda.entity.statistics.account.goods_seller.GoodsSellerStatistics;
import rostyk.stupnytskiy.andromeda.entity.account.user_account.UserSettings;
import rostyk.stupnytskiy.andromeda.entity.cart.Cart;
import rostyk.stupnytskiy.andromeda.entity.account.Account;
import rostyk.stupnytskiy.andromeda.entity.account.seller_account.goods_seller.GoodsSellerAccount;
import rostyk.stupnytskiy.andromeda.entity.account.user_account.UserAccount;
import rostyk.stupnytskiy.andromeda.entity.statistics.account.user.UserStatistics;
import rostyk.stupnytskiy.andromeda.mail.MailService;
import rostyk.stupnytskiy.andromeda.repository.account.AccountRepository;
import rostyk.stupnytskiy.andromeda.security.JwtTokenTool;
import rostyk.stupnytskiy.andromeda.security.JwtUser;
import rostyk.stupnytskiy.andromeda.service.CountryService;
import rostyk.stupnytskiy.andromeda.service.account.seller.markup.GoodsShopMarkupService;
import rostyk.stupnytskiy.andromeda.service.statistics.account.goods_seller.GoodsSellerStatisticsService;
import rostyk.stupnytskiy.andromeda.service.statistics.account.user.UserStatisticsService;
import rostyk.stupnytskiy.andromeda.tools.ConfirmationCodeGenerator;
import rostyk.stupnytskiy.andromeda.tools.FileTool;

import java.io.IOException;


@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenTool jwtTokenTool;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private CountryService countryService;

    @Autowired
    private FileTool fileTool;

    @Autowired
    private ConfirmationCodeGenerator confirmationCodeGenerator;

    @Autowired
    private MailService mailService;

    @Autowired
    private GoodsSellerStatisticsService goodsSellerStatisticsService;

    @Autowired
    private UserStatisticsService userStatisticsService;

    @Autowired
    private GoodsShopMarkupService goodsShopMarkupService;


    public String testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getPrincipal() == "anonymousUser");
//        System.out.println(auth == null);
        return "a";
    }

    // Register User
    public AuthenticationResponse registerUser(AccountLoginRequest request) throws IOException {
        UserAccount userAccount = registerUserAccount(request);
        fileTool.createUserDir(userAccount.getId());
        userStatisticsService.createStartStatistics(userAccount);
        return login(request);
    }

    //Register Goods Seller
    public AuthenticationResponse registerGoodsSeller(AccountLoginRequest request) throws IOException {
        GoodsSellerAccount seller = registerGoodsSellerAccount(request);
        fileTool.createUserDir(seller.getId());
        goodsSellerStatisticsService.createStartStatistics(seller);
        goodsShopMarkupService.createDefaultMarkup(seller);
        return login(request);
    }

    public void updateAccountData(AccountDataRequest request) {
        Account account = getAccountBySecurityContextHolder();
        if (request.getCountryCode() != null)
            account.setCountry(countryService.findCountryByCountryCode(request.getCountryCode()));
        else if (request.getCountryId() != null)
            account.setCountry(countryService.findCountryById(request.getCountryId()));
        accountRepository.save(account);
    }


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Account account = findByLogin(login);
        return new JwtUser(account.getLogin(), account.getUserRole(), account.getPassword());
    }

    public Account findByLogin(String login) {
        return accountRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User with login " + login + " not exists"));
    }

    public Account findById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User with id " + id + " not exists"));
    }

    public Account getAccountBySecurityContextHolder() {
        return findByLogin((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    public Long getIdBySecurityContextHolder() {
        return findByLogin((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public AuthenticationResponse login(AccountLoginRequest request) {
        String login = request.getLogin();
        Account account = findByLogin(login);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, request.getPassword()));
        String token = jwtTokenTool.createToken(login, account.getUserRole());

        String name = "ВИПРАВИТИ";
        Long id = account.getId();


        return new AuthenticationResponse(token, id, account.getUserRole());
    }


    private GoodsSellerAccount registerGoodsSellerAccount(AccountLoginRequest request) {
        if (accountRepository.existsByLogin(request.getLogin())) {
            throw new BadCredentialsException("User with username " + request.getLogin() + " already exists");
        }

        GoodsSellerAccount goodsSellerAccount = new GoodsSellerAccount();
        goodsSellerAccount.setLogin(request.getLogin());
        goodsSellerAccount.setPassword(encoder.encode(request.getPassword()));
        goodsSellerAccount.setStatistics(new GoodsSellerStatistics());
        goodsSellerAccount.setSettings(new GoodsSellerSettings());

        return accountRepository.save(goodsSellerAccount);
    }

    private UserAccount registerUserAccount(AccountLoginRequest request) {

        if (accountRepository.existsByLogin(request.getLogin())) {
            throw new BadCredentialsException("User with username " + request.getLogin() + " already exists");
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setLogin(request.getLogin());
        userAccount.setPassword(encoder.encode(request.getPassword()));
        userAccount.setCart(new Cart());
        userAccount.setSettings(new UserSettings());
        userAccount.setUserStatistics(new UserStatistics());
        return accountRepository.save(userAccount);
    }

    public <T extends AccountResponse> AccountResponse getAccountResponse(Long id) {
        if (id == null) {
            return getAccountBySecurityContextHolder().mapToResponse();
        } else {
            return findById(id).mapToResponse();
        }
    }

    public void save(UserAccount userAccount) {
        accountRepository.save(userAccount);
    }
}
