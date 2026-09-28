package restaurante.team3.giacobello.payment_method.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import restaurante.team3.giacobello.payment_method.dtos.PaymentMethodDTOResponse;
import restaurante.team3.giacobello.payment_method.mappers.PaymentMethodMapper;
import restaurante.team3.giacobello.payment_method.repository.PaymentMethodRepository;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    public PaymentMethodServiceImpl(
            PaymentMethodRepository paymentMethodRepository,
            PaymentMethodMapper paymentMethodMapper) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentMethodMapper = paymentMethodMapper;
    }

    @Override 
    @Transactional(readOnly = true)
    public List<PaymentMethodDTOResponse> findAll() {
        return paymentMethodRepository.findAll(Sort.by("name").ascending())
            .stream()
            .map(paymentMethodMapper::toResponse)
            .toList();
    }
}
