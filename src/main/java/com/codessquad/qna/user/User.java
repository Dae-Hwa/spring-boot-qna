package com.codessquad.qna.user;

import com.codessquad.qna.common.BaseEntity;
import com.codessquad.qna.exception.InsufficientAuthenticationException;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class User extends BaseEntity {
    @Column(nullable = false, length = 20, unique = true)
    private String userId;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 40, unique = true)
    private String email;

    protected User() {
    }

    public User(Long id, String userId, String password, String name, String email) {
        super(id);

        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String userId;
        private String password;
        private String name;
        private String email;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public User build() {
            return new User(id, userId, password, name, email);
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void update(UserDTO newUser) {
        verifyWith(newUser.toEntity());

        name = newUser.getName();
        password = newUser.hasNewPassword() ? newUser.getNewPassword() : password;
        email = newUser.getEmail();
    }

    public void checkPassword(String password) {
        if (!this.password.equals(password)) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다. password : " + password);
        }
    }

    private void checkId(Long id) {
        if (!getId().equals(id)) {
            throw new IllegalArgumentException("잘못된 ID입니다. id : " + id);
        }
    }

    public void verifyWith(User target) {
        try {
            checkId(target.getId());
            checkPassword(target.getPassword());
        } catch (IllegalArgumentException e) {
            throw new InsufficientAuthenticationException("권한이 없는 사용자입니다.", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) && Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
