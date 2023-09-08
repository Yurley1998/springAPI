package com.jwt.crudbd.security.repository;

import com.jwt.crudbd.security.entity.Rol;
import com.jwt.crudbd.security.enums.RolNombre;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Transactional
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByRolNombre(RolNombre rolNombre);
}
