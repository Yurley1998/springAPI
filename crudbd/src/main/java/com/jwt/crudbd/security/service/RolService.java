package com.jwt.crudbd.security.service;

import com.jwt.crudbd.security.repository.RolRepository;
import com.jwt.crudbd.security.entity.Rol;
import com.jwt.crudbd.security.enums.RolNombre;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class RolService {
    @Autowired
    RolRepository rolRepository;
    public Optional<Rol> getByRolNombre(RolNombre rolNombre){
        return rolRepository.findByRolNombre(rolNombre);
    }

    public void save(Rol rol){
        rolRepository.save(rol);
    }
}
