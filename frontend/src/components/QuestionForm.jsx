import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import QuestionService from '../services/QuestionService';
import AuthService from '../services/AuthService';

const QuestionForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = !!id;

    const [loading, setLoading] = useState(isEdit);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [themes, setThemes] = useState([]);

    const [form, setForm] = useState({
        questionType: 'SINGLE_CHOICE',
        questionText: '',
        themeId: '',
        difficulty: 'MEDIUM',
        // TRUE_FALSE
        correctAnswer: true,
        // SINGLE_CHOICE / MULTIPLE_CHOICE
        options: ['', '', '', ''],
        correctAnswerIndex: 0,
        correctAnswerIndices: [0]
    });

    useEffect(() => {
        if (!AuthService.isLoggedIn()) { navigate('/login'); return; }

        QuestionService.getThemes().then(res => setThemes(res.data)).catch(() => { });

        if (isEdit) {
            QuestionService.get(id)
                .then(res => {
                    const q = res.data;
                    setForm({
                        questionType: q.questionType || 'SINGLE_CHOICE',
                        questionText: q.questionText || '',
                        themeId: q.theme?.id || '',
                        difficulty: q.difficulty || 'MEDIUM',
                        correctAnswer: q.correctAnswer !== undefined ? q.correctAnswer : true,
                        options: q.options || ['', '', '', ''],
                        correctAnswerIndex: q.correctAnswerIndex || 0,
                        correctAnswerIndices: q.correctAnswerIndices || [0]
                    });
                    setLoading(false);
                })
                .catch(() => {
                    setError('No se pudo cargar la pregunta.');
                    setLoading(false);
                });
        }
    }, [id, isEdit, navigate]);

    const handleOptionChange = (index, value) => {
        const newOptions = [...form.options];
        newOptions[index] = value;
        setForm({ ...form, options: newOptions });
    };

    const handleMultipleChoiceToggle = (index) => {
        const indices = [...form.correctAnswerIndices];
        const arrIndex = indices.indexOf(index);
        if (arrIndex > -1) {
            indices.splice(arrIndex, 1);
        } else {
            indices.push(index);
        }
        setForm({ ...form, correctAnswerIndices: indices });
    };

    const handleSave = () => {
        setError('');
        if (!form.questionText || !form.themeId) {
            setError('El texto de la pregunta y el tema son obligatorios.');
            return;
        }

        const payload = {
            questionType: form.questionType,
            questionText: form.questionText,
            difficulty: form.difficulty,
            theme: { id: form.themeId }
        };

        if (form.questionType === 'TRUE_FALSE') {
            payload.correctAnswer = form.correctAnswer;
        } else if (form.questionType === 'SINGLE_CHOICE') {
            if (form.options.some(opt => !opt.trim())) {
                setError('Todas las opciones deben tener texto.'); return;
            }
            payload.options = form.options;
            payload.correctAnswerIndex = form.correctAnswerIndex;
        } else if (form.questionType === 'MULTIPLE_CHOICE') {
            if (form.options.some(opt => !opt.trim())) {
                setError('Todas las opciones deben tener texto.'); return;
            }
            if (form.correctAnswerIndices.length === 0) {
                setError('Debes seleccionar al menos una respuesta correcta.'); return;
            }
            payload.options = form.options;
            payload.correctAnswerIndices = form.correctAnswerIndices;
        }

        setSaving(true);
        const action = isEdit ? QuestionService.update(id, payload) : QuestionService.create(payload);

        action
            .then(() => {
                setSuccess(isEdit ? 'Pregunta actualizada correctamente.' : 'Pregunta agregada correctamente.');
                setTimeout(() => navigate('/admin/questions'), 1200);
            })
            .catch(err => setError(err.response?.data?.message || err.response?.data || 'Error al guardar la pregunta.'))
            .finally(() => setSaving(false));
    };

    if (loading) return (
        <div style={styles.container}><div style={styles.card}><p style={{ color: '#bdc3c7', textAlign: 'center' }}>Cargando...</p></div></div>
    );

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <div style={styles.cardHeader}>
                    <h1 style={styles.title}>{isEdit ? '✏️ Editar Pregunta' : '➕ Nueva Pregunta'}</h1>
                    <button onClick={() => navigate('/admin/questions')} style={styles.backBtn}>← Volver</button>
                </div>

                {error && <div style={styles.errorBox}>{error}</div>}
                {success && <div style={styles.successBox}>{success}</div>}

                <div style={styles.formGroup}>
                    <label style={styles.label}>Tipo de Pregunta</label>
                    <select
                        style={styles.input}
                        value={form.questionType}
                        onChange={e => setForm({ ...form, questionType: e.target.value })}
                        disabled={isEdit}
                    >
                        <option value="SINGLE_CHOICE">Elección Única</option>
                        <option value="MULTIPLE_CHOICE">Elección Múltiple</option>
                        <option value="TRUE_FALSE">Verdadero / Falso</option>
                    </select>
                    {isEdit && <p style={styles.hint}>El tipo de pregunta no se puede cambiar tras su creación.</p>}
                </div>

                <div style={styles.formGroup}>
                    <label style={styles.label}>Tema</label>
                    <select
                        style={styles.input}
                        value={form.themeId}
                        onChange={e => setForm({ ...form, themeId: parseInt(e.target.value) })}
                    >
                        <option value="">-- Selecciona un Tema --</option>
                        {themes.map(t => (
                            <option key={t.id} value={t.id}>{t.name}</option>
                        ))}
                    </select>
                </div>

                <div style={styles.formGroup}>
                    <label style={styles.label}>Dificultad</label>
                    <select
                        style={styles.input}
                        value={form.difficulty}
                        onChange={e => setForm({ ...form, difficulty: e.target.value })}
                    >
                        <option value="EASY">Fácil</option>
                        <option value="MEDIUM">Medio</option>
                        <option value="HARD">Difícil</option>
                    </select>
                </div>

                <div style={styles.formGroup}>
                    <label style={styles.label}>Texto de la Pregunta</label>
                    <textarea
                        style={{ ...styles.input, minHeight: '80px', resize: 'vertical' }}
                        value={form.questionText}
                        onChange={e => setForm({ ...form, questionText: e.target.value })}
                        placeholder="Escribe la pregunta aquí..."
                    />
                </div>

                {/* DYNAMIC FIELDS BASED ON TYPE */}
                <div style={styles.answersSection}>
                    <h3 style={styles.sectionTitle}>Respuestas</h3>

                    {form.questionType === 'TRUE_FALSE' && (
                        <div style={styles.formGroup}>
                            <label style={styles.label}>¿Cuál es la respuesta correcta?</label>
                            <select
                                style={styles.input}
                                value={form.correctAnswer}
                                onChange={e => setForm({ ...form, correctAnswer: e.target.value === 'true' })}
                            >
                                <option value="true">Verdadero</option>
                                <option value="false">Falso</option>
                            </select>
                        </div>
                    )}

                    {form.questionType === 'SINGLE_CHOICE' && (
                        <div>
                            {form.options.map((opt, idx) => (
                                <div key={idx} style={styles.optionRow}>
                                    <input
                                        type="radio"
                                        name="singleChoice"
                                        checked={form.correctAnswerIndex === idx}
                                        onChange={() => setForm({ ...form, correctAnswerIndex: idx })}
                                        style={styles.radio}
                                    />
                                    <input
                                        style={styles.input}
                                        value={opt}
                                        onChange={e => handleOptionChange(idx, e.target.value)}
                                        placeholder={`Opción ${idx + 1}`}
                                    />
                                </div>
                            ))}
                            <p style={styles.hint}>Marca el botón circular en la opción que sea correcta.</p>
                        </div>
                    )}

                    {form.questionType === 'MULTIPLE_CHOICE' && (
                        <div>
                            {form.options.map((opt, idx) => (
                                <div key={idx} style={styles.optionRow}>
                                    <input
                                        type="checkbox"
                                        checked={form.correctAnswerIndices.includes(idx)}
                                        onChange={() => handleMultipleChoiceToggle(idx)}
                                        style={styles.checkbox}
                                    />
                                    <input
                                        style={styles.input}
                                        value={opt}
                                        onChange={e => handleOptionChange(idx, e.target.value)}
                                        placeholder={`Opción ${idx + 1}`}
                                    />
                                </div>
                            ))}
                            <p style={styles.hint}>Marca las casillas de las opciones que sean correctas.</p>
                        </div>
                    )}
                </div>

                <div style={styles.formActions}>
                    <button onClick={handleSave} disabled={saving} style={styles.saveBtn}>
                        {saving ? 'Guardando...' : (isEdit ? 'Guardar cambios' : 'Agregar pregunta')}
                    </button>
                    <button onClick={() => navigate('/admin/questions')} style={styles.cancelBtn}>
                        Cancelar
                    </button>
                </div>
            </div>
        </div>
    );
};

