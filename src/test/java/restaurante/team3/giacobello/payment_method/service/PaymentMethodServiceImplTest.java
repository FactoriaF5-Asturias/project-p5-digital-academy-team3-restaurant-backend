package restaurante.team3.giacobello.payment_method.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import restaurante.team3.giacobello.payment_method.dtos.PaymentMethodDTOResponse;
import restaurante.team3.giacobello.payment_method.entity.PaymentMethodEntity;
import restaurante.team3.giacobello.payment_method.mappers.PaymentMethodMapper;
import restaurante.team3.giacobello.payment_method.repository.PaymentMethodRepository;

class PaymentMethodServiceImplTest {

    private final PaymentMethodRepository repository = mock(PaymentMethodRepository.class);
    private final PaymentMethodMapper mapper = mock(PaymentMethodMapper.class);
    private final PaymentMethodServiceImpl service = new PaymentMethodServiceImpl(repository, mapper);

    @Test
    void findAllReturnsPaymentMethodsSortedByName() {
        PaymentMethodEntity card = new PaymentMethodEntity(2, "CARD");
        PaymentMethodDTOResponse dto = new PaymentMethodDTOResponse(2, "CARD");
        when(repository.findAll(Sort.by("name").ascending())).thenReturn(List.of(card));
        when(mapper.toResponse(card)).thenReturn(dto);

        List<PaymentMethodDTOResponse> result = service.findAll();

        assertEquals(List.of(dto), result);
    }
}
