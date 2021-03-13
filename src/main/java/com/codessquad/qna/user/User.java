package com.codessquad.qna.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Entity
public class User {
    public static final String LOGIN_FAIL_MESSAGE = "아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public static List<User> getDummyData() {
        return Arrays.asList(
                new User(null, "javajigi", "1234", "자바지기", "javajigi@sample.net"),
                new User(null, "slipp", "1234", "슬립", "slipp@sample.net"),
                new User(null, "mskim", "1234", "김문수", "mskim@sample.net"),
                new User(null, "test", "test", "test", "test@test.net")
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void update(UserDTO newUser) {
        name = newUser.getName();
        password = newUser.hasNewPassword() ? newUser.getNewPassword() : password;
        email = newUser.getEmail();
    }

    public void checkPassword(String password) {
        if (!this.password.equals(password)) {
            throw HttpClientErrorException.create(
                    LOGIN_FAIL_MESSAGE,
                    HttpStatus.UNAUTHORIZED,
                    "",
                    null,
                    null,
                    StandardCharsets.UTF_8
            );
        }
    }

    public boolean isIdSameAs(Long id) {
        return this.id == id;
    }

    public UserDTO toDTO() {
        return new UserDTO(id, userId, password, name, email);
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
