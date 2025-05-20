import http from '@/utils/http.js';

export const BookCategoryAPI = async () => {
    try {
        return await http.get('/bookCategory/list');
    } catch (e) {
        console.error('获取书籍分类失败:', e);
        throw e;
    }
}

export const BookListAPI = async (data) => {
    try {
        return await http.post('/book/list', data);
    } catch (e) {
        console.error('获取书籍列表失败:', e);
        throw e;
    }
}