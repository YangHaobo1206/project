import http from "./http";

export const loginApi = (data) => http.post("/user/login", data);
export const registerApi = (data) => http.post("/user/register", data);
export const getProfileApi = () => http.get("/user/profile");
export const updateProfileApi = (data) => http.put("/user/profile", data);
