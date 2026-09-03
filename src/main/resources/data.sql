INSERT INTO rol (nombre) VALUES ('ROLE_USER');
INSERT INTO rol (nombre) VALUES ('ROLE_ADMIN');

INSERT INTO categoria (nombre) VALUES ('Electronica');
INSERT INTO categoria (nombre) VALUES ('Indumentaria');
INSERT INTO categoria (nombre) VALUES ('Hogar');
INSERT INTO categoria (nombre) VALUES ('Libros');
INSERT INTO categoria (nombre) VALUES ('Deportes');

INSERT INTO usuario (username, nombre, apellido, email, password)
VALUES ('jperez', 'Juan', 'Perez', 'jperez@uade.edu.ar', 'cambiar123');
INSERT INTO usuario (username, nombre, apellido, email, password)
VALUES ('mgomez', 'Maria', 'Gomez', 'mgomez@uade.edu.ar', 'cambiar123');

INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (1, 1);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (2, 1);

INSERT INTO producto (usuario_id, nombre, descripcion, precio, stock, activo, fecha_alta)
VALUES (1, 'Auriculares inalambricos', 'Bluetooth 5.3, 30 horas de bateria.', 89999.99, 15, TRUE, CURRENT_TIMESTAMP);
INSERT INTO producto (usuario_id, nombre, descripcion, precio, stock, activo, fecha_alta)
VALUES (1, 'Teclado mecanico', 'Switches rojos, layout ANSI, retroiluminado.', 145000.00, 0, TRUE, CURRENT_TIMESTAMP);
INSERT INTO producto (usuario_id, nombre, descripcion, precio, stock, activo, fecha_alta)
VALUES (2, 'Campera rompeviento', 'Impermeable, talles S a XL.', 62500.50, 8, TRUE, CURRENT_TIMESTAMP);

INSERT INTO producto_categoria (producto_id, categoria_id) VALUES (1, 1);
INSERT INTO producto_categoria (producto_id, categoria_id) VALUES (2, 1);
INSERT INTO producto_categoria (producto_id, categoria_id) VALUES (3, 2);

INSERT INTO imagen_producto (producto_id, url_imagen) VALUES (1, 'https://placehold.co/600x400?text=Auriculares+1');
INSERT INTO imagen_producto (producto_id, url_imagen) VALUES (1, 'https://placehold.co/600x400?text=Auriculares+2');
INSERT INTO imagen_producto (producto_id, url_imagen) VALUES (2, 'https://placehold.co/600x400?text=Teclado');
INSERT INTO imagen_producto (producto_id, url_imagen) VALUES (3, 'https://placehold.co/600x400?text=Campera');
