package restaurante.team3.giacobello.tablets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import restaurante.team3.giacobello.tablets.entity.TabletEntity;

public interface TabletRepository extends JpaRepository<TabletEntity, Integer> {
}