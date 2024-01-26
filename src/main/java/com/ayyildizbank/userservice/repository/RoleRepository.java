package com.ayyildizbank.userservice.repository;

import com.ayyildizbank.userservice.auth.model.Role;
import com.ayyildizbank.userservice.auth.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
    boolean existsById(Long roleId);
}
