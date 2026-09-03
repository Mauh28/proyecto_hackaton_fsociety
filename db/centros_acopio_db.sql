-- ==========================================================
-- Sistema de Registro y Coordinación de Centros de Acopio
-- Script de Base de Datos compatible con MySQL 8.0+ y TiDB Cloud
-- ==========================================================

create database if not exists centros_acopio;
use centros_acopio;

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