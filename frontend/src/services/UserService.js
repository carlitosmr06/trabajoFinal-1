import api from '../api/axiosConfig';

class UserService {
    // Admin: list all users (paginated)
    getAll(page = 0, size = 10) {
        return api.get(`/users?page=${page}&size=${size}`);
    }

    // Admin: get single user by id
    get(id) {
        return api.get(`/users/${id}`);
    }

    // Admin: create a new user
    create(data) {
        return api.post("/users", data);
    }

    // Admin: update user
    update(id, data) {
        return api.put(`/users/${id}`, data);
    }

    // Admin: delete user
    delete(id) {
        return api.delete(`/users/${id}`);
    }

    // Current user: get own profile
    getProfile() {
        return api.get('/users/profile');
    }

    // Current user: update own email
    updateProfile(email) {
        return api.put('/users/profile', { email });
    }

    // Current user: change password
    changePassword(oldPassword, newPassword) {
        return api.put('/users/password', { oldPassword, newPassword });
    }
}

export default new UserService();

