import request from './request'

export const listDepartments = () => request.get('/api/departments')
export const createDepartment = (data) => request.post('/api/departments', data)
export const updateDepartment = (id, data) => request.put(`/api/departments/${id}`, data)
export const deleteDepartment = (id) => request.delete(`/api/departments/${id}`)
