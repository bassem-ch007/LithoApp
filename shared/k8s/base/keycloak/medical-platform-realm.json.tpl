{
    "id":  "c48c45f3-6c80-4bce-b017-c4bb00bd41a0",
    "realm":  "medical-platform",
    "displayName":  "",
    "displayNameHtml":  "",
    "notBefore":  0,
    "defaultSignatureAlgorithm":  "RS256",
    "revokeRefreshToken":  false,
    "refreshTokenMaxReuse":  0,
    "accessTokenLifespan":  300,
    "accessTokenLifespanForImplicitFlow":  900,
    "ssoSessionIdleTimeout":  1800,
    "ssoSessionMaxLifespan":  36000,
    "ssoSessionIdleTimeoutRememberMe":  0,
    "ssoSessionMaxLifespanRememberMe":  0,
    "offlineSessionIdleTimeout":  2592000,
    "offlineSessionMaxLifespanEnabled":  false,
    "offlineSessionMaxLifespan":  5184000,
    "clientSessionIdleTimeout":  0,
    "clientSessionMaxLifespan":  0,
    "clientOfflineSessionIdleTimeout":  0,
    "clientOfflineSessionMaxLifespan":  0,
    "accessCodeLifespan":  60,
    "accessCodeLifespanUserAction":  300,
    "accessCodeLifespanLogin":  1800,
    "actionTokenGeneratedByAdminLifespan":  43200,
    "actionTokenGeneratedByUserLifespan":  300,
    "oauth2DeviceCodeLifespan":  600,
    "oauth2DevicePollingInterval":  5,
    "enabled":  true,
    "sslRequired":  "external",
    "registrationAllowed":  true,
    "registrationEmailAsUsername":  false,
    "rememberMe":  false,
    "verifyEmail":  false,
    "loginWithEmailAllowed":  true,
    "duplicateEmailsAllowed":  false,
    "resetPasswordAllowed":  true,
    "editUsernameAllowed":  false,
    "bruteForceProtected":  false,
    "permanentLockout":  false,
    "maxTemporaryLockouts":  0,
    "bruteForceStrategy":  "MULTIPLE",
    "maxFailureWaitSeconds":  900,
    "minimumQuickLoginWaitSeconds":  60,
    "waitIncrementSeconds":  60,
    "quickLoginCheckMilliSeconds":  1000,
    "maxDeltaTimeSeconds":  43200,
    "failureFactor":  30,
    "roles":  {
                  "realm":  [
                                {
                                    "id":  "362e7c3f-bb42-4edc-a794-459cc5df72eb",
                                    "name":  "BIOLOGIST",
                                    "description":  "",
                                    "composite":  false,
                                    "clientRole":  false,
                                    "containerId":  "c48c45f3-6c80-4bce-b017-c4bb00bd41a0",
                                    "attributes":  {

                                                   }
                                },
                                {
                                    "id":  "6ad2f00f-2b4f-44c0-a336-1826579c6e61",
                                    "name":  "uma_authorization",
                                    "description":  "${role_uma_authorization}",
                                    "composite":  false,
                                    "clientRole":  false,
                                    "containerId":  "c48c45f3-6c80-4bce-b017-c4bb00bd41a0",
                                    "attributes":  {

                                                   }
                                },
                                {
                                    "id":  "6c02328a-5074-4990-be37-974eac870011",
                                    "name":  "ADMIN",
                                    "description":  "",
                                    "composite":  false,
                                    "clientRole":  false,
                                    "containerId":  "c48c45f3-6c80-4bce-b017-c4bb00bd41a0",
                                    "attributes":  {

                                                   }
                                },
                                {
                                    "id":  "7d0b003e-3cb0-494a-8357-8317204da48f",
                                    "name":  "offline_access",
                                    "description":  "${role_offline-access}",
                                    "composite":  false,
                                    "clientRole":  false,
                                    "containerId":  "c48c45f3-6c80-4bce-b017-c4bb00bd41a0",
                                    "attributes":  {

                                                   }
                                },
                                {
                                    "id":  "979cc0d2-e703-4bd2-b0f2-5124f8537515",
                                    "name":  "default-roles-medical-platform",
                                    "description":  "${role_default-roles}",
                                    "composite":  true,
                                    "composites":  {
                                                       "realm":  [
                                                                     "offline_access",
                                                                     "uma_authorization"
                                                                 ],
                                                       "client":  {
                                                                      "account":  [
                                                                                      "manage-account",
                                                                                      "view-profile"
                                                                                  ]
                                                                  }
                                                   },
                                    "clientRole":  false,
                                    "containerId":  "c48c45f3-6c80-4bce-b017-c4bb00bd41a0",
                                    "attributes":  {

                                                   }
                                },
                                {
                                    "id":  "45608a4f-602f-4174-b3fd-1b5a9e6d761d",
                                    "name":  "UROLOGUE",
                                    "description":  "",
                                    "composite":  false,
                                    "clientRole":  false,
                                    "containerId":  "c48c45f3-6c80-4bce-b017-c4bb00bd41a0",
                                    "attributes":  {

                                                   }
                                }
                            ],
                  "client":  {
                                 "realm-management":  [
                                                          {
                                                              "id":  "43e8c12e-b6dc-42ce-ba82-37f9a7d2699f",
                                                              "name":  "view-clients",
                                                              "description":  "${role_view-clients}",
                                                              "composite":  true,
                                                              "composites":  {
                                                                                 "client":  {
                                                                                                "realm-management":  [
                                                                                                                         "query-clients"
                                                                                                                     ]
                                                                                            }
                                                                             },
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "afc9df97-d860-4688-b33e-ae252986ebef",
                                                              "name":  "view-events",
                                                              "description":  "${role_view-events}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "77f743a2-1a53-4772-ae43-eeb6ebd081ce",
                                                              "name":  "view-users",
                                                              "description":  "${role_view-users}",
                                                              "composite":  true,
                                                              "composites":  {
                                                                                 "client":  {
                                                                                                "realm-management":  [
                                                                                                                         "query-users",
                                                                                                                         "query-groups"
                                                                                                                     ]
                                                                                            }
                                                                             },
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "d376631e-14f3-4120-890e-d897dc668d3d",
                                                              "name":  "impersonation",
                                                              "description":  "${role_impersonation}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "09c6814f-6515-47b4-a53d-a88d060ecb72",
                                                              "name":  "realm-admin",
                                                              "description":  "${role_realm-admin}",
                                                              "composite":  true,
                                                              "composites":  {
                                                                                 "client":  {
                                                                                                "realm-management":  [
                                                                                                                         "view-clients",
                                                                                                                         "view-events",
                                                                                                                         "view-users",
                                                                                                                         "impersonation",
                                                                                                                         "view-authorization",
                                                                                                                         "query-clients",
                                                                                                                         "view-realm",
                                                                                                                         "view-identity-providers",
                                                                                                                         "manage-clients",
                                                                                                                         "manage-users",
                                                                                                                         "manage-identity-providers",
                                                                                                                         "create-client",
                                                                                                                         "manage-realm",
                                                                                                                         "query-users",
                                                                                                                         "query-realms",
                                                                                                                         "manage-events",
                                                                                                                         "query-groups",
                                                                                                                         "manage-authorization"
                                                                                                                     ]
                                                                                            }
                                                                             },
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "712fbf98-3339-495a-8327-2d503b131ea7",
                                                              "name":  "view-authorization",
                                                              "description":  "${role_view-authorization}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "87912021-dabd-458f-a176-250d2d4abec9",
                                                              "name":  "query-clients",
                                                              "description":  "${role_query-clients}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "81835fa7-8b12-48d5-bca0-f12626abbdf3",
                                                              "name":  "view-identity-providers",
                                                              "description":  "${role_view-identity-providers}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "12486b17-c52a-4cfc-baf8-46b9301eed67",
                                                              "name":  "view-realm",
                                                              "description":  "${role_view-realm}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "b8847c9f-5116-4494-926a-62e84c26a241",
                                                              "name":  "manage-clients",
                                                              "description":  "${role_manage-clients}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "2e8c4b12-308e-468d-8142-d780c8d39ad9",
                                                              "name":  "manage-identity-providers",
                                                              "description":  "${role_manage-identity-providers}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "87856679-193a-4524-8fd7-67034b535bf4",
                                                              "name":  "manage-users",
                                                              "description":  "${role_manage-users}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "bcf6681e-f120-4ddf-a37c-88a271b30cf1",
                                                              "name":  "create-client",
                                                              "description":  "${role_create-client}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "ecc8bd84-b6f6-4125-8a07-12781d662fb9",
                                                              "name":  "manage-realm",
                                                              "description":  "${role_manage-realm}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "d6586353-fee0-426e-8c14-892e86180e05",
                                                              "name":  "query-users",
                                                              "description":  "${role_query-users}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "dacb565f-d4d0-4d5d-b5dd-6d6aa4d1580f",
                                                              "name":  "query-realms",
                                                              "description":  "${role_query-realms}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "977bc63a-ff4a-4f69-bffe-5553a1f92fa5",
                                                              "name":  "manage-events",
                                                              "description":  "${role_manage-events}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "f1fa2d4b-5fb0-48ec-8198-601ac9f997f8",
                                                              "name":  "query-groups",
                                                              "description":  "${role_query-groups}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          },
                                                          {
                                                              "id":  "e2e87d62-0791-45d5-bdd5-af5c2b400a42",
                                                              "name":  "manage-authorization",
                                                              "description":  "${role_manage-authorization}",
                                                              "composite":  false,
                                                              "clientRole":  true,
                                                              "containerId":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                                                              "attributes":  {

                                                                             }
                                                          }
                                                      ],
                                 "security-admin-console":  [

                                                            ],
                                 "admin-cli":  [

                                               ],
                                 "account-console":  [

                                                     ],
                                 "broker":  [
                                                {
                                                    "id":  "e098c68a-a372-4ae1-a4d0-4347b5540fcf",
                                                    "name":  "read-token",
                                                    "description":  "${role_read-token}",
                                                    "composite":  false,
                                                    "clientRole":  true,
                                                    "containerId":  "6945864c-b3bd-4867-add3-9b0d2ed5ada0",
                                                    "attributes":  {

                                                                   }
                                                }
                                            ],
                                 "account":  [
                                                 {
                                                     "id":  "248b5925-c258-41e3-9e6d-91b76f4f1a9b",
                                                     "name":  "manage-consent",
                                                     "description":  "${role_manage-consent}",
                                                     "composite":  true,
                                                     "composites":  {
                                                                        "client":  {
                                                                                       "account":  [
                                                                                                       "view-consent"
                                                                                                   ]
                                                                                   }
                                                                    },
                                                     "clientRole":  true,
                                                     "containerId":  "6544fc75-5132-47af-bc23-e26f2baa7dbd",
                                                     "attributes":  {

                                                                    }
                                                 },
                                                 {
                                                     "id":  "9811b095-c2bf-4db3-ae28-e454921f29ea",
                                                     "name":  "view-applications",
                                                     "description":  "${role_view-applications}",
                                                     "composite":  false,
                                                     "clientRole":  true,
                                                     "containerId":  "6544fc75-5132-47af-bc23-e26f2baa7dbd",
                                                     "attributes":  {

                                                                    }
                                                 },
                                                 {
                                                     "id":  "1c3012fd-730d-45b9-a166-a3594ad58760",
                                                     "name":  "delete-account",
                                                     "description":  "${role_delete-account}",
                                                     "composite":  false,
                                                     "clientRole":  true,
                                                     "containerId":  "6544fc75-5132-47af-bc23-e26f2baa7dbd",
                                                     "attributes":  {

                                                                    }
                                                 },
                                                 {
                                                     "id":  "ecacfca6-360d-48fc-bb70-b7375d811d9c",
                                                     "name":  "manage-account",
                                                     "description":  "${role_manage-account}",
                                                     "composite":  true,
                                                     "composites":  {
                                                                        "client":  {
                                                                                       "account":  [
                                                                                                       "manage-account-links"
                                                                                                   ]
                                                                                   }
                                                                    },
                                                     "clientRole":  true,
                                                     "containerId":  "6544fc75-5132-47af-bc23-e26f2baa7dbd",
                                                     "attributes":  {

                                                                    }
                                                 },
                                                 {
                                                     "id":  "f00d910a-dbd9-46f8-af1f-88d4293d1163",
                                                     "name":  "view-profile",
                                                     "description":  "${role_view-profile}",
                                                     "composite":  false,
                                                     "clientRole":  true,
                                                     "containerId":  "6544fc75-5132-47af-bc23-e26f2baa7dbd",
                                                     "attributes":  {

                                                                    }
                                                 },
                                                 {
                                                     "id":  "28390b28-25b2-40b8-8f06-ddcfae8a8674",
                                                     "name":  "view-consent",
                                                     "description":  "${role_view-consent}",
                                                     "composite":  false,
                                                     "clientRole":  true,
                                                     "containerId":  "6544fc75-5132-47af-bc23-e26f2baa7dbd",
                                                     "attributes":  {

                                                                    }
                                                 },
                                                 {
                                                     "id":  "d56eaf0a-5033-468f-a704-f1ff0e7591ea",
                                                     "name":  "view-groups",
                                                     "description":  "${role_view-groups}",
                                                     "composite":  false,
                                                     "clientRole":  true,
                                                     "containerId":  "6544fc75-5132-47af-bc23-e26f2baa7dbd",
                                                     "attributes":  {

                                                                    }
                                                 },
                                                 {
                                                     "id":  "d48f0d3f-689c-41af-9251-96a2dc80ce69",
                                                     "name":  "manage-account-links",
                                                     "description":  "${role_manage-account-links}",
                                                     "composite":  false,
                                                     "clientRole":  true,
                                                     "containerId":  "6544fc75-5132-47af-bc23-e26f2baa7dbd",
                                                     "attributes":  {

                                                                    }
                                                 }
                                             ],
                                 "frontend-client":  [

                                                     ]
                             }
              },
    "groups":  [

               ],
    "defaultRole":  {
                        "id":  "979cc0d2-e703-4bd2-b0f2-5124f8537515",
                        "name":  "default-roles-medical-platform",
                        "description":  "${role_default-roles}",
                        "composite":  true,
                        "clientRole":  false,
                        "containerId":  "c48c45f3-6c80-4bce-b017-c4bb00bd41a0"
                    },
    "requiredCredentials":  [
                                "password"
                            ],
    "passwordPolicy":  "length(8)",
    "otpPolicyType":  "totp",
    "otpPolicyAlgorithm":  "HmacSHA1",
    "otpPolicyInitialCounter":  0,
    "otpPolicyDigits":  6,
    "otpPolicyLookAheadWindow":  1,
    "otpPolicyPeriod":  30,
    "otpPolicyCodeReusable":  false,
    "otpSupportedApplications":  [
                                     "totpAppFreeOTPName",
                                     "totpAppGoogleName",
                                     "totpAppMicrosoftAuthenticatorName"
                                 ],
    "localizationTexts":  {

                          },
    "webAuthnPolicyRpEntityName":  "keycloak",
    "webAuthnPolicySignatureAlgorithms":  [
                                              "ES256",
                                              "RS256"
                                          ],
    "webAuthnPolicyRpId":  "",
    "webAuthnPolicyAttestationConveyancePreference":  "not specified",
    "webAuthnPolicyAuthenticatorAttachment":  "not specified",
    "webAuthnPolicyRequireResidentKey":  "not specified",
    "webAuthnPolicyUserVerificationRequirement":  "not specified",
    "webAuthnPolicyCreateTimeout":  0,
    "webAuthnPolicyAvoidSameAuthenticatorRegister":  false,
    "webAuthnPolicyAcceptableAaguids":  [

                                        ],
    "webAuthnPolicyExtraOrigins":  [

                                   ],
    "webAuthnPolicyPasswordlessRpEntityName":  "keycloak",
    "webAuthnPolicyPasswordlessSignatureAlgorithms":  [
                                                          "ES256",
                                                          "RS256"
                                                      ],
    "webAuthnPolicyPasswordlessRpId":  "",
    "webAuthnPolicyPasswordlessAttestationConveyancePreference":  "not specified",
    "webAuthnPolicyPasswordlessAuthenticatorAttachment":  "not specified",
    "webAuthnPolicyPasswordlessRequireResidentKey":  "Yes",
    "webAuthnPolicyPasswordlessUserVerificationRequirement":  "required",
    "webAuthnPolicyPasswordlessCreateTimeout":  0,
    "webAuthnPolicyPasswordlessAvoidSameAuthenticatorRegister":  false,
    "webAuthnPolicyPasswordlessAcceptableAaguids":  [

                                                    ],
    "webAuthnPolicyPasswordlessExtraOrigins":  [

                                               ],
    "scopeMappings":  [
                          {
                              "clientScope":  "offline_access",
                              "roles":  [
                                            "offline_access"
                                        ]
                          }
                      ],
    "clientScopeMappings":  {
                                "account":  [
                                                {
                                                    "client":  "account-console",
                                                    "roles":  [
                                                                  "manage-account",
                                                                  "view-groups"
                                                              ]
                                                }
                                            ]
                            },
    "clients":  [
                    {
                        "id":  "6544fc75-5132-47af-bc23-e26f2baa7dbd",
                        "clientId":  "account",
                        "name":  "${client_account}",
                        "rootUrl":  "${authBaseUrl}",
                        "baseUrl":  "/realms/medical-platform/account/",
                        "surrogateAuthRequired":  false,
                        "enabled":  true,
                        "alwaysDisplayInConsole":  false,
                        "clientAuthenticatorType":  "client-secret",
                        "redirectUris":  [
                                             "/realms/medical-platform/account/*"
                                         ],
                        "webOrigins":  [

                                       ],
                        "notBefore":  0,
                        "bearerOnly":  false,
                        "consentRequired":  false,
                        "standardFlowEnabled":  true,
                        "implicitFlowEnabled":  false,
                        "directAccessGrantsEnabled":  false,
                        "serviceAccountsEnabled":  false,
                        "publicClient":  true,
                        "frontchannelLogout":  false,
                        "protocol":  "openid-connect",
                        "attributes":  {
                                           "realm_client":  "false",
                                           "post.logout.redirect.uris":  "+"
                                       },
                        "authenticationFlowBindingOverrides":  {

                                                               },
                        "fullScopeAllowed":  false,
                        "nodeReRegistrationTimeout":  0,
                        "defaultClientScopes":  [
                                                    "web-origins",
                                                    "acr",
                                                    "roles",
                                                    "profile",
                                                    "basic",
                                                    "email"
                                                ],
                        "optionalClientScopes":  [
                                                     "address",
                                                     "phone",
                                                     "offline_access",
                                                     "organization",
                                                     "microprofile-jwt"
                                                 ]
                    },
                    {
                        "id":  "bb3a47fe-4daa-4e91-aadc-5c2e36546e29",
                        "clientId":  "account-console",
                        "name":  "${client_account-console}",
                        "rootUrl":  "${authBaseUrl}",
                        "baseUrl":  "/realms/medical-platform/account/",
                        "surrogateAuthRequired":  false,
                        "enabled":  true,
                        "alwaysDisplayInConsole":  false,
                        "clientAuthenticatorType":  "client-secret",
                        "redirectUris":  [
                                             "/realms/medical-platform/account/*"
                                         ],
                        "webOrigins":  [

                                       ],
                        "notBefore":  0,
                        "bearerOnly":  false,
                        "consentRequired":  false,
                        "standardFlowEnabled":  true,
                        "implicitFlowEnabled":  false,
                        "directAccessGrantsEnabled":  false,
                        "serviceAccountsEnabled":  false,
                        "publicClient":  true,
                        "frontchannelLogout":  false,
                        "protocol":  "openid-connect",
                        "attributes":  {
                                           "realm_client":  "false",
                                           "post.logout.redirect.uris":  "+",
                                           "pkce.code.challenge.method":  "S256"
                                       },
                        "authenticationFlowBindingOverrides":  {

                                                               },
                        "fullScopeAllowed":  false,
                        "nodeReRegistrationTimeout":  0,
                        "protocolMappers":  [
                                                {
                                                    "id":  "007f15ca-c483-4963-80b7-80150ec23381",
                                                    "name":  "audience resolve",
                                                    "protocol":  "openid-connect",
                                                    "protocolMapper":  "oidc-audience-resolve-mapper",
                                                    "consentRequired":  false,
                                                    "config":  {

                                                               }
                                                }
                                            ],
                        "defaultClientScopes":  [
                                                    "web-origins",
                                                    "acr",
                                                    "roles",
                                                    "profile",
                                                    "basic",
                                                    "email"
                                                ],
                        "optionalClientScopes":  [
                                                     "address",
                                                     "phone",
                                                     "offline_access",
                                                     "organization",
                                                     "microprofile-jwt"
                                                 ]
                    },
                    {
                        "id":  "a3c35b80-a795-4566-a07b-aed497eb4b8c",
                        "clientId":  "admin-cli",
                        "name":  "${client_admin-cli}",
                        "surrogateAuthRequired":  false,
                        "enabled":  true,
                        "alwaysDisplayInConsole":  false,
                        "clientAuthenticatorType":  "client-secret",
                        "redirectUris":  [

                                         ],
                        "webOrigins":  [

                                       ],
                        "notBefore":  0,
                        "bearerOnly":  false,
                        "consentRequired":  false,
                        "standardFlowEnabled":  false,
                        "implicitFlowEnabled":  false,
                        "directAccessGrantsEnabled":  true,
                        "serviceAccountsEnabled":  false,
                        "publicClient":  true,
                        "frontchannelLogout":  false,
                        "protocol":  "openid-connect",
                        "attributes":  {
                                           "realm_client":  "false",
                                           "client.use.lightweight.access.token.enabled":  "true"
                                       },
                        "authenticationFlowBindingOverrides":  {

                                                               },
                        "fullScopeAllowed":  true,
                        "nodeReRegistrationTimeout":  0,
                        "defaultClientScopes":  [
                                                    "web-origins",
                                                    "acr",
                                                    "roles",
                                                    "profile",
                                                    "basic",
                                                    "email"
                                                ],
                        "optionalClientScopes":  [
                                                     "address",
                                                     "phone",
                                                     "offline_access",
                                                     "organization",
                                                     "microprofile-jwt"
                                                 ]
                    },
                    {
                        "id":  "6945864c-b3bd-4867-add3-9b0d2ed5ada0",
                        "clientId":  "broker",
                        "name":  "${client_broker}",
                        "surrogateAuthRequired":  false,
                        "enabled":  true,
                        "alwaysDisplayInConsole":  false,
                        "clientAuthenticatorType":  "client-secret",
                        "redirectUris":  [

                                         ],
                        "webOrigins":  [

                                       ],
                        "notBefore":  0,
                        "bearerOnly":  true,
                        "consentRequired":  false,
                        "standardFlowEnabled":  true,
                        "implicitFlowEnabled":  false,
                        "directAccessGrantsEnabled":  false,
                        "serviceAccountsEnabled":  false,
                        "publicClient":  false,
                        "frontchannelLogout":  false,
                        "protocol":  "openid-connect",
                        "attributes":  {
                                           "realm_client":  "true"
                                       },
                        "authenticationFlowBindingOverrides":  {

                                                               },
                        "fullScopeAllowed":  false,
                        "nodeReRegistrationTimeout":  0,
                        "defaultClientScopes":  [
                                                    "web-origins",
                                                    "acr",
                                                    "roles",
                                                    "profile",
                                                    "basic",
                                                    "email"
                                                ],
                        "optionalClientScopes":  [
                                                     "address",
                                                     "phone",
                                                     "offline_access",
                                                     "organization",
                                                     "microprofile-jwt"
                                                 ]
                    },
                    {
                        "id":  "7b49d6f5-9bf2-44a7-9661-9b2f53e7ba89",
                        "clientId":  "frontend-client",
                        "name":  "",
                        "description":  "",
                        "rootUrl":  "",
                        "adminUrl":  "",
                        "baseUrl":  "__FRONTEND_BASE_URL__",
                        "surrogateAuthRequired":  false,
                        "enabled":  true,
                        "alwaysDisplayInConsole":  false,
                        "clientAuthenticatorType":  "client-secret",
                        "redirectUris":  [
                                             "__FRONTEND_REDIRECT_URI__"
                                         ],
                        "webOrigins":  [
                                           "__FRONTEND_WEB_ORIGIN__"
                                       ],
                        "notBefore":  0,
                        "bearerOnly":  false,
                        "consentRequired":  false,
                        "standardFlowEnabled":  true,
                        "implicitFlowEnabled":  false,
                        "directAccessGrantsEnabled":  false,
                        "serviceAccountsEnabled":  false,
                        "publicClient":  true,
                        "frontchannelLogout":  true,
                        "protocol":  "openid-connect",
                        "attributes":  {
                                           "logout.confirmation.enabled":  "false",
                                           "oauth2.jwt.authorization.grant.enabled":  "false",
                                           "standard.token.exchange.enabled":  "false",
                                           "login_theme":  "keycloak",
                                           "frontchannel.logout.session.required":  "true",
                                           "post.logout.redirect.uris":  "__FRONTEND_POST_LOGOUT_REDIRECT_URIS__",
                                           "oauth2.device.authorization.grant.enabled":  "false",
                                           "backchannel.logout.revoke.offline.tokens":  "false",
                                           "realm_client":  "false",
                                           "oidc.ciba.grant.enabled":  "false",
                                           "backchannel.logout.session.required":  "true",
                                           "display.on.consent.screen":  "false",
                                           "pkce.code.challenge.method":  "S256",
                                           "dpop.bound.access.tokens":  "false"
                                       },
                        "authenticationFlowBindingOverrides":  {

                                                               },
                        "fullScopeAllowed":  true,
                        "nodeReRegistrationTimeout":  -1,
                        "defaultClientScopes":  [
                                                    "web-origins",
                                                    "acr",
                                                    "roles",
                                                    "profile",
                                                    "basic",
                                                    "email"
                                                ],
                        "optionalClientScopes":  [
                                                     "address",
                                                     "phone",
                                                     "offline_access",
                                                     "organization",
                                                     "microprofile-jwt"
                                                 ]
                    },
                    {
                        "id":  "82a1dcd4-5b19-4481-90b7-19aeac709c75",
                        "clientId":  "realm-management",
                        "name":  "${client_realm-management}",
                        "surrogateAuthRequired":  false,
                        "enabled":  true,
                        "alwaysDisplayInConsole":  false,
                        "clientAuthenticatorType":  "client-secret",
                        "redirectUris":  [

                                         ],
                        "webOrigins":  [

                                       ],
                        "notBefore":  0,
                        "bearerOnly":  true,
                        "consentRequired":  false,
                        "standardFlowEnabled":  true,
                        "implicitFlowEnabled":  false,
                        "directAccessGrantsEnabled":  false,
                        "serviceAccountsEnabled":  false,
                        "publicClient":  false,
                        "frontchannelLogout":  false,
                        "protocol":  "openid-connect",
                        "attributes":  {
                                           "realm_client":  "true"
                                       },
                        "authenticationFlowBindingOverrides":  {

                                                               },
                        "fullScopeAllowed":  false,
                        "nodeReRegistrationTimeout":  0,
                        "defaultClientScopes":  [
                                                    "web-origins",
                                                    "acr",
                                                    "roles",
                                                    "profile",
                                                    "basic",
                                                    "email"
                                                ],
                        "optionalClientScopes":  [
                                                     "address",
                                                     "phone",
                                                     "offline_access",
                                                     "organization",
                                                     "microprofile-jwt"
                                                 ]
                    },
                    {
                        "id":  "655b5fc4-c220-49e4-97ed-63c98d6f0c5b",
                        "clientId":  "security-admin-console",
                        "name":  "${client_security-admin-console}",
                        "rootUrl":  "${authAdminUrl}",
                        "baseUrl":  "/admin/medical-platform/console/",
                        "surrogateAuthRequired":  false,
                        "enabled":  true,
                        "alwaysDisplayInConsole":  false,
                        "clientAuthenticatorType":  "client-secret",
                        "redirectUris":  [
                                             "/admin/medical-platform/console/*"
                                         ],
                        "webOrigins":  [
                                           "+"
                                       ],
                        "notBefore":  0,
                        "bearerOnly":  false,
                        "consentRequired":  false,
                        "standardFlowEnabled":  true,
                        "implicitFlowEnabled":  false,
                        "directAccessGrantsEnabled":  false,
                        "serviceAccountsEnabled":  false,
                        "publicClient":  true,
                        "frontchannelLogout":  false,
                        "protocol":  "openid-connect",
                        "attributes":  {
                                           "realm_client":  "false",
                                           "client.use.lightweight.access.token.enabled":  "true",
                                           "post.logout.redirect.uris":  "+",
                                           "pkce.code.challenge.method":  "S256"
                                       },
                        "authenticationFlowBindingOverrides":  {

                                                               },
                        "fullScopeAllowed":  true,
                        "nodeReRegistrationTimeout":  0,
                        "protocolMappers":  [
                                                {
                                                    "id":  "a0f0d05c-b384-40d8-ba8c-918b0fa4291b",
                                                    "name":  "locale",
                                                    "protocol":  "openid-connect",
                                                    "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                    "consentRequired":  false,
                                                    "config":  {
                                                                   "introspection.token.claim":  "true",
                                                                   "userinfo.token.claim":  "true",
                                                                   "user.attribute":  "locale",
                                                                   "id.token.claim":  "true",
                                                                   "access.token.claim":  "true",
                                                                   "claim.name":  "locale",
                                                                   "jsonType.label":  "String"
                                                               }
                                                }
                                            ],
                        "defaultClientScopes":  [
                                                    "web-origins",
                                                    "acr",
                                                    "roles",
                                                    "profile",
                                                    "basic",
                                                    "email"
                                                ],
                        "optionalClientScopes":  [
                                                     "address",
                                                     "phone",
                                                     "offline_access",
                                                     "organization",
                                                     "microprofile-jwt"
                                                 ]
                    }
                ],
    "clientScopes":  [
                         {
                             "id":  "517d635d-3ac6-49c1-85ca-ec438a5b9975",
                             "name":  "web-origins",
                             "description":  "OpenID Connect scope for add allowed web origins to the access token",
                             "protocol":  "openid-connect",
                             "attributes":  {
                                                "include.in.token.scope":  "false",
                                                "consent.screen.text":  "",
                                                "display.on.consent.screen":  "false"
                                            },
                             "protocolMappers":  [
                                                     {
                                                         "id":  "04598b9b-04d9-4849-ab73-f3d94796392d",
                                                         "name":  "allowed web origins",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-allowed-origins-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "access.token.claim":  "true"
                                                                    }
                                                     }
                                                 ]
                         },
                         {
                             "id":  "9ed28ee9-5e39-45af-888c-945c63c9989f",
                             "name":  "basic",
                             "description":  "OpenID Connect scope for add all basic claims to the token",
                             "protocol":  "openid-connect",
                             "attributes":  {
                                                "include.in.token.scope":  "false",
                                                "display.on.consent.screen":  "false"
                                            },
                             "protocolMappers":  [
                                                     {
                                                         "id":  "eee4ac5e-487a-49aa-b37f-ca8650d3ee0e",
                                                         "name":  "auth_time",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usersessionmodel-note-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "user.session.note":  "AUTH_TIME",
                                                                        "id.token.claim":  "true",
                                                                        "introspection.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "auth_time",
                                                                        "jsonType.label":  "long"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "deac2c28-a356-4f67-8305-808f0db3bd03",
                                                         "name":  "sub",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-sub-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "access.token.claim":  "true"
                                                                    }
                                                     }
                                                 ]
                         },
                         {
                             "id":  "decdcc42-0cb3-4730-9884-dfab15507e74",
                             "name":  "email",
                             "description":  "OpenID Connect built-in scope: email",
                             "protocol":  "openid-connect",
                             "attributes":  {
                                                "include.in.token.scope":  "true",
                                                "consent.screen.text":  "${emailScopeConsentText}",
                                                "display.on.consent.screen":  "true"
                                            },
                             "protocolMappers":  [
                                                     {
                                                         "id":  "eb2367de-1d3b-4b5a-8697-d0d847561c6f",
                                                         "name":  "email verified",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-property-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "emailVerified",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "email_verified",
                                                                        "jsonType.label":  "boolean"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "5d528351-9474-4d6b-ad36-927a23ebda88",
                                                         "name":  "email",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "email",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "email",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     }
                                                 ]
                         },
                         {
                             "id":  "4f1c0835-a5ae-4565-81a3-ead38768277d",
                             "name":  "roles",
                             "description":  "OpenID Connect scope for add user roles to the access token",
                             "protocol":  "openid-connect",
                             "attributes":  {
                                                "include.in.token.scope":  "false",
                                                "consent.screen.text":  "${rolesScopeConsentText}",
                                                "display.on.consent.screen":  "true"
                                            },
                             "protocolMappers":  [
                                                     {
                                                         "id":  "0cc9dd7f-a080-4046-bec2-ac37df2f3920",
                                                         "name":  "client roles",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-client-role-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "user.attribute":  "foo",
                                                                        "introspection.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "resource_access.${client_id}.roles",
                                                                        "jsonType.label":  "String",
                                                                        "multivalued":  "true"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "1962c030-ceaa-42af-8d43-219ed256e538",
                                                         "name":  "audience resolve",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-audience-resolve-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "access.token.claim":  "true"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "b736d6de-39e5-45d5-9602-eefa7e936f02",
                                                         "name":  "realm roles",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-realm-role-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "user.attribute":  "foo",
                                                                        "introspection.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "realm_access.roles",
                                                                        "jsonType.label":  "String",
                                                                        "multivalued":  "true"
                                                                    }
                                                     }
                                                 ]
                         },
                         {
                             "id":  "0988c914-4669-480d-a62d-06971cf5e4bb",
                             "name":  "service_account",
                             "description":  "Specific scope for a client enabled for service accounts",
                             "protocol":  "openid-connect",
                             "attributes":  {
                                                "include.in.token.scope":  "false",
                                                "display.on.consent.screen":  "false"
                                            },
                             "protocolMappers":  [
                                                     {
                                                         "id":  "754f2f1c-a10b-4977-8881-b05e613a4184",
                                                         "name":  "Client IP Address",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usersessionmodel-note-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "user.session.note":  "clientAddress",
                                                                        "id.token.claim":  "true",
                                                                        "introspection.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "clientAddress",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "85be86b8-b125-411f-91e6-9a84d5910a6e",
                                                         "name":  "Client ID",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usersessionmodel-note-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "user.session.note":  "client_id",
                                                                        "id.token.claim":  "true",
                                                                        "introspection.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "client_id",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "806ee334-6018-4b91-9195-3e596781e6b9",
                                                         "name":  "Client Host",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usersessionmodel-note-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "user.session.note":  "clientHost",
                                                                        "id.token.claim":  "true",
                                                                        "introspection.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "clientHost",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     }
                                                 ]
                         },
                         {
                             "id":  "decbce45-21f3-4f56-b369-104e313f91bf",
                             "name":  "profile",
                             "description":  "OpenID Connect built-in scope: profile",
                             "protocol":  "openid-connect",
                             "attributes":  {
                                                "include.in.token.scope":  "true",
                                                "consent.screen.text":  "${profileScopeConsentText}",
                                                "display.on.consent.screen":  "true"
                                            },
                             "protocolMappers":  [
                                                     {
                                                         "id":  "dcb1af46-3522-4b4f-925c-1c088bba095e",
                                                         "name":  "nickname",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "nickname",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "nickname",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "d751dc9b-dfcd-48f6-a188-49cb45a7e89b",
                                                         "name":  "middle name",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "middleName",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "middle_name",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "fed1c4d6-a78a-49ca-9066-4ded7ef8aac6",
                                                         "name":  "updated at",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "updatedAt",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "updated_at",
                                                                        "jsonType.label":  "long"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "0188c632-dd63-49ec-91ef-3ba8663ec690",
                                                         "name":  "profile",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "profile",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "profile",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "744dc21b-bf16-4079-bc3d-2465db40df38",
                                                         "name":  "family name",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "lastName",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "family_name",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "3ae8edd8-330e-49e1-b270-feb93f42a3d1",
                                                         "name":  "zoneinfo",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "zoneinfo",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "zoneinfo",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "6aac27dc-b8e7-469f-8f5d-b5ed443e02e6",
                                                         "name":  "username",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "username",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "preferred_username",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "e70caa20-9e7f-4679-87fd-ac2ac845986c",
                                                         "name":  "locale",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "locale",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "locale",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "89198add-5d0b-4d65-9b90-906770d03207",
                                                         "name":  "birthdate",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "birthdate",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "birthdate",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "913bc331-8b5c-4eeb-bff4-e4eec2813d9c",
                                                         "name":  "picture",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "picture",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "picture",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "bf8f5234-c465-486c-8b4d-0b169e6dd41c",
                                                         "name":  "gender",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "gender",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "gender",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "559ed3a8-0b77-470b-99c9-006dd0102232",
                                                         "name":  "full name",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-full-name-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "id.token.claim":  "true",
                                                                        "introspection.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "20401ad1-2a96-47e5-8d30-442581a1ee7a",
                                                         "name":  "given name",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "firstName",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "given_name",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "36cfe284-93a2-43fc-bbee-a2c139b7d6b5",
                                                         "name":  "website",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "website",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "website",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     }
                                                 ]
                         },
                         {
                             "id":  "d123f7eb-5c15-4dff-8920-814d0fe5cd13",
                             "name":  "saml_organization",
                             "description":  "Organization Membership",
                             "protocol":  "saml",
                             "attributes":  {
                                                "display.on.consent.screen":  "false"
                                            },
                             "protocolMappers":  [
                                                     {
                                                         "id":  "818fdd96-d9fe-420f-bac9-b67bd5a3cff7",
                                                         "name":  "organization",
                                                         "protocol":  "saml",
                                                         "protocolMapper":  "saml-organization-membership-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {

                                                                    }
                                                     }
                                                 ]
                         },
                         {
                             "id":  "537ea812-c6e1-4297-8bcd-254500e93079",
                             "name":  "organization",
                             "description":  "Additional claims about the organization a subject belongs to",
                             "protocol":  "openid-connect",
                             "attributes":  {
                                                "include.in.token.scope":  "true",
                                                "consent.screen.text":  "${organizationScopeConsentText}",
                                                "display.on.consent.screen":  "true"
                                            },
                             "protocolMappers":  [
                                                     {
                                                         "id":  "3cfd6c14-39b3-4407-9d54-8098d1132a81",
                                                         "name":  "organization",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-organization-membership-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "id.token.claim":  "true",
                                                                        "introspection.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "organization",
                                                                        "jsonType.label":  "String",
                                                                        "multivalued":  "true"
                                                                    }
                                                     }
                                                 ]
                         },
                         {
                             "id":  "8d6b3161-5bce-464b-b4d4-7fb7ad40f991",
                             "name":  "phone",
                             "description":  "OpenID Connect built-in scope: phone",
                             "protocol":  "openid-connect",
                             "attributes":  {
                                                "include.in.token.scope":  "true",
                                                "consent.screen.text":  "${phoneScopeConsentText}",
                                                "display.on.consent.screen":  "true"
                                            },
                             "protocolMappers":  [
                                                     {
                                                         "id":  "7db6e913-fd58-4317-a2cb-e1b954f555dc",
                                                         "name":  "phone number verified",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "phoneNumberVerified",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "phone_number_verified",
                                                                        "jsonType.label":  "boolean"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "b44edb0b-9882-4f76-a810-d90a5ed23111",
                                                         "name":  "phone number",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "phoneNumber",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "phone_number",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     }
                                                 ]
                         },
                         {
                             "id":  "9b6087e3-89bf-4589-8637-0bd58d2130ec",
                             "name":  "microprofile-jwt",
                             "description":  "Microprofile - JWT built-in scope",
                             "protocol":  "openid-connect",
                             "attributes":  {
                                                "include.in.token.scope":  "true",
                                                "display.on.consent.screen":  "false"
                                            },
                             "protocolMappers":  [
                                                     {
                                                         "id":  "7d343d6d-cd5b-4345-a1d9-d68fb89ef8d1",
                                                         "name":  "groups",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-realm-role-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "multivalued":  "true",
                                                                        "user.attribute":  "foo",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "groups",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     },
                                                     {
                                                         "id":  "f9d72638-21ae-41e7-b512-76b57cf62534",
                                                         "name":  "upn",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-usermodel-attribute-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "introspection.token.claim":  "true",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute":  "username",
                                                                        "id.token.claim":  "true",
                                                                        "access.token.claim":  "true",
                                                                        "claim.name":  "upn",
                                                                        "jsonType.label":  "String"
                                                                    }
                                                     }
                                                 ]
                         },
                         {
                             "id":  "470b5238-1b33-4be5-805e-893b0d9c5d69",
                             "name":  "role_list",
                             "description":  "SAML role list",
                             "protocol":  "saml",
                             "attributes":  {
                                                "consent.screen.text":  "${samlRoleListScopeConsentText}",
                                                "display.on.consent.screen":  "true"
                                            },
                             "protocolMappers":  [
                                                     {
                                                         "id":  "8eefdb6c-29db-4467-9979-7543a467be7d",
                                                         "name":  "role list",
                                                         "protocol":  "saml",
                                                         "protocolMapper":  "saml-role-list-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "single":  "false",
                                                                        "attribute.nameformat":  "Basic",
                                                                        "attribute.name":  "Role"
                                                                    }
                                                     }
                                                 ]
                         },
                         {
                             "id":  "d46464e6-ebcf-477e-bf3e-668894f34e4a",
                             "name":  "address",
                             "description":  "OpenID Connect built-in scope: address",
                             "protocol":  "openid-connect",
                             "attributes":  {
                                                "include.in.token.scope":  "true",
                                                "consent.screen.text":  "${addressScopeConsentText}",
                                                "display.on.consent.screen":  "true"
                                            },
                             "protocolMappers":  [
                                                     {
                                                         "id":  "bfd47015-ea26-4e08-b31c-f20006b2a4e5",
                                                         "name":  "address",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-address-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "user.attribute.formatted":  "formatted",
                                                                        "user.attribute.country":  "country",
                                                                        "introspection.token.claim":  "true",
                                                                        "user.attribute.postal_code":  "postal_code",
                                                                        "userinfo.token.claim":  "true",
                                                                        "user.attribute.street":  "street",
                                                                        "id.token.claim":  "true",
                                                                        "user.attribute.region":  "region",
                                                                        "access.token.claim":  "true",
                                                                        "user.attribute.locality":  "locality"
                                                                    }
                                                     }
                                                 ]
                         },
                         {
                             "id":  "eb7bda04-4002-49b0-a338-6e077dd7e19d",
                             "name":  "acr",
                             "description":  "OpenID Connect scope for add acr (authentication context class reference) to the token",
                             "protocol":  "openid-connect",
                             "attributes":  {
                                                "include.in.token.scope":  "false",
                                                "display.on.consent.screen":  "false"
                                            },
                             "protocolMappers":  [
                                                     {
                                                         "id":  "553a254a-59f1-43db-aa20-a68ef0942e7f",
                                                         "name":  "acr loa level",
                                                         "protocol":  "openid-connect",
                                                         "protocolMapper":  "oidc-acr-mapper",
                                                         "consentRequired":  false,
                                                         "config":  {
                                                                        "id.token.claim":  "true",
                                                                        "introspection.token.claim":  "true",
                                                                        "access.token.claim":  "true"
                                                                    }
                                                     }
                                                 ]
                         },
                         {
                             "id":  "07402949-eb01-4b29-875a-272e2e1e11a1",
                             "name":  "offline_access",
                             "description":  "OpenID Connect built-in scope: offline_access",
                             "protocol":  "openid-connect",
                             "attributes":  {
                                                "consent.screen.text":  "${offlineAccessScopeConsentText}",
                                                "display.on.consent.screen":  "true"
                                            }
                         }
                     ],
    "defaultDefaultClientScopes":  [
                                       "role_list",
                                       "saml_organization",
                                       "profile",
                                       "email",
                                       "roles",
                                       "web-origins",
                                       "acr",
                                       "basic"
                                   ],
    "defaultOptionalClientScopes":  [
                                        "offline_access",
                                        "address",
                                        "phone",
                                        "microprofile-jwt",
                                        "organization"
                                    ],
    "browserSecurityHeaders":  {
                                   "contentSecurityPolicyReportOnly":  "",
                                   "xContentTypeOptions":  "nosniff",
                                   "referrerPolicy":  "no-referrer",
                                   "xRobotsTag":  "none",
                                   "xFrameOptions":  "SAMEORIGIN",
                                   "contentSecurityPolicy":  "frame-src \u0027self\u0027; frame-ancestors \u0027self\u0027; object-src \u0027none\u0027;",
                                   "strictTransportSecurity":  "max-age=31536000; includeSubDomains"
                               },
    "smtpServer":  {
                       "allowutf8":  "",
                       "debug":  "__SMTP_DEBUG__",
                       "replyToDisplayName":  "",
                       "starttls":  "__SMTP_STARTTLS__",
                       "auth":  "__SMTP_AUTH__",
                       "writeTimeout":  "10000",
                       "envelopeFrom":  "",
                       "ssl":  "__SMTP_SSL__",
                       "timeout":  "10000",
                       "password":  "__SMTP_PASSWORD__",
                       "port":  "__SMTP_PORT__",
                       "host":  "__SMTP_HOST__",
                       "replyTo":  "",
                       "from":  "__SMTP_FROM__",
                       "fromDisplayName":  "",
                       "authType":  "basic",
                       "connectionTimeout":  "10000",
                       "user":  "__SMTP_USERNAME__"
                   },
    "loginTheme":  "keycloak.v2",
    "accountTheme":  "keycloak.v3",
    "adminTheme":  "keycloak.v2",
    "emailTheme":  "keycloak",
    "eventsEnabled":  false,
    "eventsListeners":  [
                            "jboss-logging"
                        ],
    "enabledEventTypes":  [

                          ],
    "adminEventsEnabled":  false,
    "adminEventsDetailsEnabled":  false,
    "identityProviders":  [
                              {
                                  "alias":  "google",
                                  "displayName":  "",
                                  "internalId":  "84ece5a0-0392-410f-9285-dfb5235cf26b",
                                  "providerId":  "google",
                                  "enabled":  true,
                                  "trustEmail":  true,
                                  "storeToken":  false,
                                  "linkOnly":  false,
                                  "hideOnLogin":  false,
                                  "config":  {
                                                 "acceptsPromptNoneForwardFromClient":  "false",
                                                 "clientId":  "__GOOGLE_CLIENT_ID__",
                                                 "disableUserInfo":  "false",
                                                 "showInAccountConsole":  "ALWAYS",
                                                 "filteredByClaim":  "false",
                                                 "syncMode":  "IMPORT",
                                                 "clientSecret":  "__GOOGLE_CLIENT_SECRET__",
                                                 "caseSensitiveOriginalUsername":  "false"
                                             },
                                  "types":  [

                                            ]
                              }
                          ],
    "identityProviderMappers":  [

                                ],
    "components":  {
                       "org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy":  [
                                                                                                        {
                                                                                                            "id":  "b33f7e86-65db-401f-bd81-b1e4fb05f28c",
                                                                                                            "name":  "Allowed Registration Web Origins",
                                                                                                            "providerId":  "registration-web-origins",
                                                                                                            "subType":  "anonymous",
                                                                                                            "subComponents":  {

                                                                                                                              },
                                                                                                            "config":  {

                                                                                                                       }
                                                                                                        },
                                                                                                        {
                                                                                                            "id":  "ae901628-7aff-48da-8379-4be21ea2e2ac",
                                                                                                            "name":  "Trusted Hosts",
                                                                                                            "providerId":  "trusted-hosts",
                                                                                                            "subType":  "anonymous",
                                                                                                            "subComponents":  {

                                                                                                                              },
                                                                                                            "config":  {
                                                                                                                           "host-sending-registration-request-must-match":  [
                                                                                                                                                                                "true"
                                                                                                                                                                            ],
                                                                                                                           "client-uris-must-match":  [
                                                                                                                                                          "true"
                                                                                                                                                      ]
                                                                                                                       }
                                                                                                        },
                                                                                                        {
                                                                                                            "id":  "4a22407f-b3a7-407a-a9d4-939d4dfe39ca",
                                                                                                            "name":  "Consent Required",
                                                                                                            "providerId":  "consent-required",
                                                                                                            "subType":  "anonymous",
                                                                                                            "subComponents":  {

                                                                                                                              },
                                                                                                            "config":  {

                                                                                                                       }
                                                                                                        },
                                                                                                        {
                                                                                                            "id":  "c47a5312-c831-48ab-a9cf-c2b2bc9bd4ae",
                                                                                                            "name":  "Full Scope Disabled",
                                                                                                            "providerId":  "scope",
                                                                                                            "subType":  "anonymous",
                                                                                                            "subComponents":  {

                                                                                                                              },
                                                                                                            "config":  {

                                                                                                                       }
                                                                                                        },
                                                                                                        {
                                                                                                            "id":  "441ef347-26e4-4ca4-be57-02da6fb681b3",
                                                                                                            "name":  "Max Clients Limit",
                                                                                                            "providerId":  "max-clients",
                                                                                                            "subType":  "anonymous",
                                                                                                            "subComponents":  {

                                                                                                                              },
                                                                                                            "config":  {
                                                                                                                           "max-clients":  [
                                                                                                                                               "200"
                                                                                                                                           ]
                                                                                                                       }
                                                                                                        },
                                                                                                        {
                                                                                                            "id":  "e2dfe198-2abe-4fba-b8f2-1badb1625e05",
                                                                                                            "name":  "Allowed Protocol Mapper Types",
                                                                                                            "providerId":  "allowed-protocol-mappers",
                                                                                                            "subType":  "authenticated",
                                                                                                            "subComponents":  {

                                                                                                                              },
                                                                                                            "config":  {
                                                                                                                           "allowed-protocol-mapper-types":  [
                                                                                                                                                                 "oidc-usermodel-property-mapper",
                                                                                                                                                                 "oidc-sha256-pairwise-sub-mapper",
                                                                                                                                                                 "saml-user-property-mapper",
                                                                                                                                                                 "oidc-full-name-mapper",
                                                                                                                                                                 "oidc-address-mapper",
                                                                                                                                                                 "oidc-usermodel-attribute-mapper",
                                                                                                                                                                 "saml-user-attribute-mapper",
                                                                                                                                                                 "saml-role-list-mapper"
                                                                                                                                                             ]
                                                                                                                       }
                                                                                                        },
                                                                                                        {
                                                                                                            "id":  "ebddb707-6b23-4d69-8874-63efb282ff55",
                                                                                                            "name":  "Allowed Protocol Mapper Types",
                                                                                                            "providerId":  "allowed-protocol-mappers",
                                                                                                            "subType":  "anonymous",
                                                                                                            "subComponents":  {

                                                                                                                              },
                                                                                                            "config":  {
                                                                                                                           "allowed-protocol-mapper-types":  [
                                                                                                                                                                 "saml-user-attribute-mapper",
                                                                                                                                                                 "oidc-full-name-mapper",
                                                                                                                                                                 "saml-role-list-mapper",
                                                                                                                                                                 "oidc-usermodel-attribute-mapper",
                                                                                                                                                                 "oidc-sha256-pairwise-sub-mapper",
                                                                                                                                                                 "saml-user-property-mapper",
                                                                                                                                                                 "oidc-usermodel-property-mapper",
                                                                                                                                                                 "oidc-address-mapper"
                                                                                                                                                             ]
                                                                                                                       }
                                                                                                        },
                                                                                                        {
                                                                                                            "id":  "8e6819eb-4515-4301-b7ee-69b45936d052",
                                                                                                            "name":  "Allowed Registration Web Origins",
                                                                                                            "providerId":  "registration-web-origins",
                                                                                                            "subType":  "authenticated",
                                                                                                            "subComponents":  {

                                                                                                                              },
                                                                                                            "config":  {

                                                                                                                       }
                                                                                                        },
                                                                                                        {
                                                                                                            "id":  "e40dc1b4-23ad-4c1b-b2e8-dcc4e6dab6db",
                                                                                                            "name":  "Allowed Client Scopes",
                                                                                                            "providerId":  "allowed-client-templates",
                                                                                                            "subType":  "anonymous",
                                                                                                            "subComponents":  {

                                                                                                                              },
                                                                                                            "config":  {
                                                                                                                           "allow-default-scopes":  [
                                                                                                                                                        "true"
                                                                                                                                                    ]
                                                                                                                       }
                                                                                                        },
                                                                                                        {
                                                                                                            "id":  "688d40f5-f511-4b88-95b8-d7c06419908f",
                                                                                                            "name":  "Allowed Client Scopes",
                                                                                                            "providerId":  "allowed-client-templates",
                                                                                                            "subType":  "authenticated",
                                                                                                            "subComponents":  {

                                                                                                                              },
                                                                                                            "config":  {
                                                                                                                           "allow-default-scopes":  [
                                                                                                                                                        "true"
                                                                                                                                                    ]
                                                                                                                       }
                                                                                                        }
                                                                                                    ]
                   },
    "internationalizationEnabled":  false,
    "authenticationFlows":  [
                                {
                                    "id":  "8d709663-4213-40ba-8e5c-5b71d2e8a959",
                                    "alias":  "Account verification options",
                                    "description":  "Method with which to verify the existing account",
                                    "providerId":  "basic-flow",
                                    "topLevel":  false,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "idp-email-verification",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticatorFlow":  true,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  true,
                                                                         "flowAlias":  "Verify Existing Account by Re-authentication",
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "e2251c54-c11b-491b-b076-1187c808a04c",
                                    "alias":  "Browser - Conditional 2FA",
                                    "description":  "Flow to determine if any 2FA is required for the authentication",
                                    "providerId":  "basic-flow",
                                    "topLevel":  false,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "conditional-user-configured",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticatorConfig":  "browser-conditional-credential",
                                                                         "authenticator":  "conditional-credential",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "auth-otp-form",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  30,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "webauthn-authenticator",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "DISABLED",
                                                                         "priority":  40,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "auth-recovery-authn-code-form",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "DISABLED",
                                                                         "priority":  50,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "b6694b8c-9c0e-47b2-9bcd-3ba0b5fd2cf6",
                                    "alias":  "Browser - Conditional Organization",
                                    "description":  "Flow to determine if the organization identity-first login is to be used",
                                    "providerId":  "basic-flow",
                                    "topLevel":  false,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "conditional-user-configured",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "organization",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "39d53710-f931-424c-8eaf-834950892edf",
                                    "alias":  "Direct Grant - Conditional OTP",
                                    "description":  "Flow to determine if the OTP is required for the authentication",
                                    "providerId":  "basic-flow",
                                    "topLevel":  false,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "conditional-user-configured",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "direct-grant-validate-otp",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "565b0868-7e37-44a2-a66f-5fa1fa375529",
                                    "alias":  "First Broker Login - Conditional Organization",
                                    "description":  "Flow to determine if the authenticator that adds organization members is to be used",
                                    "providerId":  "basic-flow",
                                    "topLevel":  false,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "conditional-user-configured",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "idp-add-organization-member",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "17d921cc-d717-490c-955f-a59856f578bb",
                                    "alias":  "First broker login - Conditional 2FA",
                                    "description":  "Flow to determine if any 2FA is required for the authentication",
                                    "providerId":  "basic-flow",
                                    "topLevel":  false,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "conditional-user-configured",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticatorConfig":  "first-broker-login-conditional-credential",
                                                                         "authenticator":  "conditional-credential",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "auth-otp-form",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  30,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "webauthn-authenticator",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "DISABLED",
                                                                         "priority":  40,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "auth-recovery-authn-code-form",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "DISABLED",
                                                                         "priority":  50,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "91da498c-3f11-491c-8a63-dbcdc8ea04d6",
                                    "alias":  "Handle Existing Account",
                                    "description":  "Handle what to do if there is existing account with same email/username like authenticated identity provider",
                                    "providerId":  "basic-flow",
                                    "topLevel":  false,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "idp-confirm-link",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticatorFlow":  true,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  true,
                                                                         "flowAlias":  "Account verification options",
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "08ddb490-a6a1-464b-ba8e-e0b2c196d566",
                                    "alias":  "Organization",
                                    "providerId":  "basic-flow",
                                    "topLevel":  false,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticatorFlow":  true,
                                                                         "requirement":  "CONDITIONAL",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  true,
                                                                         "flowAlias":  "Browser - Conditional Organization",
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "4b7772f1-5fb3-4496-8df6-921b49c1831c",
                                    "alias":  "Reset - Conditional OTP",
                                    "description":  "Flow to determine if the OTP should be reset or not. Set to REQUIRED to force.",
                                    "providerId":  "basic-flow",
                                    "topLevel":  false,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "conditional-user-configured",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "reset-otp",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "8dea2ba6-4e92-4abb-8dbe-049515748de8",
                                    "alias":  "User creation or linking",
                                    "description":  "Flow for the existing/non-existing user alternatives",
                                    "providerId":  "basic-flow",
                                    "topLevel":  false,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticatorConfig":  "create unique user config",
                                                                         "authenticator":  "idp-create-user-if-unique",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticatorFlow":  true,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  true,
                                                                         "flowAlias":  "Handle Existing Account",
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "574d91e9-91ae-43a4-bfb2-e3da726d6335",
                                    "alias":  "Verify Existing Account by Re-authentication",
                                    "description":  "Reauthentication of existing account",
                                    "providerId":  "basic-flow",
                                    "topLevel":  false,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "idp-username-password-form",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticatorFlow":  true,
                                                                         "requirement":  "CONDITIONAL",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  true,
                                                                         "flowAlias":  "First broker login - Conditional 2FA",
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "de1f79df-1009-4bc1-933e-ee2dfb1e235b",
                                    "alias":  "browser",
                                    "description":  "Browser based authentication",
                                    "providerId":  "basic-flow",
                                    "topLevel":  true,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "auth-cookie",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "auth-spnego",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "DISABLED",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "identity-provider-redirector",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  25,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticatorFlow":  true,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  26,
                                                                         "autheticatorFlow":  true,
                                                                         "flowAlias":  "Organization",
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticatorFlow":  true,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  30,
                                                                         "autheticatorFlow":  true,
                                                                         "flowAlias":  "forms",
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "4d78a16e-7e41-49da-be42-916583f0a0c3",
                                    "alias":  "clients",
                                    "description":  "Base authentication for clients",
                                    "providerId":  "client-flow",
                                    "topLevel":  true,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "client-secret",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "client-jwt",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "client-secret-jwt",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  30,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "client-x509",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "ALTERNATIVE",
                                                                         "priority":  40,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "27b93c33-8de2-4db7-8fa0-1e9511d9878b",
                                    "alias":  "direct grant",
                                    "description":  "OpenID Connect Resource Owner Grant",
                                    "providerId":  "basic-flow",
                                    "topLevel":  true,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "direct-grant-validate-username",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "direct-grant-validate-password",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticatorFlow":  true,
                                                                         "requirement":  "CONDITIONAL",
                                                                         "priority":  30,
                                                                         "autheticatorFlow":  true,
                                                                         "flowAlias":  "Direct Grant - Conditional OTP",
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "70529228-5f1f-4a0e-ab14-77e4ea66fc44",
                                    "alias":  "docker auth",
                                    "description":  "Used by Docker clients to authenticate against the IDP",
                                    "providerId":  "basic-flow",
                                    "topLevel":  true,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "docker-http-basic-authenticator",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "d228507b-d483-4e6e-b144-c79e6c573a00",
                                    "alias":  "first broker login",
                                    "description":  "Actions taken after first broker login with identity provider account, which is not yet linked to any Keycloak account",
                                    "providerId":  "basic-flow",
                                    "topLevel":  true,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticatorConfig":  "review profile config",
                                                                         "authenticator":  "idp-review-profile",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticatorFlow":  true,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  true,
                                                                         "flowAlias":  "User creation or linking",
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticatorFlow":  true,
                                                                         "requirement":  "CONDITIONAL",
                                                                         "priority":  60,
                                                                         "autheticatorFlow":  true,
                                                                         "flowAlias":  "First Broker Login - Conditional Organization",
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "999557ea-c41d-4fde-b9ce-189462e93409",
                                    "alias":  "forms",
                                    "description":  "Username, password, otp and other auth forms.",
                                    "providerId":  "basic-flow",
                                    "topLevel":  false,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "auth-username-password-form",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticatorFlow":  true,
                                                                         "requirement":  "CONDITIONAL",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  true,
                                                                         "flowAlias":  "Browser - Conditional 2FA",
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "f384006f-cb57-4618-9f3c-b088553cb943",
                                    "alias":  "registration",
                                    "description":  "Registration flow",
                                    "providerId":  "basic-flow",
                                    "topLevel":  true,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "registration-page-form",
                                                                         "authenticatorFlow":  true,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  true,
                                                                         "flowAlias":  "registration form",
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "24b7347c-3f83-47f9-a944-54027025eac0",
                                    "alias":  "registration form",
                                    "description":  "Registration form",
                                    "providerId":  "form-flow",
                                    "topLevel":  false,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "registration-user-creation",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "registration-password-action",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  50,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "registration-recaptcha-action",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "DISABLED",
                                                                         "priority":  60,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "registration-terms-and-conditions",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "DISABLED",
                                                                         "priority":  70,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "d533f7ae-6dc1-4202-9ede-b5fc5afd1592",
                                    "alias":  "reset credentials",
                                    "description":  "Reset credentials for a user if they forgot their password or something",
                                    "providerId":  "basic-flow",
                                    "topLevel":  true,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "reset-credentials-choose-user",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "reset-credential-email",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  20,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticator":  "reset-password",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  30,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     },
                                                                     {
                                                                         "authenticatorFlow":  true,
                                                                         "requirement":  "CONDITIONAL",
                                                                         "priority":  40,
                                                                         "autheticatorFlow":  true,
                                                                         "flowAlias":  "Reset - Conditional OTP",
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                },
                                {
                                    "id":  "721e74d3-11ab-4d56-b5d7-900a5e9c0188",
                                    "alias":  "saml ecp",
                                    "description":  "SAML ECP Profile Authentication Flow",
                                    "providerId":  "basic-flow",
                                    "topLevel":  true,
                                    "builtIn":  true,
                                    "authenticationExecutions":  [
                                                                     {
                                                                         "authenticator":  "http-basic-authenticator",
                                                                         "authenticatorFlow":  false,
                                                                         "requirement":  "REQUIRED",
                                                                         "priority":  10,
                                                                         "autheticatorFlow":  false,
                                                                         "userSetupAllowed":  false
                                                                     }
                                                                 ]
                                }
                            ],
    "authenticatorConfig":  [
                                {
                                    "id":  "d5211c4b-e2bd-4cbc-a4d6-a70020e95f5d",
                                    "alias":  "browser-conditional-credential",
                                    "config":  {
                                                   "credentials":  "webauthn-passwordless"
                                               }
                                },
                                {
                                    "id":  "3285b6d7-4082-4c81-8377-1dd1bf360f41",
                                    "alias":  "create unique user config",
                                    "config":  {
                                                   "require.password.update.after.registration":  "false"
                                               }
                                },
                                {
                                    "id":  "228fe933-a934-4bae-8834-40684c28465b",
                                    "alias":  "first-broker-login-conditional-credential",
                                    "config":  {
                                                   "credentials":  "webauthn-passwordless"
                                               }
                                },
                                {
                                    "id":  "3fa16eea-8c3e-48ea-a19b-a1ca90b4ba55",
                                    "alias":  "review profile config",
                                    "config":  {
                                                   "update.profile.on.first.login":  "missing"
                                               }
                                }
                            ],
    "requiredActions":  [
                            {
                                "alias":  "webauthn-register-passwordless",
                                "name":  "Webauthn Register Passwordless",
                                "providerId":  "webauthn-register-passwordless",
                                "enabled":  true,
                                "defaultAction":  false,
                                "priority":  10,
                                "config":  {

                                           }
                            },
                            {
                                "alias":  "UPDATE_PASSWORD",
                                "name":  "Update Password",
                                "providerId":  "UPDATE_PASSWORD",
                                "enabled":  true,
                                "defaultAction":  false,
                                "priority":  20,
                                "config":  {

                                           }
                            },
                            {
                                "alias":  "CONFIGURE_TOTP",
                                "name":  "Configure OTP",
                                "providerId":  "CONFIGURE_TOTP",
                                "enabled":  true,
                                "defaultAction":  false,
                                "priority":  30,
                                "config":  {

                                           }
                            },
                            {
                                "alias":  "TERMS_AND_CONDITIONS",
                                "name":  "Terms and Conditions",
                                "providerId":  "TERMS_AND_CONDITIONS",
                                "enabled":  false,
                                "defaultAction":  false,
                                "priority":  40,
                                "config":  {

                                           }
                            },
                            {
                                "alias":  "UPDATE_PROFILE",
                                "name":  "Update Profile",
                                "providerId":  "UPDATE_PROFILE",
                                "enabled":  true,
                                "defaultAction":  false,
                                "priority":  50,
                                "config":  {

                                           }
                            },
                            {
                                "alias":  "VERIFY_EMAIL",
                                "name":  "Verify Email",
                                "providerId":  "VERIFY_EMAIL",
                                "enabled":  true,
                                "defaultAction":  false,
                                "priority":  60,
                                "config":  {

                                           }
                            },
                            {
                                "alias":  "delete_account",
                                "name":  "Delete Account",
                                "providerId":  "delete_account",
                                "enabled":  false,
                                "defaultAction":  false,
                                "priority":  70,
                                "config":  {

                                           }
                            },
                            {
                                "alias":  "UPDATE_EMAIL",
                                "name":  "Update Email",
                                "providerId":  "UPDATE_EMAIL",
                                "enabled":  false,
                                "defaultAction":  false,
                                "priority":  80,
                                "config":  {

                                           }
                            },
                            {
                                "alias":  "webauthn-register",
                                "name":  "Webauthn Register",
                                "providerId":  "webauthn-register",
                                "enabled":  true,
                                "defaultAction":  false,
                                "priority":  90,
                                "config":  {

                                           }
                            },
                            {
                                "alias":  "VERIFY_PROFILE",
                                "name":  "Verify Profile",
                                "providerId":  "VERIFY_PROFILE",
                                "enabled":  true,
                                "defaultAction":  false,
                                "priority":  100,
                                "config":  {

                                           }
                            },
                            {
                                "alias":  "delete_credential",
                                "name":  "Delete Credential",
                                "providerId":  "delete_credential",
                                "enabled":  true,
                                "defaultAction":  false,
                                "priority":  110,
                                "config":  {

                                           }
                            },
                            {
                                "alias":  "idp_link",
                                "name":  "Linking Identity Provider",
                                "providerId":  "idp_link",
                                "enabled":  true,
                                "defaultAction":  false,
                                "priority":  120,
                                "config":  {

                                           }
                            },
                            {
                                "alias":  "CONFIGURE_RECOVERY_AUTHN_CODES",
                                "name":  "Recovery Authentication Codes",
                                "providerId":  "CONFIGURE_RECOVERY_AUTHN_CODES",
                                "enabled":  true,
                                "defaultAction":  false,
                                "priority":  130,
                                "config":  {

                                           }
                            },
                            {
                                "alias":  "update_user_locale",
                                "name":  "Update User Locale",
                                "providerId":  "update_user_locale",
                                "enabled":  true,
                                "defaultAction":  false,
                                "priority":  1000,
                                "config":  {

                                           }
                            }
                        ],
    "browserFlow":  "browser",
    "registrationFlow":  "registration",
    "directGrantFlow":  "direct grant",
    "resetCredentialsFlow":  "reset credentials",
    "clientAuthenticationFlow":  "clients",
    "dockerAuthenticationFlow":  "docker auth",
    "firstBrokerLoginFlow":  "first broker login",
    "attributes":  {
                       "cibaBackchannelTokenDeliveryMode":  "poll",
                       "cibaAuthRequestedUserHint":  "login_hint",
                       "oauth2DevicePollingInterval":  "5",
                       "clientOfflineSessionMaxLifespan":  "0",
                       "clientSessionIdleTimeout":  "0",
                       "clientOfflineSessionIdleTimeout":  "0",
                       "cibaInterval":  "5",
                       "realmReusableOtpCode":  "false",
                       "cibaExpiresIn":  "120",
                       "oauth2DeviceCodeLifespan":  "600",
                       "saml.signature.algorithm":  "",
                       "parRequestUriLifespan":  "60",
                       "clientSessionMaxLifespan":  "0",
                       "frontendUrl":  "",
                       "acr.loa.map":  "{}",
                       "darkMode":  "true"
                   },
    "keycloakVersion":  "26.5.6",
    "userManagedAccessAllowed":  false,
    "organizationsEnabled":  false,
    "verifiableCredentialsEnabled":  false,
    "adminPermissionsEnabled":  false,
    "clientProfiles":  {
                           "profiles":  [

                                        ]
                       },
    "clientPolicies":  {
                           "policies":  [

                                        ]
                       }
}
