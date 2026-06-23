package SeTre.RecruiterFahrzeug.service;

import SeTre.RecruiterFahrzeug.exception.NotFoundException;
import SeTre.RecruiterFahrzeug.exception.InvalidException;
import SeTre.RecruiterFahrzeug.model.Reservierung;
import SeTre.RecruiterFahrzeug.model.Termin;
import SeTre.RecruiterFahrzeug.repository.ReservierungRepository;
import SeTre.RecruiterFahrzeug.repository.TerminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservierungService {
    private ReservierungRepository reservierungRepository;
    private TerminRepository terminRepository;

    public ReservierungService(ReservierungRepository reservierungRepository, TerminRepository terminRepository) {
        this.reservierungRepository = reservierungRepository;
        this.terminRepository = terminRepository;
    }

    public Reservierung findReservierung(int id) throws InvalidException {
        Optional<Reservierung> reservierung = reservierungRepository.findById(id);
        return reservierung.orElseThrow(NotFoundException::new);
    }

    public List<Reservierung> findReservierung() {
        List<Reservierung> reservierung = reservierungRepository.findAll();
        return reservierung;
    }

    public List<Reservierung> findReservierungNextDays(Integer days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime daysFromNow = now.plusDays(days);
        return reservierungRepository.findReservierungAnstehende(now, daysFromNow);
    }


    public List<Reservierung> findReservierungenPresent() {
        return reservierungRepository.findPresent();
    }

    public List<Reservierung> findReservierungenPast() {
        return reservierungRepository.findPast();
    }


    @Transactional
    public void addReservierung(Reservierung reservierung) throws InvalidException {

        if (reservierung.getFahrzeugId() == null ||reservierung.getFahrzeugId() == 0) {
            throw new InvalidException("Bitte ein gültiges Fahrzeug auswählen.");
        }

        // Überprüfen, ob beginn nach NOW() ist
        if (reservierung.getBeginn() != null && reservierung.getBeginn().isBefore(LocalDateTime.now())) {
            throw new InvalidException("Der Beginn der Reservierung darf nicht in der Vergangenheit liegen.");
        }

        // Überprüfen, ob Ende nach Beginn ist
        if (reservierung.getBeginn() != null && reservierung.getEnde() != null && reservierung.getEnde().isBefore(reservierung.getBeginn())) {
            throw new InvalidException("Das Ende der Reservierung muss nach dem Beginn liegen.");
        }

        // Mitarbeiterlänge
        if (reservierung.getMitarbeiter().length() > 50) {
            throw new InvalidException("Mitarbeiter darf aus max. 50 Zeichen bestehen.");
        }

        // Anmerkunglänge
        if (reservierung.getAnmerkung().length() > 500) {
            throw new InvalidException("Anmerkung darf aus max. 500 Zeichen bestehen.");
        }

        // Überprüfen, ob es bereits Reservierungen für das Fahrzeug im angegebenen Zeitraum gibt
        List<Reservierung> overlappingReservierungen = reservierungRepository.findOverlappingReservierungen(
                reservierung.getFahrzeugId(), reservierung.getBeginn(), reservierung.getEnde());
        if (!overlappingReservierungen.isEmpty()) {
            int anzahl = overlappingReservierungen.toArray().length;
            throw new InvalidException("Es gibt bereits " + anzahl + " Reservierung(en) für dieses Fahrzeug im angegebenen Zeitraum.");
        }

        // Überprüfen, ob es bereits Termine für das Fahrzeug im angegebenen Zeitraum gibt
        List<Termin> overlappingTermine = terminRepository.findOverlappingTermine(
                reservierung.getFahrzeugId(), reservierung.getBeginn(), reservierung.getEnde());
        if (!overlappingTermine.isEmpty()) {
            int anzahl = overlappingTermine.toArray().length;
            throw new InvalidException("Es gibt bereits " + anzahl + " Termin(e) für dieses Fahrzeug im angegebenen Zeitraum.");
        }

        reservierungRepository.save(reservierung);
    }

    @Transactional
    public Reservierung updateReservierung(Reservierung reservierung) throws NotFoundException, InvalidException {

        Reservierung existingReservierung = reservierungRepository.findById(reservierung.getId()).orElseThrow(NotFoundException::new);

        // Überprüfen, ob beginn nach NOW() ist
        if (reservierung.getBeginn() != null && reservierung.getBeginn().isBefore(LocalDateTime.now())) {
            throw new InvalidException("Der Beginn der Reservierung darf nicht in der Vergangenheit liegen.");
        }

        // Überprüfen, ob Ende nach Beginn ist
        if (reservierung.getBeginn() != null && reservierung.getEnde() != null && reservierung.getEnde().isBefore(reservierung.getBeginn())) {
            throw new InvalidException("Das Ende des Termins muss nach dem Beginn liegen.");
        }

        // Mitarbeiterlänge
        if (reservierung.getMitarbeiter().length() > 50) {
            throw new InvalidException("Mitarbeiter darf aus max. 50 Zeichen bestehen.");
        }

        // Anmerkunglänge
        if (reservierung.getAnmerkung().length() > 500) {
            throw new InvalidException("Anmerkung darf aus max. 500 Zeichen bestehen.");
        }

        // Überprüfen, ob es bereits Reservierungen für das Fahrzeug im angegebenen Zeitraum gibt
        List<Reservierung> overlappingReservierungen = reservierungRepository.findOverlappingReservierungenEdit(
                reservierung.getFahrzeugId(), reservierung.getBeginn(), reservierung.getEnde(), reservierung.getId());
        if (!overlappingReservierungen.isEmpty()) {
            int anzahl = overlappingReservierungen.toArray().length;
            throw new InvalidException("Es gibt bereits " + anzahl + " Reservierung(en) für dieses Fahrzeug im angegebenen Zeitraum.");
        }

        // Überprüfen, ob es bereits Termine für das Fahrzeug im angegebenen Zeitraum gibt
        List<Termin> overlappingTermine = terminRepository.findOverlappingTermine(
                reservierung.getFahrzeugId(), reservierung.getBeginn(), reservierung.getEnde());
        if (!overlappingTermine.isEmpty()) {
            int anzahl = overlappingTermine.toArray().length;
            throw new InvalidException("Es gibt bereits " + anzahl + " Termin(e) für dieses Fahrzeug im angegebenen Zeitraum.");
        }

        return reservierungRepository.save(reservierung);
    }

    public void deleteReservierung(Integer id) {
        Reservierung reservierung = reservierungRepository.findById(id).orElseThrow(NotFoundException::new);
        getReservierungRepository().delete(reservierung);
    }

    public Long count() {
        return reservierungRepository.count();
    }

    public long countByFahrzeugId(int fahrzeugId) {
        return reservierungRepository.countByFahrzeugId(fahrzeugId);
    }

    public ReservierungRepository getReservierungRepository() {
        return reservierungRepository;
    }
}
