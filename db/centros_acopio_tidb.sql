-- ==========================================================
-- Sistema de Registro y Coordinación de Centros de Acopio (<in>Hack)
-- Script de Base de Datos compatible al 100% con TiDB Cloud (Serverless)
-- SIN Procedimientos Almacenados (Lógica de negocio gestionada en Java)
-- ==========================================================

CREATE DATABASE IF NOT EXISTS centros_acopio;
USE centros_acopio;

-- Limpieza segura para recreación limpia
SET FOREIGN_KEY_CHECKS = 0;
DROP VIEW IF EXISTS v_stock_actual;
DROP TABLE IF EXISTS centros_campanias, transferencias, movimientos, donantes, articulos, campanias, usuario, centros, instituciones_receptoras;
SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------
-- 1. Tabla: Instituciones Receptoras
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS instituciones_receptoras (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    direccion TEXT NOT NULL,
    contacto VARCHAR(100) NULL
);

-- ----------------------------------------------------------
-- 2. Tabla: Centros de Acopio
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS centros (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    institucion VARCHAR(150) NOT NULL,
    ubicacion TEXT NOT NULL,
    latitud DECIMAL(10, 8) NULL,      -- Soporte para mapa interactivo
    longitud DECIMAL(11, 8) NULL,     -- Soporte para mapa interactivo
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- ----------------------------------------------------------
-- 3. Tabla: Usuarios y Roles (RBAC)
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    centro_id INT NULL,
    institucion_id INT NULL,
    rol ENUM('COORDINADOR', 'ENCARGADO', 'VOLUNTARIO', 'INSTITUCION', 'LIDER') NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_usuario_centro FOREIGN KEY (centro_id) REFERENCES centros(id),
    CONSTRAINT fk_usuario_institucion FOREIGN KEY (institucion_id) REFERENCES instituciones_receptoras(id)
);

-- ----------------------------------------------------------
-- 4. Tabla: Campañas
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS campanias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NULL,                  -- NULL si la contingencia sigue activa
    descripcion TEXT,
    meta_unidades DECIMAL(10,2) NULL DEFAULT 0, -- Soporte para barra de avance de metas
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    lider_id INT NULL,
    CONSTRAINT fk_campania_lider FOREIGN KEY (lider_id) REFERENCES usuario(id)
);

-- ----------------------------------------------------------
-- 5. Tabla: Centros participantes por Campaña
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS centros_campanias (
    id_centro INT NOT NULL,
    id_campania INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_centro, id_campania),
    CONSTRAINT fk_centroscamp_centro FOREIGN KEY (id_centro) REFERENCES centros(id),
    CONSTRAINT fk_centroscamp_campania FOREIGN KEY (id_campania) REFERENCES campanias(id)
);

-- ----------------------------------------------------------
-- 6. Tabla: Catálogo de Artículos / Insumos
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS articulos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    categoria ENUM('NO_PERECEDERO', 'PERECEDERO', 'ROPA', 'LIMPIEZA', 'MEDICAMENTO', 'OTRO') NOT NULL,
    unidad ENUM('PIEZA', 'KG', 'L', 'BOLSA', 'CAJA') NOT NULL
);

-- ----------------------------------------------------------
-- 7. Tabla: Donantes
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS donantes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NULL,
    contacto VARCHAR(100) NULL,
    es_anonimo BOOLEAN NOT NULL DEFAULT FALSE
);

-- ----------------------------------------------------------
-- 8. Tabla: Transferencias entre Centros
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS transferencias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    centro_origen_id INT NOT NULL,
    centro_destino_id INT NOT NULL,
    campania_id INT NOT NULL,
    articulo_id INT NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL,
    usuario_id INT NOT NULL,
    estado ENUM('PENDIENTE', 'COMPLETADA', 'CANCELADA') NOT NULL DEFAULT 'PENDIENTE',
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_transf_centros CHECK (centro_origen_id <> centro_destino_id),
    CONSTRAINT chk_transf_cantidad CHECK (cantidad > 0),
    CONSTRAINT fk_transf_origen FOREIGN KEY (centro_origen_id) REFERENCES centros(id),
    CONSTRAINT fk_transf_destino FOREIGN KEY (centro_destino_id) REFERENCES centros(id),
    CONSTRAINT fk_transf_campania FOREIGN KEY (campania_id) REFERENCES campanias(id),
    CONSTRAINT fk_transf_articulo FOREIGN KEY (articulo_id) REFERENCES articulos(id),
    CONSTRAINT fk_transf_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- ----------------------------------------------------------
