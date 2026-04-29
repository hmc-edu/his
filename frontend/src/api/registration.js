import request from './request'

export const createRegistration = (data) => request.post('/api/registrations', data)
export const listRegistrations = (params) => request.get('/api/registrations', { params })
export const getRegistration = (id) => request.get(`/api/registrations/${id}`)
export const cancelRegistration = (id) => request.post(`/api/registrations/${id}/cancel`)
