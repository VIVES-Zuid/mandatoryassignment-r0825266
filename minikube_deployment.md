# Minikube Deployment Documentatie

## 1. Inleiding

In deze opdracht werd de bestaande drieservicetoepassing (Angular frontend, Spring Boot backend en MySQL database) gedeployed op Kubernetes via Minikube.

---

## 2. Overzicht van de services

**Frontend**
Angular frontend gedeployed als Deployment met NodePort service voor externe toegang.

**Backend**
Spring Boot backend die API-logica, JWT-authenticatie en database-communicatie verzorgt.

**Database**
MySQL database met ConfigMap voor databaseconfiguratie en Secret voor credentials.

---

## 3. Secrets & ConfigMaps

Secrets bevatten databasegebruikers en wachtwoorden.  
ConfigMaps bevatten databanknaam en configuratie.

---

## 4. Netwerk

- Backend → Database: jdbc:mysql://database:3306/taskmanager
- Frontend → Backend: via port-forward (NodePort werd ook getest voor leerdoeleinden)

---

## 5. Deployments & Services

- Database: Deployment + ClusterIP service
- Backend: Deployment + ClusterIP service
- Frontend: Deployment + NodePort service

---

## 6. Deployen naar Minikube

minikube start --memory=4096 --cpus=2  
kubectl apply -f k8s-manifests/database  
kubectl apply -f k8s-manifests/backend  
kubectl apply -f k8s-manifests/frontend 

### **Controle**
kubectl get all
kubectl logs deployment/backend
kubectl logs deployment/frontend
kubectl logs deployment/mongo

---

## 7. Toegang

Backend testen:
kubectl port-forward service/backend 8080:8080

Frontend openen:
minikube service frontend

Test user login details:
Username: user               Username: admin
Password: user123            Password: admin 123

---

## 8. Validatie

kubectl get pods  
kubectl get svc  
kubectl logs deploy/backend  

---

## 7. Screenshots

**1. Terminal-output van Minikube deploy en suceesvol opstart**
![Deployment](./screenshots/Deployment_to_minikube.png)
![Frontend](./screenshots/Minikube_started_successfully1.png)

**2. Browserweergave van de frontend via NodePort**
![Frontend](./screenshots/terminalStartFrontend.png) 
![Frontend](./screenshots/frontendLoginPage.png)

**3. Backend logs, frontend logs, database logs, kubectl get all**
![kubectl getAll](./screenshots/kubectl_getAll.png)
![Frontend](./screenshots/kubectl_logs_backend.png)
![Frontend](./screenshots/kubectl_logs_frontend.png)
![Frontend](./screenshots/kubectl_logs_database.png)

---

## 9. Reflectie

Deze opdracht gaf inzicht in Kubernetes Deployments, Services, ConfigMaps en Secrets. De Minikube-opzet vormt de basis voor verdere cloud-deployment.