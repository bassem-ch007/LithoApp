import { Injectable } from '@angular/core';
import keycloak from './keycloak.config';
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private initialized = false;
  private userRoles: string[] = [];

  async init(): Promise<boolean> {
    if (this.initialized) {
      return this.isAuthenticated();
    }

    const authenticated = await keycloak.init({
      onLoad: 'check-sso',
      silentCheckSsoRedirectUri: `${window.location.origin}/assets/silent-check-sso.html`,
      pkceMethod: 'S256',
      checkLoginIframe: false
    });

    this.userRoles = ((keycloak.tokenParsed as any)?.realm_access?.roles ?? []);
    this.initialized = true;
    console.log(keycloak.tokenParsed as any);
    return authenticated;
  }

  async login(redirectUri?: string): Promise<void> {
    await keycloak.login({
      redirectUri: redirectUri ?? window.location.origin
    });
  }

  async logout(): Promise<void> {
    await keycloak.logout({
      redirectUri: window.location.origin
    });
  }

  isAuthenticated(): boolean {
    return !!keycloak.authenticated;
  }

  isInitialized(): boolean {
    return !!this.initialized;
  }
  getToken(): string | undefined {
    return keycloak.token;
  }

  async refreshToken(minValidity = 30): Promise<boolean> {
    try {
      const refreshed = await keycloak.updateToken(minValidity);
      this.userRoles = ((keycloak.tokenParsed as any)?.realm_access?.roles ?? []);
      return refreshed;
    } catch (error) {
      console.error('Token refresh failed', error);
      return false;
    }
  }

  getUsername(): string {
    return (keycloak.tokenParsed as any)?.preferred_username ?? '';
  }

  getFullName(): string {
    return (keycloak.tokenParsed as any)?.name ?? '';
  }
  getEmail(): string {
    return (keycloak.tokenParsed as any)?.email ?? '';
  }

  getRoles(): string[] {
    return this.userRoles;
  }

  hasRole(role: string): boolean {
    return this.userRoles.includes(role);
  }

  isUrologue(): boolean {
    return this.hasRole('UROLOGUE');
  }

  isBiologist(): boolean {
    return this.hasRole('BIOLOGIST');
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }
}
