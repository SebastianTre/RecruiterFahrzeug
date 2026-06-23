package SeTre.RecruiterFahrzeug.service;

import SeTre.RecruiterFahrzeug.exception.AlreadyExistsException;
import SeTre.RecruiterFahrzeug.exception.InvalidException;
import SeTre.RecruiterFahrzeug.exception.NotFoundException;
import SeTre.RecruiterFahrzeug.model.Fahrzeug;
import SeTre.RecruiterFahrzeug.repository.FahrzeugRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FahrzeugService {

    private FahrzeugRepository fahrzeugRepository;

    public FahrzeugService(FahrzeugRepository fahrzeugrepository) {
        this.fahrzeugRepository = fahrzeugrepository;
    }

    public Fahrzeug findFahrzeug(int id) throws NotFoundException {
        return fahrzeugRepository.findById(id).orElseThrow(() -> new NotFoundException());
    }

    public List<Fahrzeug> findFahrzeug() {
        List<Fahrzeug> fahrzeug = fahrzeugRepository.findAllSorted();
        return fahrzeug;
    }

    public void addFahrzeug(Fahrzeug fahrzeug) throws AlreadyExistsException, InvalidException {
        String unterscheidungszeichen = fahrzeug.getUnterscheidungszeichen();
        String erkennungsnummer = fahrzeug.getErkennungsnummer();
        String ziffern = fahrzeug.getZiffern();
        String modell = fahrzeug.getModell();
        String hersteller = fahrzeug.getHersteller();
        Boolean aktiv = fahrzeug.getAktiv();

        if (!unterscheidungszeichen.matches("^[A-ZÄÖÜß]{1,3}$")) {
            throw new InvalidException("Das Unterscheidungszeichen muss aus 1 bis 3 Großbuchstaben (A-Z, Ä, Ö, Ü) bestehen und darf keine Zahlen enthalten.");
        }
        else if (!erkennungsnummer.matches("^[A-Z]{1,2}$")) {
            throw new InvalidException("Die Erkennungsnummer muss aus 1 bis 2 Großbuchstaben (A-Z) bestehen und darf keine Zahlen enthalten.");
        }
        else if (!ziffern.matches("^[1-9]([0-9]{0,3}$|[0-9]{0,2}[EH]?$)$")) {
            throw new InvalidException("Die Ziffern müssen mit einer Zahl (1-9) beginnen und dürfen höchstens einen Großbuchstaben am Ende enthalten, max. 4 Zeichen.");
        }
        else if (unterscheidungszeichen.length() +
                erkennungsnummer.length() +
                ziffern.length() > 8) {
            throw new InvalidException("Das Kennzeichen ist ungültig! Maximale Länge ist 8 Zeichen.");
        }
        else if (!modell.matches("^[A-Za-z0-9äöüÄÖÜß\\s\\.\\,\\(\\)&+\\/\\-]{1,20}$")) {
            throw new InvalidException("Maximal 20 Zeichen (a-z, A-Z, 0-9, äöüÄÖÜß, -, ., (, ), &, +, /) sind für Modell erlaubt.");
        }
        else if (!hersteller.matches("^[A-Zßa-z0-9äöüÄÖÜß\\s\\.\\,\\(\\)&+\\/\\-]{1,20}$")) {
            throw new InvalidException("Maximal 20 Zeichen (a-z, A-Z, 0-9, äöüÄÖÜß, -, ., (, ), &, +, /) sind für Hersteller erlaubt.");
        }
        else if (fahrzeugRepository.existsByUnterscheidungszeichenAndErkennungsnummerAndZiffernAndAktiv(
                unterscheidungszeichen,
                erkennungsnummer,
                ziffern,
                aktiv)) {
            throw new AlreadyExistsException("Das Kennzeichen ist bereits eingetragen.");
        }
        fahrzeugRepository.save(fahrzeug);
    }

    public Fahrzeug updateFahrzeug(Fahrzeug fahrzeug) throws NotFoundException, InvalidException {

        int id = fahrzeug.getId();
        String unterscheidungszeichen = fahrzeug.getUnterscheidungszeichen();
        String erkennungsnummer = fahrzeug.getErkennungsnummer();
        String ziffern = fahrzeug.getZiffern();
        String modell = fahrzeug.getModell();
        String hersteller = fahrzeug.getHersteller();
        Boolean aktiv = fahrzeug.getAktiv();

        if (!unterscheidungszeichen.matches("^[A-ZÄÖÜß]{1,3}$")) {
            throw new InvalidException("Das Unterscheidungszeichen muss aus 1 bis 3 Großbuchstaben (A-Z, Ä, Ö, Ü) bestehen und darf keine Zahlen enthalten.");
        }
        else if (!erkennungsnummer.matches("^[A-Z]{1,2}$")) {
            throw new InvalidException("Die Erkennungsnummer muss aus 1 bis 2 Großbuchstaben (A-Z) bestehen und darf keine Zahlen enthalten.");
        }
        else if (!ziffern.matches("^[1-9][0-9]{0,3}[A-Z]?$")) {
            throw new InvalidException("Die Ziffern müssen mit einer Zahl (1-9) beginnen und dürfen höchstens einen Großbuchstaben am Ende enthalten, max. 4 Zeichen.");
        }
        else if (ziffern.length() > 4) {
            throw new InvalidException("Die Ziffern dürfen maximal aus 4 Zeichen bestehen");
        }
        else if (unterscheidungszeichen.length() +
                erkennungsnummer.length() +
                ziffern.length() > 8) {
            throw new InvalidException("Das Kennzeichen ist ungültig! Maximale Länge ist 8 Zeichen.");
        }
        else if (!modell.matches("^[A-Za-z0-9äöüÄÖÜß\\s\\.\\,\\(\\)&+\\/\\-]{1,20}$")) {
            throw new InvalidException("Maximal 20 Zeichen (a-z, A-Z, 0-9, äöüÄÖÜß, -, ., (, ), &, +, /) sind für Modell erlaubt.");
        }
        else if (!hersteller.matches("^[A-Za-z0-9äöüÄÖÜß\\s\\.\\,\\(\\)&+\\/\\-]{1,20}$")) {
            throw new InvalidException("Maximal 20 Zeichen (a-z, A-Z, 0-9, äöüÄÖÜß, -, ., (, ), &, +, /) sind für Hersteller erlaubt.");
        }
        else if (fahrzeugRepository.existsByUnterscheidungszeichenAndErkennungsnummerAndZiffernAndIdNotAndAktiv(
                unterscheidungszeichen,
                erkennungsnummer,
                ziffern,
                id,
                aktiv)) {
            throw new AlreadyExistsException("Ein Fahrzeug mit diesem Kennzeichen ist bereits eingetragen. Es wurden keine Änderungen vorgenommen");
        }

        // Fahrzeug aktualisieren
        return fahrzeugRepository.save(fahrzeug);
    }

    public void deleteFahrzeug(int id) throws NotFoundException {
        Fahrzeug fahrzeug = fahrzeugRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        fahrzeug.setAktiv(false); // Setzt das Fahrzeug als inaktiv
        fahrzeugRepository.save(fahrzeug); // Speichert die Änderung in der DB
    }

    public Long count() {
        return fahrzeugRepository.count();
    }

//    public Page<Fahrzeug> getPaginatedFahrzeugSorted(final int pageNumber, final int pageSize, String sortBy, String sortDirection) {
//        final Pageable pageable;
//        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
//        pageable = PageRequest.of(pageNumber -1, pageSize, sort);
//        Page<Fahrzeug> pagedResult = fahrzeugRepository.findAll(pageable);
//        if (pagedResult.hasContent()) {
//            return pagedResult;
//        } else {
//            //throw (new FahrzeugPageException("Fehler in der Pagination aus getPaginatedFahrzeugSorted im Service"));
//            return Page.empty(pageable);
//        }
//    }
}
