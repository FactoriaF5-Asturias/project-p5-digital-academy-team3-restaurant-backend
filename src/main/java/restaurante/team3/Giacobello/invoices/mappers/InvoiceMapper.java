package restaurante.team3.giacobello.invoices.mappers;

import org.mapstruct.Mapper;

import restaurante.team3.giacobello.invoices.dto.InvoiceDTOResponse;
import restaurante.team3.giacobello.invoices.entity.InvoiceEntity;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    InvoiceDTOResponse toResponse(InvoiceEntity invoice);
}
