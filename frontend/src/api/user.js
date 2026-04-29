import request from './request'

export const pageUsers = (params) => request.get('/api/users', { params })
export const createUser = (data) => request.post('/api/users', data)
export const updateUser = (id, data) => request.put(`/api/users/${id}`, data)
export const deleteUser = (id) => request.delete(`/api/users/${id}`)
