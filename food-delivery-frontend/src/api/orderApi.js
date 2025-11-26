import http from "./http";

export const createOrderApi = (data) => http.post("/order/create", data);
export const listUserOrdersApi = () => http.get("/order/list/user");
export const listMerchantOrdersApi = (merchantId) =>
  http.get(`/order/list/merchant/${merchantId}`);
export const updateOrderStatusApi = (orderId, status) =>
  http.put(`/order/${orderId}/status`, null, { params: { status } });
