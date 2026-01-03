DevOps en Cloud Computing

The project provides two Docker Compose setups:

A root-level compose file for running the complete system.

A database-only compose file (database/docker-compose.yml)for local backend development.






# TaskManager – DevOps & Cloud Computing Final Project

## Overview
This project is a full end‑to‑end DevOps pipeline for a TaskManager application.
It demonstrates containerization, Kubernetes orchestration, Helm deployments,
CI/CD automation, monitoring, and secure cloud exposure.

---

## Architecture Overview
- Frontend: Angular (Dockerized)
- Backend: Spring Boot (Dockerized)
- Database: MySQL
- Registry: Docker Hub
- Orchestration: Kubernetes (DigitalOcean)
- CI/CD: GitHub Actions
- Monitoring: Prometheus & Grafana
- DNS & HTTPS: Cloudflare

---

## Phase 1 — Baseline (PE3)
Docker Compose and Minikube verified working.
Baseline tagged for safe rollback.

---

## Phase 2 — Cloud Kubernetes
3-node DigitalOcean Kubernetes cluster deployed and verified.

---

## Phase 3 — Helm Deployments
Single Helm chart deployed to:
- taskmanager-test
- taskmanager-prod

---

## Phase 4 — CI/CD
Automated build & deploy using GitHub Actions.
Branch → TEST, Tag → PROD.

---

## Phase 5 — Monitoring (Prometheus & Grafana)

### Overview
Monitoring for the TaskManager Kubernetes cluster is implemented using the
**kube-prometheus-stack** Helm chart.

This stack provides:
- Prometheus (metrics collection)
- Grafana (visualization dashboards)
- Alertmanager (alert handling)
- Node Exporter & kube-state-metrics (cluster metrics)

The monitoring stack runs in a dedicated Kubernetes namespace: `monitoring`.

---

## Installation

Monitoring is installed directly on the Kubernetes cluster using Helm.

### Step 1 — Add Helm repository
```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
```

### Step 2 — Install monitoring stack
```bash
helm upgrade --install monitoring prometheus-community/kube-prometheus-stack   --namespace monitoring   --create-namespace
```

This command deploys all monitoring components into the `monitoring` namespace.

---

## Verify installation
```bash
kubectl get pods -n monitoring
```

Expected running components include:
- Prometheus
- Alertmanager
- Grafana
- Node Exporter
- kube-state-metrics

---

## Access Grafana Dashboard

Grafana is exposed internally as a ClusterIP service.

### Step 1 — Port-forward Grafana
```bash
kubectl port-forward -n monitoring svc/monitoring-grafana 3000:80
```

### Step 2 — Open Grafana in browser
```
http://localhost:3000
```

### Step 3 — Login credentials
```text
Username: admin
Password: (retrieved from Grafana secret)
```

The password can be retrieved using:
```bash
kubectl get secret -n monitoring monitoring-grafana   -o jsonpath="{.data.admin-password}" | base64 --decode
```

---

## Grafana Dashboards

Grafana comes preconfigured with Kubernetes dashboards, including:
- Kubernetes / Compute Resources / Cluster
- Kubernetes / Compute Resources / Namespace
- Kubernetes / Pods
- Kubernetes / Nodes
- Kubernetes / Deployments & StatefulSets

These dashboards visualize:
- CPU and memory usage
- Pod health and restarts
- Node resource consumption
- Namespace-level metrics

---

## How monitoring works

- **Prometheus** scrapes metrics from Kubernetes components, kubelets,
  node-exporter, and kube-state-metrics.
- **Grafana** uses Prometheus as a data source for visualization.
- Dashboards are automatically provisioned by the Helm chart.
- Metrics update in near real-time.

This setup enables full observability of:
- Cluster health
- Application resource usage
- Scaling behavior
- Failure detection

---

## Reproducibility

The monitoring stack can be reinstalled at any time using the same Helm commands.
No application code changes are required.


---

## Phase 6 — Cloudflare & HTTPS
Frontend and backend exposed securely via Cloudflare-managed domains.

---

## Docker Hub Images
- https://hub.docker.com/r/r0825266/frontend
- https://hub.docker.com/r/r0825266/backend

---

## Result
All PE3 and PE4 requirements completed successfully.
