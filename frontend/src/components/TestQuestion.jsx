import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const TestQuestion = () => {
    const [session, setSession] = useState(null);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [respuestas, setRespuestas] = useState({});
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const stored = sessionStorage.getItem('testSession');
        if (!stored) {
            navigate('/test/config');
            return;
        }
        try {
            const parsed = JSON.parse(stored);
            if (!parsed.preguntas || parsed.preguntas.length === 0) {
                setError('No hay preguntas en este test. Vuelve a configurar uno nuevo.');
                return;
            }
            setSession(parsed);
            setCurrentIndex(parsed.indiceActual || 0);
            setRespuestas(parsed.respuestas || {});
        } catch (e) {
            setError('Error al cargar el test. Inicia uno nuevo.');
        }
    }, [navigate]);

    if (error) {
        return (
            <div style={styles.container}>
                <div style={styles.card}>
                    <div style={{ textAlign: 'center', padding: '20px' }}>
                        <div style={{ fontSize: '48px', marginBottom: '16px' }}>⚠️</div>
                        <p style={{ color: '#e74c3c', marginBottom: '24px', fontSize: '15px' }}>{error}</p>
                        <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
                            <button onClick={() => navigate('/test/config')} style={styles.nextBtn}>Nuevo Test</button>
                            <button onClick={() => navigate('/dashboard')} style={styles.navBtn}>Dashboard</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    if (!session) return null;

    const preguntas = session.preguntas;
    const pregunta = preguntas[currentIndex];
    const total = preguntas.length;
    const progreso = Math.round(((currentIndex + 1) * 100) / total);

    // Answers stored by question id. For TRUE_FALSE: boolean, SINGLE_CHOICE: index, MULTIPLE_CHOICE: [index1, index2...]
    const respActual = respuestas[pregunta.id];

    const updateRespuesta = (value) => {
        const newResp = { ...respuestas, [pregunta.id]: value };
        setRespuestas(newResp);
        const updated = { ...session, respuestas: newResp, indiceActual: currentIndex };
        sessionStorage.setItem('testSession', JSON.stringify(updated));
    };

    const goNext = () => {
        if (currentIndex < total - 1) {
            setCurrentIndex(currentIndex + 1);
            const updated = { ...session, respuestas, indiceActual: currentIndex + 1 };
            sessionStorage.setItem('testSession', JSON.stringify(updated));
        }
    };

    const goPrev = () => {
        if (currentIndex > 0) {
            setCurrentIndex(currentIndex - 1);
            const updated = { ...session, respuestas, indiceActual: currentIndex - 1 };
            sessionStorage.setItem('testSession', JSON.stringify(updated));
        }
    };

    const handleFinish = () => {
        const updated = { ...session, respuestas, indiceActual: currentIndex };
        sessionStorage.setItem('testSession', JSON.stringify(updated));
        navigate('/test/result');
    };

    const isUltima = currentIndex === total - 1;

    // Count answered
    const answered = Object.keys(respuestas).filter(k => respuestas[k] !== undefined && respuestas[k] !== null).length;

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                {/* Progress */}
                <div style={styles.progressHeader}>
                    <span style={styles.progressText}>
                        Pregunta {currentIndex + 1} de {total}
                    </span>
                    <span style={styles.progressText}>
                        {answered}/{total} respondidas
                    </span>
                </div>
                <div style={styles.progressBar}>
                    <div style={{ ...styles.progressFill, width: `${progreso}%` }}></div>
                </div>

                {/* Question */}
                <div style={styles.questionBox}>
                    <span style={styles.tipoBadge}>{formatTipo(pregunta.questionType)}</span>
                    <h2 style={styles.enunciado}>{pregunta.questionText}</h2>
                </div>

                {/* Answer area */}
                <div style={styles.answerArea}>
                    {/* Answer area - TRUE_FALSE */}
                    {pregunta.questionType === 'TRUE_FALSE' && (
                        <div style={styles.vfGroup}>
                            {[{ val: true, label: '✓ Verdadero' }, { val: false, label: '✗ Falso' }].map(opt => (
                                <label
                                    key={String(opt.val)}
                                    style={{
                                        ...styles.vfOption,
                                        borderColor: respActual === opt.val ? '#1abc9c' : 'rgba(149,165,166,0.3)',
                                        backgroundColor: respActual === opt.val ? 'rgba(26,188,156,0.15)' : 'transparent'
                                    }}
                                    onClick={() => updateRespuesta(opt.val)}
                                >
                                    <input
                                        type="radio"
                                        name="vf"
                                        checked={respActual === opt.val}
                                        onChange={() => updateRespuesta(opt.val)}
                                        style={{ accentColor: '#1abc9c' }}
                                    />
                                    {opt.label}
                                </label>
                            ))}
                        </div>
                    )}

                    {/* Answer area - SINGLE_CHOICE */}
                    {pregunta.questionType === 'SINGLE_CHOICE' && pregunta.options && (
                        <div style={styles.optionsList}>
                            {pregunta.options.map((optText, idx) => (
                                <label
                                    key={idx}
                                    style={{
                                        ...styles.optionItem,
                                        borderColor: respActual === idx ? '#1abc9c' : 'rgba(149,165,166,0.3)',
                                        backgroundColor: respActual === idx ? 'rgba(26,188,156,0.15)' : 'transparent'
                                    }}
                                    onClick={() => updateRespuesta(idx)}
                                >
                                    <input
                                        type="radio"
                                        name="opcion"
                                        checked={respActual === idx}
                                        onChange={() => updateRespuesta(idx)}
                                        style={{ accentColor: '#1abc9c' }}
                                    />
                                    {optText}
                                </label>
                            ))}
                        </div>
                    )}

                    {/* Answer area - MULTIPLE_CHOICE */}
                    {pregunta.questionType === 'MULTIPLE_CHOICE' && pregunta.options && (
                        <div style={styles.optionsList}>
                            {pregunta.options.map((optText, idx) => {
                                const selected = Array.isArray(respActual) && respActual.includes(idx);
                                return (
                                    <label
                                        key={idx}
                                        style={{
                                            ...styles.optionItem,
                                            borderColor: selected ? '#1abc9c' : 'rgba(149,165,166,0.3)',
                                            backgroundColor: selected ? 'rgba(26,188,156,0.15)' : 'transparent'
                                        }}
                                        onClick={() => {
                                            const current = Array.isArray(respActual) ? respActual : [];
                                            const next = selected
                                                ? current.filter(i => i !== idx)
                                                : [...current, idx];
                                            updateRespuesta(next);
                                        }}
                                    >
                                        <input
                                            type="checkbox"
                                            checked={selected}
                                            onChange={() => { }}
                                            style={{ accentColor: '#1abc9c' }}
                                        />
                                        {optText}
                                    </label>
                                );
                            })}
                        </div>
                    )}
                </div>

                {/* Navigation */}
                <div style={styles.navRow}>
                    <button
                        onClick={goPrev}
                        disabled={currentIndex === 0}
                        style={{
                            ...styles.navBtn,
                            opacity: currentIndex === 0 ? 0.4 : 1,
                            cursor: currentIndex === 0 ? 'not-allowed' : 'pointer'
                        }}
                    >
                        ← Anterior
                    </button>

                    <button onClick={() => navigate('/test/config')} style={{ ...styles.navBtn, fontSize: '12px', opacity: 0.7 }}>
                        Cancelar
                    </button>

                    {isUltima ? (
                        <button onClick={handleFinish} style={styles.finishBtn}>
                            Finalizar Test ✓
                        </button>
                    ) : (
                        <button onClick={goNext} style={styles.nextBtn}>
                            Siguiente →
                        </button>
                    )}
                </div>

                {/* Question dots */}
                <div style={styles.dotsRow}>
                    {preguntas.map((p, i) => {
                        const isAnswered = respuestas[p.id] !== undefined && respuestas[p.id] !== null;
                        return (
                            <div
                                key={p.id}
                                onClick={() => {
                                    setCurrentIndex(i);
                                    const updated = { ...session, respuestas, indiceActual: i };
                                    sessionStorage.setItem('testSession', JSON.stringify(updated));
                                }}
                                style={{
                                    ...styles.dot,
                                    backgroundColor: i === currentIndex ? '#1abc9c' :
                                        isAnswered ? 'rgba(26,188,156,0.4)' : 'rgba(149,165,166,0.3)',
                                    cursor: 'pointer'
                                }}
                                title={`Pregunta ${i + 1}`}
                            />
                        );
                    })}
                </div>
            </div>
        </div>
    );
};

