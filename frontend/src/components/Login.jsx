import React, { useState } from 'react';
import AuthService from '../services/AuthService';
import { Link, useSearchParams, useNavigate } from 'react-router-dom';

const Login = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const urlError = searchParams.get('error');
    const [credentials, setCredentials] = useState({
        username: '',
        password: ''
    });
    const [error, setError] = useState(urlError || '');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCredentials({ ...credentials, [name]: value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);

        AuthService.login(credentials.username, credentials.password)
            .then(() => {
                navigate('/dashboard');
            })
            .catch((err) => {
                const msg = err.response?.data || 'Credenciales inválidas. Inténtalo de nuevo.';
                setError(typeof msg === 'string' ? msg : 'Error al iniciar sesión.');
            })
            .finally(() => setLoading(false));
    };

    return (
        <div style={{
            minHeight: '100vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            background: 'linear-gradient(135deg, #2c3e50 0%, #34495e 100%)',
            padding: '20px'
        }}>
            <div style={{
                width: '100%',
                maxWidth: '450px',
                backgroundColor: 'rgba(52, 73, 94, 0.95)',
                borderRadius: '12px',
                padding: '40px',
                boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)',
                border: '1px solid rgba(255, 255, 255, 0.1)'
            }}>
                <h1 style={{
                    color: '#ecf0f1',
                    fontSize: '32px',
                    fontWeight: '600',
                    textAlign: 'center',
                    marginBottom: '8px',
                    letterSpacing: '0.5px'
                }}>
                    PreguntasApp
                </h1>

                <p style={{
                    color: '#bdc3c7',
                    fontSize: '14px',
                    textAlign: 'center',
                    marginBottom: '30px'
                }}>
                    Inicia sesión para continuar
                </p>

                {success && (
                    <div style={{
                        backgroundColor: 'rgba(26, 188, 156, 0.2)',
                        border: '1px solid #1abc9c',
                        borderRadius: '6px',
                        padding: '12px 16px',
                        marginBottom: '20px',
                        color: '#1abc9c',
                        fontSize: '14px'
                    }}>
                        ✓ {success}
                    </div>
                )}

                {error && (
                    <div style={{
                        backgroundColor: 'rgba(231, 76, 60, 0.2)',
                        border: '1px solid #e74c3c',
                        borderRadius: '6px',
                        padding: '12px 16px',
                        marginBottom: '20px',
                        color: '#e74c3c',
                        fontSize: '14px'
                    }}>
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div style={{ marginBottom: '20px' }}>
                        <label style={{
                            display: 'block',
                            color: '#ecf0f1',
                            fontSize: '14px',
                            fontWeight: '500',
                            marginBottom: '8px'
                        }}>
                            Usuario
                        </label>
                        <input
                            type="text"
                            name="username"
                            value={credentials.username}
                            onChange={handleInputChange}
                            required
                            disabled={loading}
                            style={{
                                width: '100%',
                                padding: '12px 16px',
                                backgroundColor: 'rgba(44, 62, 80, 0.8)',
                                border: '2px solid #1abc9c',
                                borderRadius: '6px',
                                color: '#ecf0f1',
                                fontSize: '14px',
                                outline: 'none',
                                transition: 'all 0.3s ease',
                                boxSizing: 'border-box'
                            }}
                        />
                    </div>

                    <div style={{ marginBottom: '24px' }}>
                        <label style={{
                            display: 'block',
                            color: '#ecf0f1',
                            fontSize: '14px',
                            fontWeight: '500',
                            marginBottom: '8px'
                        }}>
                            Contraseña
                        </label>
                        <input
                            type="password"
                            name="password"
                            value={credentials.password}
                            onChange={handleInputChange}
                            required
                            disabled={loading}
                            style={{
                                width: '100%',
                                padding: '12px 16px',
                                backgroundColor: 'rgba(44, 62, 80, 0.8)',
                                border: '2px solid rgba(149, 165, 166, 0.4)',
                                borderRadius: '6px',
                                color: '#ecf0f1',
                                fontSize: '14px',
                                outline: 'none',
                                transition: 'all 0.3s ease',
                                boxSizing: 'border-box'
                            }}
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        style={{
                            width: '100%',
                            padding: '14px',
                            backgroundColor: '#1abc9c',
                            border: 'none',
                            borderRadius: '6px',
                            color: 'white',
                            fontSize: '16px',
                            fontWeight: '600',
                            cursor: loading ? 'not-allowed' : 'pointer',
                            transition: 'all 0.3s ease',
                            opacity: loading ? 0.7 : 1,
                            boxSizing: 'border-box'
                        }}
                        onMouseEnter={(e) => {
                            if (!loading) e.target.style.backgroundColor = '#16a085';
                        }}
                        onMouseLeave={(e) => {
                            if (!loading) e.target.style.backgroundColor = '#1abc9c';
                        }}
                    >
                        {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
                    </button>

                    <div style={{ textAlign: 'center', marginTop: '24px' }}>
                        <p style={{ color: '#95a5a6', fontSize: '14px' }}>
                            ¿No tienes cuenta?{' '}
                            <Link to="/register" style={{
                                color: '#1abc9c',
                                textDecoration: 'none',
                                fontWeight: '500'
                            }}>
                                Regístrate aquí
                            </Link>
                        </p>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login;
