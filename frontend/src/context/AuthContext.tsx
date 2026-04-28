import { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';

interface AuthContextType {
  user: { username: string; roles: string[] } | null;
  loading?: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  hasRole: (role: string) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<{ username: string; roles: string[] } | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const stored = localStorage.getItem('auth');
    if (stored) {
      const { username, roles } = JSON.parse(stored);
      setUser({ username, roles });
    }
  }, []);

  const login = async (username: string, password: string) => {
    setLoading(true);
    try {
      const res = await fetch('http://localhost:8080/api/v1/productos/stock-bajo', {
        headers: { Authorization: 'Basic ' + btoa(`${username}:${password}`) },
      });
      if (res.status === 401) throw new Error('Credenciales inválidas');
      if (!res.ok) throw new Error('Error al autenticar');
      
      let roles: string[] = [];
      if (username === 'admin') roles = ['ADMIN', 'PRODUCTOS', 'INVENTARIO'];
      else if (username === 'inventario') roles = ['INVENTARIO'];
      else roles = ['USER'];
      
      const userData = { username, roles };
      localStorage.setItem('auth', JSON.stringify({ username, password, roles }));
      setUser(userData);
      toast.success(`Bienvenido ${username}`);
      navigate('/');
    } catch (error) {
      toast.error('Usuario o contraseña incorrectos');
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('auth');
    setUser(null);
    navigate('/login');
    toast.success('Sesión cerrada');
  };

  const hasRole = (role: string) => user?.roles.includes(role) || false;

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, hasRole }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};