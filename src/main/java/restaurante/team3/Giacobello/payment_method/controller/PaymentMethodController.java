package restaurante.team3.Giacobello.payment_method.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import restaurante.team3.Giacobello.payment_method.dtos.PaymentMethodDTOResponse;
import restaurante.team3.Giacobello.payment_method.service.PaymentMethodService;

@RestController
@RequestMapping("${api-endpoint}/paymentmethod")  
public class PaymentMethodController {
    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @GetMapping 
    public ResponseEntity<List<PaymentMethodDTOResponse>> findAll() {
        return ResponseEntity.ok(paymentMethodService.findAll());
    }
}
