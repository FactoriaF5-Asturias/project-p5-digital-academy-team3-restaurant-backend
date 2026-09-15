package restaurante.team3.Giacobello.payment_method.mappers;

import org.mapstruct.Mapper;

import restaurante.team3.Giacobello.payment_method.dtos.PaymentMethodDTOResponse;
import restaurante.team3.Giacobello.payment_method.entity.PaymentMethodEntity;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {
    PaymentMethodDTOResponse toResponse(PaymentMethodEntity paymentMethod);
}
