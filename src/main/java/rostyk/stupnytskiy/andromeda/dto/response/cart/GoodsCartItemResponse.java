package rostyk.stupnytskiy.andromeda.dto.response.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import rostyk.stupnytskiy.andromeda.dto.response.DeliveryTypeResponse;
import rostyk.stupnytskiy.andromeda.dto.response.advertisement.goods_advertisement.parameter.ParametersValuesPriceCountResponse;
import rostyk.stupnytskiy.andromeda.entity.DeliveryType;
import rostyk.stupnytskiy.andromeda.entity.advertisement.goods_advertisement.GoodsAdvertisement;
import rostyk.stupnytskiy.andromeda.entity.cart.goods_cart_item.GoodsCartItem;

import java.time.LocalDateTime;

@Getter
@Setter
public class GoodsCartItemResponse {

    private Long id;
    private String image;
    private String title;
    private DeliveryTypeResponse deliveryType;
    private Integer count;
    private Long sellerId;
    private Long advertisementId;
    private ParametersValuesPriceCountResponse priceCountResponse;
    @JsonIgnore
    private LocalDateTime date;


    public GoodsCartItemResponse(GoodsAdvertisement advertisement, DeliveryType deliveryType,  Integer count){
        this.sellerId = advertisement.getSeller().getId();
        this.title = advertisement.getTitle();
        this.count = count;
        this.image = advertisement.getMainImage();
        this.advertisementId = advertisement.getId();
        this.date = LocalDateTime.now();
        this.deliveryType = new DeliveryTypeResponse(deliveryType);
    }

    public GoodsCartItemResponse(GoodsCartItem item){
        this.id = item.getId();
        this.sellerId = item.getGoodsAdvertisement().getSeller().getId();
        this.title = item.getGoodsAdvertisement().getTitle();
        this.deliveryType = new DeliveryTypeResponse(item.getDeliveryType());
        this.count = item.getCount();
        this.image = item.getGoodsAdvertisement().getMainImage();
        this.advertisementId = item.getGoodsAdvertisement().getId();
        this.date = item.getDate();
        this.priceCountResponse = new ParametersValuesPriceCountResponse(item.getValuesPriceCount());
    }
}
