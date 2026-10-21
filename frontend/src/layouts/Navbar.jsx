import { Link } from 'react-router-dom';

export default function Navbar() {
  return (
    <nav style={{ padding: '1rem', borderBottom: '1px solid #ccc', display: 'flex', gap: '15px' }}>
      <Link to="/" style={{ fontWeight: 'bold', textDecoration: 'none', color: '#16181C' }}>Marketplace UADE</Link>
      <Link to="/catalogo">Catálogo</Link>
      <Link to="/carrito">Carrito</Link>
      <Link to="/vendedor">Mis Productos</Link>
      <div style={{ marginLeft: 'auto', display: 'flex', gap: '10px' }}>
        <Link to="/login">Ingresar</Link>
        <Link to="/registro">Registrarse</Link>
      </div>
    </nav>
  );
}