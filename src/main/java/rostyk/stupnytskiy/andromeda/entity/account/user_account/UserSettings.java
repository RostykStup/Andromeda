package rostyk.stupnytskiy.andromeda.entity.account.user_account;

import lombok.*;
import rostyk.stupnytskiy.andromeda.entity.country.Country;
import rostyk.stupnytskiy.andromeda.entity.country.Currency;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Country country;

    @ManyToOne
    private Currency currency;

    @OneToOne(mappedBy = "settings")
    private UserAccount user;
}