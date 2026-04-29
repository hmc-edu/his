import request from './request'

export const loginApi = (data) => request.post('/api/auth/login', data)
export const logoutApi = () => request.post('/api/auth/logout')
export const meApi = () => request.get('/api/auth/me')
