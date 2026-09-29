package restaurante.team3.giacobello.tablets.service;

import java.util.List;

import restaurante.team3.giacobello.tablets.dtos.TabletDTOResponse;

public interface TabletService {
    List<TabletDTOResponse> findAll();
    TabletDTOResponse findById(Integer id);
}
