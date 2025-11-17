
## 1. Korte beschrijving van de drie services

**Frontend**
De frontend is een Angular-applicatie die de gebruikersinterface van onze Warehouse-applicatie verzorgt. Hier kunnen gebruikers de beschikbare items bekijken, en de gegevens worden dynamisch opgehaald van de backend via API-aanvragen.

**Backend**
De backend is een Node.js/Express API die fungeert als tussenlaag tussen de frontend en de database. Deze service verwerkt API-aanvragen van de frontend en haalt de juiste gegevens op uit de MongoDB-database. De backend zorgt er ook voor dat de data consistent en correct wordt aangeboden.

**Database**
De database gebruikt MongoDB als opslag voor onze warehouse-items. Hier worden alle itemgegevens, zoals naam, hoeveelheid en locatie, opgeslagen. De database wordt automatisch geïnitialiseerd met enkele voorbeelditems via een init-script.

---

## 2. Docker Hub links

* Frontend image: [https://hub.docker.com/r/r0825266/frontend]
* Backend image: [https://hub.docker.com/r/r0825266/backend]

---

## 3. Uitleg over het Docker Compose bestand

**Services in `docker-compose.yml`:**

* `frontend`: de Angular-app die via poort 4200 bereikbaar is.
* `backend`: de Node.js API, bereikbaar via poort 5000, verbindt met de database.
* `database`: MongoDB, gebruikt een volume voor persistente opslag en een init-script om standaardgegevens te laden.

**Communicatie tussen services:**

* De frontend stuurt HTTP-aanvragen naar de backend (`http://localhost:5000/api/warehouse-items`).
* De backend maakt verbinding met de MongoDB-database en de opgegeven poort intern in het netwerk.

**Open poorten:**

* Frontend: `4200` (externe toegang voor gebruikers)
* Backend: `5000` (open voor API-testen)
* Database: geen externe poort gemapt; alleen interne communicatie met backend.

---

## 4. Gebruikte environment variables

In het `.env` bestand van de backend staan de volgende variabelen:

MONGO_URL=mongodb://database:27017/warehouseDB
PORT=5000


* `MONGO_URL`: URL voor de backend om verbinding te maken met de database.
* `PORT`: poort waarop de backend luistert.

---

## 5. Screenshots

**Terminal-output van `docker compose up`:**  
![Terminal Screenshot](./screenshots/terminal.png)

**Browserweergave van de werkende frontend:**  
![Frontend Screenshot](./screenshots/frontend.png)

**API Test Screenshot:**  
![API Screenshot](./screenshots/backend.png)

**Running containers binnen Docker:**  
![API Screenshot](./screenshots/DockerDesktopImages.png)

**Frontend en Backend images binnen DockerHub:**  
![API Screenshot](./screenshots/Dockerhub.png)

---

## 6. Conclusie / Reflectie

Tijdens deze opdracht heb ik geleerd hoe je een multi-service applicatie opzet met Docker Compose. Het belangrijkste inzicht was het correct configureren van netwerken en dependencies tussen services, zodat frontend, backend en database naadloos met elkaar communiceren.

Een uitdaging was het opzetten van de MongoDB-init script en ervoor zorgen dat de backend correct wacht tot de database beschikbaar is. Uiteindelijk werkte alles soepel en heb ik ook geleerd hoe ik mijn images kan publiceren op Docker Hub en deze kan gebruiken in Docker Compose.

