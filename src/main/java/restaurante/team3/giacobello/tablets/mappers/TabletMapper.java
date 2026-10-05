package restaurante.team3.giacobello.tablets.mappers;

import org.mapstruct.Mapper;

import restaurante.team3.giacobello.tablets.dtos.TabletDTOResponse;
import restaurante.team3.giacobello.tablets.entity.TabletEntity;

@Mapper (componentModel= "spring" )
public interface TabletMapper {
    TabletDTOResponse toResponse(TabletEntity tablet);   
}
