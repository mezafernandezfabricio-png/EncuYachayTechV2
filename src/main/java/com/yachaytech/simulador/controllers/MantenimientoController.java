package com.yachaytech.simulador.controllers;

import com.yachaytech.simulador.models.Pregunta;
import com.yachaytech.simulador.repositories.PreguntaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MantenimientoController {

    @Autowired
    private PreguntaRepository preguntaRepository;

    // ──────────────────────────────────────────────────────────────
    // CREAR escenario
    // ──────────────────────────────────────────────────────────────
    @PostMapping("/admin/mantenimiento/crear")
    public String crearPregunta(
            @RequestParam("fase") String fase,
            @RequestParam("enunciado") String enunciado,
            @RequestParam("opcionA") String opcionA,
            @RequestParam("puntajeA") Double puntajeA,
            @RequestParam("opcionB") String opcionB,
            @RequestParam("puntajeB") Double puntajeB,
            @RequestParam(value = "opcionC", required = false) String opcionC,
            @RequestParam(value = "puntajeC", required = false) Double puntajeC,
            @RequestParam(value = "opcionD", required = false) String opcionD,
            @RequestParam(value = "puntajeD", required = false) Double puntajeD,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("adminLogueado") == null
                || !"ADMINISTRADOR".equals(session.getAttribute("rolAdmin"))) {
            return "redirect:/";
        }

        Pregunta p = new Pregunta();
        p.setFase(fase.trim());
        p.setEnunciado(enunciado.trim());
        p.setOpcionA(opcionA.trim());
        p.setPuntajeA(puntajeA);
        p.setOpcionB(opcionB.trim());
        p.setPuntajeB(puntajeB);
        if (opcionC != null && !opcionC.trim().isEmpty()) {
            p.setOpcionC(opcionC.trim());
            p.setPuntajeC(puntajeC != null ? puntajeC : 0.0);
        }
        if (opcionD != null && !opcionD.trim().isEmpty()) {
            p.setOpcionD(opcionD.trim());
            p.setPuntajeD(puntajeD != null ? puntajeD : 0.0);
        }

        preguntaRepository.save(p);
        redirectAttributes.addFlashAttribute("successAdmin", "Escenario creado correctamente.");
        return "redirect:/admin/panel";
    }

    // ──────────────────────────────────────────────────────────────
    // EDITAR escenario
    // ──────────────────────────────────────────────────────────────
    @PostMapping("/admin/mantenimiento/editar")
    public String editarPregunta(
            @RequestParam("id") Long id,
            @RequestParam("fase") String fase,
            @RequestParam("enunciado") String enunciado,
            @RequestParam("opcionA") String opcionA,
            @RequestParam("puntajeA") Double puntajeA,
            @RequestParam("opcionB") String opcionB,
            @RequestParam("puntajeB") Double puntajeB,
            @RequestParam(value = "opcionC", required = false) String opcionC,
            @RequestParam(value = "puntajeC", required = false) Double puntajeC,
            @RequestParam(value = "opcionD", required = false) String opcionD,
            @RequestParam(value = "puntajeD", required = false) Double puntajeD,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("adminLogueado") == null
                || !"ADMINISTRADOR".equals(session.getAttribute("rolAdmin"))) {
            return "redirect:/";
        }

        Pregunta p = preguntaRepository.findById(id).orElse(null);
        if (p != null) {
            p.setFase(fase.trim());
            p.setEnunciado(enunciado.trim());
            p.setOpcionA(opcionA.trim());
            p.setPuntajeA(puntajeA);
            p.setOpcionB(opcionB.trim());
            p.setPuntajeB(puntajeB);
            p.setOpcionC((opcionC != null && !opcionC.trim().isEmpty()) ? opcionC.trim() : null);
            p.setPuntajeC((opcionC != null && !opcionC.trim().isEmpty() && puntajeC != null) ? puntajeC : null);
            p.setOpcionD((opcionD != null && !opcionD.trim().isEmpty()) ? opcionD.trim() : null);
            p.setPuntajeD((opcionD != null && !opcionD.trim().isEmpty() && puntajeD != null) ? puntajeD : null);
            preguntaRepository.save(p);
            redirectAttributes.addFlashAttribute("successAdmin", "Escenario actualizado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorAdmin", "No se encontró el escenario.");
        }
        return "redirect:/admin/panel";
    }

    // ──────────────────────────────────────────────────────────────
    // ELIMINAR escenario
    // ──────────────────────────────────────────────────────────────
    @GetMapping("/admin/mantenimiento/eliminar/{id}")
    public String eliminarPregunta(
            @PathVariable("id") Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("adminLogueado") == null
                || !"ADMINISTRADOR".equals(session.getAttribute("rolAdmin"))) {
            return "redirect:/";
        }

        preguntaRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successAdmin", "Escenario eliminado correctamente.");
        return "redirect:/admin/panel";
    }
}