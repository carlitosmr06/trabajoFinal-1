import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import TestService from '../services/TestService';

const TestConfig = () => {
    const [themes, setThemes] = useState([]);
    const [form, setForm] = useState({
        themeId: '',
        difficulty: '',
        questionCount: 10
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);
    const [starting, setStarting] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        TestService.getThemes()
            .then(res => {
                setThemes(res.data);
                setLoading(false);
            })
            .catch(err => {
                if (err.response?.status === 401) {
                    navigate('/login');
                } else {
                    setError('Error al cargar los temas');
                    setLoading(false);
                }
            });
    }, [navigate]);

    const handleStart = (e) => {
        e.preventDefault();
        setError('');
        setStarting(true);

        const themeId = form.themeId ? Number(form.themeId) : null;
        const difficulty = form.difficulty || null;
        const questionCount = Number(form.questionCount);

        TestService.generateTest(themeId, difficulty, questionCount)
            .then(res => {
                // Backend returns List<Question> directly
                const questions = res.data;
                if (!questions || questions.length === 0) {
                    setError('No se encontraron preguntas con los parámetros seleccionados.');
                    return;
                }
                sessionStorage.setItem('testSession', JSON.stringify({
                    preguntas: questions,
                    respuestas: {},
                    indiceActual: 0
                }));
                navigate('/test/play');
            })
            .catch(err => {
                setError(err.response?.data?.message || 'Error al generar el test');
            })
            .finally(() => setStarting(false));
    };

    if (loading) {
        return (
            <div style={styles.container}>
                <div style={styles.card}>
                    <p style={{ color: '#bdc3c7', textAlign: 'center' }}>Cargando configuración...</p>
                </div>
            </div>
        );
    }

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <div style={styles.headerRow}>
                    <div>
                        <h1 style={styles.title}>Configurar Test</h1>
                        <p style={styles.subtitle}>Selecciona los parámetros del test</p>
                    </div>
                    <button onClick={() => navigate('/dashboard')} style={styles.backBtn}>
                        ← Volver
                    </button>
                </div>

                {error && (
                    <div style={styles.errorBox}>
                        {error}
                    </div>
                )}

                <form onSubmit={handleStart} style={{ marginTop: '24px' }}>
                    <div style={styles.field}>
                        <label style={styles.label}>Tema</label>
                        <select
                            value={form.themeId}
                            onChange={e => setForm({ ...form, themeId: e.target.value })}
                            style={styles.select}
                        >
                            <option value="">Todos los temas</option>
                            {themes.map(t => (
                                <option key={t.id} value={t.id}>{t.name}</option>
                            ))}
                        </select>
                    </div>

                    <div style={styles.field}>
                        <label style={styles.label}>Dificultad</label>
                        <select
                            value={form.difficulty}
                            onChange={e => setForm({ ...form, difficulty: e.target.value })}
                            style={styles.select}
                        >
                            <option value="">Cualquier dificultad</option>
                            <option value="EASY">Fácil</option>
                            <option value="MEDIUM">Medio</option>
                            <option value="HARD">Difícil</option>
                        </select>
                    </div>

                    <div style={styles.field}>
                        <label style={styles.label}>
                            Cantidad de preguntas: <strong style={{ color: '#1abc9c' }}>{form.questionCount}</strong>
                        </label>
                        <input
                            type="range"
                            min="5"
                            max="30"
                            value={form.questionCount}
                            onChange={e => setForm({ ...form, questionCount: e.target.value })}
                            style={styles.range}
                        />
                        <div style={styles.rangeLabels}>
                            <span>5</span>
                            <span>30</span>
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={starting}
                        style={{
                            ...styles.startBtn,
                            opacity: starting ? 0.7 : 1,
                            cursor: starting ? 'not-allowed' : 'pointer'
                        }}
                    >
                        {starting ? 'Generando preguntas...' : '🚀 Comenzar Test'}
                    </button>
                </form>
            </div>
        </div>
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
        maxWidth: '520px',
        backgroundColor: 'rgba(52, 73, 94, 0.95)',
        borderRadius: '12px',
        padding: '40px',
        boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)',
        border: '1px solid rgba(255, 255, 255, 0.1)'
    },
    headerRow: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'flex-start'
    },
    title: {
        color: '#ecf0f1',
        fontSize: '24px',
        fontWeight: '600',
        marginBottom: '4px'
    },
    subtitle: {
        color: '#bdc3c7',
        fontSize: '14px'
    },
    backBtn: {
        padding: '8px 16px',
        backgroundColor: 'rgba(149, 165, 166, 0.2)',
        border: '1px solid rgba(149, 165, 166, 0.4)',
        borderRadius: '6px',
        color: '#bdc3c7',
        fontSize: '13px',
        cursor: 'pointer',
        transition: 'all 0.3s ease'
    },
    errorBox: {
        backgroundColor: 'rgba(231, 76, 60, 0.2)',
        border: '1px solid #e74c3c',
        borderRadius: '6px',
        padding: '12px 16px',
        marginTop: '16px',
        color: '#e74c3c',
        fontSize: '14px'
    },
    field: {
        marginBottom: '20px'
    },
    label: {
        display: 'block',
        color: '#ecf0f1',
        fontSize: '14px',
        fontWeight: '500',
        marginBottom: '8px'
    },
    select: {
        width: '100%',
        padding: '12px 16px',
        backgroundColor: 'rgba(44, 62, 80, 0.8)',
        border: '2px solid rgba(149, 165, 166, 0.3)',
        borderRadius: '6px',
        color: '#ecf0f1',
        fontSize: '14px',
        outline: 'none',
        boxSizing: 'border-box',
        appearance: 'auto'
    },
    range: {
        width: '100%',
        accentColor: '#1abc9c'
    },
    rangeLabels: {
        display: 'flex',
        justifyContent: 'space-between',
        color: '#95a5a6',
        fontSize: '12px',
        marginTop: '4px'
    },
    radioGroup: {
        display: 'flex',
        gap: '24px'
    },
    radioLabel: {
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
        color: '#ecf0f1',
        fontSize: '14px',
        cursor: 'pointer'
    },
    radio: {
        accentColor: '#1abc9c'
    },
    startBtn: {
        width: '100%',
        padding: '14px',
        backgroundColor: '#1abc9c',
        border: 'none',
        borderRadius: '6px',
        color: 'white',
        fontSize: '16px',
        fontWeight: '600',
        transition: 'all 0.3s ease',
        marginTop: '8px',
        boxSizing: 'border-box'
    }
};

export default TestConfig;
