import api from '../api/axiosConfig';

class TestService {
    // Get all available themes for test configuration
    getThemes() {
        return api.get("/themes");
    }

    // Generate a test with the given parameters
    generateTest(themeId, difficulty, questionCount) {
        return api.post("/tests/generate", { themeId, difficulty, questionCount });
    }

    // Submit test answers
    submitTest(payload) {
        return api.post("/tests/submit", payload);
    }

    // Get user test history (results)
    getHistory(page = 0, size = 10) {
        return api.get(`/tests/results?page=${page}&size=${size}`);
    }

    // Get user statistics
    getStatistics() {
        return api.get("/tests/statistics");
    }
}

export default new TestService();

