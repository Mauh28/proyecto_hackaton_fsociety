# Guía Técnica de Integración: Procedimientos Almacenados (Fases 1 y 2)

> **Proyecto:** Sistema de Registro y Coordinación de Centros de Acopio (`<in>Hack`)  
> **Equipo:** *fsociety*  
> **Archivo SQL Fuente:** [`centros_acopio_db.sql`](file:///c:/Users/Adan1/Documents/Hackathon/proyecto_hackaton_fsociety/db/centros_acopio_db.sql)  
> **Motor:** MySQL 8.0+ / TiDB Cloud Serverless  

Esta guía documenta los **20 procedimientos almacenados** implementados en las **Fases 1 y 2**, detallando a qué módulo y pantalla del sistema corresponden, sus parámetros, reglas de negocio y ejemplos exactos de cómo invocarlos tanto desde SQL como desde Spring Boot.

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
└── 📦 MÓDULO 5: OPERACIONES DE INVENTARIO EN CAMPO (Encargados y Voluntarios)
    ├── sp_registrar_recepcion_donacion ─────► Pantalla: /inventario/recepcion
    ├── sp_registrar_entrega ────────────────► Pantalla: /inventario/entrega
    ├── sp_registrar_merma ──────────────────► Pantalla: /inventario/merma
    ├── sp_registrar_transferencia_centros ──► Pantalla: /inventario/transferencia
    └── sp_registrar_ajuste_stock ───────────► Pantalla: /inventario/ajuste
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

## 💻 Guía de Invocación desde Spring Boot (Java)

Existen dos opciones recomendadas para invocar estos procedimientos desde los `@Service` o `@Repository` en Spring Boot:

### Opción A: Mediante `JdbcTemplate` (Recomendada para rapidez y flexibilidad)
```java
@Service
public class InventarioService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> registrarRecepcion(Long centroId, Long campaniaId, Long articuloId, 
                                                  BigDecimal cantidad, Long usuarioId, 
                                                  boolean esAnonimo, String donanteNombre, String donanteContacto) {
        String sql = "CALL sp_registrar_recepcion_donacion(?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.queryForMap(sql, 
            centroId, campaniaId, articuloId, cantidad, usuarioId, esAnonimo, donanteNombre, donanteContacto
        );
    }

    public List<Map<String, Object>> listarCampaniasActivasCentro(Long centroId) {
        String sql = "CALL sp_listar_campanias_activas_centro(?)";
        return jdbcTemplate.queryForList(sql, centroId);
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
