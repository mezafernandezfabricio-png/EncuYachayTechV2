package com.yachaytech.encuesta.controllers;

import com.yachaytech.encuesta.models.Administrador;
import com.yachaytech.encuesta.models.Cliente;
import com.yachaytech.encuesta.repositories.AdministradorRepository;
import com.yachaytech.encuesta.repositories.ClienteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
            if (correo != null && !correo.trim().isEmpty()) {
                model.addAttribute("correoIngresado", correo);
            }
            return "Login";
        }

        if (!correo.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("error", "El formato del correo docente no es válido.");
            model.addAttribute("tipoError", "admin");
            return "Login"; 
        }

        Administrador admin = adminRepository.findByCorreoAndPassword(correo.trim(), password.trim());
        
        if (admin != null && admin.getEstado()) {
            session.setAttribute("adminLogueado", true);
            session.setMaxInactiveInterval(180); 
            redirectAttributes.addFlashAttribute("welcomeUser", admin.getNombre());
            return "redirect:/admin/panel";
        } else {
            model.addAttribute("error", "Credenciales de docente incorrectas o usuario inactivo.");
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
        return "EncuestaCliente"; 
    }

    @GetMapping("/admin/panel")
    public String mostrarPanel(HttpSession session, Model model) {
        if (session.getAttribute("adminLogueado") == null) {
            return "redirect:/"; 
        }
        return "PanelAdministrativo"; 
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); 
        return "redirect:/"; 
    }
}