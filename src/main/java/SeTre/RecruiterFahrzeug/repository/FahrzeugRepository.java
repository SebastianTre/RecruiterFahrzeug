package SeTre.RecruiterFahrzeug.repository;

import SeTre.RecruiterFahrzeug.model.Fahrzeug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FahrzeugRepository extends JpaRepository<Fahrzeug, Integer> {
    // prüft, ob ein Kennzeichen mit dieser Kombination von Erkennungsnummer, Unterscheidungszeichen und Ziffern existiert
    boolean existsByUnterscheidungszeichenAndErkennungsnummerAndZiffernAndAktiv(String unterscheidung, String erkennung, String ziffern, boolean aktiv);

    // prüft, ob ein Kennzeichen mit dieser Kombination von Erkennungsnummer, Unterscheidungszeichen und Ziffern existiert und schließt Einträge mit derselben id aus
    boolean existsByUnterscheidungszeichenAndErkennungsnummerAndZiffernAndIdNotAndAktiv(String unterscheidung, String erkennung, String ziffern, int id, boolean aktiv);

    @Query("SELECT f FROM Fahrzeug f WHERE f.aktiv = true ORDER BY f.unterscheidungszeichen, f.erkennungsnummer, f.ziffern")
    List<Fahrzeug> findAllSorted();
}
