import http from "./http";

export const applyMerchantApi = (data) => http.post("/merchant/apply", data);
export const getMyMerchantApi = () => http.get("/merchant/me");
export const updateMyMerchantApi = (data) => http.put("/merchant/me", data);

export const listPendingMerchantsApi = () => http.get("/admin/merchant/pending");
export const approveMerchantApi = (id, pass) =>
  http.post(`/admin/merchant/${id}/approve`, null, { params: { pass } });
