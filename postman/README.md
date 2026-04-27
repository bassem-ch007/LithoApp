# LithoApp K8s Postman Collection

Gateway-only Postman assets for testing LithoApp microservices through the Kubernetes API Gateway.

## Files

- `LithoApp-K8s.postman_collection.json`
- `LithoApp-K8s-local.postman_environment.json`
- `README.md`

## Local endpoints

- API Gateway: `http://localhost:9090`
- Keycloak: `http://localhost:8080`
- Frontend: `http://localhost:4200`

All service requests use `{{gateway_base_url}}`. The collection does not call Kubernetes service DNS names or Docker Compose service URLs.

## Required port-forward commands

```powershell
kubectl -n lithoapp port-forward svc/api-gateway 9090:9090
kubectl -n lithoapp port-forward svc/keycloak 8080:8080
kubectl -n lithoapp port-forward svc/frontend 4200:80
```

If your frontend service exposes a different target port, use the port shown by `kubectl -n lithoapp get svc frontend`.

## Authentication

Use real users. Do not use `client_credentials`.

Primary flow: OAuth2 Authorization Code with PKCE. In Postman Authorization, use:

- Auth URL: `{{keycloak_base_url}}/realms/{{realm}}/protocol/openid-connect/auth`
- Access Token URL: `{{keycloak_base_url}}/realms/{{realm}}/protocol/openid-connect/token`
- Client ID: `{{client_id}}`
- Code Challenge Method: `S256`
- Scope: `openid profile email`

Automation flow: Password Grant requests are included under `00 - Auth / Keycloak`. They save `access_token` and `refresh_token` into the environment.

Important: the checked-in Keycloak realm has `frontend-client.directAccessGrantsEnabled=false`. Enable Direct Access Grants on `frontend-client` before using the password-grant requests.

## Keycloak Users Setup

If the users do not exist, create:

- `urologist.test` / `password`
- `biologist.test` / `password`
- `admin.test` / `password`

Assign realm roles:

- `UROLOGUE` to `urologist.test`
- `BIOLOGIST` to `biologist.test`
- `ADMIN` to `admin.test`

Note: the product context says `BIOLOGISTE`, but the Kubernetes realm export defines `BIOLOGIST`. Use the role that exists in the realm, or add `BIOLOGISTE` if the application is changed to require that exact role.

## Execution Order

1. Import the environment and select it.
2. Run `00 - Auth / Keycloak / Get Token (Password Grant) - Urologist`, or configure OAuth2 Authorization Code with PKCE.
3. Run `01 - Patient Service / Create patient`.
4. Run `02 - Episode Service / Create episode`.
5. Run analysis and drainage requests as needed.
6. For the complete happy path, run `05 - Full Medical Workflow` in order.

The workflow saves `patient_id`, `episode_id`, `analysis_request_id`, `metabolic_document_id`, and `drainage_id`.

## File Upload

Metabolic PDF upload uses multipart/form-data:

- File field: `file`
- Text field: `biologistId`

Postman cannot run the included upload request until you manually select a local PDF file. A metabolic analysis can be completed after at least one PDF upload.

## Notes From Code Inspection

- Gateway routes are `/patients/**`, `/episodes/**`, `/api/analysis-requests/**`, and `/api/drainages/**`.
- Gateway security requires authentication for all routes except actuator health/info and OPTIONS.
- No explicit `start analysis` endpoint exists. First metabolic PDF upload or stone PATCH auto-transitions `CREATED` to `IN_PROGRESS`.
- The requested negative case "add result before start" is not a negative case in the current code because adding the first result is how an analysis starts.
- Episode close is implemented as `PUT /episodes/{id}` with body `{ "status": "CLOSED" }`.

## Troubleshooting

- `401`: token missing, expired, wrong issuer, or Keycloak port-forward is not on `localhost:8080`. The gateway issuer is configured as `http://localhost:8080/realms/medical-platform`.
- `403`: authenticated user lacks the expected realm role. Check assigned roles in Keycloak.
- `400`: DTO validation failed or patient/episode mismatch.
- `404`: referenced patient, episode, drainage, analysis request, or document was not found.
- `409`: duplicate DI/DMI, duplicate active drainage, concurrent stone update, or patient deletion blocked by linked episodes.
- `500`: backend error. Check the target service logs with `kubectl -n lithoapp logs deploy/<service-name>`.
