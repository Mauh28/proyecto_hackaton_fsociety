# Guía de Especificación de Operaciones, Parámetros y Validaciones del Backend

> **Proyecto:** Sistema de Registro y Coordinación de Centros de Acopio (`<in>Hack`)  
> **Equipo:** *fsociety*  
> **Persistencia Activa:** **Spring Boot 3 + Spring Data JPA** en **TiDB Cloud Serverless** (MySQL compatible)  
> **Rol de este Documento:** Especificación Funcional de Parámetros de Entrada, DTOs y Reglas de Negocio del Backend  

---

> [!IMPORTANT]
> **DIRECTIVA DE ARQUITECTURA: PERSISTENCIA VÍA SPRING DATA JPA**  
> En este proyecto **NO se utilizan procedimientos almacenados (`CALL sp_...`) en la base de datos**, debido a que la base de datos de producción opera sobre **TiDB Cloud Serverless**.  
> Toda la lógica de persistencia, cálculo de stock y transacciones se ejecuta a nivel de **Capa de Servicios y Repositorios JPA de Spring Boot**.  
> 
> **¿Cómo utilizar esta guía?**  
> Este documento funge como el **Contrato Maestro de Especificación Funcional**:
> 1. Define con precisión los **parámetros de entrada**, tipos de datos (`INT`, `VARCHAR`, `DECIMAL`) y campos obligatorios/opcionales que deben recibir los Controllers/Services.
> 2. Detalla las **reglas de validación de negocio** indispensables (ej. verificación de stock disponible para egresos, validación de cuenta activa, asignación obligatoria de motivos en mermas y ajustes).
> 3. Especifica los **resultados esperados y estructuras de retorno** para los DTOs y respuestas JSON de la API.

---

## 🗺️ Mapa de Navegación: Procedimientos vs Módulos del Sistema

```
[PLATAFORMA WEB]
│
├── 🔑 MÓDULO 1: AUTENTICACIÓN Y SESIÓN (Login)
│   └── sp_autenticar_usuario ───────────────► Pantalla: /login
│
├── 👥 MÓDULO 2: ADMINISTRACIÓN DE USUARIOS (Panel Coordinador)
│   ├── sp_crear_usuario ────────────────────► Pantalla: /admin/usuarios/nuevo
│   └── sp_cambiar_estado_usuario ───────────► Pantalla: /admin/usuarios (Activar/Desactivar)
│
├── 🏛️ MÓDULO 3: GESTIÓN DE CENTROS Y CAMPAÑAS (Panel Coordinador)
│   ├── sp_crear_centro_acopio ──────────────► Pantalla: /admin/centros/nuevo
│   ├── sp_editar_centro_acopio ─────────────► Pantalla: /admin/centros/editar/{id}
│   ├── sp_cambiar_estado_centro ────────────► Pantalla: /admin/centros (Toggle activo)
│   ├── sp_crear_campania ───────────────────► Pantalla: /admin/campanias/nueva
│   ├── sp_editar_campania ──────────────────► Pantalla: /admin/campanias/editar/{id}
│   ├── sp_cambiar_estado_campania ──────────► Pantalla: /admin/campanias (Cierre oficial)
│   └── sp_asociar_centro_campania ──────────► Pantalla: /admin/campanias/{id}/centros
│
├── 📋 MÓDULO 4: CATÁLOGOS Y SELECTORES DINÁMICOS (Dropdowns / Selects)
│   ├── sp_crear_articulo ───────────────────► Pantalla: /admin/articulos/nuevo
│   ├── sp_listar_articulos ─────────────────► Carga el <select> de insumos en formularios
│   ├── sp_listar_instituciones_receptoras ──► Carga el <select> de destino en entregas
│   ├── sp_listar_campanias_activas_centro ──► Carga el <select> de campañas según el centro
│   └── sp_listar_centros_destino_transferencia ► Carga el <select> de centros destino
│
├── 📦 MÓDULO 5: OPERACIONES DE INVENTARIO EN CAMPO (Encargados y Voluntarios)
│   ├── sp_registrar_recepcion_donacion ─────► Pantalla: /inventario/recepcion
│   ├── sp_registrar_entrega ────────────────► Pantalla: /inventario/entrega
│   ├── sp_registrar_merma ──────────────────► Pantalla: /inventario/merma
│   ├── sp_registrar_transferencia_centros ──► Pantalla: /inventario/transferencia
│   └── sp_registrar_ajuste_stock ───────────► Pantalla: /inventario/ajuste
│
├── 🏥 MÓDULO 6: PORTAL INSTITUCIÓN RECEPTORA (Consulta Externa)
│   ├── sp_consultar_entregas_institucion ──► Pantalla: /institucion/entregas
│   └── sp_confirmar_entrega_recibida ──────► Botón: Confirmar Recepción
│
├── 📊 MÓDULO 7: AUDITORÍA, KÁRDEX Y EXISTENCIAS
│   ├── sp_obtener_stock_centro ────────────► Pantalla: /inventario/stock (En vivo)
│   └── sp_consultar_historial_movimientos ─► Pantalla: /auditoria/historial (Kárdex/Exportación)
│
├── 📈 MÓDULO 8: DASHBOARDS Y ANALÍTICA EJECUTIVA
│   ├── sp_dashboard_coordinador_global ─────► Pantalla: /dashboard/global (KPIs macro)
│   ├── sp_dashboard_comparativa_centros ────► Pantalla: /dashboard/comparativa (Balance)
│   ├── sp_dashboard_articulos_mas_donados ──► Pantalla: /dashboard/top-insumos (Gráficas)
│   ├── sp_dashboard_encargado_centro ───────► Pantalla: /dashboard/centro (KPIs de sede)
│   └── sp_dashboard_lider_campania ─────────► Pantalla: /dashboard/campania (Avance metas)
│
└── 🌟 MÓDULO 9: INNOVACIÓN Y DIFERENCIADORES (<in>Hack)
    ├── sp_aprobar_merma ────────────────────► Pantalla: /admin/mermas/revision (Aprobación)
    └── sp_obtener_centros_mapa ─────────────► Pantalla: /mapa (Pines Leaflet/Google Maps)
```