-- 9. Tabla: Movimientos de Inventario (Libro Mayor Inmutable)
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS movimientos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tipo ENUM(
        'RECEPCION', 
        'ENTREGA', 
        'MERMA', 
        'TRANSFERENCIA_SALIDA', 
        'TRANSFERENCIA_ENTRADA', 
        'AJUSTE_POSITIVO', 
        'AJUSTE_NEGATIVO'
    ) NOT NULL,
    centro_id INT NOT NULL,
    campania_id INT NOT NULL,
    articulo_id INT NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id INT NOT NULL,
    motivo ENUM('CADUCIDAD', 'DANO', 'PERDIDA', 'CORRECCION_CONTEO', 'ERROR_CAPTURA', 'OTRO') NULL,
    motivo_detalle TEXT NULL,
    donante_id INT NULL,
    institucion_receptora_id INT NULL,
    transferencia_id INT NULL,
    estado_aprobacion ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') NOT NULL DEFAULT 'APROBADO',
    aprobado_por_id INT NULL,
    entrega_confirmada BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_confirmacion TIMESTAMP NULL,
    CONSTRAINT chk_mov_cantidad CHECK (cantidad > 0),
    CONSTRAINT fk_mov_centro FOREIGN KEY (centro_id) REFERENCES centros(id),
    CONSTRAINT fk_mov_campania FOREIGN KEY (campania_id) REFERENCES campanias(id),
    CONSTRAINT fk_mov_articulo FOREIGN KEY (articulo_id) REFERENCES articulos(id),
    CONSTRAINT fk_mov_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_mov_donante FOREIGN KEY (donante_id) REFERENCES donantes(id),
    CONSTRAINT fk_mov_institucion FOREIGN KEY (institucion_receptora_id) REFERENCES instituciones_receptoras(id),
    CONSTRAINT fk_mov_transferencia FOREIGN KEY (transferencia_id) REFERENCES transferencias(id),
    CONSTRAINT fk_mov_aprobador FOREIGN KEY (aprobado_por_id) REFERENCES usuario(id)
);

-- ----------------------------------------------------------
-- 10. Índices para acelerar cálculo de Stock y Dashboards
-- ----------------------------------------------------------
CREATE INDEX idx_mov_centro_camp_art ON movimientos (centro_id, campania_id, articulo_id);
CREATE INDEX idx_mov_fecha ON movimientos (fecha);

-- ----------------------------------------------------------
-- 11. Vista Oficial de Stock Actual (Fórmula del Dominio: Entradas - Salidas)
-- ----------------------------------------------------------
CREATE OR REPLACE VIEW v_stock_actual AS
SELECT 
    m.centro_id,
    m.campania_id,
    m.articulo_id,
    SUM(
        CASE 
            WHEN m.tipo IN ('RECEPCION', 'TRANSFERENCIA_ENTRADA', 'AJUSTE_POSITIVO') THEN m.cantidad
            WHEN m.tipo IN ('ENTREGA', 'MERMA', 'TRANSFERENCIA_SALIDA', 'AJUSTE_NEGATIVO') THEN -m.cantidad
            ELSE 0
        END
    ) AS stock_disponible
FROM movimientos m
WHERE m.estado_aprobacion = 'APROBADO'
GROUP BY m.centro_id, m.campania_id, m.articulo_id;

-- ==========================================================
-- 12. DATOS SEMILLA (SEEDS / FIXTURES) PARA LA DEMO
-- ==========================================================

