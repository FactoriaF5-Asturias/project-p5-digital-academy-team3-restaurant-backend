package restaurante.team3.giacobello.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users_details")
public class UserDetailsEntity {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, unique = true, length = 60)
    private String nickname;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private UserRoleEntity role;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UserDetailsEntity() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UserRoleEntity getRole() {
        return role;
    }

    public void setRole(UserRoleEntity role) {
        this.role = role;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    
}