---

## 1. Módulo de Autenticación y Contexto de Sesión

### `sp_autenticar_usuario`
* **Módulo / Pantalla:** Login (`GET/POST /login`).
* **Quién lo invoca:** El filtro de autenticación de Spring Security o el controlador de login.
* **Propósito:** Busca al usuario por email, valida que esté activo y devuelve su información de perfil y el hash de su contraseña.
* **Firma:** `sp_autenticar_usuario(p_email VARCHAR(150))`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_autenticar_usuario('encargado.central@hackaton.org');
  ```
* **Retorna:**
  `usuario_id`, `nombre`, `email`, `password_hash`, `rol`, `centro_id`, `centro_nombre`, `institucion_id`, `institucion_nombre`, `activo`.
* **Manejo de Errores:**
  * Si el email no existe: `Error: Credenciales inválidas. Usuario no registrado.`
  * Si `activo = false`: `Error: La cuenta de usuario se encuentra deshabilitada.`

---

## 2. Módulo de Administración de Usuarios (RBAC)

### `sp_crear_usuario`
* **Módulo / Pantalla:** Coordinador > Gestión de Usuarios > Nuevo Usuario (`POST /admin/usuarios`).
* **Quién lo invoca:** Coordinador General.
* **Propósito:** Da de alta a nuevos Encargados, Voluntarios o usuarios de Institución validando consistencia de asignación.
* **Firma:** `sp_crear_usuario(p_nombre, p_email, p_password, p_centro_id, p_institucion_id, p_rol)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_crear_usuario(
      'María López', 
      'maria.voluntaria@hackaton.org', 
      'password123', -- O hash BCrypt
      1,             -- centro_id (obligatorio para ENCARGADO / VOLUNTARIO)
      NULL,          -- institucion_id (NULL si no es INSTITUCION)
      'VOLUNTARIO'
  );
  ```
* **Retorna:** `usuario_id`, `nombre`, `email`, `rol`, `mensaje`.

### `sp_cambiar_estado_usuario`
* **Módulo / Pantalla:** Coordinador > Gestión de Usuarios > Botón Activar/Desactivar.
* **Firma:** `sp_cambiar_estado_usuario(p_usuario_id INT, p_nuevo_estado BOOLEAN)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_cambiar_estado_usuario(3, false); -- Deshabilita acceso
  ```

---

## 3. Módulo de Gestión de Centros de Acopio y Campañas

### `sp_crear_centro_acopio`
* **Módulo / Pantalla:** Coordinador > Sedes > Nuevo Centro (`POST /admin/centros`).
* **Firma:** `sp_crear_centro_acopio(p_nombre, p_institucion, p_ubicacion, p_latitud, p_longitud)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_crear_centro_acopio(
      'Sede Oriente - Iztapalapa',
      'DIF Municipal',
      'Calzada Ermita Iztapalapa 200',
      19.35820000,
      -99.09240000
  );
  ```
* **Retorna:** `centro_id`, `nombre`, `mensaje`.

### `sp_editar_centro_acopio`
* **Módulo / Pantalla:** Coordinador > Sedes > Editar Centro (`PUT /admin/centros/{id}`).
* **Firma:** `sp_editar_centro_acopio(p_centro_id, p_nombre, p_institucion, p_ubicacion, p_latitud, p_longitud)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_editar_centro_acopio(
      3, 
      'Sede Oriente - Macroplaza', 
      'DIF Iztapalapa', 
      'Aldama 63, San Lucas', 
      19.35900000, 
      -99.09100000
  );
  ```

### `sp_cambiar_estado_centro`
* **Módulo / Pantalla:** Coordinador > Sedes > Activar/Inactivar Centro.
* **Firma:** `sp_cambiar_estado_centro(p_centro_id INT, p_nuevo_estado BOOLEAN)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_cambiar_estado_centro(2, false); -- Inactiva el centro
  ```

### `sp_crear_campania`
* **Módulo / Pantalla:** Coordinador > Campañas > Nueva Campaña (`POST /admin/campanias`).
* **Firma:** `sp_crear_campania(p_nombre, p_descripcion, p_fecha_inicio, p_fecha_fin, p_meta_unidades, p_lider_id)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_crear_campania(
      'Campaña Frío Sin Hambre 2026',
      'Acopio urgente de cobijas, ropa térmica y alimentos no perecederos.',
      '2026-11-01',
      '2026-12-31',
      3000.00, -- Meta cuantitativa para barras de progreso (Innovación)
      5        -- usuario_id del Líder de Campaña (opcional)
  );
  ```

### `sp_editar_campania`
* **Módulo / Pantalla:** Coordinador > Campañas > Editar Campaña (`PUT /admin/campanias/{id}`).
* **Firma:** `sp_editar_campania(p_campania_id, p_nombre, p_descripcion, p_fecha_fin, p_meta_unidades, p_lider_id)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_editar_campania(1, 'Huracán 2026 - Fase Reconstrucción', 'Nueva descripción', '2026-10-31', 8000.00, 5);
  ```

### `sp_cambiar_estado_campania`
* **Módulo / Pantalla:** Coordinador > Campañas > Cierre Oficial de Campaña.
* **Firma:** `sp_cambiar_estado_campania(p_campania_id INT, p_nuevo_estado BOOLEAN)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_cambiar_estado_campania(1, false); -- Cierra recepción oficial
  ```

### `sp_asociar_centro_campania`
* **Módulo / Pantalla:** Coordinador > Campañas > Asignar Sedes Participantes.
* **Firma:** `sp_asociar_centro_campania(p_centro_id, p_campania_id, p_activo)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_asociar_centro_campania(2, 1, true); -- Habilita Centro 2 para Campaña 1
  ```

---

## 4. Módulo de Catálogos y Selectores de Interfaz (Dropdowns)

### `sp_crear_articulo`
* **Módulo / Pantalla:** Coordinador > Catálogo de Artículos > Nuevo Insumo.
* **Firma:** `sp_crear_articulo(p_nombre VARCHAR(100), p_categoria VARCHAR(30), p_unidad VARCHAR(20))`
* **Categorías válidas:** `NO_PERECEDERO`, `PERECEDERO`, `ROPA`, `LIMPIEZA`, `MEDICAMENTO`, `OTRO`.
* **Unidades válidas:** `PIEZA`, `KG`, `L`, `BOLSA`, `CAJA`.
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_crear_articulo('Lenteja en bolsa 500g', 'NO_PERECEDERO', 'BOLSA');
  ```

