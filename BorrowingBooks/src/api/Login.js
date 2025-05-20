import http from '@/utils/http.js';

export const LoginAPI = async (data)=> {
    try {
        return await http.post('/user/login', data)
    } catch (e) {
        console.log("LoginAPI error", e);
    }
}

export const RegisterAPI = async (data)=> {
    try {
        return await http.post('/user/register', data)
    } catch (e) {
        console.log("RegisterAPI error", e);
    }
}