# Wireflow y Mapa de Navegación del E-Commerce

Este documento describe los flujos principales de usuario y las conexiones entre pantallas para el marketplace de Aplicaciones Interactivas.

## 1. Flujo del Catálogo Público (Enzo)
* `Home (/)` → Muestra categorías y productos destacados.
  * *Acción:* Buscar o hacer clic en una categoría → Navega a `Catálogo`.
  * *Acción:* Hacer clic en un producto → Navega a `Detalle del Producto`.
* `Catálogo (/catalogo)` → Muestra la grilla de productos filtrados.
* `Detalle del Producto (/producto/:id)` → Muestra información completa del producto y galería.
  * *Acción:* "Agregar al Carrito" → Actualiza el estado del carrito (Tobías).

## 2. Flujo de Autenticación (Santiago)
* `Login (/login)` → Formulario para usuarios existentes.
  * *Éxito:* Redirige a `Home` o `Perfil`.
* `Registro (/registro)` → Formulario para nuevos usuarios.
  * *Éxito:* Redirige a `Login`.
* `Perfil (/perfil)` → Detalles del usuario (requiere autenticación).

## 3. Flujo del Vendedor (Ramiro)
* `Dashboard del Vendedor (/vendedor)` → Lista los productos activos del vendedor.
  * *Acción:* "Crear Producto" → Abre formulario o modal de creación.
  * *Acción:* "Editar Producto" → Abre formulario de edición.
  * *Acción:* "Gestionar Imágenes" → Abre interfaz para subir imágenes.

## 4. Flujo de Compra y Checkout (Tobías & Agustín)
* `Carrito (/carrito)` → Lista ítems pendientes y el total a pagar.
  * *Acción:* "Proceder al Checkout" → Navega a `Checkout`.
* `Checkout (/checkout)` → Resumen de la orden y confirmación de pago/envío.
  * *Acción:* "Confirmar Compra" → Navega a `Confirmación de Orden` (Agustín).
* `Historial de Órdenes (/ordenes)` → Lista las compras pasadas.
  * *Acción:* Hacer clic en una orden → Navega a `Detalle de Orden (/ordenes/:id)`.