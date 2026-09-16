package restaurante.team3.Giacobello.invoices.mappers;

import org.mapstruct.Mapper;

import restaurante.team3.Giacobello.invoices.dto.InvoiceDTOResponse;
import restaurante.team3.Giacobello.invoices.entity.InvoiceEntity;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    InvoiceDTOResponse toResponse(InvoiceEntity invoice);
}
