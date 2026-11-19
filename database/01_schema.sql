USE splendor;

INSERT INTO roles (nombre_rol) VALUES ('CLIENTE'), ('ADMIN'), ('VENDEDOR');

INSERT INTO regiones (nombre_region) VALUES
('Región de Arica y Parinacota'),('Región de Tarapacá'),('Región de Antofagasta'),
('Región de Atacama'),('Región de Coquimbo'),('Región de Valparaíso'),
('Región Metropolitana de Santiago'),("Región del Libertador General Bernardo O'Higgins"),
('Región del Maule'),('Región de Ñuble'),('Región del Biobío'),('Región de La Araucanía'),
('Región de Los Ríos'),('Región de Los Lagos'),('Región de Aysén'),('Región de Magallanes');

-- =========================================================================
-- Todas las 346 comunas oficiales de Chile distribuidas en sus 16 regiones
-- =========================================================================
INSERT INTO comunas (nombre_comuna, id_region) VALUES
-- Región 1: Arica y Parinacota
('Arica',1),('Camarones',1),('General Lagos',1),('Putre',1),
-- Región 2: Tarapacá
('Alto Hospicio',2),('Camiña',2),('Colchane',2),('Huara',2),('Iquique',2),('Pica',2),('Pozo Almonte',2),
-- Región 3: Antofagasta
('Antofagasta',3),('Calama',3),('María Elena',3),('Mejillones',3),('Ollagüe',3),('San Pedro de Atacama',3),('Sierra Gorda',3),('Taltal',3),('Tocopilla',3),
-- Región 4: Atacama
('Alto del Carmen',4),('Caldera',4),('Chañaral',4),('Copiapó',4),('Diego de Almagro',4),('Freirina',4),('Huasco',4),('Tierra Amarilla',4),('Vallenar',4),
-- Región 5: Coquimbo
('Andacollo',5),('Canela',5),('Combarbalá',5),('Coquimbo',5),('Illapel',5),('La Higuera',5),('La Serena',5),('Los Vilos',5),('Monte Patria',5),('Ovalle',5),('Paihuano',5),('Punitaqui',5),('Río Hurtado',5),('Salamanca',5),('Vicuña',5),
-- Región 6: Valparaíso
('Algarrobo',6),('Cabildo',6),('Calle Larga',6),('Cartagena',6),('Casablanca',6),('Catemu',6),('Concón',6),('El Quisco',6),('El Tabo',6),('Hijuelas',6),('Isla de Pascua',6),('Juan Fernández',6),('La Calera',6),('La Cruz',6),('La Ligua',6),('Limache',6),('Llaillay',6),('Los Andes',6),('Nogales',6),('Olmué',6),('Panquehue',6),('Papudo',6),('Petorca',6),('Puchuncaví',6),('Putaendo',6),('Quillota',6),('Quilpué',6),('Quintero',6),('Rinconada',6),('San Antonio',6),('San Esteban',6),('San Felipe',6),('Santa María',6),('Santo Domingo',6),('Valparaíso',6),('Villa Alemana',6),('Viña del Mar',6),('Zapallar',6),
-- Región 7: Región Metropolitana de Santiago
('Alhué',7),('Buin',7),('Calera de Tango',7),('Cerrillos',7),('Cerro Navia',7),('Colina',7),('Conchalí',7),('Curacaví',7),('El Bosque',7),('El Monte',7),('Estación Central',7),('Huechuraba',7),('Independencia',7),('Isla de Maipo',7),('La Cisterna',7),('La Florida',7),('La Granja',7),('La Pintana',7),('La Reina',7),('Lampa',7),('Las Condes',7),('Lo Barnechea',7),('Lo Espejo',7),('Lo Prado',7),('Macul',7),('Maipú',7),('María Pinto',7),('Melipilla',7),('Ñuñoa',7),('Padre Hurtado',7),('Paine',7),('Pedro Aguirre Cerda',7),('Peñaflor',7),('Peñalolén',7),('Pirque',7),('Providencia',7),('Pudahuel',7),('Puente Alto',7),('Quilicura',7),('Quinta Normal',7),('Recoleta',7),('Renca',7),('San Bernardo',7),('San Joaquín',7),('San José de Maipo',7),('San Miguel',7),('San Pedro',7),('San Ramón',7),('Santiago',7),('Talagante',7),('Tiltil',7),('Vitacura',7),
-- Región 8: Libertador General Bernardo O'Higgins
('Chépica',8),('Chimbarongo',8),('Codegua',8),('Coinco',8),('Coltauco',8),('Doñihue',8),('Graneros',8),('La Estrella',8),('Las Cabras',8),('Litueche',8),('Lolol',8),('Machalí',8),('Malloa',8),('Marchihue',8),('Mostazal',8),('Nancagua',8),('Navidad',8),('Olivar',8),('Palmilla',8),('Paredones',8),('Peralillo',8),('Peumo',8),('Pichidegua',8),('Pichilemu',8),('Placilla',8),('Pumanque',8),('Quinta de Tilcoco',8),('Rancagua',8),('Rengo',8),('Requínoa',8),('San Fernando',8),('San Vicente',8),('Santa Cruz',8),
-- Región 9: Maule
('Cauquenes',9),('Chanco',9),('Colbún',9),('Constitución',9),('Curepto',9),('Curicó',9),('Empedrado',9),('Hualañé',9),('Licantén',9),('Linares',9),('Longaví',9),('Maule',9),('Molina',9),('Parral',9),('Pelarco',9),('Pelluhue',9),('Pencahue',9),('Rauco',9),('Retiro',9),('Río Claro',9),('Romeral',9),('Sagrada Familia',9),('San Clemente',9),('San Javier',9),('San Rafael',9),('Talca',9),('Teno',9),('Vichuquén',9),('Villa Alegre',9),('Yerbas Buenas',9),
-- Región 10: Ñuble
('Bulnes',10),('Chillán',10),('Chillán Viejo',10),('Cobquecura',10),('Coelemu',10),('Coihueco',10),('El Carmen',10),('Ninhue',10),('Ñiquén',10),('Pemuco',10),('Pinto',10),('Portezuelo',10),('Quillón',10),('Quirihue',10),('Ránquil',10),('San Carlos',10),('San Fabián',10),('San Ignacio',10),('San Nicolás',10),('Treguaco',10),('Yungay',10),
-- Región 11: Biobío
('Alto Biobío',11),('Antuco',11),('Arauco',11),('Cabrero',11),('Cañete',11),('Chiguayante',11),('Concepción',11),('Contulmo',11),('Coronel',11),('Curanilahue',11),('Florida',11),('Hualpén',11),('Hualqui',11),('Laja',11),('Lebu',11),('Los Álamos',11),('Los Ángeles',11),('Lota',11),('Mulchén',11),('Nacimiento',11),('Negrete',11),('Penco',11),('Quilaco',11),('Quilleco',11),('San Pedro de la Paz',11),('San Rosendo',11),('Santa Bárbara',11),('Santa Juana',11),('Talcahuano',11),('Tirúa',11),('Tomé',11),('Tucapel',11),('Yumbel',11),
-- Región 12: La Araucanía
('Angol',12),('Carahue',12),('Cholchol',12),('Collipulli',12),('Cunco',12),('Curacautín',12),('Curarrehue',12),('Ercilla',12),('Freire',12),('Galvarino',12),('Gorbea',12),('Lautaro',12),('Loncoche',12),('Lonquimay',12),('Los Sauces',12),('Lumaco',12),('Melipeuco',12),('Nueva Imperial',12),('Padre Las Casas',12),('Perquenco',12),('Pitrufquén',12),('Pucón',12),('Purén',12),('Renaico',12),('Saavedra',12),('Teodoro Schmidt',12),('Toltén',12),('Traiguén',12),('Victoria',12),('Vilcún',12),('Villarrica',12),
-- Región 13: Los Ríos
('Corral',13),('Futrono',13),('La Unión',13),('Lago Ranco',13),('Lanco',13),('Los Lagos',13),('Máfil',13),('Mariquina',13),('Paillaco',13),('Panguipulli',13),('Río Bueno',13),('Valdivia',13),
-- Región 14: Los Lagos
('Ancud',14),('Calbuco',14),('Castro',14),('Chaitén',14),('Chonchi',14),('Cochamó',14),('Curaco de Vélez',14),('Dalcahue',14),('Fresia',14),('Frutillar',14),('Futaleufú',14),('Hualaihué',14),('Llanquihue',14),('Los Muermos',14),('Maullín',14),('Osorno',14),('Palena',14),('Puerto Montt',14),('Puerto Octay',14),('Puerto Varas',14),('Puqueldón',14),('Purranque',14),('Puyehue',14),('Queilén',14),('Quellón',14),('Quemchi',14),('Quinchao',14),('Río Negro',14),('San Juan de la Costa',14),('San Pablo',14),
-- Región 15: Aysén
('Aysén',15),('Chile Chico',15),('Cisnes',15),('Cochrane',15),('Coyhaique',15),('Guaitecas',15),('Lago Verde',15),("O'Higgins",15),('Río Ibáñez',15),('Tortel',15),
-- Región 16: Magallanes
('Antártica',16),('Cabo de Hornos',16),('Laguna Blanca',16),('Natales',16),('Porvenir',16),('Primavera',16),('Punta Arenas',16),('Río Verde',16),('San Gregorio',16),('Timaukel',16),('Torres del Paine',16);

