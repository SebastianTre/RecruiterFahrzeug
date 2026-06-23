package SeTre.RecruiterFahrzeug.service;

import SeTre.RecruiterFahrzeug.model.Reservierung;
import SeTre.RecruiterFahrzeug.model.Termin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexService {
    private final TerminService terminService;
    private final ReservierungService reservierungService;

    @Autowired
    public IndexService(TerminService terminService, ReservierungService reservierungService) {
        this.terminService = terminService;
        this.reservierungService = reservierungService;
    }

    public List<List<Integer>> findOverlapping() {
        List<Termin> termine = terminService.findTermin();
        List<Reservierung> reservierungen = reservierungService.findReservierung();

        List<List<Integer>> overlapping = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(); // Aktuelle Zeit holen

        for (Termin termin : termine) {
            for (Reservierung reservierung : reservierungen) {
                if (termin.getFahrzeugId() == reservierung.getFahrzeugId() &&
                        (termin.getBeginn().isAfter(now) || reservierung.getBeginn().isAfter(now)) && // Mindestens eines muss in der Zukunft liegen
                        termin.getBeginn().isBefore(reservierung.getEnde()) &&
                        termin.getEnde().isAfter(reservierung.getBeginn())) {

                    List<Integer> match = new ArrayList<>();

                    match.add(termin.getId());
                    match.add(reservierung.getId());
                    match.add(termin.getFahrzeugId());

                    overlapping.add(match);
                }
            }
        }

        return overlapping;
    }

    public List<Termin> findTermineKommend() {
        return terminService.findTerminNextDays(14);
    }

    public List<Reservierung> findReservierungenKommend() {
        return reservierungService.findReservierungNextDays(14);
    }
}
