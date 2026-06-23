package SeTre.RecruiterFahrzeug.model;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

@Component
@Entity
public class Fahrzeug {
    @Id
    @SequenceGenerator(name = "fahrzeug_sequence", sequenceName = "fahrzeug_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fahrzeug_sequence")
    @Column(name="id", nullable = false)
    private int id;

    @Column(name = "unterscheidungszeichen", nullable = false, length = 3)
    private String unterscheidungszeichen;

    @Column(name = "erkennungsnummer", nullable = false,length = 2)
    private String erkennungsnummer;

    @Column(name = "ziffern", nullable = false, length = 4)
    private String ziffern;

    @Column(name="hersteller", nullable = false, length = 20)
    private String hersteller;

    @Column(name="modell", nullable = false, length = 20)
    private String modell;

    @Column(name="aktiv", nullable = false)
    private Boolean aktiv;

    // Getter & Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnterscheidungszeichen() {
        return unterscheidungszeichen;
    }

    public void setUnterscheidungszeichen(String unterscheidungszeichen) {
        this.unterscheidungszeichen = unterscheidungszeichen;
    }

    public String getErkennungsnummer() {
        return erkennungsnummer;
    }

    public void setErkennungsnummer(String erkennungsnummer) {
        this.erkennungsnummer = erkennungsnummer;
    }

    public String getZiffern() {
        return ziffern;
    }

    public void setZiffern(String ziffern) {
        this.ziffern = ziffern;
    }

    public String getHersteller() {
        return hersteller;
    }

    public void setHersteller(String hersteller) {
        this.hersteller = hersteller;
    }

    public String getModell() {
        return modell;
    }

    public void setModell(String modell) {
        this.modell = modell;
    }

    public Boolean getAktiv() {
        return aktiv;
    }

    public void setAktiv(Boolean aktiv) {
        this.aktiv = aktiv;
    }
}
