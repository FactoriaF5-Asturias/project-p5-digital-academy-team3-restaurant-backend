package restaurante.team3.giacobello.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import restaurante.team3.giacobello.auth.entity.UserAuthEntity;

public interface UserAuthRepository extends JpaRepository<UserAuthEntity, Integer> {
    Optional<UserAuthEntity> findByUsername(String username);
}