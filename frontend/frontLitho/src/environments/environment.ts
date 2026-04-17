export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:9090', // API Gateway
  keycloak: {
    url: 'http://localhost:8080',
    realm: 'medical-platform',
    clientId: 'frontend-client'
  }
};
