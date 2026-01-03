export const environment = {
    production: true,
    //apiUrl: 'http://backend:8080/api'
    //apiUrl: 'http://localhost:8080/api'   //for port-forwading(using clusterIP in backend service.yaml) testing
    //apiUrl: 'http://127.0.0.1:6881/api'
    //apiUrl: 'http://192.168.49.2:30080/api'  //for testing using nodeport in backend service.yaml
    apiUrl: 'https://api.kloran-taskmanager.org/api'
};
