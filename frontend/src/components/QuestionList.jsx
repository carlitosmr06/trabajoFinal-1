import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import QuestionService from '../services/QuestionService';
import AuthService from '../services/AuthService';

const QuestionList = () => {
    const [questions, setQuestions] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        if (!AuthService.isLoggedIn()) { navigate('/login'); return; }
        loadQuestions(page);
    }, [page]);

    const loadQuestions = (p) => {
        setLoading(true);
        QuestionService.getAll(p, 10)
            .then(res => {
                setQuestions(res.data.content || []);
                setTotalPages(res.data.totalPages || 0);
            })
            .catch(err => {
                if (err.response?.status === 403) setError('No tienes permisos de administrador.');
                else setError('Error al cargar preguntas.');
            })
            .finally(() => setLoading(false));
    };

    const handleDelete = (id) => {
        if (!window.confirm(`¿Seguro que deseas eliminar la pregunta #${id}?`)) return;
        QuestionService.delete(id)
            .then(() => loadQuestions(page))
            .catch(() => setError('Error al eliminar la pregunta.'));
    };

    const getTypeLabel = (type) => {
        switch (type) {
            case 'TRUE_FALSE': return 'Verd. / Falso';
            case 'SINGLE_CHOICE': return 'Elección Única';
            case 'MULTIPLE_CHOICE': return 'Elección Múltiple';
            default: return type;
        }
    };

    const handleFileUpload = (e) => {
        const file = e.target.files[0];
        if (!file) return;

        setLoading(true);
        QuestionService.uploadQuestions(file)
            .then(res => {
                const msg = res.data?.message || 'Archivo subido correctamente';
                // Show a temporary success message via error box (using green color later, or simple alert)
                alert(msg);
                loadQuestions(page); // Reload to see new questions
            })
            .catch(err => {
                setError('Error al subir el archivo: ' + (err.response?.data?.message || err.message));
                setLoading(false);
            })
            .finally(() => {
                // reset file input
                e.target.value = null;
            });
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <div style={styles.header}>
                    <div>
                        <h1 style={styles.title}>📘 Gestión de Preguntas</h1>
                        <p style={styles.subtitle}>Administra la base de datos de preguntas</p>
                    </div>
                    <div style={styles.headerActions}>
                        <input
                            type="file"
                            accept=".csv, .json"
                            id="fileUpload"
                            style={{ display: 'none' }}
                            onChange={handleFileUpload}
                        />
                        <button
                            onClick={() => document.getElementById('fileUpload').click()}
                            style={{ ...styles.primaryBtn, backgroundColor: '#27ae60' }}
                        >
                            <span style={{ marginRight: '6px' }}>⬆️</span> Subir Archivo
                        </button>
                        <button onClick={() => navigate('/admin/questions/new')} style={styles.primaryBtn}>
                            + Nueva Pregunta
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
                                        {['ID', 'Texto', 'Tema', 'Dificultad', 'Tipo', 'Acciones'].map(h => (
                                            <th key={h} style={styles.th}>{h}</th>
                                        ))}
                                    </tr>
                                </thead>
                                <tbody>
                                    {questions.length === 0 ? (
                                        <tr><td colSpan={6} style={{ ...styles.td, textAlign: 'center', color: '#95a5a6' }}>No hay preguntas</td></tr>
                                    ) : questions.map(q => (
                                        <tr key={q.id} style={styles.tr}>
                                            <td style={styles.td}><span style={styles.idBadge}>#{q.id}</span></td>
                                            <td style={styles.td}>
                                                <div style={styles.truncateText} title={q.questionText}>
                                                    {q.questionText}
                                                </div>
                                            </td>
                                            <td style={styles.td}><span style={{ color: '#ecf0f1' }}>{q.theme?.name || '-'}</span></td>
                                            <td style={styles.td}>
                                                <span style={{
                                                    ...styles.statusBadge,
                                                    backgroundColor: q.difficulty === 'EASY' ? 'rgba(46,204,113,0.2)' : q.difficulty === 'MEDIUM' ? 'rgba(241,196,15,0.2)' : 'rgba(231,76,60,0.2)',
                                                    color: q.difficulty === 'EASY' ? '#2ecc71' : q.difficulty === 'MEDIUM' ? '#f1c40f' : '#e74c3c'
                                                }}>
                                                    {q.difficulty}
                                                </span>
                                            </td>
                                            <td style={styles.td}>
                                                <span style={{ color: '#95a5a6', fontSize: '13px' }}>
                                                    {getTypeLabel(q.questionType)}
                                                </span>
                                            </td>
                                            <td style={styles.td}>
                                                <div style={styles.actions}>
                                                    <button onClick={() => navigate(`/admin/questions/${q.id}`)} style={styles.editBtn}>Editar</button>
                                                    <button onClick={() => handleDelete(q.id)} style={styles.deleteBtn}>Eliminar</button>
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
    card: { maxWidth: '1000px', margin: '0 auto', backgroundColor: 'rgba(52,73,94,0.95)', borderRadius: '16px', padding: '32px', boxShadow: '0 8px 32px rgba(0,0,0,0.3)', border: '1px solid rgba(255,255,255,0.1)' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '28px', flexWrap: 'wrap', gap: '16px' },
    title: { color: '#ecf0f1', fontSize: '22px', fontWeight: '700', margin: 0 },
    subtitle: { color: '#95a5a6', fontSize: '14px', marginTop: '4px' },
    headerActions: { display: 'flex', gap: '10px', flexWrap: 'wrap' },
    primaryBtn: { padding: '9px 18px', backgroundColor: '#3498db', border: 'none', borderRadius: '7px', color: 'white', fontWeight: '600', fontSize: '14px', cursor: 'pointer' },
    secondaryBtn: { padding: '9px 18px', backgroundColor: 'rgba(149,165,166,0.2)', border: '1px solid rgba(149,165,166,0.4)', borderRadius: '7px', color: '#bdc3c7', fontSize: '14px', cursor: 'pointer' },
    errorBox: { backgroundColor: 'rgba(231,76,60,0.15)', border: '1px solid rgba(231,76,60,0.4)', borderRadius: '8px', padding: '12px 16px', marginBottom: '20px', color: '#e74c3c', fontSize: '14px' },
    tableWrapper: { overflowX: 'auto' },
    table: { width: '100%', borderCollapse: 'collapse' },
    th: { color: '#95a5a6', fontSize: '12px', fontWeight: '600', textTransform: 'uppercase', letterSpacing: '0.6px', padding: '10px 14px', borderBottom: '1px solid rgba(255,255,255,0.1)', textAlign: 'left' },
    tr: { borderBottom: '1px solid rgba(255,255,255,0.05)' },
    td: { padding: '14px', color: '#bdc3c7', fontSize: '14px' },
    idBadge: { color: '#7f8c8d', fontSize: '13px' },
    statusBadge: { padding: '3px 8px', borderRadius: '4px', fontSize: '11px', fontWeight: '700' },
    truncateText: { maxWidth: '300px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', color: '#ecf0f1' },
    actions: { display: 'flex', gap: '8px' },
    editBtn: { padding: '5px 12px', backgroundColor: 'rgba(241,196,15,0.2)', border: '1px solid rgba(241,196,15,0.4)', borderRadius: '5px', color: '#f1c40f', fontSize: '12px', cursor: 'pointer' },
    deleteBtn: { padding: '5px 12px', backgroundColor: 'rgba(231,76,60,0.2)', border: '1px solid rgba(231,76,60,0.4)', borderRadius: '5px', color: '#e74c3c', fontSize: '12px', cursor: 'pointer' },
    pagination: { display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '16px', marginTop: '24px' },
    pageBtn: { padding: '8px 16px', backgroundColor: 'rgba(149,165,166,0.2)', border: '1px solid rgba(149,165,166,0.3)', borderRadius: '6px', color: '#bdc3c7', fontSize: '14px', cursor: 'pointer' },
    pageInfo: { color: '#95a5a6', fontSize: '14px' }
};

export default QuestionList;
