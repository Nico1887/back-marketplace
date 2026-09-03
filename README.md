# back-marketplace

API REST de un sistema de e-commerce desarrollada para el TPO de Aplicaciones Interactivas (UADE).
Permite registrar usuarios y autenticarlos, publicar productos con una o mas imagenes y su categoria,
navegar el catalogo ordenado alfabeticamente y filtrarlo por categoria, y gestionar un carrito de compras.
El checkout calcula el total, valida el stock disponible y lo descuenta, generando una orden de compra
con el detalle de los productos y el precio unitario congelado al momento de la operacion.
Construida con Spring Boot, Spring Data JPA y Maven, sobre una base de datos relacional.
No incluye procesamiento de pagos.

---

## Stack

| | |
|---|---|
| Java | 17 |
| Spring Boot | 4.1.1 |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | H2 en memoria (perfil `dev`) / MySQL (perfil `prod`) |
| Build | Maven (wrapper incluido) |
| Utilidades | Lombok, Jakarta Bean Validation |

## Como levantar el proyecto

```bash
git clone https://github.com/Nico1887/back-marketplace.git
cd back-marketplace
./mvnw spring-boot:run
```

Arranca por defecto con el perfil `dev` (H2 en memoria, se recrea en cada arranque y se
carga con datos de prueba desde `src/main/resources/data.sql`).

- API: `http://localhost:8080`
- Consola H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:marketplace`
  - Usuario: `sa` — sin contrasena

Para correr contra MySQL:

```bash
DB_HOST=localhost DB_NAME=marketplace DB_USER=root DB_PASSWORD=tu_password \
  ./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## Estructura del proyecto

```
src/main/java/com/uade/tpo/marketplace/
├── config/         Configuracion transversal (CORS, OpenAPI)
├── controllers/    Capa de exposicion HTTP (@RestController)
├── service/        Logica de negocio (@Service, @Transactional)
├── repository/     Acceso a datos (JpaRepository)
├── entity/         Modelo de dominio JPA (@Entity)
├── dto/
│   ├── request/    Objetos de entrada de la API
│   └── response/   Objetos de salida de la API
└── exceptions/     Excepciones propias y @RestControllerAdvice
```

Los controllers nunca exponen entidades directamente: siempre trabajan contra DTOs.

## Modelo de dominio

11 entidades con las tres cardinalidades JPA:

| Relacion | Tipo |
|---|---|
| Producto → Usuario (vendedor) | `@ManyToOne` |
| Producto → ImagenProducto | `@OneToMany` |
| Producto ↔ Categoria | `@ManyToMany` (`producto_categoria`) |
| Usuario ↔ Rol | `@ManyToMany` (`usuario_rol`) |
| Usuario → Carrito | `@OneToOne` |
| Carrito → DetalleCarrito | `@OneToMany` con clave compuesta |
| Usuario → OrdenCompra | `@OneToMany` |
| OrdenCompra → DetalleOrden | `@OneToMany` con clave compuesta |

Notas de modelado:

- Importes con `BigDecimal` (no `double`).
- `DetalleOrden.precioUnitario` se congela al momento de la compra.
- `Producto.activo` para baja logica; el catalogo filtra por activos.
- `DetalleCarrito` y `DetalleOrden` con clave compuesta.

## Convenciones de trabajo

Ramas `feature/<dominio>`, merge a `main` por Pull Request.
