import request from './request'

export const pagePatients = (params) => request.get('/api/patients', { params })
export const getPatient = (id) => request.get(`/api/patients/${id}`)
export const createPatient = (data) => request.post('/api/patients', data)
export const updatePatient = (id, data) => request.put(`/api/patients/${id}`, data)
export const deletePatient = (id) => request.delete(`/api/patients/${id}`)
