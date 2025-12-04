Here is your complete **`minikube_deployment.md`**, written in a **natural, human, student-style tone**, ready to paste into your repository and submit.

You do **not** need to edit anything except inserting your screenshots when you make them.

---

# Minikube Deployment Documentatie (`minikube_deployment.md`)

## 1. Inleiding

In dit deel van de opdracht heb ik mijn bestaande drieservicetoepassing (Angular frontend, Node.js backend en MongoDB database) opnieuw opgezet, maar deze keer niet met Docker Compose, maar met **Kubernetes op Minikube**.
Het doel is om dezelfde applicatie als in deel 2 volledig te containeriseren en correct te deployen in een Kubernetes-cluster, waarbij elke service zijn eigen Deployment en Service krijgt.

---

## 2. Overzicht van de drie services

### **Frontend (Angular)**

De Angular-app visualiseert de inhoud van de databank. Ze haalt warehouse-items op via de backend API.
Deze draait in Kubernetes als een Deployment met NodePort zodat ik hem extern kan openen.

### **Backend (Node.js + Express API)**

De backend verzorgt alle API-logica.
Hij maakt verbinding met de MongoDB-service binnen het cluster via de interne servicenaam `mongo`.
De backend haalt alle warehouse-items op en stuurt ze door naar de frontend.

### **Database (MongoDB)**

De MongoDB container draait in Kubernetes als een aparte Deployment met:

* een Secret voor de username + password
* een ConfigMap voor de databasenaam
* een eigen ClusterIP service

De database is enkel intern toegankelijk vanuit de backend.

---

## 3. MongoDB Credentials
Deze referenties werden gebruikt in de Kubernetes Secret.
De waarden in het secret.yaml-bestand zijn Base64-gecodeerde versies van het volgende:

MONGO_USER = warehouse_user_Andang  
MONGO_PASSWORD = Andang@79

Base64 encoding:
warehouse_user → d2FyZWhvdXNlX3VzZXJfQW5kYW5n
warehouse_pass → QW5kYW5nQDc5

Gegenereerd in CLI(powershell) met: 
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes("warehouse_user_Andang"))
d2FyZWhvdXNlX3VzZXJfQW5kYW5n
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes("Andang@79"))
QW5kYW5nQDc5
---

## 4. ConfigMap & Secret voor MongoDB

Ik gebruik een ConfigMap voor de databanknaam en een Secret voor de login-gegevens.
De backend maakt verbinding via:

```
mongodb://warehouse_user:warehouse_pass@mongo:27017/warehouseDB?authSource=admin
```

Dit maakt de communicatie veilig en volgens best practices.

---

## 5. Deployments & Services

### **Database**

* **Deployment:** `mongo` image + omgevingsvariabelen uit Secret/ConfigMap
* **Service:** ClusterIP, bereikbaar als `mongo:27017` binnen het cluster

### **Backend**

* **Deployment:** gebruikt Docker Hub image `r0825266/backend:latest`
* **Service:** ClusterIP op poort 5000

### **Frontend**

* **Deployment:** gebruikt Docker Hub image `r0825266/frontend:latest`
* **Service:** NodePort (poort 31000) zodat ik de frontend extern kan openen via Minikube

---

## 6. Deployen naar Minikube

### **Starten van het Minikube cluster**

minikube start --memory=4096 --cpus=2


### **Deployments toepassen**

kubectl apply -f k8s-manifests/database
kubectl apply -f k8s-manifests/backend
kubectl apply -f k8s-manifests/frontend


### **Controle**
kubectl get all
kubectl logs deployment/backend
kubectl logs deployment/frontend
kubectl logs deployment/mongo

### **Frontend openen**

1. Haal het Minikube IP op: minikube ip

2. Open de NodePort: http://<minikube-ip>:31000


De applicatie toont dan de lijst met warehouse-items die uit de database komen.

---

## 7. Screenshots

**1. Terminal-output van Minikube deploy**


**2. Browserweergave van de frontend via NodePort**



**3. Backend logs, database logs, kubectl get svc**



---

## 8. Reflectie

Dit deel van de opdracht gaf me een goed inzicht in hoe container-gebaseerde applicaties evolueren van Docker Compose naar Kubernetes. In het begin was het even wennen om alles op te delen in Deployments, Services, ConfigMaps en Secrets, maar eenmaal de structuur duidelijk werd, zag ik hoe krachtig en flexibel Kubernetes eigenlijk is.

Een kleine uitdaging was het correct configureren van de database-authenticatie in combinatie met de backend, maar door Secrets en ConfigMaps te gebruiken werd het geheel overzichtelijk en veilig.
Ook het werken met NodePort en het ontdekken van het Minikube IP was leerrijk.

Al bij al heb ik door deze oefening een veel beter begrip gekregen van hoe cloud-native applicaties er in de praktijk uitzien.
