DROP DATABASE IF EXISTS splendor;
CREATE DATABASE splendor CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE splendor;

CREATE TABLE roles (
  id_rol INT AUTO_INCREMENT PRIMARY KEY,
  nombre_rol VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE regiones (
  id_region INT AUTO_INCREMENT PRIMARY KEY,
  nombre_region VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE comunas (
  id_comuna INT AUTO_INCREMENT PRIMARY KEY,
  nombre_comuna VARCHAR(100) NOT NULL,
  id_region INT NOT NULL,
  FOREIGN KEY (id_region) REFERENCES regiones(id_region)
) ENGINE=InnoDB;

CREATE TABLE usuarios (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  run VARCHAR(9) NOT NULL UNIQUE,
  nombre VARCHAR(50) NOT NULL,
  apellidos VARCHAR(100) NOT NULL,
  correo VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  telefono VARCHAR(20),
  fecha_nacimiento DATE,
  id_rol INT NOT NULL,
  id_region INT,
  id_comuna INT,
  direccion VARCHAR(300),
  activo TINYINT(1) NOT NULL DEFAULT 1,
  fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (id_rol) REFERENCES roles(id_rol),
  FOREIGN KEY (id_region) REFERENCES regiones(id_region),
  FOREIGN KEY (id_comuna) REFERENCES comunas(id_comuna)
) ENGINE=InnoDB;

CREATE TABLE categorias (
  id_categoria INT AUTO_INCREMENT PRIMARY KEY,
  nombre_categoria VARCHAR(50) NOT NULL UNIQUE,
  descripcion VARCHAR(255),
  activo TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE productos (
  id_producto INT AUTO_INCREMENT PRIMARY KEY,
  codigo_producto VARCHAR(50) NOT NULL UNIQUE,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(500),
  precio DECIMAL(10,2) NOT NULL CHECK (precio >= 0),
  precio_oferta DECIMAL(10,2),
  en_oferta TINYINT(1) NOT NULL DEFAULT 0,
  stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
  stock_critico INT DEFAULT 0,
  id_categoria INT NOT NULL,
  origen VARCHAR(255),
  kilates VARCHAR(20),
  corte VARCHAR(255),
  claridad VARCHAR(255),
  color VARCHAR(255),
  certificado VARCHAR(255),
  peso_gramos DECIMAL(6,3),
  imagen_principal VARCHAR(255),
  activo TINYINT(1) NOT NULL DEFAULT 1,
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
) ENGINE=InnoDB;

CREATE TABLE cupones (
  id_cupon INT AUTO_INCREMENT PRIMARY KEY,
  codigo VARCHAR(30) NOT NULL UNIQUE,
  descuento_porcentaje DECIMAL(5,2) NOT NULL,
  fecha_expiracion DATE,
  activo TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB;

-- ordenes = BOLETA y orden_detalle = DETALLE_BOLETA en la Figura 1 del enunciado
CREATE TABLE ordenes (
  id_orden INT AUTO_INCREMENT PRIMARY KEY,
  numero_orden VARCHAR(30) NOT NULL UNIQUE,
  id_usuario INT,
  nombre_cliente VARCHAR(50) NOT NULL,
  apellidos_cliente VARCHAR(100) NOT NULL,
  correo_cliente VARCHAR(100) NOT NULL,
  calle VARCHAR(150) NOT NULL,
  departamento VARCHAR(30),
  id_region INT NOT NULL,
  id_comuna INT NOT NULL,
  indicaciones VARCHAR(255),
  opcion_entrega VARCHAR(30) NOT NULL DEFAULT 'Despacho',
  estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente'
    CHECK (estado IN ('Pendiente','Procesando','Pagada','Pago fallido','Enviado','Entregado','Cancelado')),
  descuento DECIMAL(12,2) NOT NULL DEFAULT 0,
  costo_envio DECIMAL(12,2) NOT NULL DEFAULT 0,
  total DECIMAL(12,2) NOT NULL,
  fecha_orden DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
  FOREIGN KEY (id_region) REFERENCES regiones(id_region),
  FOREIGN KEY (id_comuna) REFERENCES comunas(id_comuna)
) ENGINE=InnoDB;

CREATE TABLE orden_detalle (
  id_detalle INT AUTO_INCREMENT PRIMARY KEY,
  id_orden INT NOT NULL,
  id_producto INT NOT NULL,
  nombre_producto VARCHAR(255),
  imagen_producto VARCHAR(255),
  cantidad INT NOT NULL CHECK (cantidad > 0),
  precio_unitario DECIMAL(10,2) NOT NULL,
  subtotal DECIMAL(12,2) NOT NULL,
  FOREIGN KEY (id_orden) REFERENCES ordenes(id_orden) ON DELETE CASCADE,
  FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
) ENGINE=InnoDB;

CREATE TABLE contactos (
  id_contacto INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  correo VARCHAR(100),
  comentario VARCHAR(500) NOT NULL,
  leido TINYINT(1) NOT NULL DEFAULT 0,
  fecha_envio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE blogs (
  id_blog INT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(200) NOT NULL UNIQUE,
  descripcion_corta VARCHAR(500),
  imagen VARCHAR(255),
  activo TINYINT(1) NOT NULL DEFAULT 1,
  fecha_publicacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE blog_detalle (
  id_detalle_blog INT AUTO_INCREMENT PRIMARY KEY,
  id_blog INT NOT NULL UNIQUE,
  descripcion_larga TEXT NOT NULL,
  FOREIGN KEY (id_blog) REFERENCES blogs(id_blog) ON DELETE CASCADE
) ENGINE=InnoDB;

