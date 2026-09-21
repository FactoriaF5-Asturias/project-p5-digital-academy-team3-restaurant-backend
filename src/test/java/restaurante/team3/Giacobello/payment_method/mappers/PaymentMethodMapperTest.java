/* package restaurante.team3.Giacobello.payment_method.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import restaurante.team3.Giacobello.payment_method.dtos.PaymentMethodDTOResponse;
import restaurante.team3.Giacobello.payment_method.entity.PaymentMethodEntity;

class PaymentMethodMapperTest {

    private final PaymentMethodMapper mapper = Mappers.getMapper(PaymentMethodMapper.class);

    @Test
    void shouldMapEntityToResponse() {
        PaymentMethodEntity entity = new PaymentMethodEntity(7, "Tarjeta");

        PaymentMethodDTOResponse response = mapper.toResponse(entity);

        assertEquals(7, response.id());
        assertEquals("Tarjeta", response.name());
    }
}
 */