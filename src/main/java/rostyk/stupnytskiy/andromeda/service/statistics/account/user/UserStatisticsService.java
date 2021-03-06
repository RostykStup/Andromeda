package rostyk.stupnytskiy.andromeda.service.statistics.account.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rostyk.stupnytskiy.andromeda.dto.request.PaginationRequest;
import rostyk.stupnytskiy.andromeda.dto.response.PageResponse;
import rostyk.stupnytskiy.andromeda.dto.response.statistics.adviertisement_views.UserAdvertisementViewResponse;
import rostyk.stupnytskiy.andromeda.dto.response.statistics.adviertisement_views.UserAdvertisementsViewsResponse;
import rostyk.stupnytskiy.andromeda.entity.Category;
import rostyk.stupnytskiy.andromeda.entity.account.user_account.UserAccount;
import rostyk.stupnytskiy.andromeda.entity.advertisement.goods_advertisement.GoodsAdvertisement;
import rostyk.stupnytskiy.andromeda.entity.statistics.account.user.UserAdvertisementView;
import rostyk.stupnytskiy.andromeda.entity.statistics.account.user.UserMonthStatistics;
import rostyk.stupnytskiy.andromeda.entity.statistics.account.user.UserStatistics;
import rostyk.stupnytskiy.andromeda.repository.statistics.advertisement.UserAdvertisementViewRepository;
import rostyk.stupnytskiy.andromeda.repository.statistics.account.UserMonthStatisticsRepository;
import rostyk.stupnytskiy.andromeda.repository.statistics.account.UserStatisticsRepository;
import rostyk.stupnytskiy.andromeda.service.account.UserAccountService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserStatisticsService {

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    @Autowired
    private UserMonthStatisticsRepository userMonthStatisticsRepository;

    @Autowired
    private UserAdvertisementViewRepository userAdvertisementViewRepository;

    @Autowired
    private UserAccountService userAccountService;


    public void createStartStatistics(UserAccount account) {
        UserStatistics statistics = account.getUserStatistics();
        saveForNewMonthStatistics(statistics);
    }

    public UserMonthStatistics saveForNewMonthStatistics(UserStatistics userStatistics) {
        UserMonthStatistics statistics = new UserMonthStatistics();
        statistics.setUserStatistics(userStatistics);
        return userMonthStatisticsRepository.save(statistics);
    }

    public UserMonthStatistics getByUserAndMonth(Month month, Integer year) {
        UserAccount userAccount = userAccountService.findBySecurityContextHolder();
//        return userMonthStatisticsRepository.findOneByUserStatisticsUserAndMonthAndYear(userAccount, month, year).orElseThrow(IllegalArgumentException::new);
        return userMonthStatisticsRepository.findOneByUserStatisticsAndMonthAndYear(userAccount.getUserStatistics(), month, year).orElseThrow(IllegalArgumentException::new);
    }

    public UserMonthStatistics getMonthStatisticsForUserByCurrentMonth() {
        try {
            Month month = LocalDateTime.now().getMonth();
            Integer year = LocalDateTime.now().getYear();

            return getByUserAndMonth(month, year);
        } catch (IllegalArgumentException e) {
            UserAccount user = userAccountService.findBySecurityContextHolder();
            return saveForNewMonthStatistics(user.getUserStatistics());
        }
    }


    public void saveUserAdvertisementViewRequest(GoodsAdvertisement advertisement) {
        UserAdvertisementView view = new UserAdvertisementView();
        view.setGoodsAdvertisement(advertisement);
        UserAccount user = userAccountService.findBySecurityContextHolder();
        if (user != null) {
            if (auditIfNeedToSaveInStatistics(user, advertisement))
                view.setMonthStatistics(getMonthStatisticsForUserByCurrentMonth());
        }
        userAdvertisementViewRepository.save(view);

    }

    public UserAdvertisementView findTopByMonthStatisticsUserStatisticsOrderByIdDesc(UserAccount user) {
        try {
            return userAdvertisementViewRepository.findTopByMonthStatisticsUserStatisticsOrderByIdDesc(user.getUserStatistics()).orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public Boolean auditIfNeedToSaveInStatistics(UserAccount user, GoodsAdvertisement advertisement) {
        UserAdvertisementView view = findTopByMonthStatisticsUserStatisticsOrderByIdDesc(user);
        if (view == null) return false;
        else return !view.getGoodsAdvertisement().getId().equals(advertisement.getId()) ||
                !view.getDateTime().plusHours(1L).isAfter(LocalDateTime.now());
    }


    public UserAdvertisementsViewsResponse getViews(LocalDate startDate, LocalDate endDate, PaginationRequest request) {

        UserAccount userAccount = userAccountService.findBySecurityContextHolder();

        final Page<UserAdvertisementView> page;

        if (startDate != null && endDate != null) {
            page = userAdvertisementViewRepository
                    .findAllByMonthStatisticsUserStatisticsAndDateTimeBetweenOrderByDateTimeDesc(userAccount.getUserStatistics(),
                            LocalDateTime.of(startDate, LocalTime.of(0, 0)),
                            LocalDateTime.of(endDate, LocalTime.of(0, 0)),
                            request.mapToPageable());
        } else if (startDate != null) {
            page = userAdvertisementViewRepository
                    .findAllByMonthStatisticsUserStatisticsAndDateTimeBetweenOrderByDateTimeDesc(userAccount.getUserStatistics(),
                            LocalDateTime.of(startDate, LocalTime.of(0, 0)),
                            LocalDateTime.of(startDate, LocalTime.of(0, 0)).plusDays(1L),
                            request.mapToPageable());
        } else {
            page = userAdvertisementViewRepository.findAllByMonthStatisticsUserStatisticsOrderByDateTimeDesc(userAccount.getUserStatistics(), request.mapToPageable());
        }

        return new UserAdvertisementsViewsResponse(new PageResponse<>(
                page.get()
                        .map(UserAdvertisementViewResponse::new)
                        .collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages()
        ));
    }

    public Category defineMostCommonCategoryOfLastTwentyViews() {
        UserAccount userAccount = userAccountService.findBySecurityContextHolder();
        Page<UserAdvertisementView> page = userAdvertisementViewRepository.findPageByMonthStatisticsUserStatistics(
                userAccount.getUserStatistics(),
                PageRequest.of(0, 20, Sort.Direction.DESC, "dateTime")
        );

        if (page.getTotalElements() == 0) return null;

        Map<Category, Integer> categoryMap = new HashMap<>();
        page.get().forEach((a) -> {
            Category category = a.getGoodsAdvertisement().getSubcategory().getCategory();
            if (!categoryMap.containsKey(category)) {
                categoryMap.put(category, 1);
            } else {
                categoryMap.replace(category, categoryMap.get(category) + 1);
            }
        });

        Integer max = (Collections.max(categoryMap.values()));

        for (Map.Entry<Category, Integer> entry : categoryMap.entrySet()) {
            if (entry.getValue().equals(max)) {
                return entry.getKey();
            }
        }

        return null;
    }
}
