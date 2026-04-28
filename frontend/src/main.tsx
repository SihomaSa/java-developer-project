import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

// Cargar CSS dinámicamente para evitar error de tipos
import('./index.css').catch(e => console.error('Error loading CSS', e));

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);