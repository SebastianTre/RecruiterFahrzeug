package SeTre.RecruiterFahrzeug.controller;

import SeTre.RecruiterFahrzeug.exception.AlreadyExistsException;
import SeTre.RecruiterFahrzeug.exception.InvalidException;
import SeTre.RecruiterFahrzeug.exception.NotFoundException;
import SeTre.RecruiterFahrzeug.model.Fahrzeug;
import SeTre.RecruiterFahrzeug.service.FahrzeugService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/fahrzeug") // Präfix für alle Endpunkte (Fahrzeug-Routen)
public class FahrzeugController {
    private FahrzeugService fahrzeugService;

    public FahrzeugController(FahrzeugService fahrzeugService) {
        this.fahrzeugService = fahrzeugService;
    }

    @GetMapping
    String viewHomePage(Model model) /*throws PageException*/ {
        List<Fahrzeug> fahrzeugListe = fahrzeugService.findFahrzeug();  // alle Fahrzeuge laden
        model.addAttribute("fahrzeug", fahrzeugListe);  // die gesamte Liste an das Model übergeben
        model.addAttribute("title", "Fahrzeugliste");
        return "fahrzeuglist";
    }

    @GetMapping(value = "/delete/{id}")
    String deleteFahrzeug(@PathVariable(name = "id") int id) throws NotFoundException {
        fahrzeugService.deleteFahrzeug(id);
        return "redirect:/fahrzeug";
    }

    @GetMapping(value = "/add")
    public String showAddFahrzeugForm(Model model) {
        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setAktiv(true);
        model.addAttribute("fahrzeug", fahrzeug);
        model.addAttribute("title", "Fahrzeug hinzufügen");
        return "addfahrzeug";
    }

    @PostMapping("/save")
    public String saveFahrzeug(@ModelAttribute Fahrzeug fahrzeug, Model model, RedirectAttributes redirectAttributes) {
        try {
            fahrzeugService.addFahrzeug(fahrzeug);

            // Erfolgsnachricht ins Model setzen
            String successMessage = "Das Fahrzeug " +
                    fahrzeug.getUnterscheidungszeichen() + " - " +
                    fahrzeug.getErkennungsnummer() + " " +
                    fahrzeug.getZiffern() + " (" +
                    fahrzeug.getHersteller() + " " +
                    fahrzeug.getModell() + ") wurde hinzugefügt.";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

            return "redirect:/fahrzeug"; // Erfolgreiche Speicherung, Weiterleitung zur Liste
        } catch (AlreadyExistsException e) {
            // Fehler abfangen und an das Model zur Fehleranzeige in der View übergeben
            model.addAttribute("errorMessage", "Das Kennzeichen ist bereits eingetragen.");
            return "addFahrzeug"; // Rückkehr zur addFahrzeug-Seite
        } catch (InvalidException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "addFahrzeug"; // Rückkehr zur addFahrzeug-Seite mit Fehlermeldung
        }
    }

    @GetMapping(value = "/edit/{id}")
    public ModelAndView showEditFahrzeugForm(@PathVariable(name = "id") int id) throws NotFoundException {
        ModelAndView modelAndView = new ModelAndView("editfahrzeug");
        Fahrzeug fahrzeug = fahrzeugService.findFahrzeug(id);
        modelAndView.addObject("fahrzeug", fahrzeug);
        modelAndView.addObject("title", "Fahrzeug ändern");
        return modelAndView;
    }

    @PostMapping("/update")
    public String updateFahrzeug(@ModelAttribute("fahrzeug") Fahrzeug fahrzeug, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Überprüfe, ob das Fahrzeug überhaupt existiert
            Fahrzeug existingFahrzeug = fahrzeugService.findFahrzeug(fahrzeug.getId());
            if (existingFahrzeug == null) {
                throw new NotFoundException();
            }

            fahrzeugService.updateFahrzeug(fahrzeug);

            // Erfolgsnachricht ins Model setzen
            String successMessage = "Das Fahrzeug " +
                    fahrzeug.getUnterscheidungszeichen() + " - " +
                    fahrzeug.getErkennungsnummer() + " " +
                    fahrzeug.getZiffern() + " (" +
                    fahrzeug.getHersteller() + " " +
                    fahrzeug.getModell() + ") wurde geändert.";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

            return "redirect:/fahrzeug";
        } catch (AlreadyExistsException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "editfahrzeug";
        } catch (InvalidException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "editfahrzeug";
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", "Fahrzeug nicht gefunden.");
            return "editfahrzeug";
        }
    }
}
