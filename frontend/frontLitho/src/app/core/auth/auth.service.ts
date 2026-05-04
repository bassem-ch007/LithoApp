import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import keycloak from './keycloak.config';

export type AppRole = 'ADMIN' | 'UROLOGUE' | 'BIOLOGIST';

const APP_ROLES: AppRole[] = ['ADMIN', 'UROLOGUE', 'BIOLOGIST'];

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private initialized = false;
  private userRoles: string[] = [];

  constructor(private router: Router) {}

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

    this.updateUserRoles();
    this.initialized = true;
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
      this.updateUserRoles();
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
    return this.getAppRoles();
  }

  getUserRoles(): string[] {
    return this.getAppRoles();
  }

  getRealmRoles(): string[] {
    const roles = (keycloak.tokenParsed as any)?.realm_access?.roles;
    return Array.isArray(roles) ? roles.filter(role => typeof role === 'string') : [];
  }

  getAppRoles(): AppRole[] {
    const realmRoles = new Set(this.getRealmRoles());
    return APP_ROLES.filter(role => realmRoles.has(role));
  }

  hasRole(role: string): boolean {
    return this.isAppRole(role) && this.getAppRoles().includes(role);
  }

  hasAnyRole(roles: string[]): boolean {
    return roles.some(role => this.hasRole(role));
  }

  hasAppRole(): boolean {
    return this.getAppRoles().length > 0;
  }

  getPrimaryAppRole(): AppRole | null {
    return this.getAppRoles()[0] ?? null;
  }

  redirectByRole(): Promise<boolean> {
    const role = this.getPrimaryAppRole();

    if (role === 'ADMIN') {
      return this.router.navigateByUrl('/admin/dashboard');
    }

    if (role === 'UROLOGUE') {
      return this.router.navigateByUrl('/patients');
    }

    if (role === 'BIOLOGIST') {
      return this.router.navigateByUrl('/analysis-search');
    }

    return this.router.navigateByUrl('/pending-approval');
  }

  async refreshUserRolesOrToken(): Promise<void> {
    await this.refreshToken(-1);
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

  private updateUserRoles(): void {
    this.userRoles = this.getAppRoles();
  }

  private isAppRole(role: string): role is AppRole {
    return APP_ROLES.includes(role as AppRole);
  }
}
