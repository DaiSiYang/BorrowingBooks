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

// 借阅图书API
export const BorrowBookAPI = async (id) => {
    try {
        return await http.get(`/book/${id}`);
    } catch (e) {
        console.error('借阅图书失败:', e);
        throw e;
    }
}

// 删除图书API
export const DeleteBookAPI = async (id) => {
    try {
        return await http.delete(`/book/${id}`);
    } catch (e) {
        console.error('删除图书失败:', e);
        throw e;
    }
}

// 根据ID获取图书API
export const GetBookByIdAPI = async (id) => {
    try {
        return await http.get(`/book/${id}`);
    } catch (e) {
        console.error('获取图书信息失败:', e);
        throw e;
    }
}

// 编辑图书API
export const EditBookAPI = async (id, data) => {
    try {
        // 确保data中包含id字段
        const bookData = { ...data, id };
        return await http.put('/book', bookData);
    } catch (e) {
        console.error('编辑图书失败:', e);
        throw e;
    }
}