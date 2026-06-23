package SeTre.RecruiterFahrzeug.repository;

import SeTre.RecruiterFahrzeug.model.Termin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TerminRepository extends JpaRepository<Termin, Integer> {

    // prüft auf sich überschneidende Termine für das Fahrzeug ---> für addTermin addReservierung & editReservierung
    @Query("SELECT t FROM Termin t WHERE t.fahrzeugId = :fahrzeugId " +
            "AND (t.beginn < :ende AND t.ende > :beginn)")     // Überschneidung abfragen
    List<Termin> findOverlappingTermine(@Param("fahrzeugId") Integer fahrzeugId,
                                        @Param("beginn") LocalDateTime beginn,
                                        @Param("ende") LocalDateTime ende);


    // prüft auf sich überschneidende Termine für das Fahrzeug ---> für editTermin
    @Query("SELECT t FROM Termin t WHERE t.fahrzeugId = :fahrzeugId " +
            "AND (t.beginn < :ende AND t.ende > :beginn) " +     // Überschneidung abfragen
            "AND t.id <> :terminId")  // aktuellen Termin ausschließen
    List<Termin> findOverlappingTermineEdit(@Param("fahrzeugId") Integer fahrzeugId,
                                            @Param("beginn") LocalDateTime beginn,
                                            @Param("ende") LocalDateTime ende,
                                            @Param("terminId") Integer terminId);

    public long countByFahrzeugId(int fahrzeugId);

    // prüft auf anstehende Termine
    @Query("SELECT t FROM Termin t WHERE t.beginn BETWEEN :start AND :ende " +
            "ORDER BY t.beginn ASC")
    List<Termin> findTerminAnstehende (@Param("start") LocalDateTime start, @Param("ende") LocalDateTime ende);

    // Listet laufende und anstehende Termin
    @Query("SELECT t FROM Termin t WHERE t.ende >= CURRENT_TIMESTAMP " +
            "ORDER BY t.beginn ASC")
    List<Termin> findPresent ();

    // Listet vergangene Termine
    @Query("SELECT t from Termin t WHERE t.ende < CURRENT_TIMESTAMP " +
            "ORDER BY t.beginn DESC")
    List<Termin> findPast ();

    Page<Termin> findByEndeBefore(LocalDateTime endDate, Pageable pageable);
    Page<Termin> findByEndeAfter(LocalDateTime startDate, Pageable pageable);
}
