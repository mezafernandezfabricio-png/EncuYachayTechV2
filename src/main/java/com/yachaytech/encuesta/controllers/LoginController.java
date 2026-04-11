package com.yachaytech.encuesta.controllers;

import com.yachaytech.encuesta.models.Administrador;
import com.yachaytech.encuesta.models.Cliente;
import com.yachaytech.encuesta.models.Pregunta;
import com.yachaytech.encuesta.repositories.AdministradorRepository;
import com.yachaytech.encuesta.repositories.ClienteRepository;
import com.yachaytech.encuesta.repositories.PreguntaRepository;
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
    private ClienteRepository clienteRepository;

    @Autowired
    private AdministradorRepository adminRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    private static final List<String> PALABRAS_OFENSIVAS = Arrays.asList(
            "puta", "puto", "mierda", "cabron", "cabròn", "pendejo", 
            "imbecil", "idiota", "estupido", "estùpido", "perra", 
            "maldito", "carajo", "verga", "cojudo", "zorra"
    );

    @GetMapping("/")
    public String mostrarLogin(HttpSession session, Model model) {
        if (session.getAttribute("adminLogueado") != null) {
            return "redirect:/admin/panel";
        }
        if (session.getAttribute("clienteId") != null) {
            return "redirect:/encuesta";
        }
        return "Login";
    }
    
    @PostMapping("/loginCliente")
    public String loginCliente(@RequestParam("correo") String correo,
                               @RequestParam("passwordCliente") String passwordCliente,
                               HttpSession session,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        
        if (correo == null || correo.trim().isEmpty() || passwordCliente == null || passwordCliente.trim().isEmpty()) {
            model.addAttribute("error", "El correo y la contraseña son obligatorios.");
            model.addAttribute("tipoError", "clienteLogin");
            if (correo != null && !correo.trim().isEmpty()) {
                model.addAttribute("correoIngresado", correo); 
            }
            return "Login";
        }

        if (!correo.trim().matches("^i\\d{9}@cibertec\\.edu\\.pe$")) {
            model.addAttribute("error", "El correo debe tener el formato i202409426@cibertec.edu.pe");
            model.addAttribute("tipoError", "clienteLogin");
            return "Login";
        }

        Cliente cliente = clienteRepository.findByCorreoAndPassword(correo.trim(), passwordCliente.trim());
        
        if (cliente != null) {
            if (cliente.getEstado() != null && !cliente.getEstado()) {
                model.addAttribute("error", "Tu cuenta ha sido pausada por un administrador.");
                model.addAttribute("tipoError", "clienteLogin");
                model.addAttribute("correoIngresado", correo); 
                return "Login";
            }
            
            session.setAttribute("clienteId", cliente.getId());
            session.setAttribute("clienteNombre", cliente.getNombre());
            session.setMaxInactiveInterval(1800); 
            redirectAttributes.addFlashAttribute("welcomeUser", cliente.getNombre());
            return "redirect:/encuesta";
        } else {
            model.addAttribute("error", "Credenciales incorrectas o la cuenta no existe.");
            model.addAttribute("tipoError", "clienteLogin");
            model.addAttribute("correoIngresado", correo); 
            return "Login";
        }
    }

    @PostMapping("/registroCliente")
    public String registroCliente(@RequestParam("correo") String correo,
                                  @RequestParam("passwordCliente") String passwordCliente,
                                  @RequestParam(value = "curso", required = false) String curso,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        
        if (correo == null || correo.trim().isEmpty()) {
            model.addAttribute("error", "El correo institucional es obligatorio.");
            model.addAttribute("tipoError", "clienteRegistro");
            return "Login";
        }

        if (!correo.trim().matches("^i\\d{9}@cibertec\\.edu\\.pe$")) {
            model.addAttribute("error", "El correo debe tener el formato i202409426@cibertec.edu.pe");
            model.addAttribute("tipoError", "clienteRegistro");
            return "Login"; 
        }

        Cliente existente = clienteRepository.findByCorreo(correo.trim());
        if (existente != null) {
            model.addAttribute("error", "Ya existe una cuenta con este correo institucional.");
            model.addAttribute("tipoError", "clienteRegistro");
            return "Login"; 
        }

        if (passwordCliente == null || passwordCliente.trim().isEmpty()) {
            model.addAttribute("error", "La contraseña es obligatoria.");
            model.addAttribute("tipoError", "clienteRegistro");
            model.addAttribute("correoIngresado", correo); 
            return "Login";
        }
        
        String regexSeguridad = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$";
        if (!passwordCliente.matches(regexSeguridad)) {
            model.addAttribute("error", "La contraseña debe tener más seguridad (Mayúscula, minúscula, caracter especial, minimo 8 caracteres)");
            model.addAttribute("tipoError", "clienteRegistro");
            model.addAttribute("correoIngresado", correo);
            return "Login";
        }

        String passLower = passwordCliente.toLowerCase();
        for (String malaPalabra : PALABRAS_OFENSIVAS) {
            if (passLower.contains(malaPalabra)) {
                model.addAttribute("error", "La contraseña contiene lenguaje inapropiado. Por favor, usa otra.");
                model.addAttribute("tipoError", "clienteRegistro"); 
                model.addAttribute("correoIngresado", correo); 
                return "Login";
            }
        }

        Cliente cliente = new Cliente();
        String nombreGenerado = correo.split("@")[0];
        cliente.setNombre(nombreGenerado); 
        cliente.setCorreo(correo.trim());
        cliente.setPassword(passwordCliente.trim());
        cliente.setEstado(true); 
        
        clienteRepository.save(cliente);

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
            
            session.setMaxInactiveInterval(180); 
            redirectAttributes.addFlashAttribute("welcomeUser", admin.getNombre());
            
            return "redirect:/admin/panel";
            
        } else {
            model.addAttribute("error", "Credenciales incorrectas o usuario no autorizado.");
            model.addAttribute("tipoError", "admin"); 
            model.addAttribute("correoIngresado", correo); 
            return "Login";
        }
    }
    
    @GetMapping("/encuesta")
    public String mostrarEncuesta(HttpSession session, Model model) {
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/"; 
        }
        model.addAttribute("preguntas", preguntaRepository.findAll());
        
        return "EncuestaCliente"; 
    }

    @GetMapping("/admin/panel")
    public String mostrarPanel(HttpSession session, Model model) {
        if (session.getAttribute("adminLogueado") == null) {
            return "redirect:/"; 
        }
        
        String rolActual = (String) session.getAttribute("rolAdmin");
        model.addAttribute("rolActual", rolActual);
        model.addAttribute("nombreAdmin", session.getAttribute("adminNombre"));
        
        if ("ADMINISTRADOR".equals(rolActual)) {
            model.addAttribute("estudiantes", clienteRepository.findAll());
            model.addAttribute("docentes", adminRepository.findByRol("DOCENTE"));
            model.addAttribute("preguntas", preguntaRepository.findAll()); 
        }
        
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
            redirectAttributes.addFlashAttribute("errorAdmin", "La contraseña del docente debe tener más seguridad (Mayúscula, minúscula, caracter especial, minimo 8 caracteres).");
            return "redirect:/admin/panel";
        }

        String passLower = password.toLowerCase();
        for (String malaPalabra : PALABRAS_OFENSIVAS) {
            if (passLower.contains(malaPalabra)) {
                redirectAttributes.addFlashAttribute("errorAdmin", "La contraseña contiene lenguaje inapropiado. Por favor, usa otra.");
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
                    redirectAttributes.addFlashAttribute("errorAdmin", "La nueva contraseña debe tener (Mayúscula, minúscula, un caracter especial y minimo 8 caracteres).");
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
        
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente != null) {
            cliente.setEstado(cliente.getEstado() == null ? true : !cliente.getEstado()); 
            clienteRepository.save(cliente);
        }
        return "redirect:/admin/panel";
    }

    @PostMapping("/admin/mantenimiento/crear")
    public String crearPregunta(Pregunta pregunta, HttpSession session) {
        if (session.getAttribute("adminLogueado") == null || !"ADMINISTRADOR".equals(session.getAttribute("rolAdmin"))) {
            return "redirect:/"; 
        }
        preguntaRepository.save(pregunta);
        return "redirect:/admin/panel";
    }

    @PostMapping("/admin/mantenimiento/editar")
    public String editarPregunta(Pregunta preguntaEditada, HttpSession session) {
        if (session.getAttribute("adminLogueado") == null || !"ADMINISTRADOR".equals(session.getAttribute("rolAdmin"))) {
            return "redirect:/"; 
        }
        preguntaRepository.save(preguntaEditada);
        return "redirect:/admin/panel";
    }

    @GetMapping("/admin/mantenimiento/eliminar/{id}")
    public String eliminarPregunta(@PathVariable("id") Long id, HttpSession session) {
        if (session.getAttribute("adminLogueado") == null || !"ADMINISTRADOR".equals(session.getAttribute("rolAdmin"))) {
            return "redirect:/"; 
        }
        preguntaRepository.deleteById(id);
        return "redirect:/admin/panel";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); 
        return "redirect:/"; 
    }
    
}