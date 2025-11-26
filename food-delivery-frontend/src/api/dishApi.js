import http from "./http";

export const listOnSaleByMerchantApi = (merchantId) =>
  http.get(`/dish/list/${merchantId}`);

export const listMerchantDishesApi = (merchantId) =>
  http.get(`/dish/merchant/${merchantId}`);

export const addDishApi = (data) => http.post("/dish", data);
export const updateDishApi = (data) => http.put("/dish", data);
export const deleteDishApi = (id) => http.delete(`/dish/${id}`);
