package SeTre.RecruiterFahrzeug.controller;

import SeTre.RecruiterFahrzeug.exception.AlreadyExistsException;
import SeTre.RecruiterFahrzeug.exception.InvalidException;
import SeTre.RecruiterFahrzeug.exception.NotFoundException;
import SeTre.RecruiterFahrzeug.model.Fahrzeug;
import SeTre.RecruiterFahrzeug.model.Termin;
import SeTre.RecruiterFahrzeug.repository.FahrzeugRepository;
import SeTre.RecruiterFahrzeug.service.FahrzeugService;
import SeTre.RecruiterFahrzeug.service.TerminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/termin") // Präfix für alle Endpunkte (Termin-Routen)
public class TerminController {

    private TerminService terminService;
    private FahrzeugService fahrzeugService;

    private FahrzeugRepository fahrzeugRepository;

    public TerminController(TerminService terminService, FahrzeugService fahrzeugService) {
        this.terminService = terminService;
        this.fahrzeugService = fahrzeugService;
    }

    @GetMapping
    String viewTerminPage(Model model) {
        List<Termin> terminePresent = terminService.findTerminePresent();
        Map<Integer, Fahrzeug> fahrzeugePresent = new HashMap<>();
        for (Termin termin : terminePresent) {
            if (termin.getFahrzeugId() != 0) {
                Fahrzeug fahrzeug = fahrzeugService.findFahrzeug(termin.getFahrzeugId());
                fahrzeugePresent.put(termin.getId(), fahrzeug);  // Fahrzeug mit Termin-ID als Schlüssel speichern
            }
        }

        List<Termin> terminePast = terminService.findTerminePast();
        Map<Integer, Fahrzeug> fahrzeugePast = new HashMap<>();
        for (Termin termin : terminePast) {
            if (termin.getFahrzeugId() != 0) {
                Fahrzeug fahrzeug = fahrzeugService.findFahrzeug(termin.getFahrzeugId());
                fahrzeugePast.put(termin.getId(), fahrzeug);
            }
        }

        model.addAttribute("terminePresent", terminePresent);  // Liste der aktuellen Termine an das Model übergeben
        model.addAttribute("fahrzeugePresent", fahrzeugePresent);      // Fahrzeug-Map-Aktuell an das Model übergeben

        model.addAttribute("terminePast", terminePast);  // Liste der vergangenen Termine an das Model übergeben
        model.addAttribute("fahrzeugePast", fahrzeugePast);      // Fahrzeug-Map-Vergangen an das Model übergeben

        model.addAttribute("title", "Terminliste");
        return "terminlist";
    }

    @GetMapping(value = "/delete/{id}")
    String deleteTermin(@PathVariable(name = "id") Integer id) throws NotFoundException {
        terminService.deleteTermin(id);
        return "redirect:/termin";
    }

    @GetMapping(value = "/add")
    public String showAddTerminForm(Model model) {
        Termin termin = new Termin();
        List<Fahrzeug> fahrzeuge = fahrzeugService.findFahrzeug();
        model.addAttribute("termin", new Termin());
        model.addAttribute("fahrzeug", fahrzeuge); // Fahrzeuge dem Model hinzufügen
        model.addAttribute("title", "Termin hinzufügen");

        return "addtermin";
    }

    @GetMapping(value = "/edit/{id}")
    public ModelAndView showEditTerminForm(@PathVariable(name = "id") int id) throws NotFoundException {
        ModelAndView modelAndView = new ModelAndView("edittermin");
        Termin termin = terminService.findTermin(id);
        List<Fahrzeug> fahrzeuge = fahrzeugService.findFahrzeug();;

        modelAndView.addObject("termin", termin);
        modelAndView.addObject("fahrzeuge", fahrzeuge);
        modelAndView.addObject("title", "Termin ändern");
        return modelAndView;
    }

    @PostMapping("/save")
    public String saveTermin(@ModelAttribute("termin") Termin termin, @RequestParam("fahrzeugId") Integer fahrzeugId, Model model, RedirectAttributes redirectAttributes) {

        if (fahrzeugId == null || fahrzeugId == 0) {
            model.addAttribute("errorMessage", "Gültiges Fahrzeug auswählen");

            // Fahrzeugliste erneut in das Modell hinzufügen (falls das Speichern scheitert)
            List<Fahrzeug> fahrzeuge = fahrzeugService.findFahrzeug();
            model.addAttribute("fahrzeug", fahrzeuge); // Fahrzeuge dem Model hinzufügen

            return "addtermin";
        }
        try {
            //TODO: Check und todo entfernen
            Fahrzeug fahrzeug = fahrzeugService.findFahrzeug(fahrzeugId);
            termin.setFahrzeugId(fahrzeugId);

            terminService.addTermin(termin);

            // Erfolgsnachricht ins Model setzen
            String successMessage = "Der Termin für das Fahrzeug " +
                    fahrzeug.getUnterscheidungszeichen() + " - " +
                    fahrzeug.getErkennungsnummer() + " " +
                    fahrzeug.getZiffern() + " (" +
                    fahrzeug.getHersteller() + " " +
                    fahrzeug.getModell() + ") wurde gespeichert.";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

            return "redirect:/termin";
        } catch (InvalidException e) {
            model.addAttribute("errorMessage", e.getMessage());

            // Fahrzeugliste erneut in das Modell hinzufügen (falls das Speichern scheitert)
            List<Fahrzeug> fahrzeuge = fahrzeugService.findFahrzeug();
            model.addAttribute("fahrzeug", fahrzeuge); // Fahrzeuge dem Model hinzufügen

            return "addtermin";
        }
    }

    @PostMapping("/update")
    public String updateTermin(@ModelAttribute("termin") Termin termin,
                               @RequestParam("fahrzeugId") Integer fahrzeugId,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Fahrzeug fahrzeug = fahrzeugService.findFahrzeug(fahrzeugId);
            termin.setFahrzeugId(fahrzeugId);

            // Check for overlapping appointments
            List<Termin> existingTermine = terminService.getTerminRepository().findOverlappingTermineEdit(
                    fahrzeugId, termin.getBeginn(), termin.getEnde(), termin.getId());

            if (!existingTermine.isEmpty() &&
                    existingTermine.stream().noneMatch(t -> t.getId() == termin.getId())) {
                throw new AlreadyExistsException("Der gewählte Zeitraum überschneidet sich mit dem eines existierenden Termins.");
            }
            //model.addAttribute("Termin.beginn", termin.getBeginn());
            terminService.updateTermin(termin);

            // Erfolgsnachricht ins Model setzen
            String successMessage = "Der Termin für das Fahrzeug " +
                    fahrzeug.getUnterscheidungszeichen() + " - " +
                    fahrzeug.getErkennungsnummer() + " " +
                    fahrzeug.getZiffern() + " (" +
                    fahrzeug.getHersteller() + " " +
                    fahrzeug.getModell() + ") wurde geändert.";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

            return "redirect:/termin";

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("fahrzeuge", fahrzeugService.findFahrzeug());
            return "edittermin";
        }
    }
}
