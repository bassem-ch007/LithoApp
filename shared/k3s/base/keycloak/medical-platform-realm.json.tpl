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
        "__FRONTEND_PUBLIC_URL__/*"
      ],
      "webOrigins": [
        "__FRONTEND_PUBLIC_URL__"
      ],
      "rootUrl": "__FRONTEND_PUBLIC_URL__",
      "baseUrl": "__FRONTEND_PUBLIC_URL__",
      "frontchannelLogout": true,
      "attributes": {
        "pkce.code.challenge.method": "S256",
        "post.logout.redirect.uris": "__FRONTEND_PUBLIC_URL__/*"
      }
    }
  ]
}
