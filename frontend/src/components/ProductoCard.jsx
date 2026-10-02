import React from 'react'

/**
 * Componente tarjeta de producto.
 * Muestra la descripción y el precio del producto, y recibe `children`
 * para renderizar contenido condicional (ej: envío gratis, compra internacional).
 *
 * Props:
 *  - nombre:      string  (título del producto)
 *  - descripcion: string  (descripción del producto)
 *  - precio:      number  (precio en ARS)
 *  - imagen:      string  (URL de la imagen)
 *  - stock:       number  (stock disponible)
 *  - children:    node    (contenido condicional definido por el padre)
 */
function ProductoCard({ nombre, descripcion, precio, imagen, stock, children }) {
  const formatearPrecio = (valor) =>
    new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: 'ARS',
      minimumFractionDigits: 0,
    }).format(valor)

  return (
    <article className="producto-card">
      <img className="producto-card__imagen" src={imagen} alt={nombre} />
      <div className="producto-card__cuerpo">
        <h3 className="producto-card__nombre">{nombre}</h3>
        <p className="producto-card__descripcion">{descripcion}</p>
        <p className="producto-card__precio">{formatearPrecio(precio)}</p>
        <p className="producto-card__stock">
          {stock > 0 ? `Stock: ${stock} unidades` : 'Sin stock disponible'}
        </p>
        {/* children: contenido condicional (envío gratis / compra internacional) */}
        <div className="producto-card__condicionales">{children}</div>
      </div>
    </article>
  )
}

export default ProductoCard
