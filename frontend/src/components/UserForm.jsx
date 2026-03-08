import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import UserService from '../services/UserService';
import AuthService from '../services/AuthService';

const UserForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = !!id;

    const [form, setForm] = useState({ username: '', email: '', password: '', role: 'ROLE_USER' });
    const [loading, setLoading] = useState(isEdit);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        if (!AuthService.isLoggedIn()) { navigate('/login'); return; }
        if (isEdit) {
            UserService.get(id)
                .then(res => {
                    const isAdmin = res.data.roles?.includes('ROLE_ADMIN') || res.data.roles?.includes('ADMIN');
                    setForm({
                        username: res.data.username || '',
                        email: res.data.email || '',
                        password: '',
                        role: isAdmin ? 'ROLE_ADMIN' : 'ROLE_USER'
                    });
                    setLoading(false);
                })
                .catch(() => {
                    setError('No se pudo cargar el usuario.');
                    setLoading(false);
                });
        }
    }, [id, isEdit, navigate]);

    const handleSave = () => {
        setError('');
        if (!isEdit && (!form.username || !form.password || !form.email)) {
            setError('Completa todos los campos obligatorios para un nuevo usuario.');
            return;
        }
        if (isEdit && !form.email) {
            setError('El email es obligatorio.');
            return;
        }
        setSaving(true);
        const payload = {
            username: form.username,
            email: form.email,
            password: form.password,
            roles: [form.role]
        };
        const action = isEdit ? UserService.update(id, payload) : UserService.create(form);
        action
            .then(() => {
                setSuccess(isEdit ? 'Usuario actualizado correctamente.' : 'Usuario creado correctamente.');
                setTimeout(() => navigate('/admin/users'), 1200);
            })
            .catch(err => setError(err.response?.data || 'Error al guardar el usuario.'))
            .finally(() => setSaving(false));
    };

    if (loading) return (
        <div style={styles.container}><div style={styles.card}><p style={{ color: '#bdc3c7', textAlign: 'center' }}>Cargando...</p></div></div>
    );

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <div style={styles.cardHeader}>
                    <h1 style={styles.title}>{isEdit ? '✏️ Editar Usuario' : '➕ Nuevo Usuario'}</h1>
                    <button onClick={() => navigate('/admin/users')} style={styles.backBtn}>← Volver</button>
                </div>

                {error && <div style={styles.errorBox}>{error}</div>}
                {success && <div style={styles.successBox}>{success}</div>}

                <div style={styles.formGroup}>
                    <label style={styles.label}>Nombre de usuario</label>
                    <input
                        style={styles.input}
                        type="text"
                        value={form.username}
                        onChange={e => setForm({ ...form, username: e.target.value })}
                        disabled={isEdit}
                        placeholder="Ej: carlos123"
                    />
                    {isEdit && <p style={styles.hint}>El nombre de usuario no se puede cambiar.</p>}
                </div>

                <div style={styles.formGroup}>
                    <label style={styles.label}>Email</label>
                    <input
                        style={styles.input}
                        type="email"
                        value={form.email}
                        onChange={e => setForm({ ...form, email: e.target.value })}
                        placeholder="ejemplo@correo.com"
                    />
                </div>

                <div style={styles.formGroup}>
                    <label style={styles.label}>Rol de Usuario</label>
                    <select
                        style={styles.input}
                        value={form.role}
                        onChange={e => setForm({ ...form, role: e.target.value })}
                    >
                        <option value="ROLE_USER">Usuario Normal</option>
                        <option value="ROLE_ADMIN">Administrador</option>
                    </select>
                </div>

                <div style={styles.formGroup}>
                    <label style={styles.label}>{isEdit ? 'Nueva Contraseña (Opcional)' : 'Contraseña'}</label>
                    <input
                        style={styles.input}
                        type="password"
                        value={form.password}
                        onChange={e => setForm({ ...form, password: e.target.value })}
                        placeholder={isEdit ? "Dejar en blanco para no cambiar..." : "Mínimo 6 caracteres"}
                    />
                </div>

                <div style={styles.formActions}>
                    <button onClick={handleSave} disabled={saving} style={styles.saveBtn}>
                        {saving ? 'Guardando...' : (isEdit ? 'Guardar cambios' : 'Crear usuario')}
                    </button>
                    <button onClick={() => navigate('/admin/users')} style={styles.cancelBtn}>
                        Cancelar
                    </button>
                </div>
            </div>
        </div>
    );
};

const styles = {
    container: { minHeight: '100vh', background: 'linear-gradient(135deg, #2c3e50 0%, #34495e 100%)', display: 'flex', justifyContent: 'center', alignItems: 'flex-start', padding: '40px 20px' },
    card: { width: '100%', maxWidth: '500px', backgroundColor: 'rgba(52,73,94,0.95)', borderRadius: '14px', padding: '32px', boxShadow: '0 8px 32px rgba(0,0,0,0.3)', border: '1px solid rgba(255,255,255,0.1)' },
    cardHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '28px' },
    title: { color: '#ecf0f1', fontSize: '20px', fontWeight: '700', margin: 0 },
    backBtn: { padding: '7px 14px', backgroundColor: 'rgba(149,165,166,0.2)', border: '1px solid rgba(149,165,166,0.3)', borderRadius: '6px', color: '#bdc3c7', fontSize: '13px', cursor: 'pointer' },
    errorBox: { backgroundColor: 'rgba(231,76,60,0.15)', border: '1px solid rgba(231,76,60,0.4)', borderRadius: '8px', padding: '12px', marginBottom: '20px', color: '#e74c3c', fontSize: '14px' },
    successBox: { backgroundColor: 'rgba(46,204,113,0.15)', border: '1px solid rgba(46,204,113,0.4)', borderRadius: '8px', padding: '12px', marginBottom: '20px', color: '#2ecc71', fontSize: '14px' },
    formGroup: { marginBottom: '20px' },
    label: { display: 'block', color: '#bdc3c7', fontSize: '14px', fontWeight: '500', marginBottom: '8px' },
    input: { width: '100%', padding: '11px 14px', backgroundColor: 'rgba(44,62,80,0.7)', border: '1px solid rgba(149,165,166,0.25)', borderRadius: '8px', color: '#ecf0f1', fontSize: '14px', boxSizing: 'border-box' },
    hint: { color: '#7f8c8d', fontSize: '12px', marginTop: '5px' },
    formActions: { display: 'flex', gap: '12px', marginTop: '28px' },
    saveBtn: { flex: 1, padding: '12px', backgroundColor: '#1abc9c', border: 'none', borderRadius: '8px', color: 'white', fontSize: '15px', fontWeight: '600', cursor: 'pointer' },
    cancelBtn: { padding: '12px 20px', backgroundColor: 'rgba(149,165,166,0.2)', border: '1px solid rgba(149,165,166,0.3)', borderRadius: '8px', color: '#bdc3c7', fontSize: '14px', cursor: 'pointer' }
};

export default UserForm;
