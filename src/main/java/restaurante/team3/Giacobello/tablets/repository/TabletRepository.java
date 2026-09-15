package restaurante.team3.Giacobello.tablets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import restaurante.team3.Giacobello.tablets.entity.TabletEntity;

public interface TabletRepository extends JpaRepository<TabletEntity, Integer> {
}