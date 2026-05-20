// ==========================================================================
// CẤU HÌNH API VÀ JWT TOKEN INTERCEPTOR
// ==========================================================================
const BASE_URL = 'http://localhost:8080/api';

/**
 * Hàm lấy Token từ LocalStorage
 */
function getToken() {
    return localStorage.getItem('accessToken');
}

/**
 * Hàm gọi API chung tự động đính kèm Token (nếu có)
 */
async function fetchWithAuth(endpoint, options = {}) {
    const token = getToken();
    
    // Cấu hình headers mặc định
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    // =========================================================
    // FIX LỖI TOKEN HẾT HẠN: Kiểm tra xem có phải API Auth không
    // =========================================================
    const isAuthAPI = endpoint.includes('/auth/login') || 
                      endpoint.includes('/auth/register') || 
                      endpoint.includes('/auth/forgot-password');

    // Chỉ gắn Token nếu có Token VÀ không phải là các API Xác thực
    if (token && !isAuthAPI) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        const response = await fetch(`${BASE_URL}${endpoint}`, {
            ...options,
            headers
        });

        // Xử lý đọc dạng Text trước để tránh lỗi "is not valid JSON"
        const text = await response.text();
        let data;

        try {
            data = text ? JSON.parse(text) : {};
        } catch (error) {
            data = { message: text }; 
        }

        // Xử lý lỗi từ Backend
        if (!response.ok) {
            let errorMsg = data.message || 'Có lỗi xảy ra!';
            if (typeof data.message === 'object') {
                errorMsg = Object.values(data.message).join('\n');
            }
            throw new Error(errorMsg);
        }

        return data;
    } catch (error) {
        console.error("API Error:", error);
        throw error;
    }
}