function formatTipo(tipo) {
    switch (tipo) {
        case 'TRUE_FALSE': return 'Verdadero / Falso';
        case 'SINGLE_CHOICE': return 'Selección Única';
        case 'MULTIPLE_CHOICE': return 'Selección Múltiple';
        default: return tipo;
    }
}

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
        maxWidth: '640px',
        backgroundColor: 'rgba(52, 73, 94, 0.95)',
        borderRadius: '12px',
        padding: '32px',
        boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)',
        border: '1px solid rgba(255, 255, 255, 0.1)'
    },
    progressHeader: {
        display: 'flex',
        justifyContent: 'space-between',
        marginBottom: '8px'
    },
    progressText: {
        color: '#95a5a6',
        fontSize: '13px'
    },
    progressBar: {
        width: '100%',
        height: '6px',
        backgroundColor: 'rgba(149, 165, 166, 0.2)',
        borderRadius: '3px',
        overflow: 'hidden',
        marginBottom: '24px'
    },
    progressFill: {
        height: '100%',
        backgroundColor: '#1abc9c',
        borderRadius: '3px',
        transition: 'width 0.3s ease'
    },
    questionBox: {
        marginBottom: '24px'
    },
    tipoBadge: {
        display: 'inline-block',
        padding: '4px 10px',
        backgroundColor: 'rgba(26, 188, 156, 0.2)',
        color: '#1abc9c',
        fontSize: '11px',
        fontWeight: '600',
        borderRadius: '4px',
        marginBottom: '12px',
        textTransform: 'uppercase',
        letterSpacing: '0.5px'
    },
    enunciado: {
        color: '#ecf0f1',
        fontSize: '18px',
        fontWeight: '500',
        lineHeight: '1.5'
    },
    answerArea: {
        marginBottom: '24px'
    },
    vfGroup: {
        display: 'flex',
        gap: '12px'
    },
    vfOption: {
        flex: 1,
        display: 'flex',
        alignItems: 'center',
        gap: '10px',
        padding: '16px',
        border: '2px solid',
        borderRadius: '8px',
        color: '#ecf0f1',
        fontSize: '15px',
        cursor: 'pointer',
        transition: 'all 0.2s ease'
    },
    optionsList: {
        display: 'flex',
        flexDirection: 'column',
        gap: '10px'
    },
    optionItem: {
        display: 'flex',
        alignItems: 'center',
        gap: '12px',
        padding: '14px 16px',
        border: '2px solid',
        borderRadius: '8px',
        color: '#ecf0f1',
        fontSize: '14px',
        cursor: 'pointer',
        transition: 'all 0.2s ease'
    },
    navRow: {
        display: 'flex',
        justifyContent: 'space-between',
        gap: '12px',
        marginBottom: '20px'
    },
    navBtn: {
        padding: '10px 20px',
        backgroundColor: 'rgba(149, 165, 166, 0.2)',
        border: '1px solid rgba(149, 165, 166, 0.4)',
        borderRadius: '6px',
        color: '#bdc3c7',
        fontSize: '14px',
        transition: 'all 0.3s ease'
    },
    nextBtn: {
        padding: '10px 20px',
        backgroundColor: '#1abc9c',
        border: 'none',
        borderRadius: '6px',
        color: 'white',
        fontSize: '14px',
        fontWeight: '600',
        cursor: 'pointer',
        transition: 'all 0.3s ease'
    },
    finishBtn: {
        padding: '10px 24px',
        backgroundColor: '#e67e22',
        border: 'none',
        borderRadius: '6px',
        color: 'white',
        fontSize: '14px',
        fontWeight: '600',
        cursor: 'pointer',
        transition: 'all 0.3s ease'
    },
    dotsRow: {
        display: 'flex',
        flexWrap: 'wrap',
        gap: '6px',
        justifyContent: 'center'
    },
    dot: {
        width: '12px',
        height: '12px',
        borderRadius: '50%',
        transition: 'all 0.2s ease'
    }
};

export default TestQuestion;
