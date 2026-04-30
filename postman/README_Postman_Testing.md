# LithoApp Postman RBAC Workflow

This README explains how to use the generated Postman files. It was generated from the current LithoApp source without changing frontend or backend code.

## Files

- LithoApp_RBAC_Workflow.postman_collection.json
- LithoApp_Local.postman_environment.json

## Import into Postman

1. Open Postman.
2. Import the collection JSON file.
3. Import the environment JSON file.
4. Select the LithoApp Local environment.
5. Make sure Keycloak is running at http://localhost:8080.
6. Make sure the API Gateway is running at http://localhost:9090.

## OAuth2 Authorization Code with PKCE

Use these values in the Postman Authorization tab:

- Type: OAuth 2.0
- Grant Type: Authorization Code with PKCE
- Auth URL: environment variable authorizationUrl
- Access Token URL: environment variable tokenUrl
- Client ID: environment variable clientId
- Client ID value discovered from frontend config: frontend-client
- Code Challenge Method: S256
- Scope: openid profile email
- Callback URL: environment variable redirectUri

The default redirectUri in the environment is https://oauth.pstmn.io/v1/callback.

Important: the inspected Keycloak export for frontend-client only allows http://localhost:4200/* as a redirect URI. For Postman OAuth2 login, add https://oauth.pstmn.io/v1/callback to the frontend-client redirect URIs in Keycloak, or set redirectUri to a callback URL that is already allowed in your local Keycloak setup.

## Save role tokens manually

Postman cannot reliably keep three active OAuth browser sessions in one collection. Use this manual token setup:

1. Get a new OAuth2 token as a UROLOGUE user.
2. Copy the returned access token into the urologueAccessToken environment variable.
3. Get a new OAuth2 token as a BIOLOGIST user.
4. Copy the returned access token into the biologistAccessToken environment variable.
5. Get a new OAuth2 token as an ADMIN user.
6. Copy the returned access token into the adminAccessToken environment variable.

No JWTs or passwords are hardcoded. Password grant requests are intentionally omitted because frontend-client has directAccessGrantsEnabled=false in the inspected Keycloak realm export.

## Recommended folder order

1. 00 - OAuth2 Setup Instructions
2. 01 - Health and Security Smoke Tests
3. 02 - Patient Workflow
4. 03 - Episode Workflow
5. 04 - Analysis Request Workflow
6. 05 - Metabolic PDF Workflow
7. 06 - Stone Analysis Workflow
8. 07 - Drainage/JJ Workflow
9. 08 - Audit Workflow
10. 09 - Complete End-to-End Scenario

The create requests save these variables automatically from JSON responses: patientId, patientDI, patientDMI, episodeId, metabolicAnalysisId, stoneAnalysisId, and drainageId.

For PDF upload requests, open the multipart request in Postman and manually select a valid local PDF for the file field. For the negative fake upload test, select a non-PDF file.

## Confirmed gateway routes

- /patients/** -> patient-service, port 8086
- /episodes/** -> episode-service, port 8085
- /api/analysis-requests/** -> analysis-service, port 8082
- /api/drainages/** -> drainage-service, port 8084

## Confirmed enum values

- Roles: ADMIN, UROLOGUE, BIOLOGIST
- Analysis types: METABOLIC, STONE
- Analysis statuses: CREATED, IN_PROGRESS, COMPLETED
- Metabolic document types: BLOOD_TEST, MORNING_URINE, H24_URINE
- Episode statuses: ACTIVE, CLOSED
- Patient gender: MALE, FEMALE
- Kidney type: ANATOMICAL, FUNCTIONAL
- Drainage type: JJ, URETERAL, NEPHROSTOMY
- Drainage side: LEFT, RIGHT, BILATERAL
- Drainage status: ACTIVE, REMOVED
- JJ type: STANDARD_6F, LARGE_7F, BIODEGRADABLE, METALLIC

## Endpoints not found or assumptions

- No explicit start analysis endpoint was found. First PDF upload or stone result update moves the request from CREATED to IN_PROGRESS.
- No admin-specific business endpoint was found beyond patient deletion and gateway actuator endpoints.
- No gateway route for service Swagger UI or API docs was found. The smoke test allows 401 or 404 for gateway Swagger without token.
- METABOLIC completion is tested after one valid PDF contribution. This matches AnalysisRequestService.validateMetabolicCompletable.
- Delete patient is included because DELETE /patients/{id} exists and requires ADMIN. It uses deleteCandidatePatientId so the main workflow patient is not accidentally targeted.
