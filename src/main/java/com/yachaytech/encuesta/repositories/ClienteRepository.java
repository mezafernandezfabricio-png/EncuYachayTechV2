package com.yachaytech.encuesta.repositories;

import com.yachaytech.encuesta.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByCorreo(String correo);
    
    Cliente findByCorreoAndPassword(String correo, String password);
}