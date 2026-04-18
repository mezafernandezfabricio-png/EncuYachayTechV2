package com.yachaytech.simulador.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yachaytech.simulador.models.Administrador;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    Administrador findByCorreoAndPassword(String correo, String password);
    List<Administrador> findByRol(String rol);
}