### `sp_listar_articulos`
* **Módulo / Pantalla:** Se invoca vía AJAX/Fetch para poblar el `<select id="articulo_id">` en todos los formularios de inventario.
* **Firma:** `sp_listar_articulos(p_categoria VARCHAR(30))` (Enviar `NULL` para listar todos los artículos).
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_listar_articulos(NULL);          -- Todos los insumos
  CALL sp_listar_articulos('MEDICAMENTO'); -- Solo medicamentos
  ```
* **Retorna:** `id`, `nombre`, `categoria`, `unidad`.

### `sp_listar_instituciones_receptoras`
* **Módulo / Pantalla:** Se invoca al abrir la pantalla de **Entrega de Víveres** para poblar `<select id="institucion_id">`.
* **Firma:** `sp_listar_instituciones_receptoras()`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_listar_instituciones_receptoras();
  ```
* **Retorna:** `id`, `nombre`, `direccion`, `contacto`.

### `sp_listar_campanias_activas_centro`
* **Módulo / Pantalla:** Al entrar un voluntario a capturar en su centro, puebla el `<select id="campania_id">`.
* **Firma:** `sp_listar_campanias_activas_centro(p_centro_id INT)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_listar_campanias_activas_centro(1); -- Campañas habilitadas para el Centro 1
  ```
