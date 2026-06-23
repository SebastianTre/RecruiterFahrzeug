package SeTre.RecruiterFahrzeug.controller;

import SeTre.RecruiterFahrzeug.exception.AlreadyExistsException;
import SeTre.RecruiterFahrzeug.exception.InvalidException;
import SeTre.RecruiterFahrzeug.exception.NotFoundException;
import SeTre.RecruiterFahrzeug.model.Fahrzeug;
import SeTre.RecruiterFahrzeug.model.Reservierung;
import SeTre.RecruiterFahrzeug.repository.FahrzeugRepository;
import SeTre.RecruiterFahrzeug.service.FahrzeugService;
import SeTre.RecruiterFahrzeug.service.ReservierungService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reservierung") // Präfix für alle Endpunkte (Reservierung-Routen)
public class ReservierungController {

    private ReservierungService reservierungService;
    private FahrzeugService fahrzeugService;

    private FahrzeugRepository fahrzeugRepository;

    public ReservierungController(ReservierungService reservierungService, FahrzeugService fahrzeugService) {
        this.reservierungService = reservierungService;
        this.fahrzeugService = fahrzeugService;
    }

    @GetMapping
    String viewReservierungPage(Model model) {
        List<Reservierung> reservierungenPresent = reservierungService.findReservierungenPresent();
        Map<Integer, Fahrzeug> fahrzeugePresent = new HashMap<>();
        for (Reservierung reservierung : reservierungenPresent) {
            if (reservierung.getFahrzeugId() != 0) {
                Fahrzeug fahrzeug = fahrzeugService.findFahrzeug(reservierung.getFahrzeugId());
                fahrzeugePresent.put(reservierung.getId(), fahrzeug);
            }
        }

        List<Reservierung> reservierungenPast = reservierungService.findReservierungenPast();
        Map<Integer, Fahrzeug> fahrzeugePast = new HashMap<>();
        for (Reservierung reservierung : reservierungenPast) {
            if (reservierung.getFahrzeugId() != 0) {
                Fahrzeug fahrzeug = fahrzeugService.findFahrzeug(reservierung.getFahrzeugId());
                fahrzeugePast.put(reservierung.getId(), fahrzeug);
            }
        }

        model.addAttribute("reservierungenPresent", reservierungenPresent);  // Liste der aktuellen Reservierungen an das Model übergeben
        model.addAttribute("fahrzeugePresent", fahrzeugePresent);      // Fahrzeug-Map-Aktuell an das Model übergeben

        model.addAttribute("reservierungenPast", reservierungenPast);  // Liste der vergangenen Reservierungen an das Model übergeben
        model.addAttribute("fahrzeugePast", fahrzeugePast);      // Fahrzeug-Map-Vergangen an das Model übergeben

        model.addAttribute("title", "Reservierungsliste");
        return "reservierunglist";
    }

    @GetMapping(value = "/delete/{id}")
    String deleteReservierung(@PathVariable(name = "id") int id) throws NotFoundException {
        reservierungService.deleteReservierung(id);
        return "redirect:/reservierung";
    }

    @GetMapping(value = "/add")
    public String showAddReservierungForm(Model model) {
        Reservierung reservierung = new Reservierung();
        List<Fahrzeug> fahrzeuge = fahrzeugService.findFahrzeug();
        model.addAttribute("reservierung", new Reservierung());
        model.addAttribute("fahrzeug", fahrzeuge); // Fahrzeuge dem Model hinzufügen
        model.addAttribute("title", "Reservierung hinzufügen");

        return "addreservierung";
    }

    @GetMapping(value = "/edit/{id}")
    public ModelAndView showEditReservierungForm(@PathVariable(name = "id") int id) throws NotFoundException {
        ModelAndView modelAndView = new ModelAndView("editreservierung");
        Reservierung reservierung = reservierungService.findReservierung(id);
        List<Fahrzeug> fahrzeuge = fahrzeugService.findFahrzeug();;

        modelAndView.addObject("reservierung", reservierung);
        modelAndView.addObject("fahrzeuge", fahrzeuge);
        modelAndView.addObject("title", "Reservierung ändern");
        return modelAndView;
    }

    @PostMapping("/save")
    public String saveReservierung(@ModelAttribute("reservierung") Reservierung reservierung, @RequestParam("fahrzeugId") Integer fahrzeugId, Model model, RedirectAttributes redirectAttributes) {

        if (fahrzeugId == null || fahrzeugId == 0) {
            model.addAttribute("errorMessage", "Gültiges Fahrzeug auswählen");

            // Fahrzeugliste erneut in das Modell hinzufügen (falls das Speichern scheitert)
            List<Fahrzeug> fahrzeuge = fahrzeugService.findFahrzeug();
            model.addAttribute("fahrzeug", fahrzeuge); // Fahrzeuge dem Model hinzufügen

            return "addreservierung";
        }
        try {
            Fahrzeug fahrzeug = fahrzeugService.findFahrzeug(fahrzeugId);
            reservierung.setFahrzeugId(fahrzeugId);

            reservierungService.addReservierung(reservierung);

            // Erfolgsnachricht ins Model setzen
            String successMessage = "Die Reservierung für das Fahrzeug " +
                    fahrzeug.getUnterscheidungszeichen() + " - " +
                    fahrzeug.getErkennungsnummer() + " " +
                    fahrzeug.getZiffern() + " wurde gespeichert.";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

            return "redirect:/reservierung";
        } catch (InvalidException e) {
            model.addAttribute("errorMessage", e.getMessage());

            // Fahrzeugliste erneut in das Modell hinzufügen (falls das Speichern scheitert)
            List<Fahrzeug> fahrzeuge = fahrzeugService.findFahrzeug();
            model.addAttribute("fahrzeug", fahrzeuge); // Fahrzeuge dem Model hinzufügen

            return "addreservierung";
        }
    }

    @PostMapping("/update")
    public String updateReservierung(@ModelAttribute("reservierung") Reservierung reservierung,
                                     @RequestParam("fahrzeugId") Integer fahrzeugId,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            Fahrzeug fahrzeug = fahrzeugService.findFahrzeug(fahrzeugId);
            reservierung.setFahrzeugId(fahrzeugId);

            // Nach Reservierungsüberschneidungen suchen
            List<Reservierung> existingReservierungen = reservierungService.getReservierungRepository().findOverlappingReservierungenEdit(
                    fahrzeugId, reservierung.getBeginn(), reservierung.getEnde(), reservierung.getId());

            if (!existingReservierungen.isEmpty() &&
                    existingReservierungen.stream().noneMatch(t -> t.getId() == reservierung.getId())) {
                throw new AlreadyExistsException("Der gewählte Zeitraum überschneidet sich mit dem einer existierenden Reservierung.");
            }
            reservierungService.updateReservierung(reservierung);

            // Erfolgsnachricht ins Model setzen
            String successMessage = "Die Reservierung für das Fahrzeug " +
                    fahrzeug.getUnterscheidungszeichen() + " - " +
                    fahrzeug.getErkennungsnummer() + " " +
                    fahrzeug.getZiffern() + " (" +
                    fahrzeug.getHersteller() + " " +
                    fahrzeug.getModell() + ") wurde geändert.";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

            return "redirect:/reservierung";

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("fahrzeuge", fahrzeugService.findFahrzeug());
            return "editreservierung";
        }
    }
}
