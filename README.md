
# TaskManager – DevOps & Cloud Computing Eindproject

## Overzicht
Dit project vormt een volledig end‑to‑end DevOps‑traject voor de **TaskManager** applicatie.
Het project demonstreert containerisatie, Kubernetes‑orkestratie, Helm‑deployments,
CI/CD‑automatisatie, monitoring en veilige cloud‑exposure via HTTPS.

Dit document combineert **PE2 (Docker & Docker Compose)**, **PE3 (Minikube & Kubernetes)** en
**PE4 (Cloud, Helm, CI/CD, Monitoring & Cloudflare)**.

---

## Architectuuroverzicht
- **Frontend:** Angular (Dockerized)
- **Backend:** Spring Boot (Dockerized)
- **Database:** MySQL
- **Container Registry:** Docker Hub
- **Orkestratie:** Kubernetes (DigitalOcean)
- **Package manager:** Helm
- **CI/CD:** GitHub Actions
- **Monitoring:** Prometheus & Grafana
- **DNS & HTTPS:** Cloudflare Tunnel

---

## PE2 — Docker & Docker Compose

### Beschrijving van de services

**Frontend**  
De frontend is een Angular‑applicatie die de gebruikersinterface van de TaskManager verzorgt.
Gebruikers kunnen zich aanmelden en hun projecten en taken beheren.
De frontend communiceert uitsluitend met de backend via REST‑API’s.

**Backend**  
De backend is een Spring Boot (Java) applicatie die fungeert als API‑laag.
Deze service verwerkt authenticatie (JWT), business‑logica en database‑toegang via JPA/Hibernate.

**Database**  
De database maakt gebruik van MySQL 8.x voor persistente opslag.
Bij het opstarten wordt de database automatisch geïnitialiseerd met een SQL‑script.

---

### Docker Hub images
- Frontend image: https://hub.docker.com/r/r0825266/frontend
- Backend image: https://hub.docker.com/r/r0825266/backend

---

### Docker Compose configuratie

**Services**
- `frontend`: Angular + Nginx (poort 4200)
- `backend`: Spring Boot API (poort 8080)
- `database`: MySQL (enkel intern bereikbaar)

**Communicatie**
- Frontend → Backend: `http://backend:8080/api`
- Backend → Database: `jdbc:mysql://database:3306/taskmanager`

---

### Environment variables
```
SPRING_DATASOURCE_URL=jdbc:mysql://database:3306/taskmanager
SPRING_DATASOURCE_USERNAME=taskuser
SPRING_DATASOURCE_PASSWORD=taskpass
SPRING_JPA_HIBERNATE_DDL_AUTO=none
```

---

### Starten en testen

```bash
docker compose up -d
```

Frontend:
```
http://localhost:4200
```

Backend:
```
POST http://localhost:8080/api/auth/login
```

Testgebruikers:

| Rol | Username | Password |
|----|---------|----------|
| User | user | user123 |
| Admin | admin | admin123 |

---

## PE3 — Kubernetes met Minikube

### Overzicht
In deze fase werd dezelfde drieservicetoepassing gedeployed op Kubernetes via **Minikube**.

### Deployments & Services
- Database: Deployment + ClusterIP
- Backend: Deployment + ClusterIP
- Frontend: Deployment + NodePort

### Netwerk
- Backend → Database via interne service
- Frontend → Backend via port‑forward of NodePort

### Deploy commando’s
```bash
minikube start --memory=4096 --cpus=2
kubectl apply -f k8s-manifests/database
kubectl apply -f k8s-manifests/backend
kubectl apply -f k8s-manifests/frontend
```

### Toegang
```bash
kubectl port-forward service/backend 8080:8080
minikube service frontend
```

---

## PE4 — Cloud Deployment & Automatisatie (Hoofdfocus)

### Fase 1 — Cloud Kubernetes
Een **3‑node DigitalOcean Kubernetes cluster** werd aangemaakt en gevalideerd.

```bash
kubectl get nodes
```

---

### Fase 2 — Helm Deployments

Eén Helm chart wordt gebruikt voor twee omgevingen:

- `taskmanager-test`
- `taskmanager-prod`

```bash
kubectl create ns taskmanager-test
kubectl create ns taskmanager-prod

helm upgrade --install taskmanager ./helm/taskmanager   -n taskmanager-test -f values-test.yaml

helm upgrade --install taskmanager ./helm/taskmanager   -n taskmanager-prod -f values-prod.yaml
```

---

### Fase 3 — CI/CD met GitHub Actions

**Gedrag:**
- Push naar branch → deploy naar TEST
- Git tag (`vX.Y.Z`) → deploy naar PROD

**Pipeline taken:**
- Build & push Docker images
- Kubernetes deploy via Helm

**Secrets:**
- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`
- `KUBECONFIG_BASE64`

---

## Fase 4 — Monitoring (Prometheus & Grafana)

### Overzicht
Monitoring is geïmplementeerd via **kube‑prometheus‑stack**.

### Installatie
```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm upgrade --install monitoring prometheus-community/kube-prometheus-stack   --namespace monitoring --create-namespace
```

### Grafana toegang
```bash
kubectl port-forward -n monitoring svc/monitoring-grafana 3000:80
```

Open Grafana in browser:
```
http://localhost:3000
```

### Step 3 — Login credentials
```text
Username: admin
Password: (opgehaald van Grafana secret)
```

Het wachtwoord kan worden opgehaald met behulp van:
```bash
kubectl get secret -n monitoring monitoring-grafana   -o jsonpath="{.data.admin-password}" | base64 --decode
```

Grafana dashboards tonen:
- CPU & geheugen
- Pod status
- Node gebruik
- Namespace metrics

---

## Fase 5 — Cloudflare Tunnel & HTTPS

De applicatie is veilig publiek toegankelijk gemaakt via **Cloudflare Tunnel**.

**Domeinen:**
- Frontend: https://app.kloran-taskmanager.org
- Backend: https://api.kloran-taskmanager.org           Backend documentatie(swagger): https://api.kloran-taskmanager.org

**Voordelen:**
- HTTPS via Cloudflare
- Geen NodePort exposure
- Geen firewall‑problemen

Cloudflared draait als Deployment binnen Kubernetes.

---

## Resultaat

Alle vereisten van **PE2, PE3 en PE4** zijn succesvol geïmplementeerd:
- Lokale Docker setup
- Kubernetes orchestration
- Cloud deployment
- CI/CD automatisatie
- Monitoring
- Veilige HTTPS exposure

Dit project demonstreert een volledige DevOps workflow van development tot productie.