* **Retorna:** `campania_id`, `nombre`, `fecha_inicio`, `fecha_fin`, `meta_unidades`.

### `sp_listar_centros_destino_transferencia`
* **Módulo / Pantalla:** Formulario de **Transferencia entre Centros**, puebla `<select id="centro_destino_id">`.
* **Regla:** Excluye al centro emisor y solo muestra centros activos en la misma campaña.
* **Firma:** `sp_listar_centros_destino_transferencia(p_centro_origen_id INT, p_campania_id INT)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_listar_centros_destino_transferencia(1, 1);
  ```
* **Retorna:** `centro_id`, `nombre`, `institucion`, `ubicacion`.

---

## 5. Módulo de Operaciones de Inventario en Campo (Core Ledger)

### `sp_registrar_recepcion_donacion`
* **Módulo / Pantalla:** Operativa > Registrar Donación (`POST /inventario/recepcion`).
* **Quién lo invoca:** Voluntario o Encargado de Centro.
* **Firma:**
  ```sql
  sp_registrar_recepcion_donacion(
      p_centro_id INT,
      p_campania_id INT,
      p_articulo_id INT,
      p_cantidad DECIMAL(10,2),
      p_usuario_id INT,
      p_es_anonimo BOOLEAN,
      p_donante_nombre VARCHAR(150),
      p_donante_contacto VARCHAR(100)
  )
  ```
* **Ejemplo 1 (Donante Nominal):**
  ```sql
  CALL sp_registrar_recepcion_donacion(
      1, 1, 1, 50.00, 3, false, 'Carlos Mendoza', '555-223344'
  );
  ```
* **Ejemplo 2 (Donación Anónima):**
  ```sql
  CALL sp_registrar_recepcion_donacion(
      1, 1, 4, 30.00, 3, true, NULL, NULL
  );
  ```
* **Retorna:** `movimiento_id`, `tipo`, `cantidad_recibida`, `stock_actual`, `mensaje`.

---

### `sp_registrar_entrega`
* **Módulo / Pantalla:** Operativa > Canalizar / Entrega de Víveres (`POST /inventario/entrega`).
* **Quién lo invoca:** Encargado o Voluntario de Centro.
* **Firma:**
  ```sql
  sp_registrar_entrega(
      p_centro_id INT,
      p_campania_id INT,
      p_articulo_id INT,
      p_cantidad DECIMAL(10,2),
      p_institucion_receptora_id INT,
      p_usuario_id INT
  )
  ```
* **Ejemplo:**
  ```sql
  CALL sp_registrar_entrega(1, 1, 1, 25.00, 1, 2);
  ```
* **Control Crítico:** Si `stock_disponible < p_cantidad`, se aborta la transacción con:  
  `Error: Stock insuficiente. No se puede realizar una entrega que cause stock negativo.`
* **Retorna:** `movimiento_id`, `tipo`, `cantidad_entregada`, `stock_restante`, `mensaje`.

---

### `sp_registrar_merma`
* **Módulo / Pantalla:** Operativa > Reportar Merma / Desecho (`POST /inventario/merma`).
* **Quién lo invoca:** **Exclusivamente Encargado de Centro**.
* **Motivos válidos:** `CADUCIDAD`, `DANO`, `PERDIDA`, `OTRO`.
* **Firma:**
  ```sql
  sp_registrar_merma(
      p_centro_id INT,
      p_campania_id INT,
      p_articulo_id INT,
      p_cantidad DECIMAL(10,2),
      p_motivo VARCHAR(50),
      p_motivo_detalle TEXT,
      p_usuario_id INT
  )
  ```
* **Ejemplo:**
  ```sql
  CALL sp_registrar_merma(
      1, 1, 1, 5.00, 'DANO', 'Empaque roto y botellas aplastadas durante maniobra.', 2
  );
  ```
* **Retorna:** `movimiento_id`, `tipo`, `motivo`, `cantidad_merma`, `stock_restante`, `mensaje`.

---

