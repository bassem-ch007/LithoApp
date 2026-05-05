{
  "realm": "__REALM_NAME__",
  "enabled": true,
  "sslRequired": "none",
  "registrationAllowed": false,
  "registrationEmailAsUsername": false,
  "rememberMe": false,
  "verifyEmail": false,
  "loginWithEmailAllowed": true,
  "duplicateEmailsAllowed": false,
  "resetPasswordAllowed": true,
  "editUsernameAllowed": false,
  "bruteForceProtected": true,
  "roles": {
    "realm": [
      {
        "name": "ADMIN",
        "description": "LithoApp administrator"
      },
      {
        "name": "UROLOGUE",
        "description": "LithoApp urologist"
      },
      {
        "name": "BIOLOGIST",
        "description": "LithoApp biologist"
      }
    ]
  },
  "clients": [
    {
      "clientId": "frontend-client",
      "name": "LithoApp Frontend",
      "enabled": true,
      "protocol": "openid-connect",
      "publicClient": true,
      "standardFlowEnabled": true,
      "implicitFlowEnabled": false,
      "directAccessGrantsEnabled": false,
      "serviceAccountsEnabled": false,
      "redirectUris": [
        "https://lithoapp.online/*",
        "https://www.lithoapp.online/*"
      ],
      "webOrigins": [
        "https://lithoapp.online",
        "https://www.lithoapp.online"
      ],
      "rootUrl": "https://lithoapp.online",
      "baseUrl": "https://lithoapp.online",
      "frontchannelLogout": true,
      "attributes": {
        "pkce.code.challenge.method": "S256",
        "post.logout.redirect.uris": "__FRONTEND_PUBLIC_URL__/*"
      }
    }
  ]
}