-- Instituciones Receptoras de Prueba
INSERT INTO instituciones_receptoras (id, nombre, direccion, contacto) VALUES
(1, 'Albergue Comunitario Esperanza', 'Av. Reforma 123, Col. Juárez', '555-123-4567'),
(2, 'Comedor Solidario San José', 'Calle Hidalgo 45, Centro Histórico', '555-987-6543')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Centros de Acopio de Prueba (con coordenadas para el mapa)
INSERT INTO centros (id, nombre, institucion, ubicacion, latitud, longitud, activo) VALUES
(1, 'Campus Central - Explanada', 'Universidad Nacional', 'Av. Insurgentes Sur 3000, Coyoacán', 19.33230000, -99.18650000, TRUE),
(2, 'Sede Comunitaria Norte', 'Cruz Roja Delegación Norte', 'Calzada Vallejo 789, Gustavo A. Madero', 19.48520000, -99.16230000, TRUE)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Usuarios de Prueba (Uno por cada rol del sistema RBAC)
-- Contraseña de prueba: 'password123'
INSERT INTO usuario (id, nombre, email, password, centro_id, institucion_id, rol, activo) VALUES
(1, 'Admin Coordinadora', 'coordinador@hackaton.org', 'password123', NULL, NULL, 'COORDINADOR', TRUE),
(2, 'Encargado Campus Central', 'encargado.central@hackaton.org', 'password123', 1, NULL, 'ENCARGADO', TRUE),
(3, 'Voluntario Campus Central', 'voluntario.central@hackaton.org', 'password123', 1, NULL, 'VOLUNTARIO', TRUE),
(4, 'Contacto Albergue Esperanza', 'receptor@albergue.org', 'password123', NULL, 1, 'INSTITUCION', TRUE),
(5, 'Líder de Campaña Emergencia', 'lider.emergencia@hackaton.org', 'password123', NULL, NULL, 'LIDER', TRUE)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- Campañas de Prueba
INSERT INTO campanias (id, nombre, fecha_inicio, fecha_fin, descripcion, meta_unidades, activo, lider_id) VALUES
(1, 'Plan de Contingencia Huracán 2026', '2026-09-01', NULL, 'Recolección de víveres e insumos para comunidades costeras afectadas.', 5000.00, TRUE, 5),
(2, 'Campaña Invernal Abriga a un Hermano', '2026-10-15', '2026-12-31', 'Acopio de ropa abrigadora y cobijas.', 2000.00, TRUE, 5)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Centros asignados a la campaña activa
INSERT INTO centros_campanias (id_centro, id_campania, activo) VALUES
(1, 1, TRUE),
(2, 1, TRUE)
ON DUPLICATE KEY UPDATE activo = VALUES(activo);

-- Catálogo de Insumos Estándar
INSERT INTO articulos (id, nombre, categoria, unidad) VALUES
(1, 'Agua embotellada 1L', 'NO_PERECEDERO', 'PIEZA'),
(2, 'Arroz en grano', 'NO_PERECEDERO', 'KG'),
(3, 'Frijol negro', 'NO_PERECEDERO', 'KG'),
(4, 'Atún en lata 140g', 'NO_PERECEDERO', 'PIEZA'),
(5, 'Paracetamol 500mg (caja 20 tabs)', 'MEDICAMENTO', 'CAJA'),
(6, 'Suero oral en polvo', 'MEDICAMENTO', 'BOLSA'),
(7, 'Jabón neutro en barra', 'LIMPIEZA', 'PIEZA'),
(8, 'Cloro concentrado 1L', 'LIMPIEZA', 'L'),
(9, 'Cobija térmica', 'ROPA', 'PIEZA'),
(10, 'Leche ultrapasteurizada 1L', 'PERECEDERO', 'L')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Donantes Iniciales
INSERT INTO donantes (id, nombre, contacto, es_anonimo) VALUES
(1, 'Ciudadano Solidario', '555-444-3322', FALSE),
(2, 'Donante Anónimo', NULL, TRUE)
ON DUPLICATE KEY UPDATE id = VALUES(id);

-- Movimientos Semilla Iniciales (Inventario positivo para la demo)
INSERT INTO movimientos (
    id, tipo, centro_id, campania_id, articulo_id, cantidad, usuario_id, 
    motivo, donante_id, institucion_receptora_id, transferencia_id, estado_aprobacion, entrega_confirmada
) VALUES
(1, 'RECEPCION', 1, 1, 1, 150.00, 2, NULL, 1, NULL, NULL, 'APROBADO', FALSE), -- 150 Agua en Central
(2, 'RECEPCION', 1, 1, 2, 80.00, 2, NULL, 1, NULL, NULL, 'APROBADO', FALSE),  -- 80 Kg Arroz en Central
(3, 'RECEPCION', 1, 1, 4, 120.00, 3, NULL, 2, NULL, NULL, 'APROBADO', FALSE), -- 120 Latas Atún en Central
(4, 'RECEPCION', 1, 1, 5, 40.00, 2, NULL, 1, NULL, NULL, 'APROBADO', FALSE),  -- 40 Paracetamol en Central
(5, 'RECEPCION', 2, 1, 1, 100.00, 1, NULL, 2, NULL, NULL, 'APROBADO', FALSE), -- 100 Agua en Norte
(6, 'RECEPCION', 2, 1, 3, 60.00, 1, NULL, 1, NULL, NULL, 'APROBADO', FALSE)   -- 60 Kg Frijol en Norte
ON DUPLICATE KEY UPDATE id = VALUES(id);



