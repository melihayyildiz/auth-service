package com.ayyildizbank.userservice.auth.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
    })
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private RoleName name;

}