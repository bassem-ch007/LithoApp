export const environment = {
  production: true,
  // Point to the API Gateway port defined in your README
  apiBaseUrl: 'http://192.168.1.13:30090',
  keycloak: {
    // Point to the Keycloak port defined in your README
    url: 'http://192.168.1.13:30081',
    realm: 'medical-platform',
    clientId: 'frontend-client'
  }
};
