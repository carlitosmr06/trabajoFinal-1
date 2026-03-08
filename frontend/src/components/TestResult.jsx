import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import TestService from '../services/TestService';

const TestResult = () => {
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const submitted = useRef(false);

    useEffect(() => {
        if (submitted.current) return;
        submitted.current = true;

        const stored = sessionStorage.getItem('testSession');
        if (!stored) {
            navigate('/test/config');
            return;
        }

        const session = JSON.parse(stored);
        const { preguntas, respuestas } = session;

        // Build SubmitTestRequest: { questionIds: [...], answers: { "id": answer } }
        const questionIds = preguntas.map(p => p.id);
        const answers = {};
        preguntas.forEach(p => {
            const r = respuestas[p.id];
            if (r === undefined || r === null) return;
            if (p.questionType === 'TRUE_FALSE') {
                answers[String(p.id)] = String(r); // "true" or "false"
            } else if (p.questionType === 'SINGLE_CHOICE') {
                answers[String(p.id)] = String(r); // index as string
            } else if (p.questionType === 'MULTIPLE_CHOICE') {
                answers[String(p.id)] = Array.isArray(r) ? r : [r]; // list of indices
            }
        });

        TestService.submitTest({ questionIds, answers })
            .then(res => {
                setResult(res.data);
                sessionStorage.removeItem('testSession');
            })
            .catch(err => {
                setError(err.response?.data?.message || 'Error al enviar el test');
            })
            .finally(() => setLoading(false));
    }, [navigate]);

    if (loading) {
        return (
            <div style={styles.container}>
                <div style={styles.card}>
                    <p style={{ color: '#bdc3c7', textAlign: 'center' }}>Corrigiendo test...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div style={styles.container}>
                <div style={styles.card}>
                    <div style={styles.errorBox}>{error}</div>
                    <button onClick={() => navigate('/dashboard')} style={styles.primaryBtn}>
                        ← Volver
                    </button>
                </div>
            </div>
        );
    }

    const getScoreColor = (pct) => {
        if (pct >= 70) return '#2ecc71';
        if (pct >= 50) return '#f39c12';
        return '#e74c3c';
    };

    // TestResult entity has: score (0-100%), correctAnswers, totalQuestions, testDate
    const pct = Math.round(result.score || 0);
    const notaSobre10 = (pct / 10).toFixed(1);

    return (
        <div style={styles.container}>
            <div style={{ ...styles.card, maxWidth: '700px' }}>
                <h1 style={styles.title}>Resultado del Test</h1>

                {/* Score card */}
                <div style={styles.scoreCard}>
                    <div style={styles.scoreMain}>
                        <span style={{
                            ...styles.scoreNumber,
                            color: getScoreColor(pct)
                        }}>
                            {notaSobre10}
                        </span>
                        <span style={styles.scoreDenom}>/10</span>
                    </div>
                    <div style={styles.scoreDetails}>
                        <div style={styles.scoreStat}>
                            <span style={styles.statLabel}>Aciertos</span>
                            <span style={{ ...styles.statValue, color: '#2ecc71' }}>{result.correctAnswers}</span>
                        </div>
                        <div style={styles.scoreStat}>
                            <span style={styles.statLabel}>Fallos</span>
                            <span style={{ ...styles.statValue, color: '#e74c3c' }}>{result.totalQuestions - result.correctAnswers}</span>
                        </div>
                        <div style={styles.scoreStat}>
                            <span style={styles.statLabel}>Total</span>
                            <span style={styles.statValue}>{result.totalQuestions}</span>
                        </div>
                        <div style={styles.scoreStat}>
                            <span style={styles.statLabel}>Porcentaje</span>
                            <span style={styles.statValue}>{pct}%</span>
                        </div>
                    </div>
                </div>

                {/* Actions */}
                <div style={styles.actions}>
                    <button onClick={() => navigate('/test/config')} style={styles.primaryBtn}>
                        🔄 Nuevo Test
                    </button>
                    <button onClick={() => navigate('/test/history')} style={styles.secondaryBtn}>
                        📊 Ver Historial
                    </button>
                    <button onClick={() => navigate('/dashboard')} style={styles.secondaryBtn}>
                        ← Dashboard
                    </button>
                </div>
            </div>
        </div>
    );
};

