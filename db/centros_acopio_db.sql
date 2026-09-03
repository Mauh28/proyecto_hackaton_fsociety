drop database if exists centros_acopio;
create database centros_acopio;
use centros_acopio;

create table instituciones_receptoras (
    id int primary key auto_increment,
    nombre varchar(150) not null,
    direccion text not null
);

create table centros (
    id int primary key auto_increment,
    nombre varchar(150) not null,
    institucion varchar(150) not null,
    ubicacion text not null,
    activo boolean not null default true
);

create table usuario (
    id int primary key auto_increment,
    nombre varchar(100) not null,
    email varchar(150) not null unique,
    password varchar(255) not null,
    centro_id int null,
    institucion_id int null,
    rol enum('Coordinador', 'Encargado', 'Voluntario', 'Institucion', 'Lider') not null,
    constraint fk_usuario_centro foreign key (centro_id) references centros(id),
    constraint fk_usuario_institucion foreign key (institucion_id) references instituciones_receptoras(id)
);

create table campanias (
    id int primary key auto_increment,
    nombre varchar(150) not null,
    fecha_inicio date not null,
    fecha_fin date not null,
    descripcion text,
    activo boolean not null default true,
    lider_id int null,
    constraint fk_campania_lider foreign key (lider_id) references usuario(id)
);

create table centros_campanias (
    id_centro int not null,
    id_campania int not null,
    activo boolean not null default true,
    primary key (id_centro, id_campania),
    constraint fk_centroscamp_centro foreign key (id_centro) references centros(id),
    constraint fk_centroscamp_campania foreign key (id_campania) references campanias(id)
);

create table articulos (
    id int primary key auto_increment,
    nombre varchar(100) not null,
    categoria enum('no perecedero', 'perecedero', 'ropa', 'limpieza', 'medicamento', 'otro') not null,
    unidad enum('pieza', 'kg', 'l', 'bolsa', 'caja') not null
);

create table donantes (
    id int primary key auto_increment,
    nombre varchar(150)
);

create table transferencias (
    id int primary key auto_increment,
    centro_origen_id int not null,
    centro_destino_id int not null,
    campania_id int not null,
    articulo_id int not null,
    cantidad decimal(10,2) not null,
    estado enum('pendiente', 'completada', 'cancelada') not null default 'pendiente',
    fecha timestamp not null default current_timestamp,
    constraint fk_transf_origen foreign key (centro_origen_id) references centros(id),
    constraint fk_transf_destino foreign key (centro_destino_id) references centros(id),
    constraint fk_transf_campania foreign key (campania_id) references campanias(id),
    constraint fk_transf_articulo foreign key (articulo_id) references articulos(id)
);

create table movimientos (
    id int primary key auto_increment,
    tipo enum(
        'recepcion', 
        'entrega', 
        'merma', 
        'transferencia_salida', 
        'transferencia_entrada', 
        'ajuste_positivo', 
        'ajuste_negativo'
    ) not null,
    centro_id int not null,
    campania_id int not null,
    articulo_id int not null,
    cantidad decimal(10,2) not null,
    fecha timestamp not null default current_timestamp,
    usuario_id int not null, -- actor obligatorio (quien captura)
    
    -- motivo obligatorio en merma (caducidad, daño, perdida) y en ajuste (error, conteo)
    motivo enum('caducidad', 'daño', 'perdida', 'correccion_conteo', 'error_captura', 'otro') null,
    motivo_detalle text null,
    
    -- referencias segun el tipo de flujo
    donante_id int null,
    institucion_receptora_id int null,
    transferencia_id int null,
    
    -- requerimiento: confirmacion de entrega por la institucion receptora
    entrega_confirmada boolean not null default false,
    fecha_confirmacion timestamp null,
    
    constraint fk_mov_centro foreign key (centro_id) references centros(id),
    constraint fk_mov_campania foreign key (campania_id) references campanias(id),
    constraint fk_mov_articulo foreign key (articulo_id) references articulos(id),
    constraint fk_mov_usuario foreign key (usuario_id) references usuario(id),
    constraint fk_mov_donante foreign key (donante_id) references donantes(id),
    constraint fk_mov_institucion foreign key (institucion_receptora_id) references instituciones_receptoras(id),
    constraint fk_mov_transferencia foreign key (transferencia_id) references transferencias(id)
);