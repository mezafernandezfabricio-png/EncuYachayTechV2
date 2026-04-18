package com.yachaytech.simulador.controllers;

import com.yachaytech.simulador.models.Administrador;
import com.yachaytech.simulador.models.Estudiante;
import com.yachaytech.simulador.repositories.AdministradorRepository;
import com.yachaytech.simulador.repositories.EstudianteRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private AdministradorRepository adminRepository;

    private static final List<String> PALABRAS_OFENSIVAS = Arrays.asList(
            "puta", "puto", "mierda", "cabron", "cabròn", "pendejo", 
            "imbecil", "idiota", "estupido", "estùpido", "perra", 
            "maldito", "carajo", "verga", "cojudo", "zorra"
    );

    @GetMapping("/")
    public String mostrarLogin(HttpSession session, Model model) {
        if (session.getAttribute("adminLogueado") != null) {
            String rol = (String) session.getAttribute("rolAdmin");
            if ("ADMINISTRADOR".equals(rol)) {
                return "redirect:/admin/panel";
            } else if ("DOCENTE".equals(rol)) {
                return "redirect:/docente/panel";
            }
        }
        if (session.getAttribute("estudianteId") != null) {
            return "redirect:/simulador";
        }
        return "Login";
    }
    
    @PostMapping("/loginEstudiante")
    public String loginEstudiante(@RequestParam("correo") String correo,
                               @RequestParam("passwordEstudiante") String passwordEstudiante,
                               HttpSession session,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        
        if (correo == null || correo.trim().isEmpty() || passwordEstudiante == null || passwordEstudiante.trim().isEmpty()) {
            model.addAttribute("error", "El correo y la contraseña son obligatorios.");
            model.addAttribute("tipoError", "estudianteLogin");
            if (correo != null && !correo.trim().isEmpty()) {
                model.addAttribute("correoIngresado", correo); 
            }
            return "Login";
        }

        if (!correo.trim().matches("^i\\d{9}@cibertec\\.edu\\.pe$")) {
            model.addAttribute("error", "El correo debe tener el formato i202409426@cibertec.edu.pe");
            model.addAttribute("tipoError", "estudianteLogin");
            return "Login";
        }

        Estudiante estudiante = estudianteRepository.findByCorreoAndPassword(correo.trim(), passwordEstudiante.trim());
        
        if (estudiante != null) {
            if (estudiante.getEstado() != null && !estudiante.getEstado()) {
                model.addAttribute("error", "Tu cuenta ha sido pausada por un administrador.");
                model.addAttribute("tipoError", "estudianteLogin");
                model.addAttribute("correoIngresado", correo); 
                return "Login";
            }
            
            session.setAttribute("estudianteId", estudiante.getId());
            session.setAttribute("estudianteNombre", estudiante.getNombre());
            session.setMaxInactiveInterval(1800); 
            redirectAttributes.addFlashAttribute("welcomeUser", estudiante.getNombre());
            return "redirect:/simulador";
        } else {
            model.addAttribute("error", "Credenciales incorrectas o la cuenta no existe.");
            model.addAttribute("tipoError", "estudianteLogin");
            model.addAttribute("correoIngresado", correo); 
            return "Login";
        }
    }

    @PostMapping("/registroEstudiante")
    public String registroEstudiante(@RequestParam("correo") String correo,
                                  @RequestParam("passwordEstudiante") String passwordEstudiante,
                                  @RequestParam(value = "curso", required = false) String curso,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        
        if (correo == null || correo.trim().isEmpty()) {
            model.addAttribute("error", "El correo institucional es obligatorio.");
            model.addAttribute("tipoError", "estudianteRegistro");
            return "Login";
        }

        if (!correo.trim().matches("^i\\d{9}@cibertec\\.edu\\.pe$")) {
            model.addAttribute("error", "El correo debe tener el formato i202409426@cibertec.edu.pe");
            model.addAttribute("tipoError", "estudianteRegistro");
            return "Login"; 
        }

        Estudiante existente = estudianteRepository.findByCorreo(correo.trim());
        if (existente != null) {
            model.addAttribute("error", "Ya existe una cuenta con este correo institucional.");
            model.addAttribute("tipoError", "estudianteRegistro");
            return "Login"; 
        }

        if (passwordEstudiante == null || passwordEstudiante.trim().isEmpty()) {
            model.addAttribute("error", "La contraseña es obligatoria.");
            model.addAttribute("tipoError", "estudianteRegistro");
            model.addAttribute("correoIngresado", correo); 
            return "Login";
        }
        
        String regexSeguridad = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$";
        if (!passwordEstudiante.matches(regexSeguridad)) {
            model.addAttribute("error", "La contraseña debe tener más seguridad (Mayúscula, minúscula, caracter especial, minimo 8 caracteres)");
            model.addAttribute("tipoError", "estudianteRegistro");
            model.addAttribute("correoIngresado", correo);
            return "Login";
        }

        String passLower = passwordEstudiante.toLowerCase();
        for (String malaPalabra : PALABRAS_OFENSIVAS) {
            if (passLower.contains(malaPalabra)) {
                model.addAttribute("error", "La contraseña contiene lenguaje inapropiado. Por favor, usa otra.");
                model.addAttribute("tipoError", "estudianteRegistro"); 
                model.addAttribute("correoIngresado", correo); 
                return "Login";
            }
        }

        Estudiante estudiante = new Estudiante();
        String nombreGenerado = correo.split("@")[0];
        estudiante.setNombre(nombreGenerado); 
        estudiante.setCorreo(correo.trim());
        estudiante.setPassword(passwordEstudiante.trim());
        estudiante.setEstado(true); 
        
        estudianteRepository.save(estudiante);

        redirectAttributes.addFlashAttribute("success", "Cuenta creada exitosamente. Ahora puedes iniciar sesión.");
        return "redirect:/";
    }

    @PostMapping("/admin/login")
    public String loginAdmin(@RequestParam("correo") String correo,
                             @RequestParam("password") String password,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        
        if (correo == null || correo.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "El correo y la contraseña son obligatorios.");
            model.addAttribute("tipoError", "admin"); 
            return "Login";
        }

        Administrador admin = adminRepository.findByCorreoAndPassword(correo.trim(), password.trim());
        
        if (admin != null) {
            if (admin.getEstado() != null && !admin.getEstado()) {
                model.addAttribute("error", "Tu cuenta ha sido pausada por el administrador.");
                model.addAttribute("tipoError", "admin"); 
                model.addAttribute("correoIngresado", correo); 
                return "Login";
            }
            
            session.setAttribute("adminLogueado", true);
            session.setAttribute("rolAdmin", admin.getRol()); 
            session.setAttribute("adminNombre", admin.getNombre());
            
            session.setMaxInactiveInterval(1800); 
            redirectAttributes.addFlashAttribute("welcomeUser", admin.getNombre());
            
            // REDIRECCIÓN SEGÚN ROL
            if ("ADMINISTRADOR".equals(admin.getRol())) {
                return "redirect:/admin/panel";
            } else {
                return "redirect:/docente/panel";
            }
            
        } else {
            model.addAttribute("error", "Credenciales incorrectas o usuario no autorizado.");
            model.addAttribute("tipoError", "admin"); 
            model.addAttribute("correoIngresado", correo); 
            return "Login";
        }
    }
    
    @GetMapping("/simulador")
    public String mostrarSimulador(HttpSession session, Model model) {
        if (session.getAttribute("estudianteId") == null) {
            return "redirect:/"; 
        }
        return "SimuladorEstudiante"; 
    }

    @GetMapping("/admin/panel")
    public String mostrarPanel(HttpSession session, Model model) {
        if (session.getAttribute("adminLogueado") == null || !"ADMINISTRADOR".equals(session.getAttribute("rolAdmin"))) {
            return "redirect:/"; 
        }
        
        model.addAttribute("rolActual", "ADMINISTRADOR");
        model.addAttribute("nombreAdmin", session.getAttribute("adminNombre"));
        model.addAttribute("estudiantes", estudianteRepository.findAll());
        model.addAttribute("docentes", adminRepository.findByRol("DOCENTE"));
        
        return "PanelAdministrativo"; 
    }

    @GetMapping("/docente/panel")
    public String mostrarPanelDocente(HttpSession session, Model model) {
        if (session.getAttribute("adminLogueado") == null || !"DOCENTE".equals(session.getAttribute("rolAdmin"))) {
            return "redirect:/"; 
        }
        
        model.addAttribute("rolActual", "DOCENTE");
        model.addAttribute("nombreAdmin", session.getAttribute("adminNombre"));
        
        return "PanelAdministrativo"; 
    }
    
    @PostMapping("/admin/crearDocente")
    public String crearDocente(@RequestParam("nombre") String nombre,
                               @RequestParam("correo") String correo,
                               @RequestParam("password") String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLogueado") == null || !"ADMINISTRADOR".equals(session.getAttribute("rolAdmin"))) {
            return "redirect:/"; 
        }

        if (correo == null || correo.trim().isEmpty() || password == null || password.trim().isEmpty() || nombre == null || nombre.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorAdmin", "Todos los campos son obligatorios para crear al docente.");
            return "redirect:/admin/panel";
        }

        if (!correo.trim().toLowerCase().endsWith("@cibertec.edu.pe")) {
            redirectAttributes.addFlashAttribute("errorAdmin", "El correo institucional debe terminar en @cibertec.edu.pe");
            return "redirect:/admin/panel";
        }

        boolean existeCorreo = adminRepository.findAll().stream().anyMatch(a -> a.getCorreo().equalsIgnoreCase(correo.trim()));
        if (existeCorreo) {
            redirectAttributes.addFlashAttribute("errorAdmin", "Ya existe un administrador o docente con este correo.");
            return "redirect:/admin/panel";
        }

        String regexSeguridad = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$";
        if (!password.matches(regexSeguridad)) {
            redirectAttributes.addFlashAttribute("errorAdmin", "La contraseña del docente debe tener más seguridad.");
            return "redirect:/admin/panel";
        }

        String passLower = password.toLowerCase();
        for (String malaPalabra : PALABRAS_OFENSIVAS) {
            if (passLower.contains(malaPalabra)) {
                redirectAttributes.addFlashAttribute("errorAdmin", "La contraseña contiene lenguaje inapropiado.");
                return "redirect:/admin/panel";
            }
        }

        Administrador docente = new Administrador();
        docente.setNombre(nombre.trim());
        docente.setCorreo(correo.trim());
        docente.setPassword(password.trim());
        docente.setRol("DOCENTE");
        docente.setEstado(true); 
        
        adminRepository.save(docente);
        redirectAttributes.addFlashAttribute("successAdmin", "Docente creado exitosamente.");
        return "redirect:/admin/panel"; 
    }

    @PostMapping("/admin/editarDocente")
    public String editarDocente(@RequestParam("id") Integer id,
                                @RequestParam("nombre") String nombre,
                                @RequestParam("correo") String correo,
                                @RequestParam(value = "password", required = false) String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLogueado") == null || !"ADMINISTRADOR".equals(session.getAttribute("rolAdmin"))) {
            return "redirect:/"; 
        }
        
        Administrador docente = adminRepository.findById(id).orElse(null);
        if(docente != null) {
            
            if (!correo.trim().toLowerCase().endsWith("@cibertec.edu.pe")) {
                redirectAttributes.addFlashAttribute("errorAdmin", "El correo institucional debe terminar en @cibertec.edu.pe");
                return "redirect:/admin/panel";
            }

            if (!docente.getCorreo().equalsIgnoreCase(correo.trim())) {
                boolean existeCorreo = adminRepository.findAll().stream().anyMatch(a -> a.getCorreo().equalsIgnoreCase(correo.trim()));
                if (existeCorreo) {
                    redirectAttributes.addFlashAttribute("errorAdmin", "No se puede actualizar. El nuevo correo ya está en uso.");
                    return "redirect:/admin/panel";
                }
            }

            docente.setNombre(nombre.trim());
            docente.setCorreo(correo.trim());
            
            if(password != null && !password.trim().isEmpty()) {
                String regexSeguridad = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$";
                if (!password.matches(regexSeguridad)) {
                    redirectAttributes.addFlashAttribute("errorAdmin", "La nueva contraseña debe tener mayor seguridad.");
                    return "redirect:/admin/panel";
                }

                String passLower = password.toLowerCase();
                for (String malaPalabra : PALABRAS_OFENSIVAS) {
                    if (passLower.contains(malaPalabra)) {
                        redirectAttributes.addFlashAttribute("errorAdmin", "La nueva contraseña contiene lenguaje inapropiado.");
                        return "redirect:/admin/panel";
                    }
                }
                docente.setPassword(password.trim());
            }
            adminRepository.save(docente);
            redirectAttributes.addFlashAttribute("successAdmin", "Datos del docente actualizados correctamente.");
        }
        return "redirect:/admin/panel";
    }

    @GetMapping("/admin/docente/eliminar/{id}")
    public String eliminarDocente(@PathVariable("id") Integer id, HttpSession session) {
        if (session.getAttribute("adminLogueado") == null || !"ADMINISTRADOR".equals(session.getAttribute("rolAdmin"))) {
            return "redirect:/"; 
        }
        adminRepository.deleteById(id);
        return "redirect:/admin/panel";
    }

    @GetMapping("/admin/docente/toggle/{id}")
    public String toggleDocente(@PathVariable("id") Integer id, HttpSession session) {
        if (session.getAttribute("adminLogueado") == null) return "redirect:/"; 
        
        Administrador docente = adminRepository.findById(id).orElse(null);
        if (docente != null) {
            docente.setEstado(docente.getEstado() == null ? true : !docente.getEstado());
            adminRepository.save(docente);
        }
        return "redirect:/admin/panel";
    }

    @GetMapping("/admin/usuario/toggle/{id}")
    public String toggleEstudiante(@PathVariable("id") Integer id, HttpSession session) {
        if (session.getAttribute("adminLogueado") == null) return "redirect:/"; 
        
        Estudiante estudiante = estudianteRepository.findById(id).orElse(null);
        if (estudiante != null) {
            estudiante.setEstado(estudiante.getEstado() == null ? true : !estudiante.getEstado()); 
            estudianteRepository.save(estudiante);
        }
        return "redirect:/admin/panel";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); 
        return "redirect:/"; 
    }
}