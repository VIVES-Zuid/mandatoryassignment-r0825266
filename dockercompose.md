## Docker Compose Deployment Documentatie

### 1. Korte beschrijving van de drie services

**Frontend**
De frontend is een Angular-applicatie die de gebruikersinterface van de Task Manager applicatie verzorgt. Gebruikers kunnen zich aanmelden en hun projecten en taken bekijken. De frontend communiceert uitsluitend met de backend via REST-API’s.

**Backend**
De backend is een Spring Boot (Java) applicatie die fungeert als API-laag. Deze service verwerkt login- en authenticatieverzoeken (JWT), beheert projecten en taken en communiceert met de MySQL-database via JPA/Hibernate.

**Database**
De database gebruikt MySQL 8.x voor persistente opslag. De database bevat tabellen voor users, projects en tasks. Bij het opstarten wordt de database automatisch geïnitialiseerd met een SQL-script.

---

### 2. Docker Hub images
* Frontend image: [https://hub.docker.com/r/r0825266/frontend]
* Backend image: [https://hub.docker.com/r/r0825266/backend]

---

### 3. Uitleg van docker-compose.yml

**Services**
- frontend: Angular + Nginx, bereikbaar via poort 4200
- backend: Spring Boot API, bereikbaar via poort 8080
- database: MySQL 8.x, enkel intern bereikbaar

**Communicatie**
- Frontend → Backend: http://backend:8080/api
- Backend → Database: jdbc:mysql://database:3306/taskmanager

---

### 4. Environment variables

SPRING_DATASOURCE_URL=jdbc:mysql://database:3306/taskmanager  
SPRING_DATASOURCE_USERNAME=taskuser  
SPRING_DATASOURCE_PASSWORD=taskpass  
SPRING_JPA_HIBERNATE_DDL_AUTO=none  

---

### Good catch — this section needs to be **clear, testable, and consistent**.
Here is a **clean, corrected version** you can paste straight into your `dockercompose.md`.

---

## 5. Toegang & Testen

### Starten van de applicatie

Start alle drie de services met Docker Compose:

```bash
docker compose up -d
```

Controleer of alle containers correct draaien:

```bash
docker compose ps
```

### Backend testen

De backend API is bereikbaar via:

```
http://localhost:8080/api
```

Voorbeeld test (login endpoint):

```
POST http://localhost:8080/api/auth/login
```

Met JSON body:

```json
{
  "username": "user",
  "password": "user123"
}
```

Een succesvolle login geeft een **JWT-token** terug.

### Frontend testen

De frontend is bereikbaar via:

```
http://localhost:4200
```

Bij het openen van de applicatie verschijnt het login-scherm.
Na succesvol inloggen wordt de gebruikersinterface met projecten en taken getoond.

### Testgebruikers

De database wordt geïnitialiseerd met de volgende testaccounts:

| Gebruiker     | Username | Password   |
| ------------- | -------- | ---------- |
| Regular user  | `user`   | `user123`  |
| Administrator | `admin`  | `admin123` |

### Verwacht resultaat

* Frontend kan communiceren met de backend
* Backend kan data ophalen uit de database
* Authenticatie werkt correct voor beide gebruikersrollen

---

### 6. Screenshots

**Terminal-output van `docker compose up`:**  
![Terminal Screenshot](./screenshots/terminal.png)

**Browserweergave van de werkende frontend:**  
![Frontend Screenshot](./screenshots/frontend.png)

**API Test Screenshot:**  
![API Screenshot](./screenshots/backend.png)

**Running containers binnen Docker:**  
![Docker containers Screenshot](./screenshots/DockerDesktopImages.png)

**Frontend en Backend images binnen DockerHub:**  
![DockerHub images Screenshot](./screenshots/Dockerhub.png)

---

### 7. Reflectie

Deze opdracht toonde hoe een multi-service applicatie lokaal kan worden opgezet met Docker Compose. De focus lag op correcte service-communicatie, database-initialisatie en image-hergebruik voor Kubernetes.
