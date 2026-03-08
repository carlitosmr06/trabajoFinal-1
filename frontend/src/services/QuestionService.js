import api from '../api/axiosConfig';

class QuestionService {
    getAll(page = 0, size = 10, themeId = '', difficulty = '', type = '') {
        const params = new URLSearchParams({ page, size });
        if (themeId) params.append('themeId', themeId);
        if (difficulty) params.append('difficulty', difficulty);
        if (type) params.append('questionType', type);

        return api.get(`/questions?${params.toString()}`);
    }

    get(id) {
        return api.get(`/questions/${id}`);
    }

    getThemes() {
        return api.get("/themes");
    }

    create(data) {
        return api.post("/questions", data);
    }

    update(id, data) {
        return api.put(`/questions/${id}`, data);
    }

    delete(id) {
        return api.delete(`/questions/${id}`);
    }

    uploadQuestions(file) {
        const formData = new FormData();
        formData.append('file', file);
        return api.post("/questions/upload", formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    }
}

export default new QuestionService();
