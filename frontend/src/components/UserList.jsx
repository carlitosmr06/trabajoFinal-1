import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import UserService from '../services/UserService';
import AuthService from '../services/AuthService';

const UserList = () => {
    const [users, setUsers] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        if (!AuthService.isLoggedIn()) { navigate('/login'); return; }
        loadUsers(page);
    }, [page]);

    const loadUsers = (p) => {
        setLoading(true);
        UserService.getAll(p, 10)
            .then(res => {
                setUsers(res.data.content || []);
                setTotalPages(res.data.totalPages || 0);
            })
            .catch(err => {
                if (err.response?.status === 403) setError('No tienes permisos de administrador.');
                else setError('Error al cargar usuarios.');
            })
            .finally(() => setLoading(false));
    };

    const handleDelete = (id, username) => {
        if (!window.confirm(`¿Eliminar al usuario "${username}"?`)) return;
        UserService.delete(id)
            .then(() => loadUsers(page))
            .catch(() => setError('Error al eliminar el usuario.'));
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <div style={styles.header}>
                    <div>
                        <h1 style={styles.title}>👥 Gestión de Usuarios</h1>
                        <p style={styles.subtitle}>Administra los usuarios del sistema</p>
                    </div>
                    <div style={styles.headerActions}>
                        <button onClick={() => navigate('/admin/users/new')} style={styles.primaryBtn}>
                            + Nuevo Usuario
                        </button>
                        <button onClick={() => navigate('/dashboard')} style={styles.secondaryBtn}>
                            ← Volver
                        </button>
                    </div>
                </div>

                {error && <div style={styles.errorBox}>{error}</div>}

                {loading ? (
                    <p style={{ color: '#bdc3c7', textAlign: 'center', padding: '40px' }}>Cargando...</p>
                ) : (
                    <>
                        <div style={styles.tableWrapper}>
                            <table style={styles.table}>
                                <thead>
                                    <tr>
                                        {['ID', 'Usuario', 'Email', 'Rol', 'Estado', 'Acciones'].map(h => (
                                            <th key={h} style={styles.th}>{h}</th>
                                        ))}
                                    </tr>
                                </thead>
                                <tbody>
                                    {users.length === 0 ? (
                                        <tr><td colSpan={6} style={{ ...styles.td, textAlign: 'center', color: '#95a5a6' }}>No hay usuarios</td></tr>
                                    ) : users.map(u => (
                                        <tr key={u.id} style={styles.tr}>
                                            <td style={styles.td}><span style={styles.idBadge}>#{u.id}</span></td>
                                            <td style={styles.td}><strong style={{ color: '#ecf0f1' }}>{u.username}</strong></td>
                                            <td style={styles.td}><span style={{ color: '#95a5a6' }}>{u.email}</span></td>
                                            <td style={styles.td}>
                                                <span style={{
                                                    ...styles.roleBadge,
                                                    backgroundColor: u.role?.includes('ADMIN') ? 'rgba(231,76,60,0.2)' : 'rgba(26,188,156,0.2)',
                                                    color: u.role?.includes('ADMIN') ? '#e74c3c' : '#1abc9c'
                                                }}>
                                                    {u.role?.replace('ROLE_', '') || 'USER'}
                                                </span>
                                            </td>
                                            <td style={styles.td}>
                                                <span style={{
                                                    ...styles.statusBadge,
                                                    backgroundColor: u.enabled ? 'rgba(46,204,113,0.2)' : 'rgba(149,165,166,0.2)',
                                                    color: u.enabled ? '#2ecc71' : '#95a5a6'
                                                }}>
                                                    {u.enabled ? 'Activo' : 'Inactivo'}
                                                </span>
                                            </td>
                                            <td style={styles.td}>
                                                <div style={styles.actions}>
                                                    <button onClick={() => navigate(`/admin/users/${u.id}`)} style={styles.editBtn}>Editar</button>
                                                    <button onClick={() => handleDelete(u.id, u.username)} style={styles.deleteBtn}>Eliminar</button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        {totalPages > 1 && (
                            <div style={styles.pagination}>
                                <button onClick={() => setPage(p => p - 1)} disabled={page === 0} style={styles.pageBtn}>← Anterior</button>
                                <span style={styles.pageInfo}>Página {page + 1} de {totalPages}</span>
                                <button onClick={() => setPage(p => p + 1)} disabled={page >= totalPages - 1} style={styles.pageBtn}>Siguiente →</button>
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    );
};

const styles = {
    container: { minHeight: '100vh', background: 'linear-gradient(135deg, #2c3e50 0%, #34495e 100%)', padding: '30px 20px' },
    card: { maxWidth: '900px', margin: '0 auto', backgroundColor: 'rgba(52,73,94,0.95)', borderRadius: '16px', padding: '32px', boxShadow: '0 8px 32px rgba(0,0,0,0.3)', border: '1px solid rgba(255,255,255,0.1)' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '28px', flexWrap: 'wrap', gap: '16px' },
    title: { color: '#ecf0f1', fontSize: '22px', fontWeight: '700', margin: 0 },
    subtitle: { color: '#95a5a6', fontSize: '14px', marginTop: '4px' },
    headerActions: { display: 'flex', gap: '10px', flexWrap: 'wrap' },
    primaryBtn: { padding: '9px 18px', backgroundColor: '#1abc9c', border: 'none', borderRadius: '7px', color: 'white', fontWeight: '600', fontSize: '14px', cursor: 'pointer' },
    secondaryBtn: { padding: '9px 18px', backgroundColor: 'rgba(149,165,166,0.2)', border: '1px solid rgba(149,165,166,0.4)', borderRadius: '7px', color: '#bdc3c7', fontSize: '14px', cursor: 'pointer' },
    errorBox: { backgroundColor: 'rgba(231,76,60,0.15)', border: '1px solid rgba(231,76,60,0.4)', borderRadius: '8px', padding: '12px 16px', marginBottom: '20px', color: '#e74c3c', fontSize: '14px' },
    tableWrapper: { overflowX: 'auto' },
    table: { width: '100%', borderCollapse: 'collapse' },
    th: { color: '#95a5a6', fontSize: '12px', fontWeight: '600', textTransform: 'uppercase', letterSpacing: '0.6px', padding: '10px 14px', borderBottom: '1px solid rgba(255,255,255,0.1)', textAlign: 'left' },
    tr: { borderBottom: '1px solid rgba(255,255,255,0.05)' },
    td: { padding: '14px', color: '#bdc3c7', fontSize: '14px' },
    idBadge: { color: '#7f8c8d', fontSize: '13px' },
    roleBadge: { padding: '3px 8px', borderRadius: '4px', fontSize: '11px', fontWeight: '700' },
    statusBadge: { padding: '3px 8px', borderRadius: '4px', fontSize: '12px', fontWeight: '600' },
    actions: { display: 'flex', gap: '8px' },
    editBtn: { padding: '5px 12px', backgroundColor: 'rgba(241,196,15,0.2)', border: '1px solid rgba(241,196,15,0.4)', borderRadius: '5px', color: '#f1c40f', fontSize: '12px', cursor: 'pointer' },
    deleteBtn: { padding: '5px 12px', backgroundColor: 'rgba(231,76,60,0.2)', border: '1px solid rgba(231,76,60,0.4)', borderRadius: '5px', color: '#e74c3c', fontSize: '12px', cursor: 'pointer' },
    pagination: { display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '16px', marginTop: '24px' },
    pageBtn: { padding: '8px 16px', backgroundColor: 'rgba(149,165,166,0.2)', border: '1px solid rgba(149,165,166,0.3)', borderRadius: '6px', color: '#bdc3c7', fontSize: '14px', cursor: 'pointer' },
    pageInfo: { color: '#95a5a6', fontSize: '14px' }
};

export default UserList;
