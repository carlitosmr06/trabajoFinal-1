import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import TestService from '../services/TestService';
import AuthService from '../services/AuthService';

const TestHistory = () => {
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const navigate = useNavigate();

    const loadHistory = (p) => {
        setLoading(true);
        setError('');
        TestService.getHistory(p)
            .then(res => {
                setHistory(res.data.content || []);
                setTotalPages(res.data.totalPages || 0);
                setPage(res.data.number ?? p);
            })
            .catch(err => {
                if (err.response?.status === 401) {
                    AuthService.logout();
                    navigate('/login');
                } else {
                    const msg = err.response?.data?.message || err.response?.data || err.message || 'Error desconocido';
                    setError('Error al cargar el historial: ' + msg);
                }
            })
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        if (!AuthService.isLoggedIn()) { navigate('/login'); return; }
        loadHistory(0);
    }, []);

    const formatDate = (dateStr) => {
        if (!dateStr) return '—';
        try {
            const d = new Date(dateStr);
            return d.toLocaleDateString('es-ES', {
                day: '2-digit', month: '2-digit', year: 'numeric',
                hour: '2-digit', minute: '2-digit'
            });
        } catch { return dateStr; }
    };

    const getScoreColor = (score) => {
        if (score >= 70) return '#2ecc71';
        if (score >= 50) return '#f39c12';
        return '#e74c3c';
    };

    return (
        <div style={s.container}>
            <div style={s.card}>
                {/* Header */}
                <div style={s.header}>
                    <div>
                        <h1 style={s.title}>📊 Historial de Tests</h1>
                        <p style={s.subtitle}>Resultados de todos tus tests</p>
                    </div>
                    <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                        <button onClick={() => navigate('/test/config')} style={s.primaryBtn}>
                            + Nuevo Test
                        </button>
                        <button onClick={() => navigate('/dashboard')} style={s.backBtn}>
                            ← Volver
                        </button>
                    </div>
                </div>

                {/* Error */}
                {error && (
                    <div style={s.errorBox}>
                        <p style={{ margin: '0 0 10px 0' }}>{error}</p>
                        <button onClick={() => loadHistory(page)} style={s.retryBtn}>
                            🔄 Reintentar
                        </button>
                    </div>
                )}

                {/* Loading */}
                {loading && (
                    <p style={{ color: '#bdc3c7', textAlign: 'center', padding: '40px 0' }}>
                        Cargando historial...
                    </p>
                )}

                {/* Empty state */}
                {!loading && !error && history.length === 0 && (
                    <div style={s.emptyState}>
                        <div style={{ fontSize: '48px', marginBottom: '16px' }}>📋</div>
                        <p style={{ color: '#95a5a6', fontSize: '16px', marginBottom: '20px' }}>
                            No has realizado ningún test todavía.
                        </p>
                        <button onClick={() => navigate('/test/config')} style={s.primaryBtn}>
                            Realizar mi primer test
                        </button>
                    </div>
                )}

                {/* Results table */}
                {!loading && !error && history.length > 0 && (
                    <>
                        <div style={s.tableWrapper}>
                            <table style={s.table}>
                                <thead>
                                    <tr>
                                        {['Fecha', 'Tema', 'Resultado', 'Nota'].map(h => (
                                            <th key={h} style={s.th}>{h}</th>
                                        ))}
                                    </tr>
                                </thead>
                                <tbody>
                                    {history.map(r => (
                                        <tr key={r.id} style={s.tr}>
                                            <td style={s.td}>
                                                <span style={{ color: '#95a5a6', fontSize: '13px' }}>
                                                    {formatDate(r.completedAt)}
                                                </span>
                                            </td>
                                            <td style={s.td}>
                                                <span style={s.badge}>
                                                    {r.theme?.name || 'Mixto'}
                                                </span>
                                            </td>
                                            <td style={s.td}>
                                                <span style={{ color: '#2ecc71', fontWeight: '600' }}>
                                                    {r.correctAnswers}
                                                </span>
                                                <span style={{ color: '#95a5a6' }}>
                                                    {' / '}{r.totalQuestions}
                                                </span>
                                            </td>
                                            <td style={s.td}>
                                                <span style={{
                                                    fontWeight: '700',
                                                    fontSize: '16px',
                                                    color: getScoreColor(r.score)
                                                }}>
                                                    {Math.round(r.score)}%
                                                </span>
                                                <span style={{ color: '#7f8c8d', fontSize: '12px', marginLeft: '4px' }}>
                                                    ({(r.score / 10).toFixed(1)}/10)
                                                </span>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        {/* Pagination */}
                        {totalPages > 1 && (
                            <div style={s.pagination}>
                                <button
                                    onClick={() => loadHistory(page - 1)}
                                    disabled={page === 0}
                                    style={{ ...s.pageBtn, opacity: page === 0 ? 0.4 : 1, cursor: page === 0 ? 'not-allowed' : 'pointer' }}
                                >
                                    ← Anterior
                                </button>
                                <span style={{ color: '#95a5a6', fontSize: '14px' }}>
                                    Página {page + 1} de {totalPages}
                                </span>
                                <button
                                    onClick={() => loadHistory(page + 1)}
                                    disabled={page >= totalPages - 1}
                                    style={{ ...s.pageBtn, opacity: page >= totalPages - 1 ? 0.4 : 1, cursor: page >= totalPages - 1 ? 'not-allowed' : 'pointer' }}
                                >
                                    Siguiente →
                                </button>
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    );
};

const s = {
    container: {
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'flex-start',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #2c3e50 0%, #34495e 100%)',
        padding: '40px 20px'
    },
    card: {
        width: '100%',
        maxWidth: '720px',
        backgroundColor: 'rgba(52, 73, 94, 0.95)',
        borderRadius: '14px',
        padding: '32px',
        boxShadow: '0 8px 32px rgba(0,0,0,0.3)',
        border: '1px solid rgba(255,255,255,0.1)'
    },
    header: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'flex-start',
        marginBottom: '28px',
        flexWrap: 'wrap',
        gap: '16px'
    },
    title: { color: '#ecf0f1', fontSize: '22px', fontWeight: '700', margin: 0 },
    subtitle: { color: '#95a5a6', fontSize: '14px', marginTop: '4px' },
    primaryBtn: {
        padding: '9px 18px',
        backgroundColor: '#1abc9c',
        border: 'none',
        borderRadius: '7px',
        color: 'white',
        fontWeight: '600',
        fontSize: '14px',
        cursor: 'pointer'
    },
    backBtn: {
        padding: '9px 18px',
        backgroundColor: 'rgba(149,165,166,0.2)',
        border: '1px solid rgba(149,165,166,0.3)',
        borderRadius: '7px',
        color: '#bdc3c7',
        fontSize: '14px',
        cursor: 'pointer'
    },
    errorBox: {
        backgroundColor: 'rgba(231,76,60,0.15)',
        border: '1px solid rgba(231,76,60,0.4)',
        borderRadius: '8px',
        padding: '16px',
        marginBottom: '20px',
        color: '#e74c3c',
        fontSize: '14px'
    },
    retryBtn: {
        padding: '7px 14px',
        backgroundColor: 'rgba(231,76,60,0.2)',
        border: '1px solid rgba(231,76,60,0.5)',
        borderRadius: '6px',
        color: '#e74c3c',
        fontSize: '13px',
        cursor: 'pointer'
    },
    emptyState: {
        textAlign: 'center',
        padding: '40px 0'
    },
    tableWrapper: { overflowX: 'auto' },
    table: { width: '100%', borderCollapse: 'collapse' },
    th: {
        color: '#95a5a6',
        fontSize: '11px',
        fontWeight: '600',
        textTransform: 'uppercase',
        letterSpacing: '0.6px',
        padding: '10px 14px',
        borderBottom: '1px solid rgba(255,255,255,0.1)',
        textAlign: 'left'
    },
    tr: { borderBottom: '1px solid rgba(255,255,255,0.05)' },
    td: { padding: '14px', color: '#bdc3c7', fontSize: '14px' },
    badge: {
        backgroundColor: 'rgba(26,188,156,0.15)',
        color: '#1abc9c',
        padding: '3px 8px',
        borderRadius: '4px',
        fontSize: '12px',
        fontWeight: '600'
    },
    pagination: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        gap: '16px',
        marginTop: '24px'
    },
    pageBtn: {
        padding: '8px 16px',
        backgroundColor: 'rgba(149,165,166,0.2)',
        border: '1px solid rgba(149,165,166,0.3)',
        borderRadius: '6px',
        color: '#bdc3c7',
        fontSize: '14px'
    }
};

export default TestHistory;
