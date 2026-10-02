import React from 'react'
import ProductoCard from './ProductoCard.jsx'
import productos from '../data/productos.json'

/**
 * Componente que lista los productos obtenidos desde un archivo JSON.
 * Para cada producto renderiza una ProductoCard, pasándole como `children`
 * los condicionales de envío gratis y compra internacional.
 */
function ProductoList() {
  return (
    <section className="producto-list">
      <h2 className="producto-list__titulo">Listado de Productos</h2>
      <div className="producto-list__grid">
        {productos.map((producto) => (
          <ProductoCard
            key={producto.id}
            nombre={producto.nombre}
            descripcion={producto.descripcion}
            precio={producto.precio}
            imagen={producto.imagen}
            stock={producto.stock}
          >
            {/* Children con condicionales */}
            {producto.envioGratis && (
              <span className="badge badge--envio">Envío gratis</span>
            )}
            {producto.compraInternacional && (
              <span className="badge badge--internacional">
                Compra internacional
              </span>
            )}
            {!producto.envioGratis && !producto.compraInternacional && (
              <span className="badge badge--neutro">Envío estándar</span>
            )}
          </ProductoCard>
        ))}
      </div>
    </section>
  )
}

export default ProductoList
