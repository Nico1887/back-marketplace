import React from 'react'
import ProductoList from './components/ProductoList.jsx'

function App() {
  return (
    <div className="app">
      <header className="app__header">
        <h1>Back Marketplace</h1>
        <p>Trabajo Práctico Obligatorio — Listado de productos</p>
      </header>
      <main className="app__main">
        <ProductoList />
      </main>
    </div>
  )
}

export default App
