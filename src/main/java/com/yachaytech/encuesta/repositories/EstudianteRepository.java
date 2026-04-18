package com.yachaytech.encuesta.repositories;

import com.yachaytech.encuesta.models.Estudiante;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Estudiante findByCorreo(String correo);
    
    Estudiante findByCorreoAndPassword(String correo, String password);

    Optional<Estudiante> findById(Integer id);
}