### `sp_registrar_transferencia_centros`
* **Módulo / Pantalla:** Logística > Transferencia entre Centros (`POST /inventario/transferencia`).
* **Quién lo invoca:** Encargado de Centro emisor.
* **Firma:**
  ```sql
  sp_registrar_transferencia_centros(
      p_centro_origen_id INT,
      p_centro_destino_id INT,
      p_campania_id INT,
      p_articulo_id INT,
      p_cantidad DECIMAL(10,2),
      p_usuario_id INT
  )
  ```
* **Ejemplo:**
  ```sql
  CALL sp_registrar_transferencia_centros(1, 2, 1, 2, 40.00, 2);
  ```
* **Garantía Transaccional:** En un solo bloque atómico (`START TRANSACTION ... COMMIT`):
  1. Registra la transferencia en estado `COMPLETADA`.
  2. Genera `TRANSFERENCIA_SALIDA` en Centro 1 (descuenta stock).
  3. Genera `TRANSFERENCIA_ENTRADA` en Centro 2 (incrementa stock).
* **Retorna:** `transferencia_id`, `centro_origen`, `centro_destino`, `cantidad_transferida`, `stock_remanente_origen`, `mensaje`.

---

### `sp_registrar_ajuste_stock`
* **Módulo / Pantalla:** Auditoría > Ajuste Manual de Inventario (`POST /inventario/ajuste`).
* **Quién lo invoca:** Encargado de Centro o Coordinador General tras conteo físico.
* **Tipos de ajuste:** `AJUSTE_POSITIVO` o `AJUSTE_NEGATIVO`.
* **Motivos válidos:** `CORRECCION_CONTEO`, `ERROR_CAPTURA`, `OTRO`, `DANO`, `PERDIDA`.
* **Firma:**
  ```sql
  sp_registrar_ajuste_stock(
      p_centro_id INT,
      p_campania_id INT,
      p_articulo_id INT,
      p_cantidad DECIMAL(10,2),
      p_tipo_ajuste VARCHAR(20),
      p_motivo VARCHAR(50),
      p_motivo_detalle TEXT,
      p_usuario_id INT
  )
  ```
* **Ejemplo 1 (Ajuste Faltante):**
  ```sql
  CALL sp_registrar_ajuste_stock(
      1, 1, 4, 2.00, 'AJUSTE_NEGATIVO', 'CORRECCION_CONTEO', 'Faltante físico en anaquel 3', 2
  );
  ```
* **Ejemplo 2 (Ajuste Sobrante):**
  ```sql
  CALL sp_registrar_ajuste_stock(
      1, 1, 1, 10.00, 'AJUSTE_POSITIVO', 'ERROR_CAPTURA', 'Caja omitida en recepción matutina', 2
  );
  ```
* **Retorna:** `movimiento_id`, `tipo_ajuste`, `motivo`, `cantidad_ajustada`, `stock_oficial_actualizado`, `mensaje`.

---

## 6. Módulo de Institución Receptora (Consulta Externa y Confirmación)

### `sp_consultar_entregas_institucion`
* **Módulo / Pantalla:** Institución > Mis Entregas (`GET /institucion/entregas`).
* **Quién lo invoca:** Usuario con rol `INSTITUCION` (o Coordinador).
* **Propósito:** Muestra todas las donaciones canalizadas hacia esa institución, permitiendo filtrar solo las que están pendientes de llegar o ver todo el histórico.
* **Firma:** `sp_consultar_entregas_institucion(p_institucion_id INT, p_solo_pendientes BOOLEAN)`
* **Ejemplo de llamada SQL:**
  ```sql
  -- Ver solo entregas pendientes de confirmar
  CALL sp_consultar_entregas_institucion(1, true);

  -- Ver histórico completo de entregas
  CALL sp_consultar_entregas_institucion(1, false);
  ```
* **Retorna:** `movimiento_id`, `fecha`, `centro_origen`, `campania_nombre`, `articulo_id`, `articulo_nombre`, `categoria`, `cantidad`, `unidad`, `despachado_por`, `entrega_confirmada`, `fecha_confirmacion`.

---

### `sp_confirmar_entrega_recibida`
* **Módulo / Pantalla:** Institución > Mis Entregas > Botón "Confirmar de Recibido" (`POST /institucion/confirmar/{id}`).
* **Quién lo invoca:** Usuario con rol `INSTITUCION` asignado a esa institución receptora.
* **Propósito:** Certifica la llegada física de los víveres al albergue o comedor comunitario.
* **Control de Seguridad (RBAC):** Valida que si el usuario es de rol `INSTITUCION`, únicamente pueda confirmar entregas dirigidas a su propia sede (`institucion_id`).
* **Firma:** `sp_confirmar_entrega_recibida(p_movimiento_id INT, p_usuario_id INT)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_confirmar_entrega_recibida(1, 4); -- movimiento_id = 1, usuario_id = 4 (Receptor)
  ```
