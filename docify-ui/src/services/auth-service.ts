import { AuthResponse } from '../lib/types';
import { apiRequest, authStorage } from './api-client';

export const authService = {
  login: async (email: string, password: string) => {
    const response = await apiRequest<AuthResponse>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
      skipAuth: true,
    });
    authStorage.setToken(response.accessToken);
    authStorage.setUser(response.user);
    return response;
  },

  register: async (name: string, email: string, password: string) => {
    const response = await apiRequest<AuthResponse>('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify({ name, email, password }),
      skipAuth: true,
    });
    authStorage.setToken(response.accessToken);
    authStorage.setUser(response.user);
    return response;
  },

  logout: () => authStorage.clear(),
  isAuthenticated: () => Boolean(authStorage.getToken()),
  getUser: () => authStorage.getUser(),
};
