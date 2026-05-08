import request from './request'

export const listBills = (params) => request.get('/api/bills', { params })
export const getBill = (id) => request.get(`/api/bills/${id}`)
export const chargeBill = (id) => request.post(`/api/bills/${id}/charge`)
export const refundBill = (id) => request.post(`/api/bills/${id}/refund`)
