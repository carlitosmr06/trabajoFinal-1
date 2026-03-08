import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';
import api from '../api/axiosConfig';

const Dashboard = () => {
    const [user, setUser] = useState(null);
    const [stats, setStats] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (!AuthService.isLoggedIn()) {
            navigate('/login');
            return;
        }
        api.get('/users/profile')
            .then(res => setUser(res.data))
            .catch(() => {
                AuthService.logout();
                navigate('/login');
            });
        api.get('/tests/statistics')
            .then(res => setStats(res.data))
            .catch(() => { }); // stats are optional
    }, [navigate]);

    const handleLogout = () => {
        AuthService.logout();
        navigate('/login');
    };

    // Estricto: Se verifica explícitamente el rol de administrador
    const isAdmin = user && user.roles && Array.isArray(user.roles) &&
        (user.roles.includes('ROLE_ADMIN') || user.roles.includes('ADMIN'));

    if (!user) {
        return (
            <div style={styles.container}>
                <div style={styles.card}>
                    <p style={{ color: '#bdc3c7', textAlign: 'center' }}>Cargando...</p>
                </div>
            </div>
        );
    }

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                {/* Header */}
                <div style={styles.header}>
                    <div>
                        <h1 style={styles.appTitle}>PreguntasApp</h1>
                        <p style={styles.welcome}>
                            Bienvenido, <span style={styles.username}>{user.username}</span>
                            {isAdmin && <span style={styles.adminBadge}>ADMIN</span>}
                        </p>
                        <p style={styles.email}>{user.email}</p>
                    </div>
                    <button onClick={handleLogout} style={styles.logoutBtn}>
                        Cerrar Sesión
                    </button>
                </div>

                <div style={styles.divider} />

                {/* Stats bar */}
                {stats && (
                    <div style={styles.statsRow}>
                        <div style={styles.statBox}>
                            <span style={styles.statNum}>{stats.totalTests ?? 0}</span>
                            <span style={styles.statLabel}>Tests realizados</span>
                        </div>
                        <div style={styles.statBox}>
                            <span style={styles.statNum}>{stats.averageScore?.toFixed(1) ?? '—'}%</span>
                            <span style={styles.statLabel}>Puntuación media</span>
                        </div>
                        <div style={styles.statBox}>
                            <span style={styles.statNum}>{stats.bestScore?.toFixed(1) ?? '—'}%</span>
                            <span style={styles.statLabel}>Mejor nota</span>
                        </div>
                    </div>
                )}

                {/* Main menu */}
                <div style={styles.menuGrid}>
                    <div style={styles.menuCard} onClick={() => navigate('/test/config')}>
                        <div style={styles.menuIcon}>📝</div>
                        <h3 style={styles.menuTitle}>Realizar Test</h3>
                        <p style={styles.menuDesc}>Configura y responde un test de preguntas</p>
                    </div>
                    <div style={styles.menuCard} onClick={() => navigate('/test/history')}>
                        <div style={styles.menuIcon}>📊</div>
                        <h3 style={styles.menuTitle}>Historial</h3>
                        <p style={styles.menuDesc}>Revisa tus resultados anteriores</p>
                    </div>
                    {isAdmin && (
                        <>
                            <div style={{ ...styles.menuCard, ...styles.adminCard }} onClick={() => navigate('/admin/users')}>
                                <div style={styles.menuIcon}>👥</div>
                                <h3 style={styles.menuTitle}>Gestión de Usuarios (Admin)</h3>
                                <p style={styles.menuDesc}>Administra los usuarios del sistema</p>
                            </div>
                            <div style={{ ...styles.menuCard, ...styles.adminCard }} onClick={() => navigate('/admin/questions')}>
                                <div style={styles.menuIcon}>📘</div>
                                <h3 style={styles.menuTitle}>Gestión de Preguntas (Admin)</h3>
                                <p style={styles.menuDesc}>Administra la base de datos de preguntas</p>
                            </div>
                        </>
                    )}
                </div>
            </div>
        </div >
    );
};

const styles = {
    container: {
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #2c3e50 0%, #34495e 100%)',
        padding: '20px'
    },
    card: {
        width: '100%',
        maxWidth: '700px',
        backgroundColor: 'rgba(52, 73, 94, 0.95)',
        borderRadius: '16px',
        padding: '36px',
        boxShadow: '0 8px 32px rgba(0,0,0,0.3)',
        border: '1px solid rgba(255,255,255,0.1)'
    },
    header: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'flex-start'
    },
    appTitle: {
        color: '#ecf0f1',
        fontSize: '26px',
        fontWeight: '700',
        margin: 0
    },
    welcome: {
        color: '#bdc3c7',
        fontSize: '15px',
        marginTop: '6px',
        display: 'flex',
        alignItems: 'center',
        gap: '8px'
    },
    username: { color: '#1abc9c', fontWeight: '600' },
    email: { color: '#7f8c8d', fontSize: '13px', marginTop: '2px' },
    adminBadge: {
        backgroundColor: 'rgba(231,76,60,0.2)',
        color: '#e74c3c',
        fontSize: '10px',
        fontWeight: '700',
        padding: '2px 7px',
        borderRadius: '4px',
        border: '1px solid rgba(231,76,60,0.4)',
        letterSpacing: '0.5px'
    },
    logoutBtn: {
        padding: '8px 18px',
        backgroundColor: 'rgba(231,76,60,0.15)',
        border: '1px solid rgba(231,76,60,0.4)',
        borderRadius: '6px',
        color: '#e74c3c',
        fontSize: '14px',
        cursor: 'pointer',
        transition: 'all 0.3s ease'
    },
    divider: {
        height: '1px',
        backgroundColor: 'rgba(255,255,255,0.1)',
        margin: '24px 0'
    },
    statsRow: {
        display: 'flex',
        gap: '12px',
        marginBottom: '24px'
    },
    statBox: {
        flex: 1,
        backgroundColor: 'rgba(44,62,80,0.6)',
        borderRadius: '10px',
        padding: '16px',
        textAlign: 'center',
        display: 'flex',
        flexDirection: 'column',
        gap: '4px'
    },
    statNum: { color: '#1abc9c', fontSize: '22px', fontWeight: '700' },
    statLabel: { color: '#95a5a6', fontSize: '11px', textTransform: 'uppercase', letterSpacing: '0.5px' },
    menuGrid: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
        gap: '16px'
    },
    menuCard: {
        backgroundColor: 'rgba(44,62,80,0.6)',
        borderRadius: '12px',
        padding: '24px 20px',
        cursor: 'pointer',
        border: '1px solid rgba(255,255,255,0.08)',
        transition: 'all 0.3s ease',
        textAlign: 'center'
    },
    adminCard: {
        borderColor: 'rgba(231,76,60,0.3)'
    },
    menuIcon: { fontSize: '32px', marginBottom: '12px' },
    menuTitle: { color: '#ecf0f1', fontSize: '16px', fontWeight: '600', margin: '0 0 8px 0' },
    menuDesc: { color: '#95a5a6', fontSize: '13px', margin: 0, lineHeight: '1.4' }
};

export default Dashboard;