* **Retorna:** `movimiento_id`, `entrega_confirmada`, `fecha_confirmacion`, `mensaje`.

---

## 7. Módulo de Auditoría, Kárdex y Control de Existencias

### `sp_obtener_stock_centro`
* **Módulo / Pantalla:** Almacén > Mi Inventario (`GET /inventario/stock`).
* **Quién lo invoca:** Encargado de Centro o Voluntario.
* **Propósito:** Consulta el inventario disponible en tiempo real consumiendo directamente la vista oficial `v_stock_actual`.
* **Firma:** `sp_obtener_stock_centro(p_centro_id INT, p_campania_id INT)` (Enviar `NULL` o `0` en campaña para ver todo el stock consolidado del centro).
* **Ejemplo de llamada SQL:**
  ```sql
  -- Stock del Centro 1 en la Campaña 1
  CALL sp_obtener_stock_centro(1, 1);

  -- Stock total de todos los insumos en el Centro 1
  CALL sp_obtener_stock_centro(1, NULL);
  ```
* **Retorna:** `centro_id`, `centro_nombre`, `campania_id`, `campania_nombre`, `articulo_id`, `articulo_nombre`, `categoria`, `unidad`, `stock_disponible`.

---

### `sp_consultar_historial_movimientos`
* **Módulo / Pantalla:** Auditoría > Historial Cronológico y Kárdex (`GET /auditoria/historial`).
* **Quién lo invoca:** Encargado (ve solo su centro) o Coordinador (ve toda la red). También alimenta la exportación a Excel / CSV.
* **Propósito:** Consulta flexible y multicriterio del Libro Mayor de movimientos con nombres descriptivos de actores, insumos y centros.
* **Firma:**
  ```sql
  sp_consultar_historial_movimientos(
      p_centro_id INT,       -- NULL para todos los centros
      p_campania_id INT,     -- NULL para todas las campañas
      p_articulo_id INT,     -- NULL para todos, o ID específico para Kárdex de un insumo
      p_tipo VARCHAR(30),    -- NULL para todos, o 'RECEPCION', 'ENTREGA', 'MERMA', etc.
      p_fecha_desde DATE,    -- NULL para sin límite inferior
      p_fecha_hasta DATE     -- NULL para sin límite superior
  )
  ```
* **Ejemplo 1 (Kárdex de Agua embotellada en Centro 1 durante septiembre):**
  ```sql
  CALL sp_consultar_historial_movimientos(1, 1, 1, NULL, '2026-09-01', '2026-09-30');
  ```
* **Ejemplo 2 (Auditoría global de todas las Mermas registradas en la red):**
  ```sql
  CALL sp_consultar_historial_movimientos(NULL, NULL, NULL, 'MERMA', NULL, NULL);
  ```
* **Retorna:** `movimiento_id`, `fecha`, `tipo`, `centro_nombre`, `campania_nombre`, `articulo_id`, `articulo_nombre`, `categoria`, `cantidad`, `unidad`, `actor_nombre`, `actor_rol`, `motivo`, `motivo_detalle`, `destinatario_o_fuente`, `entrega_confirmada`, `estado_aprobacion`.

---

## 8. Módulo de Dashboards y Analítica Ejecutiva (Soporte a la Decisión)

### `sp_dashboard_coordinador_global`
* **Módulo / Pantalla:** Dashboard General del Coordinador (`GET /dashboard/global`).
* **Quién lo invoca:** Coordinador General.
* **Propósito:** Calcula las tarjetas macro KPI de toda la red: donaciones recibidas, víveres canalizados, mermas, stock remanente y porcentaje de avance contra las metas cuantitativas.
* **Firma:** `sp_dashboard_coordinador_global(p_campania_id INT)` (Enviar `NULL` o `0` para ver el consolidado de todas las campañas activas).
* **Ejemplo de llamada SQL:**
  ```sql
  -- Métricas de la Campaña 1
  CALL sp_dashboard_coordinador_global(1);

  -- Métricas globales consolidadas de toda la red
  CALL sp_dashboard_coordinador_global(NULL);
  ```
* **Retorna:** `campania_id_filtro`, `total_recaudado`, `total_entregado`, `total_merma`, `stock_disponible_global`, `meta_cuantitativa`, `porcentaje_avance_meta`, `centros_activos_participando`.

---

