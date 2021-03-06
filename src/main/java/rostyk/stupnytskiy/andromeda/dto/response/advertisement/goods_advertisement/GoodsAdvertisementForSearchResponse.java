package rostyk.stupnytskiy.andromeda.dto.response.advertisement.goods_advertisement;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import rostyk.stupnytskiy.andromeda.dto.response.country.CurrencyResponse;
import rostyk.stupnytskiy.andromeda.entity.advertisement.goods_advertisement.GoodsAdvertisement;
import rostyk.stupnytskiy.andromeda.entity.advertisement.goods_advertisement.discount.Discount;
import rostyk.stupnytskiy.andromeda.entity.advertisement.goods_advertisement.parameters.ParametersValuesPriceCount;

import java.time.LocalDateTime;

@Getter
@Setter
public class GoodsAdvertisementForSearchResponse {

    private Long id;
    private String title;

    private String image;

    private String seller;
    private Double rating;
    private Long sellerId;

    private Double minPrice;
    private Double maxPrice;

    private Double minPriceWithDiscount;
    private Double maxPriceWithDiscount;

    private Boolean hasDiscount = false;

    private Long sold;

    private String currencyCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDateTime date;


    public GoodsAdvertisementForSearchResponse(GoodsAdvertisement advertisement) {
        this.id = advertisement.getId();
        this.title = advertisement.getTitle();
        this.image = advertisement.getMainImage();
        this.seller = advertisement.getSeller().getShopName();
        this.sellerId = advertisement.getSeller().getId();
        this.sold = advertisement.getStatistics().getSoldSum();
        this.date = advertisement.getStatistics().getCreationDate();

        this.maxPrice = advertisement.getMaxPrice();
        this.minPrice = advertisement.getMinPrice();


        this.hasDiscount = advertisement.hasDiscount();
        this.maxPriceWithDiscount = advertisement.getMaxPriceWithDiscounts();
        this.minPriceWithDiscount = advertisement.getMinPriceWithDiscounts();

        this.rating = advertisement.getStatistics().getRating();
    }
}