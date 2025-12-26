# TaskManager API

Een professionele REST API voor project- en taakbeheer, gebouwd met Spring Boot.
De applicatie implementeert JWT-authenticatie, rolgebaseerde autorisatie, ownership-regels, uitgebreide unit- en integratietests en en OpenAPI/Swagger documentatie.

---

## Technologieën

- Java 23
- Spring Boot 3.5.7
- Spring Security met JWT authenticatie
- Spring Data JPA & Hibernate
- Hibernate Validator (Jakarta Bean Validation)
- H2 Database (testprofiel)
- MySQL (productie)
- SpringDoc OpenAPI (Swagger UI)
- Maven
- JUnit 5, Mockito & Spring Boot Test

---

## Functionaliteiten

### Authenticatie & Gebruikersbeheer
- Registreren en inloggen van gebruikers
- JWT bevat gebruikersnaam en rol (USER / ADMIN)
- Beveiligde endpoints via Spring Security
- Token gebruik via Authorization header:
  Authorization: Bearer <token>

### Project Management
- Projecten aanmaken
- Elk project heeft exact één eigenaar
- Alleen de eigenaar heeft toegang tot zijn project en taken

### Task Management
- Taken aanmaken binnen projecten
- Taken bekijken (gepagineerd)
- Filteren op status: TODO, IN_PROGRESS, DONE
- PUT en PATCH ondersteuning

#### Enkele Business Rules
- DONE taken mogen niet aangepast worden
- DONE taken mogen niet verwijderd worden
- PATCH op DONE taken enkel toegestaan voor statuswijziging

### Autorisatie
- USER: enkel eigen projecten en taken
- ADMIN: kan taken verwijderen ongeacht eigenaar
- Ownership wordt afgeleid uit JWT + project owner

---

## Testing

### Unit Tests
- Service-laag getest met Mockito
- Business rules en exceptions afgedekt

### Integratie Tests
- SpringBootTest + MockMvc
- Auth, Project en Task controllers getest
- Security en ownership scenario’s inbegrepen

Tests uitvoeren:
mvn test

---

## Profielen en configuratie
De applicatie ondersteunt meerdere Spring profiles.

dev
- Lokale development omgeving
- MySQL database
- Uitgebreide logging
- Geschikt voor development en debugging

test
- Automatisch gebruikt bij testen
- H2 in-memory database
- Elke test draait transactioneel met rollback
- Volledig geïsoleerd van productiegegevens

prod
- Productieconfiguratie
- MySQL database
- Geen testdata of development tooling
- Geoptimaliseerd voor performance en security
- Geschikt voor deployment

Actief profiel instellen: spring.profiles.active=prod

Of via Maven: mvn spring-boot:run -Dspring-boot.run.profiles=prod

| Profiel  | Database          | Gebruik                                     |
| -------- | ----------------- | ------------------------------------------- |
| default  | MySQL             | Lokale developmentomgeving                  |
| test     | H2 (in-memory)    | Geautomatiseerde tests (unit & integration) |
| prod     | MySQL             | Productieomgeving                           |

Het testprofiel wordt automatisch gebruikt bij integratietests.

---