### `sp_dashboard_comparativa_centros`
* **Módulo / Pantalla:** Dashboard Coordinador > Balance y Comparativa de Sedes (`GET /dashboard/comparativa`).
* **Quién lo invoca:** Coordinador General.
* **Propósito:** Muestra el balance operativo de cada centro para identificar sobreabastecimientos o desabastos críticos y decidir transferencias.
* **Firma:** `sp_dashboard_comparativa_centros(p_campania_id INT)` (Enviar `NULL` o `0` para todas).
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_dashboard_comparativa_centros(1);
  ```
* **Retorna:** Tabla con `centro_id`, `centro_nombre`, `institucion`, `ubicacion`, `latitud`, `longitud`, `total_recibido`, `total_entregado`, `total_merma`, `stock_remanente`.

---

### `sp_dashboard_articulos_mas_donados`
* **Módulo / Pantalla:** Gráfica de Barras "Top Insumos" (`GET /dashboard/top-articulos`).
* **Quién lo invoca:** Coordinador General o Líder de Campaña.
* **Propósito:** Identifica los 5 o 10 artículos con mayor volumen recaudado para priorizar logística y embalaje.
* **Firma:** `sp_dashboard_articulos_mas_donados(p_campania_id INT, p_limite INT)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_dashboard_articulos_mas_donados(1, 5); -- Top 5 de la Campaña 1
  ```
* **Retorna:** `articulo_id`, `articulo_nombre`, `categoria`, `unidad`, `total_donado`, `numero_recepciones`.

---

### `sp_dashboard_encargado_centro`
* **Módulo / Pantalla:** Inicio del Encargado de Sede (`GET /dashboard/centro`).
* **Quién lo invoca:** Encargado de Centro.
* **Propósito:** Muestra las tarjetas operativas de su sede: stock físico actual, movimientos registrados hoy, entregas que faltan por ser confirmadas y merma local.
* **Firma:** `sp_dashboard_encargado_centro(p_centro_id INT)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_dashboard_encargado_centro(1);
  ```
* **Retorna:** `centro_id`, `stock_total_disponible`, `recibido_hoy`, `entregado_hoy`, `entregas_por_confirmar`, `merma_acumulada`.

---

### `sp_dashboard_lider_campania`
* **Módulo / Pantalla:** Inicio del Líder de Campaña (`GET /dashboard/campania/{id}`).
* **Quién lo invoca:** Líder de Campaña (o Coordinador).
* **Propósito:** Monitoreo analítico del evento específico asignado y seguimiento en tiempo real de la meta de recolección.
* **Firma:** `sp_dashboard_lider_campania(p_campania_id INT, p_usuario_id INT)`
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_dashboard_lider_campania(1, 5); -- Campaña 1, Líder con usuario_id = 5
  ```
* **Retorna:** `campania_id`, `campania_nombre`, `meta_unidades`, `total_recolectado`, `total_canalizado`, `total_merma`, `stock_actual_campania`, `porcentaje_avance_meta`, `centros_participantes`.

---

## 9. Módulo de Innovación y Diferenciadores (<in>Hack - Rúbrica 25 pts)

