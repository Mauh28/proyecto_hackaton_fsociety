-- ==========================================================
-- Sistema de Registro y Coordinación de Centros de Acopio
-- Script de Base de Datos compatible con MySQL 8.0+ y TiDB Cloud
-- ==========================================================

-- el nombre ya no es centros de acopio debido a las limitaciones que tiene clever cloud que es donde alojamos la BD

-- create database if not exists centros_acopio;
use b0efvzjhpegivufhzick;

-- ----------------------------------------------------------
-- 1. Tabla: Instituciones Receptoras
-- ----------------------------------------------------------
create table if not exists instituciones_receptoras (
    id int primary key auto_increment,
    nombre varchar(150) not null,
    direccion text not null,
    contacto varchar(100) null
);

-- ----------------------------------------------------------
-- 2. Tabla: Centros de Acopio
-- ----------------------------------------------------------
create table if not exists centros (
    id int primary key auto_increment,
    nombre varchar(150) not null,
    institucion varchar(150) not null,
    ubicacion text not null,
    latitud decimal(10, 8) null,      -- Soporte para mapa interactivo (Innovación 9.3)
    longitud decimal(11, 8) null,     -- Soporte para mapa interactivo (Innovación 9.3)
    activo boolean not null default true
);

-- ----------------------------------------------------------
-- 3. Tabla: Usuarios y Roles (RBAC)
-- ----------------------------------------------------------
create table if not exists usuario (
    id int primary key auto_increment,
    nombre varchar(100) not null,
    email varchar(150) not null unique,
    password varchar(255) not null,
    centro_id int null,
    institucion_id int null,
    rol enum('COORDINADOR', 'ENCARGADO', 'VOLUNTARIO', 'INSTITUCION', 'LIDER') not null,
    activo boolean not null default true,
    constraint fk_usuario_centro foreign key (centro_id) references centros(id),
    constraint fk_usuario_institucion foreign key (institucion_id) references instituciones_receptoras(id)
);

-- ----------------------------------------------------------
-- 4. Tabla: Campañas
-- ----------------------------------------------------------
create table if not exists campanias (
    id int primary key auto_increment,
    nombre varchar(150) not null,
    fecha_inicio date not null,
    fecha_fin date null,                  -- Null si la contingencia sigue activa
    descripcion text,
    meta_unidades decimal(10,2) null default 0, -- Soporte para barra de avance de metas (Innovación 9.4)
    activo boolean not null default true,
    lider_id int null,
    constraint fk_campania_lider foreign key (lider_id) references usuario(id)
);

-- ----------------------------------------------------------
-- 5. Tabla: Centros participantes por Campaña
-- ----------------------------------------------------------
create table if not exists centros_campanias (
    id_centro int not null,
    id_campania int not null,
    activo boolean not null default true,
    primary key (id_centro, id_campania),
    constraint fk_centroscamp_centro foreign key (id_centro) references centros(id),
    constraint fk_centroscamp_campania foreign key (id_campania) references campanias(id)
);

-- ----------------------------------------------------------
-- 6. Tabla: Catálogo de Artículos / Insumos
-- ----------------------------------------------------------
create table if not exists articulos (
    id int primary key auto_increment,
    nombre varchar(100) not null,
    categoria enum('NO_PERECEDERO', 'PERECEDERO', 'ROPA', 'LIMPIEZA', 'MEDICAMENTO', 'OTRO') not null,
    unidad enum('PIEZA', 'KG', 'L', 'BOLSA', 'CAJA') not null
);

-- ----------------------------------------------------------
-- 7. Tabla: Donantes
-- ----------------------------------------------------------
create table if not exists donantes (
    id int primary key auto_increment,
    nombre varchar(150) null,
    contacto varchar(100) null,
    es_anonimo boolean not null default false
);