-- ==========================================================
-- SCRIPT DE REGISTROS ADICIONALES (TAMPICO, TAMAULIPAS)
-- Compatible con TiDB Cloud Serverless / MySQL 8
-- ==========================================================

USE centros_acopio;

-- ----------------------------------------------------------
-- 1. INSTITUCIONES RECEPTORAS (3 Nuevas en Tampico)
-- ----------------------------------------------------------
INSERT INTO instituciones_receptoras (id, nombre, direccion, contacto) VALUES
(3, 'Casa Hogar San Pedro', 'Av. Miguel Hidalgo 3405, Col. Águila, Tampico, Tamaulipas', '833-213-4567'),
(4, 'Comedor Comunitario Cascajal', 'Calle Francisco I. Madero 112, Col. Cascajal, Tampico, Tamaulipas', '833-214-8899'),
(5, 'Refugio Temporal Ejército de Salvación', 'Calle Venustiano Carranza 502, Zona Centro, Tampico, Tamaulipas', '833-212-3344')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), direccion = VALUES(direccion), contacto = VALUES(contacto);

-- ----------------------------------------------------------
-- 2. CENTROS DE ACOPIO (3 Nuevos en Tampico, Tamaulipas)
-- Coordenadas reales georreferenciadas para el mapa
-- ----------------------------------------------------------
INSERT INTO centros (id, nombre, institucion, ubicacion, latitud, longitud, activo) VALUES
(3, 'Centro Laguna del Carpintero', 'DIF Tampico', 'Blvd. Perimetral Fidel Velázquez s/n, Col. Obrera, Tampico, Tamaulipas', 22.23560000, -97.85640000, TRUE),
(4, 'Centro Comunitario UAT Tampico', 'Universidad Autónoma de Tamaulipas (UAT Campus Sur)', 'Centro Universitario Tampico-Madero, Col. Universidad, Tampico, Tamaulipas', 22.27890000, -97.83890000, TRUE),
(5, 'Sede Operativa Zona Centro Tampico', 'Cruz Roja Mexicana Delegación Tampico', 'Calle Tamaulipas 201, Zona Centro, Tampico, Tamaulipas', 22.21580000, -97.85720000, TRUE)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), ubicacion = VALUES(ubicacion), latitud = VALUES(latitud), longitud = VALUES(longitud);

-- ----------------------------------------------------------
-- 3. USUARIOS Y ROLES (3 Nuevos para los centros de Tampico)
-- Contraseña genérica de prueba: 'password123'
-- ----------------------------------------------------------
INSERT INTO usuario (id, nombre, email, password, centro_id, institucion_id, rol, activo) VALUES
(6, 'Carlos Méndez (Encargado Laguna)', 'carlos.mendez@amarea.org', 'password123', 3, NULL, 'ENCARGADO', TRUE),
(7, 'Mariana Garza (Voluntaria UAT)', 'mariana.garza@amarea.org', 'password123', 4, NULL, 'VOLUNTARIO', TRUE),
(8, 'Dra. Patricia Silva (Hogar San Pedro)', 'patricia.silva@casahogar.org', 'password123', NULL, 3, 'INSTITUCION', TRUE)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), email = VALUES(email);

-- ----------------------------------------------------------
-- 4. CAMPAÑAS (3 Nuevas Campañas Locales y Regionales)
-- ----------------------------------------------------------
INSERT INTO campanias (id, nombre, fecha_inicio, fecha_fin, descripcion, meta_unidades, activo, lider_id) VALUES
(3, 'Contingencia Inundaciones Río Pánuco', '2026-09-02', NULL, 'Apoyo emergente a colonias ribereñas vulnerables del sur de Tamaulipas.', 8000.00, TRUE, 1),
(4, 'Acopio Solidario Zona Conurbada Sur', '2026-09-05', '2026-11-30', 'Recolección de víveres, agua y botiquines médicos en Tampico y Madero.', 6500.00, TRUE, 5),
(5, 'Campaña Salud y Alivio Huasteca', '2026-09-10', '2026-12-15', 'Kits de curación y desinfectantes para brigadas comunitarias.', 3500.00, TRUE, 1)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), descripcion = VALUES(descripcion);

