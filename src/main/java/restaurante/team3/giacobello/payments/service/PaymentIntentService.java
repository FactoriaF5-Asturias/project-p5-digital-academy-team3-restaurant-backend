package restaurante.team3.giacobello.payments.service;

import restaurante.team3.giacobello.payments.dto.PaymentIntentCreateDTORequest;
import restaurante.team3.giacobello.payments.dto.PaymentIntentDTOResponse;

public interface PaymentIntentService {

    PaymentIntentDTOResponse createIntent(PaymentIntentCreateDTORequest request);
}
