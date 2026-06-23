CREATE DATABASE fahrzeugdb
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LOCALE_PROVIDER = 'libc'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;



---------------- die Fahrzeug-Tabelle erstellen ----------------
-- Sequenz für den Primary Key der Fahrzeugtabelle erstellen
--DROP SEQUENCE IF EXISTS fahrzeug_sequence CASCADE;
CREATE SEQUENCE IF NOT EXISTS fahrzeug_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Fahrzeug Tabelle erstellen
--DROP TABLE IF EXISTS fahrzeug CASCADE;
CREATE TABLE IF NOT EXISTS public.fahrzeug
(
    id INTEGER DEFAULT nextval('fahrzeug_sequence'),
    unterscheidungszeichen VARCHAR(3) NOT NULL CHECK (unterscheidungszeichen ~ '^[A-ZÄÖÜß]{1,3}$'),
    erkennungsnummer VARCHAR(2) NOT NULL CHECK (erkennungsnummer ~ '^[A-Z]{1,2}$'),
    ziffern VARCHAR(4) NOT NULL CHECK (ziffern ~ '^[1-9]([0-9]{0,3}$|[0-9]{0,2}[EH]?$)$'),
    hersteller VARCHAR(20) NOT NULL CHECK(hersteller ~ '^[A-Za-z0-9ßäöüÄÖÜ\s\.\,\(\)\&\+\?\/\-]{1,20}$'),
    modell VARCHAR(20) NOT NULL CHECK(modell ~ '^[A-Za-z0-9ßäöüÄÖÜ\s\.\,\(\)\&\+\?\/\-]{1,20}$'),
    aktiv BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_fahrzeug PRIMARY KEY (id),
    CONSTRAINT max_length_fahrzeug CHECK (length(unterscheidungszeichen) + length(erkennungsnummer) + length(ziffern) <= 8)
);

-- UNIQUE-Constraint mit Bedingung, dass aktiv = TRUE sein muss
CREATE UNIQUE INDEX unique_fahrzeug_aktiv ON public.fahrzeug (unterscheidungszeichen, erkennungsnummer, ziffern)
WHERE aktiv = TRUE;

-- Eigentümer setzen
ALTER TABLE IF EXISTS public.fahrzeug
    OWNER to postgres;



---------------- die Termin-Tabelle erstellen ----------------
--DROP SEQUENCE IF EXISTS termin_sequence CASCADE;
CREATE SEQUENCE IF NOT EXISTS termin_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--DROP TABLE IF EXISTS termin CASCADE;
CREATE TABLE IF NOT EXISTS public.termin (
    id INTEGER DEFAULT nextval('termin_sequence'),
    fahrzeug_id INTEGER NOT NULL,
    beginn TIMESTAMP without time zone NOT NULL CHECK (beginn >= NOW()),
    ende TIMESTAMP without time zone NOT NULL CHECK (ende >= beginn),
    art VARCHAR(50),
    mitarbeiter VARCHAR(50) NOT NULL,
    anmerkung VARCHAR(500),
    CONSTRAINT pk_termin PRIMARY KEY (id),
    CONSTRAINT fk_termin_f_id
        FOREIGN KEY (fahrzeug_id)
        REFERENCES fahrzeug(id)
        ON DELETE CASCADE
);
ALTER TABLE IF EXISTS public.termin
    OWNER to postgres;



---------------- die Reservierung-Tabelle erstellen ----------------
--DROP SEQUENCE IF EXISTS reservierung_sequence CASCADE;
CREATE SEQUENCE IF NOT EXISTS reservierung_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--DROP TABLE IF EXISTS reservierung CASCADE;
CREATE TABLE IF NOT EXISTS public.reservierung (
    id INTEGER DEFAULT nextval('reservierung_sequence'),
    fahrzeug_id INTEGER NOT NULL,
    beginn TIMESTAMP without time zone NOT NULL CHECK (beginn >= NOW()),
    ende TIMESTAMP without time zone NOT NULL CHECK (ende >= beginn),
    mitarbeiter VARCHAR(50) NOT NULL,
    anmerkung VARCHAR(500),
    CONSTRAINT pk_reservierung PRIMARY KEY (id),
    CONSTRAINT fk_reservierung_f_id
        FOREIGN KEY (fahrzeug_id)
        REFERENCES fahrzeug(id)
        ON DELETE CASCADE
);
ALTER TABLE IF EXISTS public.reservierung
    OWNER to postgres;


---------------- Fahrzeug-Testdaten ----------------
INSERT INTO public.fahrzeug (unterscheidungszeichen, erkennungsnummer, ziffern, hersteller, modell, aktiv)
VALUES
('DO', 'AB', '1234', 'Volkswagen', 'Golf', FALSE),
('DO', 'CD', '5678', 'BMW', '320i', FALSE),
('DO', 'AB', '1234', 'Volkswagen', 'Golf', TRUE),
('DO', 'CD', '5678', 'BMW', '320i', TRUE),
('DO', 'EF', '9012', 'Audi', 'A4', TRUE),
('DO', 'GH', '3456', 'Mercedes-Benz', 'C-Class', TRUE),
('DO', 'IJ', '7890', 'Ford', 'Focus', TRUE),
('DO', 'KL', '2345', 'Volkswagen', 'Passat', TRUE),
('DO', 'MN', '6789', 'Opel', 'Astra', TRUE),
('DO', 'OP', '1122', 'BMW', 'X5', TRUE),
('DO', 'QR', '3344', 'Toyota', 'Corolla', TRUE),
('DO', 'ST', '5566', 'Renault', 'Clio', TRUE),
('DO', 'UV', '7788', 'Peugeot', '208', TRUE),
('DO', 'WX', '9900', 'Hyundai', 'Tucson', TRUE),
('DO', 'YZ', '123A', 'Nissan', 'Qashqai', TRUE),
('DO', 'AB', '456B', 'Kia', 'Sportage', TRUE),
('DO', 'CD', '789C', 'Mazda', 'CX-5', TRUE),
('DO', 'EF', '12D', 'Honda', 'Civic', TRUE),
('DO', 'GH', '345E', 'Subaru', 'Outback', TRUE),
('DO', 'IJ', '678F', 'Ford', 'Fiesta', TRUE),
('DO', 'KL', '234G', 'Chevrolet', 'Cruze', TRUE),
('DO', 'MN', '789H', 'Volkswagen', 'Tiguan', TRUE);