-- ----------------------------------------------------------
-- 8. Tabla: Transferencias entre Centros
-- ----------------------------------------------------------
create table if not exists transferencias (
    id int primary key auto_increment,
    centro_origen_id int not null,
    centro_destino_id int not null,
    campania_id int not null,
    articulo_id int not null,
    cantidad decimal(10,2) not null,
    usuario_id int not null,              -- Actor que registra/despacha la transferencia
    estado enum('PENDIENTE', 'COMPLETADA', 'CANCELADA') not null default 'PENDIENTE',
    fecha timestamp not null default current_timestamp,
    constraint chk_transf_centros check (centro_origen_id <> centro_destino_id),
    constraint chk_transf_cantidad check (cantidad > 0),
    constraint fk_transf_origen foreign key (centro_origen_id) references centros(id),
    constraint fk_transf_destino foreign key (centro_destino_id) references centros(id),
    constraint fk_transf_campania foreign key (campania_id) references campanias(id),
    constraint fk_transf_articulo foreign key (articulo_id) references articulos(id),
    constraint fk_transf_usuario foreign key (usuario_id) references usuario(id)
);

-- ----------------------------------------------------------
-- 9. Tabla: Movimientos de Inventario (Libro Mayor Inmutable)
-- ----------------------------------------------------------
create table if not exists movimientos (
    id int primary key auto_increment,
    tipo enum(
        'RECEPCION', 
        'ENTREGA', 
        'MERMA', 
        'TRANSFERENCIA_SALIDA', 
        'TRANSFERENCIA_ENTRADA', 
        'AJUSTE_POSITIVO', 
        'AJUSTE_NEGATIVO'
    ) not null,
    centro_id int not null,
    campania_id int not null,
    articulo_id int not null,
    cantidad decimal(10,2) not null,
    fecha timestamp not null default current_timestamp,
    usuario_id int not null, -- Actor obligatorio de auditoría
    
    -- Motivo obligatorio en Merma y Ajuste (sin 'ñ' para total compatibilidad Java Enum)
    motivo enum('CADUCIDAD', 'DANO', 'PERDIDA', 'CORRECCION_CONTEO', 'ERROR_CAPTURA', 'OTRO') null,
    motivo_detalle text null,
    
    -- Referencias según el flujo
    donante_id int null,
    institucion_receptora_id int null,
    transferencia_id int null,
    
    -- Soporte para aprobación de merma por Coordinador (Innovación 9.2)
    estado_aprobacion enum('PENDIENTE', 'APROBADO', 'RECHAZADO') not null default 'APROBADO',
    aprobado_por_id int null,
    
    -- Confirmación de recepción externa por Institución Receptora
    entrega_confirmada boolean not null default false,
    fecha_confirmacion timestamp null,
    
    constraint chk_mov_cantidad check (cantidad > 0),
    constraint fk_mov_centro foreign key (centro_id) references centros(id),
    constraint fk_mov_campania foreign key (campania_id) references campanias(id),
    constraint fk_mov_articulo foreign key (articulo_id) references articulos(id),
    constraint fk_mov_usuario foreign key (usuario_id) references usuario(id),
    constraint fk_mov_donante foreign key (donante_id) references donantes(id),
    constraint fk_mov_institucion foreign key (institucion_receptora_id) references instituciones_receptoras(id),
    constraint fk_mov_transferencia foreign key (transferencia_id) references transferencias(id),
    constraint fk_mov_aprobador foreign key (aprobado_por_id) references usuario(id)
);

-- ----------------------------------------------------------
-- 10. Índices para acelerar cálculo de Stock y Dashboards
-- ----------------------------------------------------------
create index idx_mov_centro_camp_art on movimientos (centro_id, campania_id, articulo_id);
create index idx_mov_fecha on movimientos (fecha);

-- ----------------------------------------------------------
-- 11. Vista Oficial de Stock Actual (Fórmula del Dominio 5.2)
-- ----------------------------------------------------------
create or replace view v_stock_actual as
select 
    m.centro_id,
    m.campania_id,
    m.articulo_id,
    sum(
        case 
            when m.tipo in ('RECEPCION', 'TRANSFERENCIA_ENTRADA', 'AJUSTE_POSITIVO') then m.cantidad
            when m.tipo in ('ENTREGA', 'MERMA', 'TRANSFERENCIA_SALIDA', 'AJUSTE_NEGATIVO') then -m.cantidad
            else 0
        end
    ) as stock_disponible
