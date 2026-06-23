package SeTre.RecruiterFahrzeug.repository;

import SeTre.RecruiterFahrzeug.model.Reservierung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservierungRepository extends JpaRepository<Reservierung, Integer> {
    // prüft auf sich überschneidende Reservierungen für das Fahrzeug ---> für addReservierung
    @Query("SELECT r FROM Reservierung r WHERE r.fahrzeugId = :fahrzeugId " +
            "AND (r.beginn < :ende AND r.ende > :beginn)")
    List<Reservierung> findOverlappingReservierungen(@Param("fahrzeugId") Integer fahrzeugId,
                                                     @Param("beginn") LocalDateTime beginn,
                                                     @Param("ende") LocalDateTime ende);


    // prüft auf sich überschneidende Reservierungen für das Fahrzeug ---> für editReservierung
    @Query("SELECT r FROM Reservierung r WHERE r.fahrzeugId = :fahrzeugId " +
            "AND (r.beginn < :ende AND r.ende > :beginn) " +     // Überschneidung abfragen
            "AND r.id <> :reservierungId")  // aktuelle Reservierung ausschließen
    List<Reservierung> findOverlappingReservierungenEdit(@Param("fahrzeugId") Integer fahrzeugId,
                                                         @Param("beginn") LocalDateTime beginn,
                                                         @Param("ende") LocalDateTime ende,
                                                         @Param("reservierungId") Integer reservierungId);

    public long countByFahrzeugId(int fahrzeugId);

    // prüft auf anstehende Reservierungen
    @Query("SELECT r FROM Reservierung r WHERE r.beginn BETWEEN :start AND :ende " +
            "ORDER BY r.beginn ASC")
    List<Reservierung> findReservierungAnstehende (@Param("start") LocalDateTime start, @Param("ende") LocalDateTime ende);


    // Listet laufende und anstehende Reservierungen
    @Query("SELECT r FROM Reservierung r WHERE r.ende >= CURRENT_TIMESTAMP " +
            "ORDER BY r.beginn ASC")
    List<Reservierung> findPresent ();

    // Listet vergangene Reservierungen
    @Query("SELECT r from Reservierung r WHERE r.ende < CURRENT_TIMESTAMP " +
            "ORDER BY r.beginn DESC")
    List<Reservierung> findPast ();

    Page<Reservierung> findByEndeBefore(LocalDateTime endDate, Pageable pageable);
    Page<Reservierung> findByEndeAfter(LocalDateTime startDate, Pageable pageable);
}