const styles = {
    container: { minHeight: '100vh', background: 'linear-gradient(135deg, #2c3e50 0%, #34495e 100%)', display: 'flex', justifyContent: 'center', alignItems: 'flex-start', padding: '40px 20px' },
    card: { width: '100%', maxWidth: '600px', backgroundColor: 'rgba(52,73,94,0.95)', borderRadius: '14px', padding: '32px', boxShadow: '0 8px 32px rgba(0,0,0,0.3)', border: '1px solid rgba(255,255,255,0.1)' },
    cardHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '28px' },
    title: { color: '#ecf0f1', fontSize: '20px', fontWeight: '700', margin: 0 },
    backBtn: { padding: '7px 14px', backgroundColor: 'rgba(149,165,166,0.2)', border: '1px solid rgba(149,165,166,0.3)', borderRadius: '6px', color: '#bdc3c7', fontSize: '13px', cursor: 'pointer' },
    errorBox: { backgroundColor: 'rgba(231,76,60,0.15)', border: '1px solid rgba(231,76,60,0.4)', borderRadius: '8px', padding: '12px', marginBottom: '20px', color: '#e74c3c', fontSize: '14px' },
    successBox: { backgroundColor: 'rgba(46,204,113,0.15)', border: '1px solid rgba(46,204,113,0.4)', borderRadius: '8px', padding: '12px', marginBottom: '20px', color: '#2ecc71', fontSize: '14px' },
    formGroup: { marginBottom: '20px' },
    label: { display: 'block', color: '#bdc3c7', fontSize: '14px', fontWeight: '500', marginBottom: '8px' },
    input: { width: '100%', padding: '11px 14px', backgroundColor: 'rgba(44,62,80,0.7)', border: '1px solid rgba(149,165,166,0.25)', borderRadius: '8px', color: '#ecf0f1', fontSize: '14px', boxSizing: 'border-box' },
    hint: { color: '#7f8c8d', fontSize: '12px', marginTop: '5px' },
    answersSection: { marginTop: '30px', padding: '20px', backgroundColor: 'rgba(0,0,0,0.15)', borderRadius: '10px', border: '1px solid rgba(255,255,255,0.05)' },
    sectionTitle: { color: '#ecf0f1', fontSize: '16px', fontWeight: '600', marginTop: 0, marginBottom: '16px' },
    optionRow: { display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '12px' },
    radio: { cursor: 'pointer', transform: 'scale(1.2)' },
    checkbox: { cursor: 'pointer', transform: 'scale(1.2)' },
    formActions: { display: 'flex', gap: '12px', marginTop: '30px' },
    saveBtn: { flex: 1, padding: '12px', backgroundColor: '#3498db', border: 'none', borderRadius: '8px', color: 'white', fontSize: '15px', fontWeight: '600', cursor: 'pointer' },
    cancelBtn: { padding: '12px 20px', backgroundColor: 'rgba(149,165,166,0.2)', border: '1px solid rgba(149,165,166,0.3)', borderRadius: '8px', color: '#bdc3c7', fontSize: '14px', cursor: 'pointer' }
};

export default QuestionForm;