-- =========================================================================
-- Usuarios iniciales de prueba (con vinculación dinámica a sus comunas)
-- Contraseñas en texto plano: el login las migra a BCrypt al primer login
-- =========================================================================
INSERT INTO usuarios (run, nombre, apellidos, correo, password, telefono, id_rol, id_region, id_comuna, direccion)
SELECT '11111111K','Admin','Splendor','admin@profesor.duoc.cl','admin123','+56911111111',2,7,id_comuna,'Av. Siempre Viva 123'
FROM comunas WHERE nombre_comuna='Santiago' AND id_region=7 LIMIT 1;

INSERT INTO usuarios (run, nombre, apellidos, correo, password, telefono, id_rol, id_region, id_comuna, direccion)
SELECT '22222222K','Vera','Vendedora','vendedor@duocuc.cl','vend1234','+56922222222',3,7,id_comuna,'Calle Falsa 456'
FROM comunas WHERE nombre_comuna='Providencia' AND id_region=7 LIMIT 1;

INSERT INTO usuarios (run, nombre, apellidos, correo, password, telefono, id_rol, id_region, id_comuna, direccion)
SELECT '33333333K','Carlos','Cliente','cliente@gmail.com','cli12345','+56933333333',1,7,id_comuna,'Los Leones 789'
FROM comunas WHERE nombre_comuna='Las Condes' AND id_region=7 LIMIT 1;

