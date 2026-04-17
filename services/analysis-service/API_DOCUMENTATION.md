# Analysis Service – API Documentation

## Overview

The Analysis Service manages the full lifecycle of clinical analysis requests (**bilans**) for the LithoApp platform.
It supports two types of analysis:

| Type | Description |
|---|---|
| `METABOLIC` | Three PDF sub-results uploaded separately: blood test, morning urine, 24h urine |
| `STONE` | Structured data entry: morphology + infrared spectrophotometry + final classification |

**Base URL:** `http://localhost:8082`
**All endpoints are prefixed with:** `/api/analysis-requests`

---

## Table of Contents

1. [Status Lifecycle](#1-status-lifecycle)
2. [Create Analysis Request](#2-create-analysis-request)
3. [Get Analysis Request by ID](#3-get-analysis-request-by-id)
4. [List Analysis Requests](#4-list-analysis-requests)
5. [Complete Analysis Request](#5-complete-analysis-request)
6. [Upload Metabolic PDF](#6-upload-metabolic-pdf)
7. [Get Metabolic Result](#7-get-metabolic-result)
8. [Download Active PDF](#8-download-active-pdf)
9. [Download PDF by Version](#9-download-pdf-by-version)
10. [Update Stone Result](#10-update-stone-result)
11. [Get Stone Result](#11-get-stone-result)
12. [Get Audit Log](#12-get-audit-log)
13. [Error Reference](#13-error-reference)

---

## 1. Status Lifecycle

Every analysis request follows this state machine:

```
CREATED ──► IN_PROGRESS ──► COMPLETED
```

| Transition | Trigger | Rule |
|---|---|---|
| `CREATED → IN_PROGRESS` | Automatic | Fires on the first PDF upload (METABOLIC) or the first stone field update (STONE) |
| `IN_PROGRESS → COMPLETED` | Manual | Caller explicitly calls the `/complete` endpoint |
| `CREATED → COMPLETED` | **Blocked** | At least one contribution must exist before completing |
| Any → `COMPLETED` (repeat) | **Blocked** | Once completed, the request is permanently locked |

**Locked state:** When a request is `COMPLETED`, all write operations (upload, replace, field update) return `409 Conflict`.
Read operations and audit log access remain available indefinitely after completion.

---

## 2. Create Analysis Request

Creates a new analysis request and immediately provisions an empty result container for the chosen type.

```
POST /api/analysis-requests
Content-Type: application/json
```

### Request Body

| Field | Type | Required | Description |
|---|---|---|---|
| `patientId` | `String` | ✅ | External patient identifier (resolved by the patient-service) |
| `episodeId` | `String` | ✅ | The clinical episode this bilan belongs to — every bilan must be linked to an episode |
| `createdBy` | `String` | ✅ | Identifier of the urologist opening the request |
| `type` | `Enum` | ✅ | `METABOLIC` or `STONE` |

### Example Request

```json
{
  "patientId": "PATIENT-001",
  "episodeId": "EPISODE-001",
  "createdBy": "DOC-001",
  "type": "METABOLIC"
}
```

### Response – `201 Created`

Returns the full `AnalysisRequestDto` with the embedded empty result.

```json
{
  "id": 1,
  "patientId": "PATIENT-001",
  "episodeId": "EPISODE-001",
  "createdBy": "DOC-001",
  "type": "METABOLIC",
  "status": "CREATED",
  "createdAt": "2026-04-04T10:00:00",
  "completedAt": null,
  "completedBy": null,
  "version": 0,
  "metabolicResult": {
    "id": 1,
    "analysisRequestId": 1,
    "latestDocuments": [],
    "versionHistory": [],
    "uploadedTypesCount": 0
  },
  "stoneResult": null
}
```

### Business Rules

- `status` is always `CREATED` at creation.
- For `METABOLIC`: an empty `MetabolicResult` container is created in the same transaction.
- For `STONE`: an empty `StoneResult` container is created in the same transaction.
- The result container always exists — downstream operations never encounter a missing result.
- A `REQUEST_CREATED` audit entry is written immediately.

### Error Cases

| HTTP | Condition |
|---|---|
| `400 Bad Request` | `patientId`, `episodeId`, `createdBy`, or `type` is missing or blank |

---

## 3. Get Analysis Request by ID

Retrieves the full state of a request including its embedded result.

```
GET /api/analysis-requests/{id}
```

### Path Parameters

| Parameter | Type | Description |
|---|---|---|
| `id` | `Long` | The analysis request ID |

### Response – `200 OK`

For a `METABOLIC` request, `metabolicResult` is populated and `stoneResult` is `null`.
For a `STONE` request, `stoneResult` is populated and `metabolicResult` is `null`.

```json
{
  "id": 1,
  "patientId": "PATIENT-001",
  "episodeId": "EPISODE-001",
  "createdBy": "DOC-001",
  "type": "METABOLIC",
  "status": "IN_PROGRESS",
  "createdAt": "2026-04-04T10:00:00",
  "completedAt": null,
  "completedBy": null,
  "version": 1,
  "metabolicResult": {
    "id": 1,
    "analysisRequestId": 1,
    "latestDocuments": [
      {
        "id": 3,
        "documentType": "BLOOD_TEST",
        "versionNumber": 3,
        "isActive": true,
        "originalFilename": "blood_test_final.pdf",
        "fileSizeBytes": 204800,
        "uploadedBy": "BIO-001",
        "uploadedAt": "2026-04-04T11:30:00"
      }
    ],
    "versionHistory": [
      {
        "id": 3,
        "documentType": "BLOOD_TEST",
        "versionNumber": 3,
        "isActive": true,
        "originalFilename": "blood_test_final.pdf",
        "fileSizeBytes": 204800,
        "uploadedBy": "BIO-001",
        "uploadedAt": "2026-04-04T11:30:00"
      },
      {
        "id": 2,
        "documentType": "BLOOD_TEST",
        "versionNumber": 2,
        "isActive": false,
        "originalFilename": "blood_test_v2.pdf",
        "fileSizeBytes": 198400,
        "uploadedBy": "BIO-002",
        "uploadedAt": "2026-04-04T11:15:00"
      },
      {
        "id": 1,
        "documentType": "BLOOD_TEST",
        "versionNumber": 1,
        "isActive": false,
        "originalFilename": "blood_test.pdf",
        "fileSizeBytes": 190000,
        "uploadedBy": "BIO-001",
        "uploadedAt": "2026-04-04T10:30:00"
      }
    ],
    "uploadedTypesCount": 1
  },
  "stoneResult": null
}
```

### Error Cases

| HTTP | Condition |
|---|---|
| `404 Not Found` | No request exists with the given `id` |

---

## 4. List Analysis Requests

Returns a filtered list of analysis requests. At least one query parameter should be provided.

```
GET /api/analysis-requests
```

### Query Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| `patientId` | `String` | ❌ | Filter by patient identifier |
| `episodeId` | `String` | ❌ | Filter by episode identifier |
| `status` | `Enum` | ❌ | Filter by status: `CREATED`, `IN_PROGRESS`, `COMPLETED` |

### Parameter Combinations

| `patientId` | `episodeId` | `status` | Behaviour |
|---|---|---|---|
| ✅ | — | — | All requests for this patient |
| ✅ | — | ✅ | Requests for this patient with this status |
| — | ✅ | — | All requests for this episode |
| — | — | ✅ | All requests with this status |
| — | — | — | Returns empty list (no unguarded full-table scan) |

> **Note:** `patientId` takes precedence over `episodeId` when both are provided. For patient-level search by DI, DMI, name, or phone number, the patient-service Feign integration will resolve those identifiers to a `patientId` in a future phase. For now, pass `patientId` directly.

### Example Requests

```
GET /api/analysis-requests?patientId=PATIENT-001
GET /api/analysis-requests?patientId=PATIENT-001&status=IN_PROGRESS
GET /api/analysis-requests?episodeId=EPISODE-001
GET /api/analysis-requests?status=COMPLETED
```

### Response – `200 OK`

Array of `AnalysisRequestDto` objects (same structure as [Get by ID](#3-get-analysis-request-by-id)), each with its embedded result.

```json
[
  { "id": 1, "type": "METABOLIC", "status": "IN_PROGRESS", "..." },
  { "id": 2, "type": "STONE",     "status": "CREATED",     "..." }
]
```

Returns `[]` when no requests match the filter.

---

## 5. Complete Analysis Request

Explicitly marks a request as `COMPLETED`. This is an irreversible action.

```
POST /api/analysis-requests/{id}/complete
Content-Type: application/json
```

### Path Parameters

| Parameter | Type | Description |
|---|---|---|
| `id` | `Long` | The analysis request ID |

### Request Body

| Field | Type | Required | Description |
|---|---|---|---|
| `completedBy` | `String` | ✅ | Identifier of the biologist closing the request |

### Example Request

```json
{
  "completedBy": "BIO-001"
}
```

### Response – `200 OK`

Returns the updated `AnalysisRequestDto` with `status: COMPLETED`.

```json
{
  "id": 1,
  "status": "COMPLETED",
  "completedAt": "2026-04-04T14:00:00",
  "completedBy": "BIO-001",
  "..."
}
```

### Business Rules

- The request **must be `IN_PROGRESS`** before it can be completed.
- **No content validation is enforced**: a METABOLIC request can be completed with 0, 1, 2, or 3 PDFs uploaded. A STONE request can be completed with any combination of fields filled or empty. The biologist decides when the work is done.
- Once completed, **all write operations are permanently blocked** (uploads, replacements, field updates).
- Read operations (GET, audit log, PDF download) remain available after completion.
- Two audit entries are written: one `STATUS_CHANGED` (`IN_PROGRESS → COMPLETED`) and one `REQUEST_COMPLETED`.

### Error Cases

| HTTP | Condition |
|---|---|
| `400 Bad Request` | `completedBy` is missing or blank |
| `404 Not Found` | Request does not exist |
| `409 Conflict` | Request is already `COMPLETED` |
| `422 Unprocessable Entity` | Request is still `CREATED` — at least one contribution (PDF upload or stone field update) is required first |

---

## 6. Upload Metabolic PDF

Uploads a new version of a metabolic PDF document for a specific document type.
If a previous version already exists for that type, it becomes historical and the new upload becomes the active version.

```
POST /api/analysis-requests/{id}/metabolic/documents/{documentType}
Content-Type: multipart/form-data
```

### Path Parameters

| Parameter | Type | Description |
|---|---|---|
| `id` | `Long` | The analysis request ID (must be of type `METABOLIC`) |
| `documentType` | `Enum` | One of: `BLOOD_TEST`, `MORNING_URINE`, `H24_URINE` |

### Form Data Fields

| Field | Type | Required | Description |
|---|---|---|---|
| `file` | `File` | ✅ | The PDF file to upload (max 20 MB) |
| `biologistId` | `String` | ✅ | Identifier of the biologist performing the upload |

### Response – `200 OK`

Returns the `PdfDocumentDto` of the newly created version.

```json
{
  "id": 4,
  "documentType": "BLOOD_TEST",
  "versionNumber": 2,
  "isActive": true,
  "originalFilename": "blood_test_corrected.pdf",
  "fileSizeBytes": 215040,
  "uploadedBy": "BIO-002",
  "uploadedAt": "2026-04-04T12:00:00"
}
```

### Versioning Behaviour

| Scenario | Result |
|---|---|
| First upload for this type | New row with `versionNumber = 1`, `isActive = true`. Audit: `PDF_UPLOADED` |
| Subsequent upload for same type | Previous active row set to `isActive = false`. New row with `versionNumber = N+1`, `isActive = true`. Audit: `PDF_REPLACED` |
| Upload by a different biologist | Allowed — no ownership restriction. Any biologist can replace any other biologist's document |

### Business Rules

- **Old files are never deleted from storage.** Every version retains its file, making all historical PDFs downloadable.
- Each upload creates exactly one new row in `pdf_documents` — old rows are never modified (only their `isActive` flag is set to `false`).
- **Auto-transition:** If the request status is `CREATED`, it automatically transitions to `IN_PROGRESS` on the first upload. A `STATUS_CHANGED` audit entry is written for this transition.
- `storageKey` (internal MinIO/local path) is never exposed in the response. Callers use the download endpoints.

### Error Cases

| HTTP | Condition |
|---|---|
| `400 Bad Request` | The request `id` refers to a `STONE` type request |
| `400 Bad Request` | File size exceeds 20 MB |
| `404 Not Found` | Request does not exist |
| `409 Conflict` | Request is already `COMPLETED` |

---

## 7. Get Metabolic Result

Returns the full metabolic result for a request, including the latest active version per document type and the complete version history.

```
GET /api/analysis-requests/{id}/metabolic
```

### Path Parameters

| Parameter | Type | Description |
|---|---|---|
| `id` | `Long` | The analysis request ID (must be of type `METABOLIC`) |

### Response – `200 OK`

```json
{
  "id": 1,
  "analysisRequestId": 1,
  "latestDocuments": [
    {
      "id": 3,
      "documentType": "BLOOD_TEST",
      "versionNumber": 3,
      "isActive": true,
      "originalFilename": "blood_test_final.pdf",
      "fileSizeBytes": 204800,
      "uploadedBy": "BIO-001",
      "uploadedAt": "2026-04-04T11:30:00"
    },
    {
      "id": 4,
      "documentType": "MORNING_URINE",
      "versionNumber": 1,
      "isActive": true,
      "originalFilename": "morning_urine.pdf",
      "fileSizeBytes": 153600,
      "uploadedBy": "BIO-002",
      "uploadedAt": "2026-04-04T11:45:00"
    }
  ],
  "versionHistory": [
    {
      "id": 3, "documentType": "BLOOD_TEST", "versionNumber": 3, "isActive": true,
      "uploadedBy": "BIO-001", "uploadedAt": "2026-04-04T11:30:00", "..."
    },
    {
      "id": 2, "documentType": "BLOOD_TEST", "versionNumber": 2, "isActive": false,
      "uploadedBy": "BIO-002", "uploadedAt": "2026-04-04T11:15:00", "..."
    },
    {
      "id": 1, "documentType": "BLOOD_TEST", "versionNumber": 1, "isActive": false,
      "uploadedBy": "BIO-001", "uploadedAt": "2026-04-04T10:30:00", "..."
    },
    {
      "id": 4, "documentType": "MORNING_URINE", "versionNumber": 1, "isActive": true,
      "uploadedBy": "BIO-002", "uploadedAt": "2026-04-04T11:45:00", "..."
    }
  ],
  "uploadedTypesCount": 2
}
```

### Response Fields

| Field | Description |
|---|---|
| `latestDocuments` | One entry per document type that has been uploaded. Only `isActive = true` entries. At most 3 entries (one per `MetabolicDocumentType`) |
| `versionHistory` | All versions across all types. Sorted by `documentType ASC`, then `versionNumber DESC` (newest first within each type). Includes both active and superseded versions |
| `uploadedTypesCount` | Number of distinct document types with at least one active version. Range: 0–3. Informational only — completion is not gated on this value |

### Error Cases

| HTTP | Condition |
|---|---|
| `400 Bad Request` | The request `id` refers to a `STONE` type request |
| `404 Not Found` | Request does not exist |

---

## 8. Download Active PDF

Downloads the **current active (latest) version** of a specific metabolic document type.

```
GET /api/analysis-requests/{id}/metabolic/documents/{documentType}/download
```

### Path Parameters

| Parameter | Type | Description |
|---|---|---|
| `id` | `Long` | The analysis request ID |
| `documentType` | `Enum` | One of: `BLOOD_TEST`, `MORNING_URINE`, `H24_URINE` |

### Response – `200 OK`

| Header | Value |
|---|---|
| `Content-Type` | `application/pdf` |
| `Content-Disposition` | `attachment; filename="{originalFilename}"` |

Response body is the raw PDF bytes.

### Error Cases

| HTTP | Condition |
|---|---|
| `404 Not Found` | Request does not exist, or no document of this type has been uploaded yet |

---

## 9. Download PDF by Version

Downloads a **specific historical version** of a metabolic document type.
Useful for reviewing what was submitted at each stage of the workflow.

```
GET /api/analysis-requests/{id}/metabolic/documents/{documentType}/versions/{version}/download
```

### Path Parameters

| Parameter | Type | Description |
|---|---|---|
| `id` | `Long` | The analysis request ID |
| `documentType` | `Enum` | One of: `BLOOD_TEST`, `MORNING_URINE`, `H24_URINE` |
| `version` | `Integer` | Version number to retrieve (1-based). Version 1 is the first upload, version 2 is the first replacement, etc. |

### Response – `200 OK`

| Header | Value |
|---|---|
| `Content-Type` | `application/pdf` |
| `Content-Disposition` | `attachment; filename="{DOCUMENT_TYPE}_v{version}.pdf"` |

### Business Rules

- Historical versions are always accessible, including after the request is `COMPLETED`.
- The `storageKey` for all versions is permanently preserved in MinIO/local storage.

### Error Cases

| HTTP | Condition |
|---|---|
| `404 Not Found` | Request does not exist, or the specified version does not exist for the given document type |

---

## 10. Update Stone Result

Partially updates the structured stone analysis result.
Only the fields explicitly provided in the request body are updated. `null` fields are ignored.

```
PATCH /api/analysis-requests/{id}/stone
Content-Type: application/json
```

### Path Parameters

| Parameter | Type | Description |
|---|---|---|
| `id` | `Long` | The analysis request ID (must be of type `STONE`) |

### Request Body

| Field | Type | Required | Description |
|---|---|---|---|
| `modifiedBy` | `String` | ✅ | Identifier of the biologist making this update |
| `morphSize` | `String` | ❌ | Stone size (e.g. `"8mm"`) |
| `morphSurface` | `String` | ❌ | Surface texture (e.g. `"Rough"`, `"Smooth"`) |
| `morphColor` | `String` | ❌ | Color (e.g. `"Brown"`, `"Yellow"`) |
| `morphSection` | `String` | ❌ | Cross-section appearance (e.g. `"Laminated"`) |
| `morphOuterLayers` | `String` | ❌ | Outer layer composition |
| `morphCore` | `String` | ❌ | Core composition |
| `spectroSurface` | `String` | ❌ | Infrared spectrophotometry – surface reading |
| `spectroSection` | `String` | ❌ | Infrared spectrophotometry – section reading |
| `spectroOuterLayers` | `String` | ❌ | Infrared spectrophotometry – outer layers reading |
| `spectroCore` | `String` | ❌ | Infrared spectrophotometry – core reading |
| `finalStoneType` | `String` | ❌ | Final biochemical classification (e.g. `"Calcium Oxalate Monohydrate (Whewellite)"`) |

> All result fields are optional — the biologist fills them progressively across multiple calls.

### Example Request – morphology only

```json
{
  "modifiedBy": "BIO-001",
  "morphSize": "8mm",
  "morphSurface": "Rough",
  "morphColor": "Brown",
  "morphSection": "Laminated",
  "morphOuterLayers": "Calcium oxalate",
  "morphCore": "Uric acid"
}
```

### Example Request – spectrophotometry by a different biologist

```json
{
  "modifiedBy": "BIO-002",
  "spectroSurface": "Whewellite 70%",
  "spectroSection": "Weddellite 20%",
  "spectroOuterLayers": "Carbapatite 10%",
  "spectroCore": "Uric acid 100%"
}
```

### Response – `200 OK`

Returns the full current state of the `StoneResultDto`.

```json
{
  "id": 1,
  "analysisRequestId": 2,
  "morphSize": "8mm",
  "morphSurface": "Rough",
  "morphColor": "Brown",
  "morphSection": "Laminated",
  "morphOuterLayers": "Calcium oxalate",
  "morphCore": "Uric acid",
  "spectroSurface": "Whewellite 70%",
  "spectroSection": "Weddellite 20%",
  "spectroOuterLayers": "Carbapatite 10%",
  "spectroCore": "Uric acid 100%",
  "finalStoneType": null,
  "lastModifiedBy": "BIO-002",
  "lastModifiedAt": "2026-04-04T13:00:00",
  "createdAt": "2026-04-04T10:00:00",
  "version": 2
}
```

### Business Rules

- **Partial update semantics:** sending only `finalStoneType` does not affect `morphSize`, `spectroSurface`, or any other field.
- **No field is required** at any point, including `finalStoneType`. Completion does not validate field content.
- **Collaboration:** any biologist can modify any field at any time, including overwriting another biologist's contribution. Full traceability is maintained via the audit log.
- **Concurrency protection:** `StoneResult` uses optimistic locking (`@Version`). If two biologists submit updates simultaneously, the second request receives `409 Conflict` and must reload and retry.
- **Auto-transition:** If the request status is `CREATED`, it automatically transitions to `IN_PROGRESS` on the first update.
- **Audit granularity:** A separate `STONE_RESULT_FIELD_UPDATED` audit entry is written for **each individual field** that changed, capturing `oldValue → newValue`. Fields that were not provided, or whose value did not change, produce no audit entry.

### Error Cases

| HTTP | Condition |
|---|---|
| `400 Bad Request` | `modifiedBy` is missing or blank |
| `400 Bad Request` | The request `id` refers to a `METABOLIC` type request |
| `404 Not Found` | Request does not exist |
| `409 Conflict` | Request is already `COMPLETED` |
| `409 Conflict` | Concurrent modification detected — reload and retry |

---

## 11. Get Stone Result

Returns the current state of the stone analysis result.

```
GET /api/analysis-requests/{id}/stone
```

### Path Parameters

| Parameter | Type | Description |
|---|---|---|
| `id` | `Long` | The analysis request ID (must be of type `STONE`) |

### Response – `200 OK`

```json
{
  "id": 1,
  "analysisRequestId": 2,
  "morphSize": "8mm",
  "morphSurface": "Rough",
  "morphColor": "Brown",
  "morphSection": "Laminated",
  "morphOuterLayers": "Calcium oxalate",
  "morphCore": "Uric acid",
  "spectroSurface": "Whewellite 70%",
  "spectroSection": "Weddellite 20%",
  "spectroOuterLayers": "Carbapatite 10%",
  "spectroCore": "Uric acid 100%",
  "finalStoneType": "Calcium Oxalate Monohydrate (Whewellite)",
  "lastModifiedBy": "BIO-001",
  "lastModifiedAt": "2026-04-04T13:30:00",
  "createdAt": "2026-04-04T10:00:00",
  "version": 3
}
```

### Response Fields

| Field | Description |
|---|---|
| `version` | Optimistic lock counter. Increments on each update. Useful for detecting stale reads |
| `lastModifiedBy` | The biologist who performed the most recent update |
| `lastModifiedAt` | Timestamp of the most recent update |
| `createdAt` | When the stone result container was provisioned (same as request creation) |

### Error Cases

| HTTP | Condition |
|---|---|
| `400 Bad Request` | The request `id` refers to a `METABOLIC` type request |
| `404 Not Found` | Request does not exist |

---

## 12. Get Audit Log

Returns the complete, chronological audit trail for an analysis request.
The log answers: *who did what, on which field, when, and what changed.*

```
GET /api/analysis-requests/{id}/audit
```

### Path Parameters

| Parameter | Type | Description |
|---|---|---|
| `id` | `Long` | The analysis request ID |

### Response – `200 OK`

Array of `AuditEntryDto` ordered by `timestamp ASC` (oldest first).

```json
[
  {
    "id": 1,
    "analysisRequestId": 1,
    "actorId": "DOC-001",
    "actionType": "REQUEST_CREATED",
    "targetField": null,
    "oldValue": null,
    "newValue": "METABOLIC",
    "timestamp": "2026-04-04T10:00:00"
  },
  {
    "id": 2,
    "analysisRequestId": 1,
    "actorId": "BIO-001",
    "actionType": "STATUS_CHANGED",
    "targetField": "status",
    "oldValue": "CREATED",
    "newValue": "IN_PROGRESS",
    "timestamp": "2026-04-04T10:30:00"
  },
  {
    "id": 3,
    "analysisRequestId": 1,
    "actorId": "BIO-001",
    "actionType": "PDF_UPLOADED",
    "targetField": "BLOOD_TEST",
    "oldValue": null,
    "newValue": "a3f8c2d1-4b5e-4c6f-8d7e-9f0a1b2c3d4e.pdf",
    "timestamp": "2026-04-04T10:30:00"
  },
  {
    "id": 4,
    "analysisRequestId": 1,
    "actorId": "BIO-002",
    "actionType": "PDF_REPLACED",
    "targetField": "BLOOD_TEST",
    "oldValue": "a3f8c2d1-4b5e-4c6f-8d7e-9f0a1b2c3d4e.pdf",
    "newValue": "b4e9d3f2-5c6f-4d7g-9e8f-0g1b2c3d4e5f.pdf",
    "timestamp": "2026-04-04T11:15:00"
  },
  {
    "id": 5,
    "analysisRequestId": 1,
    "actorId": "BIO-001",
    "actionType": "REQUEST_COMPLETED",
    "targetField": null,
    "oldValue": null,
    "newValue": "BIO-001",
    "timestamp": "2026-04-04T14:00:00"
  }
]
```

### Audit Action Types

| `actionType` | `targetField` | `oldValue` | `newValue` | Triggered by |
|---|---|---|---|---|
| `REQUEST_CREATED` | `null` | `null` | `METABOLIC` or `STONE` | Create endpoint |
| `STATUS_CHANGED` | `"status"` | Previous status | New status | Auto-transition or completion |
| `PDF_UPLOADED` | Document type (e.g. `"BLOOD_TEST"`) | `null` | New storage key | First upload of a type |
| `PDF_REPLACED` | Document type (e.g. `"BLOOD_TEST"`) | Previous storage key | New storage key | Subsequent upload of same type |
| `STONE_RESULT_CREATED` | `null` | `null` | `null` | First stone update |
| `STONE_RESULT_FIELD_UPDATED` | Field name (e.g. `"morphSize"`) | Previous value | New value | Stone PATCH (one entry per changed field) |
| `REQUEST_COMPLETED` | `null` | `null` | `completedBy` value | Complete endpoint |

### Business Rules

- Audit entries are written **in the same database transaction** as the operation they describe. A rolled-back operation produces no dangling audit entry.
- The audit log is **append-only** — entries are never deleted or modified.
- The audit log remains accessible even after the request is `COMPLETED`.
- For stone updates, only fields whose value **actually changed** produce an audit entry. Sending the same value twice produces no entry.

### Error Cases

| HTTP | Condition |
|---|---|
| `404 Not Found` | Request does not exist |

---

## 13. Error Reference

All errors follow a consistent response structure:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Analysis request 1 is already COMPLETED and cannot be modified.",
  "timestamp": "2026-04-04T14:05:00"
}
```

### HTTP Status Code Summary

| Code | Meaning | Common Causes |
|---|---|---|
| `200 OK` | Success | Standard successful response |
| `201 Created` | Resource created | Request creation |
| `400 Bad Request` | Invalid input | Missing required field, wrong analysis type for operation |
| `404 Not Found` | Resource missing | Unknown request ID, document not yet uploaded |
| `409 Conflict` | State conflict | Request already completed, invalid status transition, concurrent modification |
| `422 Unprocessable Entity` | Business rule violation | Attempting to complete a `CREATED` request with no contributions |
| `500 Internal Server Error` | Unexpected error | File storage failure |

### Concurrency – 409 on Optimistic Lock

When two biologists update the same `StoneResult` simultaneously, the slower request receives:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Concurrent modification detected. Please reload the resource and retry your changes.",
  "timestamp": "2026-04-04T13:00:05"
}
```

The client should `GET /api/analysis-requests/{id}/stone` to reload the latest state, then resubmit the PATCH.
