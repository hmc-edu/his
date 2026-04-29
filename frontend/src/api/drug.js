import request from './request'

export const listDrugs = (params) => request.get('/api/drugs', { params })
export const createDrug = (data) => request.post('/api/drugs', data)
export const updateDrug = (id, data) => request.put(`/api/drugs/${id}`, data)
export const deleteDrug = (id) => request.delete(`/api/drugs/${id}`)
