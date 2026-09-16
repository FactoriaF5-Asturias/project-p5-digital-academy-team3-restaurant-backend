package restaurante.team3.Giacobello.tablets.mappers;

import org.mapstruct.Mapper;

import restaurante.team3.Giacobello.tablets.dtos.TabletDTOResponse;
import restaurante.team3.Giacobello.tablets.entity.TabletEntity;

@Mapper (componentModel= "spring" )
public interface TabletMapper {
    TabletDTOResponse toResponse(TabletEntity tablet);   
}
