package restaurante.team3.Giacobello.payment_method.service;

import java.util.List;
import restaurante.team3.Giacobello.payment_method.dtos.PaymentMethodDTOResponse;

public interface PaymentMethodService {
    List<PaymentMethodDTOResponse> findAll();   
}
