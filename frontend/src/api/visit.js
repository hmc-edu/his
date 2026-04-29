import request from './request'

export const queueVisits = (params) => request.get('/api/visits/queue', { params })
export const startVisit = (data) => request.post('/api/visits', data)
export const getVisit = (id) => request.get(`/api/visits/${id}`)
export const updateVisit = (id, data) => request.put(`/api/visits/${id}`, data)
export const finishVisit = (id) => request.post(`/api/visits/${id}/finish`)
export const prescribe = (id, data) => request.post(`/api/visits/${id}/prescriptions`, data)
