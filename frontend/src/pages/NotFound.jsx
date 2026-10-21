import { Link } from 'react-router-dom';

export default function NotFound() {
  return (
    <div style={{ padding: '2rem', textAlign: 'center' }}>
      <h2>Error 404</h2>
      <p>La página que buscás no existe.</p>
      <Link to="/">Volver al inicio</Link>
    </div>
  );
}