const styles = {
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
        maxWidth: '600px',
        backgroundColor: 'rgba(52, 73, 94, 0.95)',
        borderRadius: '12px',
        padding: '32px',
        boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)',
        border: '1px solid rgba(255, 255, 255, 0.1)'
    },
    title: {
        color: '#ecf0f1',
        fontSize: '24px',
        fontWeight: '600',
        marginBottom: '24px',
        textAlign: 'center'
    },
    errorBox: {
        backgroundColor: 'rgba(231, 76, 60, 0.2)',
        border: '1px solid #e74c3c',
        borderRadius: '6px',
        padding: '12px 16px',
        marginBottom: '16px',
        color: '#e74c3c',
        fontSize: '14px'
    },
    scoreCard: {
        backgroundColor: 'rgba(44, 62, 80, 0.6)',
        borderRadius: '10px',
        padding: '24px',
        marginBottom: '24px',
        textAlign: 'center'
    },
    scoreMain: {
        marginBottom: '20px'
    },
    scoreNumber: {
        fontSize: '56px',
        fontWeight: '700'
    },
    scoreDenom: {
        fontSize: '24px',
        color: '#95a5a6',
        fontWeight: '400'
    },
    scoreDetails: {
        display: 'flex',
        justifyContent: 'space-around',
        flexWrap: 'wrap',
        gap: '12px'
    },
    scoreStat: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: '4px'
    },
    statLabel: {
        color: '#95a5a6',
        fontSize: '12px',
        textTransform: 'uppercase',
        letterSpacing: '0.5px'
    },
    statValue: {
        color: '#ecf0f1',
        fontSize: '20px',
        fontWeight: '600'
    },
    sectionTitle: {
        color: '#ecf0f1',
        fontSize: '16px',
        fontWeight: '600',
        marginBottom: '16px'
    },
    itemsList: {
        display: 'flex',
        flexDirection: 'column',
        gap: '12px',
        marginBottom: '24px',
        maxHeight: '400px',
        overflowY: 'auto'
    },
    resultItem: {
        backgroundColor: 'rgba(44, 62, 80, 0.5)',
        borderRadius: '8px',
        padding: '16px',
        borderLeft: '4px solid'
    },
    itemHeader: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '8px'
    },
    itemNumber: {
        color: '#95a5a6',
        fontSize: '13px',
        fontWeight: '600'
    },
    itemBadge: {
        padding: '3px 8px',
        borderRadius: '4px',
        fontSize: '12px',
        fontWeight: '600'
    },
    itemEnunciado: {
        color: '#ecf0f1',
        fontSize: '14px',
        lineHeight: '1.4',
        marginBottom: '10px'
    },
    itemAnswers: {
        display: 'flex',
        flexDirection: 'column',
        gap: '4px'
    },
    answerRow: {
        display: 'flex',
        gap: '8px',
        fontSize: '13px'
    },
    answerLabel: {
        color: '#95a5a6',
        minWidth: '120px'
    },
    answerValue: {
        fontWeight: '500'
    },
    actions: {
        display: 'flex',
        gap: '12px',
        flexWrap: 'wrap'
    },
    primaryBtn: {
        flex: 1,
        padding: '12px',
        backgroundColor: '#1abc9c',
        border: 'none',
        borderRadius: '6px',
        color: 'white',
        fontSize: '14px',
        fontWeight: '600',
        cursor: 'pointer',
        transition: 'all 0.3s ease',
        minWidth: '120px'
    },
    secondaryBtn: {
        flex: 1,
        padding: '12px',
        backgroundColor: 'rgba(149, 165, 166, 0.2)',
        border: '1px solid rgba(149, 165, 166, 0.4)',
        borderRadius: '6px',
        color: '#bdc3c7',
        fontSize: '14px',
        cursor: 'pointer',
        transition: 'all 0.3s ease',
        minWidth: '120px'
    }
};

export default TestResult;
