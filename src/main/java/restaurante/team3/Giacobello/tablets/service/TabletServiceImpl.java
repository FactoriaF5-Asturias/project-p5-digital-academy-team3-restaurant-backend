package restaurante.team3.Giacobello.tablets.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import restaurante.team3.Giacobello.tablets.dtos.TabletDTOResponse;
import restaurante.team3.Giacobello.tablets.entity.TabletEntity;
import restaurante.team3.Giacobello.tablets.mappers.TabletMapper;
import restaurante.team3.Giacobello.tablets.repository.TabletRepository;

@Service 
public class TabletServiceImpl implements TabletService {
    private final TabletRepository tabletRepository;
    private final TabletMapper tabletMapper;

    public TabletServiceImpl(TabletRepository tabletRepository, TabletMapper tabletMapper) {
        this.tabletRepository = tabletRepository;
        this.tabletMapper = tabletMapper;
    }

    @Override 
    @Transactional(readOnly = true)
    public List<TabletDTOResponse> findAll() {
        return tabletRepository.findAll(Sort.by("name").ascending())
            .stream()
            .map(tabletMapper::toResponse)
            .toList();
    }

    @Override 
    @Transactional (readOnly = true)
    public TabletDTOResponse findById(Integer id) {
        TabletEntity tablet = tabletRepository.findById(id)
        .orElseThrow();

        return tabletMapper.toResponse(tablet);
            
    }
}
