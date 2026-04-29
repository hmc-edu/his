import request from './request'

export const listDoctors = (params) => request.get('/api/doctors', { params })
export const getDoctor = (id) => request.get(`/api/doctors/${id}`)
export const createDoctor = (data) => request.post('/api/doctors', data)
export const updateDoctor = (id, data) => request.put(`/api/doctors/${id}`, data)
export const deleteDoctor = (id) => request.delete(`/api/doctors/${id}`)
