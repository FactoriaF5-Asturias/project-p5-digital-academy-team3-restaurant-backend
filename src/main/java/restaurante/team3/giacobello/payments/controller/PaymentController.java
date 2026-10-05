package restaurante.team3.giacobello.payments.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import restaurante.team3.giacobello.payments.dto.PaymentIntentCreateDTORequest;
import restaurante.team3.giacobello.payments.dto.PaymentIntentDTOResponse;
import restaurante.team3.giacobello.payments.service.PaymentIntentService;

@RestController
@RequestMapping("${api-endpoint}/payments")
public class PaymentController {

    private final PaymentIntentService paymentIntentService;

    public PaymentController(PaymentIntentService paymentIntentService) {
        this.paymentIntentService = paymentIntentService;
    }

    @PostMapping("/create-intent")
    public ResponseEntity<PaymentIntentDTOResponse> createIntent(
            @Valid @RequestBody PaymentIntentCreateDTORequest request) {
        return ResponseEntity.ok(paymentIntentService.createIntent(request));
    }
}
