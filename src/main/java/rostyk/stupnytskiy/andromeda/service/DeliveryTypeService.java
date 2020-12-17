package rostyk.stupnytskiy.andromeda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rostyk.stupnytskiy.andromeda.dto.request.delivery.DeliveryTypeRequest;
import rostyk.stupnytskiy.andromeda.dto.response.DeliveryTypeResponse;
import rostyk.stupnytskiy.andromeda.entity.DeliveryType;
import rostyk.stupnytskiy.andromeda.repository.DeliveryTypeRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeliveryTypeService {

    @Autowired
    private DeliveryTypeRepository deliveryTypeRepository;

    @Autowired
    private CountryService countryService;


    public void save(DeliveryTypeRequest request){
        deliveryTypeRepository.save(deliveryRequestToDeliveryType(request,null));
    }

    public void update(DeliveryTypeRequest request, Long id){
        deliveryTypeRepository.save(deliveryRequestToDeliveryType(request, findById(id)));
    }

    private DeliveryType deliveryRequestToDeliveryType(DeliveryTypeRequest request, DeliveryType deliveryType){
        if (deliveryType == null) deliveryType = new DeliveryType();
        deliveryType.setInternational(request.getInternational());
        deliveryType.setTitle(request.getTitle());
        if (request.getCountryId() != null) deliveryType.setCountry(countryService.findCountryById(request.getCountryId()));
        else deliveryType.setCountry(countryService.findCountryByCountryCode(request.getCountryCode()));
        return deliveryType;
    }

    public DeliveryType findById(Long id){
        return deliveryTypeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Delivery with Id " + id + " doesn't exist in database"));
    }

    public Set<DeliveryType> getDeliverySetByIds(List<Long> ids){
        Set<DeliveryType> deliveryTypes = new HashSet<>();
        ids.forEach((id) -> deliveryTypes.add(findById(id)));
        return deliveryTypes;
    }

    public List<DeliveryTypeResponse> getAll() {
        return deliveryTypeRepository.findAll()
                .stream()
                .map(DeliveryTypeResponse::new)
                .collect(Collectors.toList());
    }
}
