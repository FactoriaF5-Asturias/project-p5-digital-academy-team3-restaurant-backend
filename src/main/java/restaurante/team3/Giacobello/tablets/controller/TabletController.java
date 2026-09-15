package restaurante.team3.Giacobello.tablets.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import restaurante.team3.Giacobello.tablets.dtos.TabletDTOResponse;
import restaurante.team3.Giacobello.tablets.service.TabletService;

@RestController 
@RequestMapping("${api-endpoint}/tablets")
public class TabletController {
    private final TabletService tabletService;

    public TabletController(TabletService tabletService) {
        this.tabletService = tabletService;
    }

    @GetMapping 
    public ResponseEntity<List<TabletDTOResponse>> findAll() {
        return ResponseEntity.ok(tabletService.findAll());
    }  
    
    @GetMapping ("/{id}")
    public ResponseEntity<TabletDTOResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(tabletService.findById(id));
    }
}
