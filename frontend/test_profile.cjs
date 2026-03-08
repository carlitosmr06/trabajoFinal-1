const axios = require('axios');

async function test() {
    try {
        // Register a test user
        const rand = Math.floor(Math.random() * 10000);
        const username = `testuser${rand}`;
        await axios.post('http://localhost:8080/api/auth/register', {
            username: username,
            email: `${username}@test.com`,
            password: 'password123'
        });

        // Login
        const loginRes = await axios.post('http://localhost:8080/api/auth/login', {
            username: username,
            password: 'password123'
        });

        const token = loginRes.data.token;
        console.log('Token received');

        // Get profile
        const profileRes = await axios.get('http://localhost:8080/api/users/profile', {
            headers: { Authorization: `Bearer ${token}` }
        });

        console.log('Profile Data:', JSON.stringify(profileRes.data, null, 2));

        const user = profileRes.data;
        const isAdmin = Array.isArray(user?.roles)
            ? user.roles.some(r => r === 'ROLE_ADMIN' || r === 'ADMIN')
            : (user?.role === 'ROLE_ADMIN' || user?.role === 'ADMIN');
        console.log('isAdmin evaluates to:', isAdmin);

    } catch (err) {
        console.error('Error:', err.response ? err.response.data : err.message);
    }
}

test();
