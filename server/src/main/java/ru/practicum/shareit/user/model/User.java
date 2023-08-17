package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@Entity
@Table(name = "USERS")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotEmpty
    @Column(name = "name")
    private String name;

    @NotEmpty(message = "{validation.email.NotEmpty.message}")
    @Column(name = "email", unique = true)
    private String email;
}