-- ----------------------------------------------------------
-- 5. CENTROS PARTICIPANTES POR CAMPAÑA (Vinculaciones)
-- ----------------------------------------------------------
INSERT INTO centros_campanias (id_centro, id_campania, activo) VALUES
(3, 1, TRUE), -- Centro Laguna en Campaña Huracán
(4, 1, TRUE), -- Centro UAT en Campaña Huracán
(5, 1, TRUE), -- Centro Cruz Roja en Campaña Huracán
(3, 3, TRUE), -- Centro Laguna en Inundaciones Río Pánuco
(4, 4, TRUE), -- Centro UAT en Zona Conurbada Sur
(5, 5, TRUE)  -- Centro Cruz Roja en Salud Huasteca
ON DUPLICATE KEY UPDATE activo = VALUES(activo);

-- ----------------------------------------------------------
-- 6. CATÁLOGO DE ARTÍCULOS (3 Nuevos Insumos)
-- ----------------------------------------------------------
INSERT INTO articulos (id, nombre, categoria, unidad) VALUES
(11, 'Kit de Primeros Auxilios', 'MEDICAMENTO', 'CAJA'),
(12, 'Papel Higiénico (paquete 4 rollos)', 'LIMPIEZA', 'BOLSA'),
(13, 'Fórmula Infantil en Polvo 400g', 'PERECEDERO', 'PIEZA')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), categoria = VALUES(categoria);

-- ----------------------------------------------------------
-- 7. DONANTES (3 Nuevos Donantes Locales)
-- ----------------------------------------------------------
INSERT INTO donantes (id, nombre, contacto, es_anonimo) VALUES
(3, 'Empacadora del Golfo Tampico', '833-228-9000', FALSE),
(4, 'Sociedad Civil Tampiqueña', '833-219-5566', FALSE),
(5, 'Comerciantes Unidos Zona Centro', NULL, TRUE)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), contacto = VALUES(contacto);

-- ----------------------------------------------------------
-- 8. TRANSFERENCIAS ENTRE CENTROS (3 Registros de prueba)
-- ----------------------------------------------------------
INSERT INTO transferencias (id, centro_origen_id, centro_destino_id, campania_id, articulo_id, cantidad, usuario_id, estado, fecha) VALUES
(1, 1, 3, 1, 1, 50.00, 2, 'COMPLETADA', '2026-09-03 10:00:00'), -- 50L Agua de Campus Central a Laguna Tampico
(2, 2, 4, 1, 2, 30.00, 1, 'COMPLETADA', '2026-09-03 12:30:00'), -- 30kg Arroz de Sede Norte a UAT Tampico
(3, 3, 5, 1, 7, 25.00, 6, 'PENDIENTE', '2026-09-03 15:45:00')   -- 25 Jabones de Laguna a Cruz Roja Tampico
ON DUPLICATE KEY UPDATE estado = VALUES(estado);

-- ----------------------------------------------------------
-- 9. MOVIMIENTOS DE INVENTARIO (3 Nuevos Movimientos)
-- Generan stock positivo inicial en los nuevos centros de Tampico
-- ----------------------------------------------------------
INSERT INTO movimientos (
    id, tipo, centro_id, campania_id, articulo_id, cantidad, fecha, usuario_id, 
    motivo, donante_id, institucion_receptora_id, transferencia_id, estado_aprobacion, entrega_confirmada
) VALUES
-- Recepción de 120 botellas de agua en Centro Laguna donadas por Empacadora del Golfo
(10, 'RECEPCION', 3, 1, 1, 120.00, '2026-09-03 09:00:00', 6, NULL, 3, NULL, NULL, 'APROBADO', FALSE),

-- Recepción de 25 Kits de Primeros Auxilios en Centro UAT donados por Sociedad Civil Tampiqueña
(11, 'RECEPCION', 4, 1, 11, 25.00, '2026-09-03 11:15:00', 7, NULL, 4, NULL, NULL, 'APROBADO', FALSE),

-- Entrega benéfica de 30 botellas de agua desde Centro Laguna hacia Casa Hogar San Pedro
(12, 'ENTREGA', 3, 1, 1, 30.00, '2026-09-03 14:00:00', 6, NULL, NULL, 3, NULL, 'APROBADO', TRUE)
ON DUPLICATE KEY UPDATE cantidad = VALUES(cantidad);
