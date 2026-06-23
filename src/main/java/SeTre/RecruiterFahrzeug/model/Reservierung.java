package SeTre.RecruiterFahrzeug.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Reservierung {
    @Id
    @SequenceGenerator(name = "termin_sequence", sequenceName = "termin_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "termin_sequence")
    @Column(name="id", nullable = false)
    private Integer id;

    @Column(name = "fahrzeug_id", nullable = false)
    private Integer fahrzeugId;

    @Column(nullable = false)
    private LocalDateTime beginn;

    @Column(nullable = false)
    private LocalDateTime ende;

    @Column(nullable = false, length = 50)
    private String mitarbeiter;

    @Column(length = 500)
    private String anmerkung;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFahrzeugId() {
        return fahrzeugId;
    }

    public void setFahrzeugId(Integer fahrzeugId) {
        this.fahrzeugId = fahrzeugId;
    }

    public LocalDateTime getBeginn() {
        return beginn;
    }

    public void setBeginn(LocalDateTime beginn) {
        this.beginn = beginn;
    }

    public LocalDateTime getEnde() {
        return ende;
    }

    public void setEnde(LocalDateTime ende) {
        this.ende = ende;
    }

    public String getMitarbeiter() {
        return mitarbeiter;
    }

    public void setMitarbeiter(String mitarbeiter) {
        this.mitarbeiter = mitarbeiter;
    }

    public String getAnmerkung() {
        return anmerkung;
    }

    public void setAnmerkung(String anmerkung) {
        this.anmerkung = anmerkung;
    }
}
