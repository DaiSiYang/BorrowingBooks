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

export const HotBookCarouselAPI = async () => {
    try {
        return await http.get('/hot-book-carousel/list');
    } catch (e) {
        console.error('获取热门图书轮播图数据失败:', e);
        throw e;
    }
}

export const HotBooksListAPI = async () => {
    try {
        return await http.get('/hot-books/list');
    } catch (e) {
        console.error('获取热门图书数据失败:', e);
        throw e;
    }
}

export const BookDetailAPI = async (id) => {
    try {
        return await http.get(`/book/detail/${id}`);
    } catch (e) {
        console.error('获取图书详情失败:', e);
        throw e;
    }
}