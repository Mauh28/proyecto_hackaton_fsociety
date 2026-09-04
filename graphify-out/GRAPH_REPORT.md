# Graph Report - proyecto_hackaton_fsociety  (2026-09-04)

## Corpus Check
- 86 files · ~77,716 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1112 nodes · 2094 edges · 62 communities (33 shown, 29 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 282 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `00501af4`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- MovimientoRepository
- Movimiento
- CatalogosEncargadoDTO
- RecepcionRequestDTO
- ResumenRecepcionDTO
- MovimientoHistorialDTO
- LiderController
- RegistroMovimientoRequest
- DashboardCentroDTO
- org.junit.jupiter.api.Test
- DashboardGlobalDTO
- .obtenerDashboardCentro
- GuardarCentroRequest
- GuardarCampaniaRequest
- UsuarioContextoDTO
- Transferencia
- Guía de Especificación de Operaciones, Parámetros y Validaciones del Backend
- CentroMapaDTO
- AlertaDesabastoDTO
- 3.6 Catálogo y Guía Operativa de los 31 Procedimientos Almacenados
- Campania
- Contexto del Proyecto: Sistema de Registro y Coordinación de Centros de Acopio (<in>Hack)
- Usuario
- Override
- RolUsuario
- 3. SECCIÓN I: ARQUITECTURA Y DESARROLLO DE LA BASE DE DATOS
- org.springframework.web.bind.annotation.ExceptionHandler
- Centro
- README.md
- CentroComparativaDTO
- MotivoMovimiento
- InstitucionReceptora
- mvnw
- Getting Started
- ProgApplication
- com.hackaton:prog
- rules/graphify.md
- workflows/graphify.md
- TipoMovimiento
- DashboardLiderDTO
- .actualizarCampania
- CategoriaArticulo
- RecepcionService.java
- 3.4 Diccionario de Datos y Especificación DDL
- 4.3 Catálogo de Controladores y Endpoints REST
- 5.2 Detalle de Pantallas Implementadas
- 6. SECCIÓN IV: MATRIZ DE EVALUACIÓN (<in>Hack), DEPLOYMENT Y DEVOPS
- Documentación Técnica del Sistema de Registro y Coordinación de Centros de Acopio (<in>Hack)
- CentroAporteCampaniaDTO
- OpcionSimpleDTO
- ArticuloStockDTO
- .obtenerContextoValidado
- ErrorResponse
- .obtenerDashboard
- CentroCampania
- org.springframework.http.ResponseEntity
- Articulo
- .validarPermisoModulo
- StockInsuficienteException
- AccesoDenegadoException
- CredencialesInvalidasException
- UsuarioNoEncontradoException

## God Nodes (most connected - your core abstractions)
1. `Movimiento` - 57 edges
2. `Campania` - 46 edges
3. `DashboardLiderDTO` - 44 edges
4. `Centro` - 43 edges
5. `OpcionSimpleDTO` - 41 edges
6. `Usuario` - 40 edges
7. `Transferencia` - 35 edges
8. `UsuarioContextoDTO` - 32 edges
9. `MovimientoRepository` - 32 edges
10. `RegistroMovimientoRequest` - 31 edges

## Surprising Connections (you probably didn't know these)
- `CoordinadorController` --references--> `CoordinadorService`  [EXTRACTED]
  src/main/java/com/hackaton/prog/controller/CoordinadorController.java → src/main/java/com/hackaton/prog/service/CoordinadorService.java
- `EncargadoController` --references--> `EncargadoService`  [EXTRACTED]
  src/main/java/com/hackaton/prog/controller/EncargadoController.java → src/main/java/com/hackaton/prog/service/EncargadoService.java
- `LiderController` --references--> `LiderService`  [EXTRACTED]
  src/main/java/com/hackaton/prog/controller/LiderController.java → src/main/java/com/hackaton/prog/service/LiderService.java
- `LiderControllerTest` --references--> `LiderController`  [EXTRACTED]
  src/test/java/com/hackaton/prog/LiderControllerTest.java → src/main/java/com/hackaton/prog/controller/LiderController.java
- `MenuController` --references--> `MenuService`  [EXTRACTED]
  src/main/java/com/hackaton/prog/controller/MenuController.java → src/main/java/com/hackaton/prog/service/MenuService.java

## Import Cycles
- None detected.

## Communities (62 total, 29 thin omitted)

### Community 0 - "MovimientoRepository"
Cohesion: 0.11
Nodes (23): org.springframework.data.jpa.repository.JpaRepository, org.springframework.stereotype.Repository, org.springframework.stereotype.Service, org.springframework.transaction.annotation.Transactional, AuthController, RequestMapping, RestController, desdeValorDb() (+15 more)

### Community 1 - "Movimiento"
Cohesion: 0.09
Nodes (7): Donante, Entity, Table, Entity, PrePersist, Table, Movimiento

### Community 3 - "RecepcionRequestDTO"
Cohesion: 0.07
Nodes (3): PostMapping, RecepcionRequestDTO, RecepcionResponseDTO

### Community 4 - "ResumenRecepcionDTO"
Cohesion: 0.06
Nodes (6): GetMapping, RequestMapping, RestController, RecepcionController, ArticuloItemDTO, ResumenRecepcionDTO

### Community 6 - "LiderController"
Cohesion: 0.14
Nodes (7): DeleteMapping, GetMapping, PostMapping, RequestMapping, RestController, LiderController, AsociarCentroCampaniaRequest

### Community 9 - "org.junit.jupiter.api.Test"
Cohesion: 0.09
Nodes (18): org.junit.jupiter.api.BeforeEach, org.junit.jupiter.api.DisplayName, org.junit.jupiter.api.Test, org.springframework.boot.test.context.SpringBootTest, org.springframework.test.web.servlet.MockMvc, EncargadoController, GetMapping, RequestMapping (+10 more)

### Community 15 - "Transferencia"
Cohesion: 0.09
Nodes (10): desdeValorDb(), EstadoTransferencia, CANCELADA, COMPLETADA, PENDIENTE, Entity, PrePersist, Table (+2 more)

### Community 16 - "Guía de Especificación de Operaciones, Parámetros y Validaciones del Backend"
Cohesion: 0.04
Nodes (46): 1. Módulo de Autenticación y Contexto de Sesión, 2. Módulo de Administración de Usuarios (RBAC), 3. Módulo de Gestión de Centros de Acopio y Campañas, 4. Módulo de Catálogos y Selectores de Interfaz (Dropdowns), 5. Módulo de Operaciones de Inventario en Campo (Core Ledger), 6. Módulo de Institución Receptora (Consulta Externa y Confirmación), 7. Módulo de Auditoría, Kárdex y Control de Existencias, 8. Módulo de Dashboards y Analítica Ejecutiva (Soporte a la Decisión) (+38 more)

### Community 19 - "3.6 Catálogo y Guía Operativa de los 31 Procedimientos Almacenados"
Cohesion: 0.05
Nodes (40): 3.6 Catálogo y Guía Operativa de los 31 Procedimientos Almacenados, MÓDULO 1: Core de Inventario y Movimientos de Almacén, MÓDULO 2: Autenticación, Sesión y RBAC, MÓDULO 3: Gestión Maestra de Centros y Campañas, MÓDULO 4: Catálogos y Selectores Dinámicos (Dropdowns de Formularios), MÓDULO 5: Institución Receptora y Confirmaciones Externas, MÓDULO 6: Kárdex, Auditoría y Stock en Vivo, MÓDULO 7: Dashboards y Analítica Ejecutiva (+32 more)

### Community 20 - "Campania"
Cohesion: 0.14
Nodes (3): Campania, Entity, Table

### Community 21 - "Contexto del Proyecto: Sistema de Registro y Coordinación de Centros de Acopio (<in>Hack)"
Cohesion: 0.06
Nodes (31): 10.1 Innovación (25 pts), 10.2 Calidad Técnica (25 pts), 10.3 Impacto y Adecuación al Reto (25 pts), 10.4 Presentación y Demo (25 pts), 10. Rúbrica de Evaluación (<in>Hack - 100 Puntos), 11. Entregables Obligatorios, 1.1 Antecedentes, 1.2 La Problemática Actual (+23 more)

### Community 22 - "Usuario"
Cohesion: 0.15
Nodes (3): Entity, Table, Usuario

### Community 23 - "Override"
Cohesion: 0.23
Nodes (8): jakarta.persistence.AttributeConverter, jakarta.persistence.Converter, CategoriaArticuloConverter, EnumsConverters, EstadoTransferenciaConverter, Override, RolUsuarioConverter, UnidadMedidaConverter

### Community 24 - "RolUsuario"
Cohesion: 0.18
Nodes (7): desdeValorDb(), RolUsuario, COORDINADOR, ENCARGADO, INSTITUCION, LIDER, VOLUNTARIO

### Community 25 - "3. SECCIÓN I: ARQUITECTURA Y DESARROLLO DE LA BASE DE DATOS"
Cohesion: 0.17
Nodes (12): 3.1 Fundamentos y Decisiones de Diseño de la Base de Datos, 3.2 Correcciones Técnicas Recientes y Adaptaciones Cloud (Clever Cloud), 3.3 Diagrama Entidad-Relación Conceptual (EER), 3.5 Vista Oficial de Stock Actual (`v_stock_actual`), 3.7 Datos Semilla (Fixtures / Seeds para Demostración), 3.8 Guía de Despliegue y Ejecución del Script SQL, 3. SECCIÓN I: ARQUITECTURA Y DESARROLLO DE LA BASE DE DATOS, A. Adaptación del Nombre de Esquema por Políticas de PaaS Cloud (+4 more)

### Community 26 - "org.springframework.web.bind.annotation.ExceptionHandler"
Cohesion: 0.20
Nodes (4): MethodArgumentTypeMismatchException, org.springframework.web.bind.annotation.ExceptionHandler, org.springframework.web.method.annotation.MethodArgumentTypeMismatchException, CuentaInactivaException

### Community 27 - "Centro"
Cohesion: 0.18
Nodes (3): Centro, Entity, Table

### Community 28 - "README.md"
Cohesion: 0.20
Nodes (9): 1. Clonar el Repositorio, 2. Compilar y Ejecutar, 3. Acceder en Local, 🌐 Acceso a la Aplicación (Despliegue en Vivo), 🤖 Declaración de Uso de Herramientas de IA, 📄 Documentación del Proyecto, 🛠️ Herramientas y Stack Tecnológico (Resumen), 💻 Instrucciones de Instalación y Ejecución Local (+1 more)

### Community 30 - "MotivoMovimiento"
Cohesion: 0.18
Nodes (9): MotivoMovimientoConverter, desdeValorDb(), MotivoMovimiento, CADUCIDAD, CORRECCION_CONTEO, DANO, ERROR_CAPTURA, OTRO (+1 more)

### Community 31 - "InstitucionReceptora"
Cohesion: 0.14
Nodes (3): InstitucionReceptora, Entity, Table

### Community 32 - "mvnw"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 33 - "Getting Started"
Cohesion: 0.40
Nodes (4): Getting Started, Guides, Maven Parent overrides, Reference Documentation

### Community 38 - "TipoMovimiento"
Cohesion: 0.22
Nodes (9): TipoMovimientoConverter, TipoMovimiento, AJUSTE_NEGATIVO, AJUSTE_POSITIVO, ENTREGA, MERMA, RECEPCION, TRANSFERENCIA_ENTRADA (+1 more)

### Community 41 - "CategoriaArticulo"
Cohesion: 0.18
Nodes (8): CategoriaArticulo, LIMPIEZA, MEDICAMENTO, NO_PERECEDERO, OTRO, PERECEDERO, ROPA, desdeValorDb()

### Community 42 - "RecepcionService.java"
Cohesion: 0.18
Nodes (7): desdeValorDb(), UnidadMedida, BOLSA, CAJA, KG, L, PIEZA

### Community 43 - "3.4 Diccionario de Datos y Especificación DDL"
Cohesion: 0.20
Nodes (10): 1. Tabla: `instituciones_receptoras`, 2. Tabla: `centros`, 3.4 Diccionario de Datos y Especificación DDL, 3. Tabla: `usuario`, 4. Tabla: `campanias`, 5. Tabla: `centros_campanias`, 6. Tabla: `articulos`, 7. Tabla: `donantes` (+2 more)

### Community 44 - "4.3 Catálogo de Controladores y Endpoints REST"
Cohesion: 0.20
Nodes (10): 4.1 Arquitectura en Capas y Responsabilidades, 4.2 Matriz de Control de Acceso y Roles (RBAC), 4.3 Catálogo de Controladores y Endpoints REST, 4.4 Reglas Fundamentales de Negocio y Prevención de Stock Negativo, 4. SECCIÓN II: CAPA BACKEND Y REGLAS DE NEGOCIO, A. Autenticación (`AuthController.java` - `/api/auth`), B. Contexto de Navegación y Permisos (`MenuController.java` - `/api/menu`), C. Módulo de Recepción (`RecepcionController.java` - `/api/recepcion`) (+2 more)

### Community 45 - "5.2 Detalle de Pantallas Implementadas"
Cohesion: 0.25
Nodes (8): 1. Pantalla de Acceso (`index.html`), 2. Menú Centralizado Dinámico (`menu.html`), 3. Módulo de Recepción de Donaciones (`recepcion.html`), 4. Panel Operativo de Encargado (`encargado.html`), 5.1 Estructura de Vistas y Flujo de Navegación, 5.2 Detalle de Pantallas Implementadas, 5. Dashboard Global y Administración (`coordinador.html`), 5. SECCIÓN III: CAPA FRONTEND Y EXPERIENCIA DE USUARIO

### Community 46 - "6. SECCIÓN IV: MATRIZ DE EVALUACIÓN (<in>Hack), DEPLOYMENT Y DEVOPS"
Cohesion: 0.25
Nodes (8): 6.1 Matriz de Criterios de Aceptación del MVP (<in>Hack - Checklist Oficial), 6.2 Cuentas de Acceso de Prueba para la Demo del Jurado, 6.3 Despliegue, Contenerización y Ejecución Multiplataforma, 6.4 Declaración de Uso de Herramientas de IA y Diferenciadores de Innovación, 6. SECCIÓN IV: MATRIZ DE EVALUACIÓN (<in>Hack), DEPLOYMENT Y DEVOPS, A. Ejecución Rápida Local con Maven Wrapper, B. Construcción del Artefacto JAR, C. Despliegue con Docker (Render / AWS / Servidor Cloud)

### Community 47 - "Documentación Técnica del Sistema de Registro y Coordinación de Centros de Acopio (<in>Hack)"
Cohesion: 0.33
Nodes (5): 1.1 Contexto y Problemática, 1.2 Objetivo del Sistema, 1. Introducción y Visión General del Proyecto, 2. Mapa Arquitectónico de la Documentación, Documentación Técnica del Sistema de Registro y Coordinación de Centros de Acopio (<in>Hack)

### Community 54 - "CentroCampania"
Cohesion: 0.12
Nodes (6): jakarta.persistence.Embeddable, CentroCampania, Entity, Table, CentroCampaniaId, Override

### Community 55 - "org.springframework.http.ResponseEntity"
Cohesion: 0.24
Nodes (5): org.springframework.http.ResponseEntity, CoordinadorController, GetMapping, RequestMapping, RestController

### Community 56 - "Articulo"
Cohesion: 0.18
Nodes (3): Articulo, Entity, Table

## Knowledge Gaps
- **176 isolated node(s):** `com.hackaton:prog`, `NO_PERECEDERO`, `PERECEDERO`, `ROPA`, `LIMPIEZA` (+171 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **29 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Movimiento` connect `Movimiento` to `MovimientoRepository`, `TipoMovimiento`, `RecepcionService.java`, `.obtenerDashboardCentro`, `Transferencia`, `Campania`, `Usuario`, `Articulo`, `Centro`, `MotivoMovimiento`, `InstitucionReceptora`?**
  _High betweenness centrality (0.086) - this node is a cross-community bridge._
- **Why does `Centro` connect `Centro` to `MovimientoRepository`, `Movimiento`, `RecepcionService.java`, `.obtenerDashboardCentro`, `GuardarCentroRequest`, `Transferencia`, `.obtenerContextoValidado`, `.obtenerDashboard`, `CentroCampania`, `org.springframework.http.ResponseEntity`, `Usuario`, `RolUsuario`?**
  _High betweenness centrality (0.080) - this node is a cross-community bridge._
- **Why does `OpcionSimpleDTO` connect `OpcionSimpleDTO` to `MovimientoRepository`, `CatalogosEncargadoDTO`, `ResumenRecepcionDTO`, `DashboardLiderDTO`, `DashboardCentroDTO`, `DashboardGlobalDTO`, `.obtenerDashboardCentro`, `CentroAporteCampaniaDTO`, `.obtenerDashboard`?**
  _High betweenness centrality (0.068) - this node is a cross-community bridge._
- **What connects `com.hackaton:prog`, `NO_PERECEDERO`, `PERECEDERO` to the rest of the system?**
  _176 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `MovimientoRepository` be split into smaller, more focused modules?**
  _Cohesion score 0.10650281618023553 - nodes in this community are weakly interconnected._
- **Should `Movimiento` be split into smaller, more focused modules?**
  _Cohesion score 0.09143686502177069 - nodes in this community are weakly interconnected._
- **Should `RecepcionRequestDTO` be split into smaller, more focused modules?**
  _Cohesion score 0.06765327695560254 - nodes in this community are weakly interconnected._