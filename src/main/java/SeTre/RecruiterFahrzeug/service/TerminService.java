package SeTre.RecruiterFahrzeug.service;

import SeTre.RecruiterFahrzeug.exception.InvalidException;
import SeTre.RecruiterFahrzeug.exception.NotFoundException;
import SeTre.RecruiterFahrzeug.model.Termin;
import SeTre.RecruiterFahrzeug.repository.TerminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TerminService {
    private TerminRepository terminRepository;
    private FahrzeugService fahrzeugService;

    //@Autowired
    public TerminService(TerminRepository terminRepository, FahrzeugService fahrzeugService) {
        this.terminRepository = terminRepository;
        this.fahrzeugService = fahrzeugService;
    }

    public Termin findTermin(int id) throws NotFoundException {
        Optional<Termin> termin = terminRepository.findById(id);
        return termin.orElseThrow(NotFoundException::new);
    }

    public List<Termin> findTermin() {
        List<Termin> termin = terminRepository.findAll();
        return termin;
    }

    public List<Termin> findTerminNextDays(Integer days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime daysFromNow = now.plusDays(days);
        return terminRepository.findTerminAnstehende(now, daysFromNow);
    }

    public List<Termin> findTerminePresent() {
        return terminRepository.findPresent();
    }

    public List<Termin> findTerminePast() {
        return terminRepository.findPast();
    }


    @Transactional
    public void addTermin(Termin termin) throws InvalidException {

        if (termin.getFahrzeugId() == 0) {
            throw new InvalidException("Bitte ein gültiges Fahrzeug auswählen.");
        }

        // Überprüfen, ob Beginn nach NOW() ist
        if (termin.getBeginn() != null && termin.getBeginn().isBefore(LocalDateTime.now())) {
            throw new InvalidException("Der Beginn des Termins darf nicht in der Vergangenheit liegen.");
        }

        // Überprüfen, ob Ende nach Beginn ist
        if (termin.getBeginn() != null && termin.getEnde() != null && termin.getEnde().isBefore(termin.getBeginn())) {
            throw new InvalidException("Das Ende des Termins muss nach dem Beginn liegen.");
        }

        // Artlänge
        if (termin.getArt().length() > 50) {
            throw new InvalidException("Terminart darf aus max. 50 Zeichen bestehen.");
        }

        // Mitarbeiterlänge
        if (termin.getMitarbeiter().length() > 50) {
            throw new InvalidException("Mitarbeiter darf aus max. 50 Zeichen bestehen.");
        }

        // Anmerkunglänge
        if (termin.getAnmerkung().length() > 500) {
            throw new InvalidException("Anmerkung darf aus max. 500 Zeichen bestehen.");
        }

        // Überprüfen, ob es bereits Termine für das Fahrzeug im angegebenen Zeitraum gibt
        List<Termin> overlappingTermine = terminRepository.findOverlappingTermine(
                termin.getFahrzeugId(), termin.getBeginn(), termin.getEnde());
        if (!overlappingTermine.isEmpty()) {
            int anzahl = overlappingTermine.toArray().length;
            throw new InvalidException("Es gibt bereits " + anzahl + " Termin(e) für dieses Fahrzeug im angegebenen Zeitraum.");
        }

        terminRepository.save(termin);
    }

    @Transactional
    public Termin updateTermin(Termin termin) throws NotFoundException, InvalidException {

        Termin existingTermin = terminRepository.findById(termin.getId()).orElseThrow(NotFoundException::new);

        // Überprüfen, ob beginn nach NOW() ist
        if (termin.getBeginn() != null && termin.getBeginn().isBefore(LocalDateTime.now())) {
            throw new InvalidException("Der Beginn des Termins darf nicht in der Vergangenheit liegen.");
        }

        // Überprüfen, ob ende nach beginn ist
        if (termin.getBeginn() != null && termin.getEnde() != null && termin.getEnde().isBefore(termin.getBeginn())) {
            throw new InvalidException("Das Ende des Termins muss nach dem Beginn liegen.");
        }

        // Artlänge
        if (termin.getArt().length() > 50) {
            throw new InvalidException("Terminart darf aus max. 50 Zeichen bestehen.");
        }

        // Mitarbeiterlänge
        if (termin.getMitarbeiter().length() > 50) {
            throw new InvalidException("Mitarbeiter darf aus max. 50 Zeichen bestehen.");
        }

        // Anmerkunglänge
        if (termin.getAnmerkung().length() > 500) {
            throw new InvalidException("Anmerkung darf aus max. 500 Zeichen bestehen.");
        }

        // Überprüfen, ob es bereits Termine für das Fahrzeug im angegebenen Zeitraum gibt
        List<Termin> overlappingTermine = terminRepository.findOverlappingTermineEdit(
                termin.getFahrzeugId(), termin.getBeginn(), termin.getEnde(), termin.getId());
        if (!overlappingTermine.isEmpty()) {
            int anzahl = overlappingTermine.toArray().length;
            throw new InvalidException("Es gibt bereits " + anzahl + " Termin(e) für dieses Fahrzeug im angegebenen Zeitraum.");
        }

        return terminRepository.save(termin);
    }

    public void deleteTermin(Integer id) throws NotFoundException {
        Termin termin = terminRepository.findById(id).orElseThrow(NotFoundException::new);
        terminRepository.delete(termin);
    }

    public Long count() {
        return terminRepository.count();
    }

    public long countByFahrzeugId(int fahrzeugId) {
        return terminRepository.countByFahrzeugId(fahrzeugId);
    }

    public TerminRepository getTerminRepository() {
        return terminRepository;
    }
}