### `sp_aprobar_merma` (Innovación 9.2: Flujo de Aprobación de Merma)
* **Módulo / Pantalla:** Auditoría > Bandeja de Mermas por Autorizar (`POST /admin/mermas/aprobar`).
* **Quién lo invoca:** **Exclusivamente Coordinador General**.
* **Propósito:** Permite al Coordinador aprobar o descartar formalmente un reporte de merma antes de que afecte el balance oficial.
* **Firma:** `sp_aprobar_merma(p_movimiento_id INT, p_coordinador_id INT, p_nuevo_estado VARCHAR(20))`
* **Estados válidos:** `'APROBADO'` o `'RECHAZADO'`.
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_aprobar_merma(3, 1, 'APROBADO'); -- movimiento_id = 3, coordinador = 1
  ```
* **Retorna:** `movimiento_id`, `estado_aprobacion`, `aprobado_por`, `mensaje`.

---

### `sp_obtener_centros_mapa` (Innovación 9.3: Mapa Interactivo de Centros)
* **Módulo / Pantalla:** Vista Pública o Privada de Geolocalización (`GET /mapa`).
* **Quién lo invoca:** Frontend para pintar los marcadores en Leaflet / Google Maps.
* **Propósito:** Devuelve la lista de centros activos con sus coordenadas geográficas (`latitud`, `longitud`), su stock total disponible y el número de campañas en las que participan.
* **Firma:** `sp_obtener_centros_mapa(p_campania_id INT)` (Enviar `NULL` o `0` para todos los centros activos).
* **Ejemplo de llamada SQL:**
  ```sql
  CALL sp_obtener_centros_mapa(NULL);
  ```
* **Retorna:** `centro_id`, `nombre`, `institucion`, `ubicacion`, `latitud`, `longitud`, `activo`, `total_stock_disponible`, `campanias_activas_conteo`.

---

## 💻 Guía de Invocación desde Spring Boot (Java)

Existen dos opciones recomendadas para invocar estos procedimientos desde los `@Service` o `@Repository` en Spring Boot:

### Opción A: Mediante `JdbcTemplate` (Recomendada para consultas tabulares y Dashboards)
```java
@Service
public class InventarioService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // --- OPERACIONES DE INVENTARIO ---
    public Map<String, Object> registrarRecepcion(Long centroId, Long campaniaId, Long articuloId, 
                                                  BigDecimal cantidad, Long usuarioId, 
                                                  boolean esAnonimo, String donanteNombre, String donanteContacto) {
        String sql = "CALL sp_registrar_recepcion_donacion(?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.queryForMap(sql, 
            centroId, campaniaId, articuloId, cantidad, usuarioId, esAnonimo, donanteNombre, donanteContacto
        );
    }

    public List<Map<String, Object>> obtenerStockCentro(Long centroId, Long campaniaId) {
        String sql = "CALL sp_obtener_stock_centro(?, ?)";
        return jdbcTemplate.queryForList(sql, centroId, campaniaId);
    }

    public List<Map<String, Object>> consultarHistorial(Long centroId, Long campaniaId, Long articuloId, 
                                                        String tipo, LocalDate desde, LocalDate hasta) {
        String sql = "CALL sp_consultar_historial_movimientos(?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.queryForList(sql, centroId, campaniaId, articuloId, tipo, desde, hasta);
    }

    // --- DASHBOARDS Y ANALÍTICA ---
    public Map<String, Object> obtenerDashboardGlobal(Long campaniaId) {
        String sql = "CALL sp_dashboard_coordinador_global(?)";
        return jdbcTemplate.queryForMap(sql, campaniaId);
    }

    public List<Map<String, Object>> obtenerComparativaCentros(Long campaniaId) {
        String sql = "CALL sp_dashboard_comparativa_centros(?)";
        return jdbcTemplate.queryForList(sql, campaniaId);
    }

    public List<Map<String, Object>> obtenerTopArticulos(Long campaniaId, Integer limite) {
        String sql = "CALL sp_dashboard_articulos_mas_donados(?, ?)";
        return jdbcTemplate.queryForList(sql, campaniaId, limite);
    }

    public Map<String, Object> obtenerDashboardCentro(Long centroId) {
        String sql = "CALL sp_dashboard_encargado_centro(?)";
        return jdbcTemplate.queryForMap(sql, centroId);
    }

    // --- MÓDULO DE INNOVACIÓN (MAPA Y APROBACIONES) ---
    public List<Map<String, Object>> obtenerCentrosMapa(Long campaniaId) {
        String sql = "CALL sp_obtener_centros_mapa(?)";
        return jdbcTemplate.queryForList(sql, campaniaId);
    }

    public Map<String, Object> aprobarMerma(Long movimientoId, Long coordinadorId, String estado) {
        String sql = "CALL sp_aprobar_merma(?, ?, ?)";
        return jdbcTemplate.queryForMap(sql, movimientoId, coordinadorId, estado);
    }
}
```

### Opción B: Mediante `@Procedure` en Spring Data JPA
```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Procedure(procedureName = "sp_cambiar_estado_usuario")
    void cambiarEstadoUsuario(@Param("p_usuario_id") Integer usuarioId, 
                              @Param("p_nuevo_estado") Boolean nuevoEstado);
}
```

---

## 🚦 Manejo de Errores y Validaciones en Frontend
Todos los procedimientos de movimiento utilizan `SIGNAL SQLSTATE '45000'` para lanzar errores descriptivos de negocio (ej. *"Stock insuficiente..."* o *"Motivo de merma obligatorio..."*). 

En el controlador de Spring Boot:
```java
try {
    inventarioService.registrarEntrega(...);
    redirectAttributes.addFlashAttribute("mensajeExito", "Entrega canalizada con éxito");
} catch (DataAccessException ex) {
    // Captura el mensaje exacto generado por el procedimiento almacenado
    redirectAttributes.addFlashAttribute("mensajeError", ex.getRootCause().getMessage());
}
```
Esto garantiza que las validaciones del motor relacional se muestren directamente al usuario en las alertas de Bootstrap de la interfaz web.