from movimientos m
where m.estado_aprobacion = 'APROBADO'
group by m.centro_id, m.campania_id, m.articulo_id;

-- ==========================================================
-- PROCEDIMIENTOS ALMACENADOS - FASE 1: CORE DE INVENTARIO
-- ==========================================================

-- ----------------------------------------------------------
-- SP 1: Registro de Recepción de Donación
-- ----------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_registrar_recepcion_donacion;
DELIMITER //
CREATE PROCEDURE sp_registrar_recepcion_donacion(
    IN p_centro_id INT,
    IN p_campania_id INT,
    IN p_articulo_id INT,
    IN p_cantidad DECIMAL(10,2),
    IN p_usuario_id INT,
    IN p_es_anonimo BOOLEAN,
    IN p_donante_nombre VARCHAR(150),
    IN p_donante_contacto VARCHAR(100)
)
BEGIN
    DECLARE v_donante_id INT DEFAULT NULL;
    DECLARE v_centro_activo BOOLEAN;
    DECLARE v_campania_activa BOOLEAN;
    DECLARE v_asociacion_activa BOOLEAN;
    DECLARE v_movimiento_id INT;
    DECLARE v_nuevo_stock DECIMAL(10,2);

    -- 1. Validar cantidad mayor a cero
    IF p_cantidad <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: La cantidad recibida debe ser mayor a cero.';
    END IF;

    -- 2. Validar que el centro exista y esté activo
    SELECT activo INTO v_centro_activo FROM centros WHERE id = p_centro_id;
    IF v_centro_activo IS NULL OR v_centro_activo = FALSE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El centro de acopio no existe o está inactivo.';
    END IF;

    -- 3. Validar que la campaña exista y esté activa
    SELECT activo INTO v_campania_activa FROM campanias WHERE id = p_campania_id;
    IF v_campania_activa IS NULL OR v_campania_activa = FALSE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: La campaña no existe o está inactiva.';
    END IF;

    -- 4. Validar que el centro participe activamente en la campaña
    SELECT activo INTO v_asociacion_activa 
    FROM centros_campanias 
    WHERE id_centro = p_centro_id AND id_campania = p_campania_id;
    
    IF v_asociacion_activa IS NULL OR v_asociacion_activa = FALSE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El centro no está habilitado para recibir insumos en esta campaña.';
    END IF;

    -- 5. Manejo del Donante (Anónimo o Registrado)
    IF p_es_anonimo = FALSE AND p_donante_nombre IS NOT NULL AND TRIM(p_donante_nombre) <> '' THEN
        INSERT INTO donantes (nombre, contacto, es_anonimo)
        VALUES (p_donante_nombre, p_donante_contacto, FALSE);
        SET v_donante_id = LAST_INSERT_ID();
    ELSE
        SET v_donante_id = NULL;
    END IF;

    -- 6. Insertar movimiento inmutable de recepción
    INSERT INTO movimientos (
        tipo, centro_id, campania_id, articulo_id, cantidad, 
        usuario_id, donante_id, estado_aprobacion
    ) VALUES (
        'RECEPCION', p_centro_id, p_campania_id, p_articulo_id, p_cantidad, 
        p_usuario_id, v_donante_id, 'APROBADO'
    );
    SET v_movimiento_id = LAST_INSERT_ID();

    -- 7. Consultar el nuevo stock disponible resultante
    SELECT COALESCE(stock_disponible, 0) INTO v_nuevo_stock
    FROM v_stock_actual
    WHERE centro_id = p_centro_id AND campania_id = p_campania_id AND articulo_id = p_articulo_id;

    -- 8. Devolver resultado
    SELECT 
        v_movimiento_id AS movimiento_id,
        'RECEPCION' AS tipo,
        p_cantidad AS cantidad_recibida,
        v_nuevo_stock AS stock_actual,
        'Donación registrada exitosamente.' AS mensaje;
END //
DELIMITER ;

