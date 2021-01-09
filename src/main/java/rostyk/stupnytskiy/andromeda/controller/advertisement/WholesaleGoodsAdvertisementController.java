package rostyk.stupnytskiy.andromeda.controller.advertisement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rostyk.stupnytskiy.andromeda.dto.request.advertisement.goods_advertisement.retail.RetailPriceRequest;
import rostyk.stupnytskiy.andromeda.dto.request.advertisement.goods_advertisement.wholesale.WholesaleGoodsAdvertisementRequest;
import rostyk.stupnytskiy.andromeda.dto.request.advertisement.goods_advertisement.wholesale.WholesalePriceRequest;
import rostyk.stupnytskiy.andromeda.service.advertisement.goods_advertisement.wholesale.WholesaleGoodsAdvertisementService;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/wholesale-goods")
public class WholesaleGoodsAdvertisementController {

    @Autowired
    private WholesaleGoodsAdvertisementService wholesaleGoodsAdvertisementService;

    @PutMapping("/change-price")
    private void changeAdvertisementPrice(@RequestBody WholesalePriceRequest request, Long id) throws IllegalAccessException {
        wholesaleGoodsAdvertisementService.addNewWholesalePriceToWholesaleGoodsAdvertisement(request, id);
    }
}