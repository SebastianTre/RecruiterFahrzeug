package SeTre.RecruiterFahrzeug.controller;

import SeTre.RecruiterFahrzeug.model.Fahrzeug;
import SeTre.RecruiterFahrzeug.model.Reservierung;
import SeTre.RecruiterFahrzeug.model.Termin;
import SeTre.RecruiterFahrzeug.service.FahrzeugService;
import SeTre.RecruiterFahrzeug.service.IndexService;
import SeTre.RecruiterFahrzeug.service.ReservierungService;
import SeTre.RecruiterFahrzeug.service.TerminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {
    private final IndexService indexService;
    private final FahrzeugService fahrzeugService;
    private final TerminService terminService;
    private final ReservierungService reservierungService;

    @Autowired
    public IndexController(IndexService indexService, FahrzeugService fahrzeugService, TerminService terminService, ReservierungService reservierungService) {
        this.indexService = indexService;
        this.fahrzeugService = fahrzeugService;
        this.terminService = terminService;
        this.reservierungService = reservierungService;
    }

    @GetMapping("/")
    public String index(Model model) {

        List<List<Integer>> overlapping = indexService.findOverlapping();
        List<Termin> termineOverlapping = new ArrayList<>();
        List<Reservierung> reservierungenOverlapping = new ArrayList<>();
        List<Fahrzeug> fahrzeugeOverlapping = new ArrayList<>();

        List<Termin> termineKommend = indexService.findTermineKommend();
        List<Fahrzeug> terminFahrzeugeKommend = new ArrayList<>();

        List<Reservierung> reservierungenKommend = indexService.findReservierungenKommend();
        List<Fahrzeug> reservierungFahrzeugeKommend = new ArrayList<>();


        for (List<Integer> match : overlapping) {
            termineOverlapping.add(terminService.findTermin(match.get(0)));
            reservierungenOverlapping.add(reservierungService.findReservierung(match.get(1)));
            fahrzeugeOverlapping.add(fahrzeugService.findFahrzeug(match.get(2)));
        }


        for (Termin termin : termineKommend) {
            Fahrzeug fahrzeug = fahrzeugService.findFahrzeug(termin.getFahrzeugId());
            terminFahrzeugeKommend.add(fahrzeug);
        }

        for (Reservierung reservierung : reservierungenKommend) {
            Fahrzeug fahrzeug = fahrzeugService.findFahrzeug(reservierung.getFahrzeugId());
            reservierungFahrzeugeKommend.add(fahrzeug);
        }

        // Listen dem model hinzufügen ---> für die View
        model.addAttribute("overlapping", overlapping);
        model.addAttribute("termineOverlapping", termineOverlapping);
        model.addAttribute("reservierungenOverlapping", reservierungenOverlapping);
        model.addAttribute("fahrzeugeOverlapping", fahrzeugeOverlapping);
        model.addAttribute("termineKommend", termineKommend);
        model.addAttribute("reservierungenKommend", reservierungenKommend);
        model.addAttribute("terminFahrzeugeKommend", terminFahrzeugeKommend);
        model.addAttribute("reservierungFahrzeugeKommend", reservierungFahrzeugeKommend);
        model.addAttribute("title", "Fahrzeugverwaltung");

        return "index";
    }
}
