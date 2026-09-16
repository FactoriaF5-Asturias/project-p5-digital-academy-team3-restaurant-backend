package restaurante.team3.Giacobello.tablets.service;

import java.util.List;

import restaurante.team3.Giacobello.tablets.dtos.TabletDTOResponse;

public interface TabletService {
    List<TabletDTOResponse> findAll();
    TabletDTOResponse findById(Integer id);
}