## Projectstructuur
```text
## 📁 Projectstructuur

```text
src/main/java/be/vives/taskmanager/
├── api/
│   └── controller/        # REST controllers
├── application/
│   ├── service/           # Business logic
│   ├── dto/               # Request / Response DTOs
│   ├── mapper/            # Entity ↔ DTO mapping
│   └── exception/         # Custom exceptions
├── domain/
│   └── model/             # Entities & enums
├── infrastructure/
│   ├── persistence/       # JPA repositories
│   └── security/          # JWT & security configuratie
├── config/                # Applicatie config
│   ├── PasswordConfig
│   ├── SecurityConfig
│   ├── OpenApiConfig
│   └── DataInitializer
└── TaskmanagerApplication.java
```

---

## API Base URL

De backend API is bereikbaar via onderstaande base URL.

- **Lokaal (development):**  
  http://localhost:8080

- **Productie (deployment):**  
  https://api.taskmanager.be

Alle API-endpoints zijn bereikbaar onder het `/api` pad, bijvoorbeeld:

- `/api/auth/login`
- `/api/projects`
- `/api/tasks`

---

## API-endpoints

Alle endpoints zijn voorafgegaan door /api en beveiligd met JWT-authenticatie, tenzij anders vermeld.

### Authenticatie (/api/auth)

| Methode | Endpoint             | Beschrijving                                             | Toegang |
| ------- | -------------------- | -------------------------------------------------------- | ------- |
| `POST`  | `/api/auth/register` | Registreert een nieuwe gebruiker met rol **USER**        | Publiek |
| `POST`  | `/api/auth/login`    | Authenticeert een gebruiker en retourneert een JWT-token | Publiek |

### Gebruikersbeheer (/api/user)

| Methode | Endpoint              |   Beschrijving                                       | Toegang        |
| ------- | --------------------  | ---------------------------------------------------- | -------        |
| `POST`  | `/api/user/{id}/role` | Update van een use role naar **ADMIN**               | **Admin only** |

### Projectbeheer (/api/projects)

Alle project-endpoints vereisen authenticatie.
Gebruikers hebben alleen toegang tot hun eigen projecten.
Sommige acties zijn enkel voor ADMIN

| Methode  | Endpoint             | Beschrijving                                                             | Toegang        |
| -------- | -------------------- | ------------------------------------------------------------------------ | -------------- |
| `GET`    | `/api/projects`      | Haalt een gepagineerde lijst op van projecten van de ingelogde gebruiker | USER / ADMIN   |
| `GET`    | `/api/projects/{id}` | Haalt een project op via ID (alleen indien eigenaar)                     | USER / ADMIN   |
| `POST`   | `/api/projects`      | Maakt een nieuw project aan voor de ingelogde gebruiker                  | USER / ADMIN   |
| `PUT`    | `/api/projects/{id}` | Volledige update van een project                                         | USER / ADMIN   |
| `PATCH`  | `/api/projects/{id}` | Gedeeltelijke update van een project                                     | USER / ADMIN   |
| `DELETE` | `/api/projects/{id}` | Verwijdert een project (mag geen actieve taken bevatten)                 | **ADMIN only** |

### Taakbeheer (/api)

Taken zijn altijd gekoppeld aan een project van de gebruiker.
Voltooide taken (DONE) hebben beperkingen

| Methode  | Endpoint                                      | Beschrijving                                                          | Toegang        |
| -------- | --------------------------------------------- | --------------------------------------------------------------------- | -------------- |
| `GET`    | `/api/tasks/{id}`                             | Haalt een taak op via ID (indien project eigendom is)                 | USER / ADMIN   |
| `GET`    | `/api/projects/{projectId}/tasks`             | Haalt alle taken van een project op (gepagineerd)                     | USER / ADMIN   |
| `GET`    | `/api/projects/{projectId}/tasks?status=TODO` | Haalt taken op gefilterd op status                                    | USER / ADMIN   |
| `POST`   | `/api/projects/{projectId}/tasks`             | Maakt een nieuwe taak aan binnen een project                          | USER / ADMIN   |
| `PUT`    | `/api/tasks/{id}`                             | Volledige update van een taak (niet toegestaan indien status `DONE`)  | USER / ADMIN   |
| `PATCH`  | `/api/tasks/{id}`                             | Gedeeltelijke update. Bij `DONE` mag enkel de status gewijzigd worden | USER / ADMIN   |
| `DELETE` | `/api/tasks/{id}`                             | Verwijdert een taak (voltooide taken kunnen niet verwijderd worden)   | **ADMIN only** |

---

## API Documentatie

### Lokaal (development)
Swagger UI is beschikbaar op:
http://localhost:8080/swagger-ui.html

### Productie (gehost)
Na deployment is de Swagger UI beschikbaar via de publieke backend-URL, bijvoorbeeld:
https://api.taskmanager.be/swagger-ui.html

---

## Applicatie starten

```bash
mvn spring-boot:run
```
of
```
./mvnw spring-boot:run
```

---

## Testaccounts (Demodata)

Voor test- en evaluatiedoeleinden wordt de applicatie automatisch voorzien van demo-gebruikers wanneer deze wordt gestart met het dev- of prod-profiel.

### Administratoraccount

Gebruikersnaam: admin

Wachtwoord: admin123

Rol: ADMIN

Rechten: Volledige toegang (projecten, taken, verwijderacties)

### Standaard gebruikersaccount

Gebruikersnaam: user

Wachtwoord: user123

Rol: USER

Rechten: Beheer van eigen projecten en taken(zonder verwijderacties)

Deze accounts worden automatisch aangemaakt bij het opstarten van de applicatie via een data-initializer.
De initializer is idempotent en zal geen dubbele data aanmaken wanneer de database reeds gebruikers bevat.

---

## Educatieve Context

Dit project demonstreert:
- REST API ontwerp
- JWT security
- Role-based access control
- Ownership & business rules
- Unit- en integratietesting
- Spring Boot enterprise architectuur
