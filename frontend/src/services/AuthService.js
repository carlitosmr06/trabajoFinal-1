import api from '../api/axiosConfig';

class AuthService {
    login(username, password) {
        return api.post("/auth/login", { username, password })
            .then(response => {
                if (response.data && response.data.token) {
                    localStorage.setItem("jwt_token", response.data.token);
                    localStorage.setItem("jwt_user", JSON.stringify({
                        username: response.data.username,
                        email: response.data.email
                    }));
                }
                return response;
            });
    }

    register(username, email, password) {
        return api.post("/auth/register", { username, email, password });
    }

    logout() {
        localStorage.removeItem("jwt_token");
        localStorage.removeItem("jwt_user");
        return Promise.resolve();
    }

    getCurrentUser() {
        return api.get("/users/profile");
    }

    getToken() {
        return localStorage.getItem("jwt_token");
    }

    isLoggedIn() {
        return !!localStorage.getItem("jwt_token");
    }
}

export default new AuthService();

