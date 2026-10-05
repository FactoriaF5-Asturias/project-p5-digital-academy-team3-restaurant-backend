package restaurante.team3.giacobello.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import restaurante.team3.giacobello.auth.entity.UserDetailsEntity;

public interface UserDetailsRepository extends JpaRepository<UserDetailsEntity, Integer> {
    @EntityGraph(attributePaths = "role")
    Optional<UserDetailsEntity> findByUserId(Integer userId);
}