package com.yachaytech.encuesta.repositories;
import com.yachaytech.encuesta.models.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    Administrador findByCorreoAndPassword(String correo, String password);
}