-- =========================================================================
-- Catálogo de productos y categorías iniciales
-- =========================================================================
INSERT INTO categorias (nombre_categoria, descripcion) VALUES
('Diamante','Diamantes certificados'),
('Rubí','Rubíes naturales'),
('Zafiro','Zafiros de distintos colores'),
('Esmeralda','Esmeraldas colombianas y zambianas');

INSERT INTO productos (codigo_producto, nombre, descripcion, precio, precio_oferta, en_oferta, stock, stock_critico,
  id_categoria, origen, kilates, corte, claridad, color, certificado, peso_gramos, imagen_principal) VALUES
('DIA-001','Diamante Princesa 0.75ct','Corte princesa, certificado GIA',990000,NULL,0,5,2,1,'Sudáfrica','0.75','Princesa','VS1','D','GIA',0.150,'diamond.jpg'),
('DIA-002','Diamante Rosa 0.50ct','Tonalidad rosa natural',1500000,1250000,1,3,1,1,'Botswana','0.50','Brillante','VVS2','Rosa','GIA',0.100,'diamanterosa1.jpg'),
('RUB-001','Rubí Birmano 1.2ct','Rojo intenso sin tratamientos',780000,NULL,0,4,2,2,'Birmania','1.20','Oval','VS','Rojo sangre de paloma','IGI',0.240,'ruby1.jpg'),
('ZAF-001','Zafiro Azul 2ct','Azul profundo de Sri Lanka',650000,NULL,0,6,2,3,'Sri Lanka','2.00','Cojín','VS','Azul real','GIA',0.400,'zafiro1.jpg'),
('ESM-001','Esmeralda Colombiana 1ct','Verde vivo',890000,NULL,0,1,2,4,'Colombia','1.00','Esmeralda','VS','Verde intenso','GIA',0.200,'esmeralda1.jpg');

INSERT INTO blogs (titulo, descripcion_corta, imagen) VALUES
('Cómo elegir tu primer diamante','Las 4C explicadas de forma simple','diamond.jpg');

INSERT INTO blog_detalle (id_blog, descripcion_larga) VALUES
(1,'<p>Corte, color, claridad y quilates: las <strong>4C</strong> definen el valor de un diamante.</p>');
