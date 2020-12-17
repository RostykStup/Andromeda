package rostyk.stupnytskiy.andromeda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rostyk.stupnytskiy.andromeda.entity.advertisement.WholesalePrice;
import rostyk.stupnytskiy.andromeda.entity.statistics.AccountStatistics;

@Repository
public interface AccountStatisticsRepository extends JpaRepository<AccountStatistics, Long> {

}