-- ----------------------------------------------------------
-- SP 2: Registro de Entrega / Canalización hacia Institución
-- ----------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_registrar_entrega;
DELIMITER //
CREATE PROCEDURE sp_registrar_entrega(
    IN p_centro_id INT,
    IN p_campania_id INT,
    IN p_articulo_id INT,
    IN p_cantidad DECIMAL(10,2),
    IN p_institucion_receptora_id INT,
    IN p_usuario_id INT
)
BEGIN
    DECLARE v_stock_actual DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_movimiento_id INT;

    -- 1. Validar cantidad positiva
    IF p_cantidad <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: La cantidad a entregar debe ser mayor a cero.';
    END IF;

    -- 2. Validar existencia de la institución receptora
    IF NOT EXISTS (SELECT 1 FROM instituciones_receptoras WHERE id = p_institucion_receptora_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: La institución receptora especificada no existe.';
    END IF;

    -- 3. VALIDACIÓN CRÍTICA: Prohibido Stock Negativo (Sección 5.3)
    SELECT COALESCE(stock_disponible, 0) INTO v_stock_actual
    FROM v_stock_actual
    WHERE centro_id = p_centro_id AND campania_id = p_campania_id AND articulo_id = p_articulo_id;

    IF v_stock_actual < p_cantidad THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: Stock insuficiente. No se puede realizar una entrega que cause stock negativo.';
    END IF;

    -- 4. Registrar movimiento de entrega
    INSERT INTO movimientos (
        tipo, centro_id, campania_id, articulo_id, cantidad, 
        usuario_id, institucion_receptora_id, estado_aprobacion, entrega_confirmada
    ) VALUES (
        'ENTREGA', p_centro_id, p_campania_id, p_articulo_id, p_cantidad, 
        p_usuario_id, p_institucion_receptora_id, 'APROBADO', FALSE
    );
    SET v_movimiento_id = LAST_INSERT_ID();

    -- 5. Devolver resultado
    SELECT 
        v_movimiento_id AS movimiento_id,
        'ENTREGA' AS tipo,
        p_cantidad AS cantidad_entregada,
        (v_stock_actual - p_cantidad) AS stock_restante,
        'Entrega canalizada exitosamente. Pendiente de confirmación por la institución.' AS mensaje;
END //
DELIMITER ;

-- ----------------------------------------------------------
-- SP 3: Registro de Merma con Motivo Obligatorio
-- ----------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_registrar_merma;
DELIMITER //
CREATE PROCEDURE sp_registrar_merma(
    IN p_centro_id INT,
    IN p_campania_id INT,
    IN p_articulo_id INT,
    IN p_cantidad DECIMAL(10,2),
    IN p_motivo VARCHAR(50),
    IN p_motivo_detalle TEXT,
    IN p_usuario_id INT
)
BEGIN
    DECLARE v_stock_actual DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_movimiento_id INT;

    -- 1. Validar cantidad positiva
    IF p_cantidad <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: La cantidad de merma debe ser mayor a cero.';
    END IF;

    -- 2. Validar motivo obligatorio (CADUCIDAD, DANO, PERDIDA, OTRO)
    IF p_motivo IS NULL OR p_motivo NOT IN ('CADUCIDAD', 'DANO', 'PERDIDA', 'OTRO') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El motivo de merma es obligatorio y debe ser CADUCIDAD, DANO, PERDIDA u OTRO.';
    END IF;

    -- 3. Validar existencias suficientes para descontar la merma
    SELECT COALESCE(stock_disponible, 0) INTO v_stock_actual
    FROM v_stock_actual
    WHERE centro_id = p_centro_id AND campania_id = p_campania_id AND articulo_id = p_articulo_id;

    IF v_stock_actual < p_cantidad THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: No hay existencias suficientes en inventario para asentar esta merma.';
    END IF;

    -- 4. Registrar movimiento de merma
    INSERT INTO movimientos (
        tipo, centro_id, campania_id, articulo_id, cantidad, 
        usuario_id, motivo, motivo_detalle, estado_aprobacion
    ) VALUES (
        'MERMA', p_centro_id, p_campania_id, p_articulo_id, p_cantidad, 
        p_usuario_id, p_motivo, p_motivo_detalle, 'APROBADO'
    );
    SET v_movimiento_id = LAST_INSERT_ID();

    -- 5. Devolver resultado
    SELECT 
        v_movimiento_id AS movimiento_id,
        'MERMA' AS tipo,
        p_motivo AS motivo,
        p_cantidad AS cantidad_merma,
        (v_stock_actual - p_cantidad) AS stock_restante,
        'Merma asentada exitosamente en el historial.' AS mensaje;
END //
DELIMITER ;

-- ----------------------------------------------------------
-- SP 4: Registro Atómico de Transferencia entre Centros
-- ----------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_registrar_transferencia_centros;
DELIMITER //
CREATE PROCEDURE sp_registrar_transferencia_centros(
    IN p_centro_origen_id INT,
    IN p_centro_destino_id INT,
    IN p_campania_id INT,
    IN p_articulo_id INT,
    IN p_cantidad DECIMAL(10,2),
    IN p_usuario_id INT
)
BEGIN
    DECLARE v_stock_origen DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_transferencia_id INT;

    -- 1. Validar que origen y destino sean distintos
    IF p_centro_origen_id = p_centro_destino_id THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El centro de origen y destino no pueden ser el mismo.';
    END IF;

    -- 2. Validar cantidad positiva
    IF p_cantidad <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: La cantidad a transferir debe ser mayor a cero.';
    END IF;

    -- 3. Validar que ambos centros participen activamente en la campaña
    IF NOT EXISTS (SELECT 1 FROM centros_campanias WHERE id_centro = p_centro_origen_id AND id_campania = p_campania_id AND activo = TRUE) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El centro de origen no está habilitado en esta campaña.';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM centros_campanias WHERE id_centro = p_centro_destino_id AND id_campania = p_campania_id AND activo = TRUE) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El centro de destino no está habilitado en esta campaña.';
    END IF;

    -- 4. Validar existencias suficientes en el centro de origen
    SELECT COALESCE(stock_disponible, 0) INTO v_stock_origen
    FROM v_stock_actual
    WHERE centro_id = p_centro_origen_id AND campania_id = p_campania_id AND articulo_id = p_articulo_id;

    IF v_stock_origen < p_cantidad THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: Stock insuficiente en el centro de origen para realizar la transferencia.';
    END IF;

    -- 5. Operación Atómica (Salida en Origen + Entrada en Destino)
    START TRANSACTION;
        -- Insertar registro maestro de transferencia
        INSERT INTO transferencias (
            centro_origen_id, centro_destino_id, campania_id, 
            articulo_id, cantidad, usuario_id, estado
        ) VALUES (
            p_centro_origen_id, p_centro_destino_id, p_campania_id, 
            p_articulo_id, p_cantidad, p_usuario_id, 'COMPLETADA'
        );
        SET v_transferencia_id = LAST_INSERT_ID();

        -- Movimiento de salida en el centro origen
        INSERT INTO movimientos (
            tipo, centro_id, campania_id, articulo_id, cantidad, 
            usuario_id, transferencia_id, estado_aprobacion
        ) VALUES (
            'TRANSFERENCIA_SALIDA', p_centro_origen_id, p_campania_id, p_articulo_id, p_cantidad, 
            p_usuario_id, v_transferencia_id, 'APROBADO'
        );

        -- Movimiento de entrada en el centro destino
        INSERT INTO movimientos (
            tipo, centro_id, campania_id, articulo_id, cantidad, 
            usuario_id, transferencia_id, estado_aprobacion
        ) VALUES (
            'TRANSFERENCIA_ENTRADA', p_centro_destino_id, p_campania_id, p_articulo_id, p_cantidad, 
            p_usuario_id, v_transferencia_id, 'APROBADO'
        );
    COMMIT;

    -- 6. Devolver confirmación
    SELECT 
        v_transferencia_id AS transferencia_id,
        p_centro_origen_id AS centro_origen,
        p_centro_destino_id AS centro_destino,
        p_cantidad AS cantidad_transferida,
        (v_stock_origen - p_cantidad) AS stock_remanente_origen,
        'Transferencia entre centros completada atómicamente con éxito.' AS mensaje;
END //
DELIMITER ;

-- ----------------------------------------------------------
-- SP 5: Registro de Ajuste Manual de Stock con Motivo
-- ----------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_registrar_ajuste_stock;
DELIMITER //
CREATE PROCEDURE sp_registrar_ajuste_stock(
    IN p_centro_id INT,
    IN p_campania_id INT,
    IN p_articulo_id INT,
    IN p_cantidad DECIMAL(10,2),
    IN p_tipo_ajuste VARCHAR(20),
    IN p_motivo VARCHAR(50),
    IN p_motivo_detalle TEXT,
    IN p_usuario_id INT
)
BEGIN
    DECLARE v_stock_actual DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_movimiento_id INT;
    DECLARE v_nuevo_stock DECIMAL(10,2);

    -- 1. Validar cantidad positiva
    IF p_cantidad <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: La cantidad de ajuste debe ser mayor a cero.';
    END IF;

    -- 2. Validar tipo de ajuste
    IF p_tipo_ajuste NOT IN ('AJUSTE_POSITIVO', 'AJUSTE_NEGATIVO') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El tipo de ajuste debe ser AJUSTE_POSITIVO o AJUSTE_NEGATIVO.';
    END IF;

    -- 3. Validar motivo obligatorio (CORRECCION_CONTEO, ERROR_CAPTURA, OTRO, etc.)
    IF p_motivo IS NULL OR p_motivo NOT IN ('CORRECCION_CONTEO', 'ERROR_CAPTURA', 'OTRO', 'DANO', 'PERDIDA') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El motivo de ajuste es obligatorio (CORRECCION_CONTEO, ERROR_CAPTURA, OTRO).';
    END IF;

    -- 4. Validar existencias si el ajuste es negativo
    SELECT COALESCE(stock_disponible, 0) INTO v_stock_actual
    FROM v_stock_actual
    WHERE centro_id = p_centro_id AND campania_id = p_campania_id AND articulo_id = p_articulo_id;

    IF p_tipo_ajuste = 'AJUSTE_NEGATIVO' AND v_stock_actual < p_cantidad THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El ajuste negativo excede las existencias registradas en almacén.';
    END IF;

    -- 5. Registrar movimiento de ajuste
    INSERT INTO movimientos (
        tipo, centro_id, campania_id, articulo_id, cantidad, 
        usuario_id, motivo, motivo_detalle, estado_aprobacion
    ) VALUES (
        p_tipo_ajuste, p_centro_id, p_campania_id, p_articulo_id, p_cantidad, 
        p_usuario_id, p_motivo, p_motivo_detalle, 'APROBADO'
    );
    SET v_movimiento_id = LAST_INSERT_ID();

    -- 6. Consultar nuevo stock disponible
    SELECT COALESCE(stock_disponible, 0) INTO v_nuevo_stock
    FROM v_stock_actual
    WHERE centro_id = p_centro_id AND campania_id = p_campania_id AND articulo_id = p_articulo_id;

    -- 7. Devolver resultado
    SELECT 
        v_movimiento_id AS movimiento_id,
        p_tipo_ajuste AS tipo_ajuste,
        p_motivo AS motivo,
        p_cantidad AS cantidad_ajustada,
        v_nuevo_stock AS stock_oficial_actualizado,
        'Ajuste de inventario registrado y auditado exitosamente.' AS mensaje;
END //
DELIMITER ;

-- ==========================================================
-- 12. DATOS SEMILLA (SEEDS / FIXTURES) PARA LA DEMO
-- ==========================================================

-- Instituciones Receptoras de Prueba
insert into instituciones_receptoras (id, nombre, direccion, contacto) values
(1, 'Albergue Comunitario Esperanza', 'Av. Reforma 123, Col. Juárez', '555-123-4567'),
(2, 'Comedor Solidario San José', 'Calle Hidalgo 45, Centro Histórico', '555-987-6543')
on duplicate key update nombre = values(nombre);

-- Centros de Acopio de Prueba (con coordenadas para el mapa)
insert into centros (id, nombre, institucion, ubicacion, latitud, longitud, activo) values
(1, 'Campus Central - Explanada', 'Universidad Nacional', 'Av. Insurgentes Sur 3000, Coyoacán', 19.33230000, -99.18650000, true),
(2, 'Sede Comunitaria Norte', 'Cruz Roja Delegación Norte', 'Calzada Vallejo 789, Gustavo A. Madero', 19.48520000, -99.16230000, true)
on duplicate key update nombre = values(nombre);

-- Usuarios de Prueba (Uno por cada rol del sistema RBAC)
-- Contraseña temporal: 'password123' (actualizar con BCrypt si se activa Spring Security)
insert into usuario (id, nombre, email, password, centro_id, institucion_id, rol, activo) values
(1, 'Admin Coordinadora', 'coordinador@hackaton.org', 'password123', null, null, 'COORDINADOR', true),
(2, 'Encargado Campus Central', 'encargado.central@hackaton.org', 'password123', 1, null, 'ENCARGADO', true),
(3, 'Voluntario Campus Central', 'voluntario.central@hackaton.org', 'password123', 1, null, 'VOLUNTARIO', true),
(4, 'Contacto Albergue Esperanza', 'receptor@albergue.org', 'password123', null, 1, 'INSTITUCION', true),
(5, 'Líder de Campaña Emergencia', 'lider.emergencia@hackaton.org', 'password123', null, null, 'LIDER', true)
on duplicate key update email = values(email);

-- Campañas de Prueba (Con meta para barra de progreso)
insert into campanias (id, nombre, fecha_inicio, fecha_fin, descripcion, meta_unidades, activo, lider_id) values
(1, 'Plan de Contingencia Huracán 2026', '2026-09-01', null, 'Recolección de víveres e insumos para comunidades costeras afectadas.', 5000.00, true, 5),
(2, 'Campaña Invernal Abriga a un Hermano', '2026-10-15', '2026-12-31', 'Acopio de ropa abrigadora y cobijas.', 2000.00, true, 5)
on duplicate key update nombre = values(nombre);

-- Centros asignados a la campaña activa
insert into centros_campanias (id_centro, id_campania, activo) values
(1, 1, true),
(2, 1, true)
on duplicate key update activo = values(activo);

-- Catálogo de Insumos Estándar (cubriendo categorías y unidades)
insert into articulos (id, nombre, categoria, unidad) values
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
on duplicate key update nombre = values(nombre);

-- Donantes Iniciales
insert into donantes (id, nombre, contacto, es_anonimo) values
(1, 'Ciudadano Solidario', '555-444-3322', false),
(2, 'Donante Anónimo', null, true)
on duplicate key update id = values(id);

-- Movimientos Semilla Iniciales (Para disponer de inventario positivo en la demo)
insert into movimientos (
    id, tipo, centro_id, campania_id, articulo_id, cantidad, usuario_id, 
    motivo, donante_id, institucion_receptora_id, transferencia_id, estado_aprobacion, entrega_confirmada
) values
-- Campus Central: Donaciones recibidas
(1, 'RECEPCION', 1, 1, 1, 150.00, 2, null, 1, null, null, 'APROBADO', false), -- 150 Agua
(2, 'RECEPCION', 1, 1, 2, 80.00, 2, null, 1, null, null, 'APROBADO', false),  -- 80 Kg Arroz
(3, 'RECEPCION', 1, 1, 4, 120.00, 3, null, 2, null, null, 'APROBADO', false), -- 120 Latas Atún
(4, 'RECEPCION', 1, 1, 5, 40.00, 2, null, 1, null, null, 'APROBADO', false),  -- 40 Cajas Paracetamol
-- Sede Norte: Donaciones recibidas
(5, 'RECEPCION', 2, 1, 1, 100.00, 1, null, 2, null, null, 'APROBADO', false), -- 100 Agua
(6, 'RECEPCION', 2, 1, 3, 60.00, 1, null, 1, null, null, 'APROBADO', false)   -- 60 Kg Frijol
on duplicate key update id = values(id);