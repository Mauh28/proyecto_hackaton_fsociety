# Graph Report - proyecto_hackaton_fsociety  (2026-09-03)

## Corpus Check
- 78 files · ~37,794 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 942 nodes · 1679 edges · 45 communities (31 shown, 14 thin omitted)
- Extraction: 88% EXTRACTED · 12% INFERRED · 0% AMBIGUOUS · INFERRED: 206 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `9a71e857`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- MovimientoService
- Movimiento
- CatalogosEncargadoDTO
- RecepcionRequestDTO
- ResumenRecepcionDTO
- MovimientoHistorialDTO
- ErrorResponse
- RegistroMovimientoRequest
- RolUsuario
- org.junit.jupiter.api.Test
- DashboardGlobalDTO
- Usuario
- GuardarCentroRequest
- Campania
- UsuarioContextoDTO
- Transferencia
- Guía de Especificación de Operaciones, Parámetros y Validaciones del Backend
- CentroMapaDTO
- TipoMovimiento
- 3.6 Catálogo y Guía Operativa de los 31 Procedimientos Almacenados
- .registrarCentro
- Contexto del Proyecto: Sistema de Registro y Coordinación de Centros de Acopio (<in>Hack)
- InstitucionReceptora
- CentroCampaniaId
- CuentaInactivaException
- 3.4 Diccionario de Datos y Especificación DDL
- MenuService
- Centro
- 🚀 Instrucciones de Instalación y Ejecución
- AuthController.java
- Articulo
- CentroCampania
- mvnw
- Getting Started
- ProgApplication
- com.hackaton:prog
- rules/graphify.md
- workflows/graphify.md
- Override
- MotivoMovimiento
- EncargadoController
- UnidadMedida
- CategoriaArticulo
- org.springframework.http.ResponseEntity
- StockInsuficienteException

## God Nodes (most connected - your core abstractions)
1. `Movimiento` - 54 edges
2. `Centro` - 42 edges
3. `Campania` - 40 edges
4. `Usuario` - 39 edges
5. `Transferencia` - 35 edges
6. `RecepcionRequestDTO` - 29 edges
7. `UsuarioContextoDTO` - 28 edges
8. `RegistroMovimientoRequest` - 27 edges
9. `Articulo` - 27 edges
10. `MovimientoService` - 27 edges

## Surprising Connections (you probably didn't know these)
- `AuthController` --references--> `UsuarioRepository`  [EXTRACTED]
  src/main/java/com/hackaton/prog/controller/AuthController.java → src/main/java/com/hackaton/prog/repository/UsuarioRepository.java
- `CoordinadorController` --references--> `CoordinadorService`  [EXTRACTED]
  src/main/java/com/hackaton/prog/controller/CoordinadorController.java → src/main/java/com/hackaton/prog/service/CoordinadorService.java
- `EncargadoController` --references--> `EncargadoService`  [EXTRACTED]
  src/main/java/com/hackaton/prog/controller/EncargadoController.java → src/main/java/com/hackaton/prog/service/EncargadoService.java
- `MenuControllerTest` --references--> `MenuController`  [EXTRACTED]
  src/test/java/com/hackaton/prog/MenuControllerTest.java → src/main/java/com/hackaton/prog/controller/MenuController.java
- `RecepcionController` --references--> `RecepcionService`  [EXTRACTED]
  src/main/java/com/hackaton/prog/controller/RecepcionController.java → src/main/java/com/hackaton/prog/service/RecepcionService.java

## Import Cycles
- None detected.

## Communities (45 total, 14 thin omitted)

### Community 0 - "MovimientoService"
Cohesion: 0.11
Nodes (18): org.springframework.data.jpa.repository.JpaRepository, org.springframework.data.jpa.repository.Query, org.springframework.stereotype.Repository, org.springframework.stereotype.Service, org.springframework.transaction.annotation.Transactional, ArticuloRepository, CampaniaRepository, CentroCampaniaRepository (+10 more)

### Community 1 - "Movimiento"
Cohesion: 0.09
Nodes (7): Donante, Entity, Table, Entity, PrePersist, Table, Movimiento

### Community 2 - "CatalogosEncargadoDTO"
Cohesion: 0.07
Nodes (4): ArticuloStockDTO, CatalogosCoordinadorDTO, CatalogosEncargadoDTO, OpcionSimpleDTO

### Community 3 - "RecepcionRequestDTO"
Cohesion: 0.07
Nodes (3): PostMapping, RecepcionRequestDTO, RecepcionResponseDTO

### Community 4 - "ResumenRecepcionDTO"
Cohesion: 0.07
Nodes (6): GetMapping, RequestMapping, RestController, RecepcionController, ArticuloItemDTO, ResumenRecepcionDTO

### Community 6 - "ErrorResponse"
Cohesion: 0.09
Nodes (9): MethodArgumentTypeMismatchException, org.springframework.web.bind.annotation.ExceptionHandler, org.springframework.web.bind.annotation.RestControllerAdvice, org.springframework.web.method.annotation.MethodArgumentTypeMismatchException, ErrorResponse, AccesoDenegadoException, CredencialesInvalidasException, GlobalExceptionHandler (+1 more)

### Community 8 - "RolUsuario"
Cohesion: 0.18
Nodes (7): desdeValorDb(), RolUsuario, COORDINADOR, ENCARGADO, INSTITUCION, LIDER, VOLUNTARIO

### Community 9 - "org.junit.jupiter.api.Test"
Cohesion: 0.20
Nodes (8): org.junit.jupiter.api.BeforeEach, org.junit.jupiter.api.DisplayName, org.junit.jupiter.api.Test, org.springframework.boot.test.context.SpringBootTest, org.springframework.test.web.servlet.MockMvc, MenuControllerTest, ProgApplicationTests, RecepcionControllerTest

### Community 11 - "Usuario"
Cohesion: 0.12
Nodes (4): PostMapping, Entity, Table, Usuario

### Community 13 - "Campania"
Cohesion: 0.08
Nodes (4): GuardarCampaniaRequest, Campania, Entity, Table

### Community 15 - "Transferencia"
Cohesion: 0.07
Nodes (11): EstadoTransferenciaConverter, desdeValorDb(), EstadoTransferencia, CANCELADA, COMPLETADA, PENDIENTE, Entity, PrePersist (+3 more)

### Community 16 - "Guía de Especificación de Operaciones, Parámetros y Validaciones del Backend"
Cohesion: 0.04
Nodes (46): 1. Módulo de Autenticación y Contexto de Sesión, 2. Módulo de Administración de Usuarios (RBAC), 3. Módulo de Gestión de Centros de Acopio y Campañas, 4. Módulo de Catálogos y Selectores de Interfaz (Dropdowns), 5. Módulo de Operaciones de Inventario en Campo (Core Ledger), 6. Módulo de Institución Receptora (Consulta Externa y Confirmación), 7. Módulo de Auditoría, Kárdex y Control de Existencias, 8. Módulo de Dashboards y Analítica Ejecutiva (Soporte a la Decisión) (+38 more)

### Community 18 - "TipoMovimiento"
Cohesion: 0.14
Nodes (9): desdeValorDb(), TipoMovimiento, AJUSTE_NEGATIVO, AJUSTE_POSITIVO, ENTREGA, MERMA, RECEPCION, TRANSFERENCIA_ENTRADA (+1 more)

### Community 19 - "3.6 Catálogo y Guía Operativa de los 31 Procedimientos Almacenados"
Cohesion: 0.05
Nodes (40): 3.6 Catálogo y Guía Operativa de los 31 Procedimientos Almacenados, MÓDULO 1: Core de Inventario y Movimientos de Almacén, MÓDULO 2: Autenticación, Sesión y RBAC, MÓDULO 3: Gestión Maestra de Centros y Campañas, MÓDULO 4: Catálogos y Selectores Dinámicos (Dropdowns de Formularios), MÓDULO 5: Institución Receptora y Confirmaciones Externas, MÓDULO 6: Kárdex, Auditoría y Stock en Vivo, MÓDULO 7: Dashboards y Analítica Ejecutiva (+32 more)

### Community 21 - "Contexto del Proyecto: Sistema de Registro y Coordinación de Centros de Acopio (<in>Hack)"
Cohesion: 0.06
Nodes (31): 10.1 Innovación (25 pts), 10.2 Calidad Técnica (25 pts), 10.3 Impacto y Adecuación al Reto (25 pts), 10.4 Presentación y Demo (25 pts), 10. Rúbrica de Evaluación (<in>Hack - 100 Puntos), 11. Entregables Obligatorios, 1.1 Antecedentes, 1.2 La Problemática Actual (+23 more)

### Community 22 - "InstitucionReceptora"
Cohesion: 0.15
Nodes (3): InstitucionReceptora, Entity, Table

### Community 23 - "CentroCampaniaId"
Cohesion: 0.18
Nodes (3): jakarta.persistence.Embeddable, CentroCampaniaId, Override

### Community 25 - "3.4 Diccionario de Datos y Especificación DDL"
Cohesion: 0.06
Nodes (30): 1.1 Contexto y Problemática, 1.2 Objetivo del Sistema, 1. Introducción y Visión General del Proyecto, 1. Tabla: `instituciones_receptoras`, 2. Mapa Arquitectónico de la Documentación, 2. Tabla: `centros`, 3.1 Fundamentos y Decisiones de Diseño de la Base de Datos, 3.2 Correcciones Técnicas Recientes y Adaptaciones Cloud (Clever Cloud) (+22 more)

### Community 26 - "MenuService"
Cohesion: 0.15
Nodes (8): AuthController, RequestMapping, RestController, GetMapping, RequestMapping, RestController, MenuController, MenuService

### Community 27 - "Centro"
Cohesion: 0.15
Nodes (3): Centro, Entity, Table

### Community 28 - "🚀 Instrucciones de Instalación y Ejecución"
Cohesion: 0.18
Nodes (10): 1. Prerrequisitos, 2. Clonar el repositorio desde GitHub, 3. Configuración de Base de Datos, 4. Compilar y Ejecutar el Proyecto, 5. Acceder a la Aplicación, 🤖 Declaración de Uso de Herramientas de IA, 🛠️ Herramientas y Stack Tecnológico, 🚀 Instrucciones de Instalación y Ejecución (+2 more)

### Community 30 - "Articulo"
Cohesion: 0.17
Nodes (3): Articulo, Entity, Table

### Community 31 - "CentroCampania"
Cohesion: 0.20
Nodes (3): CentroCampania, Entity, Table

### Community 32 - "mvnw"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 33 - "Getting Started"
Cohesion: 0.40
Nodes (4): Getting Started, Guides, Maven Parent overrides, Reference Documentation

### Community 38 - "Override"
Cohesion: 0.26
Nodes (8): jakarta.persistence.AttributeConverter, jakarta.persistence.Converter, CategoriaArticuloConverter, EnumsConverters, Override, RolUsuarioConverter, TipoMovimientoConverter, UnidadMedidaConverter

### Community 39 - "MotivoMovimiento"
Cohesion: 0.18
Nodes (9): MotivoMovimientoConverter, desdeValorDb(), MotivoMovimiento, CADUCIDAD, CORRECCION_CONTEO, DANO, ERROR_CAPTURA, OTRO (+1 more)

### Community 40 - "EncargadoController"
Cohesion: 0.23
Nodes (5): EncargadoController, GetMapping, RequestMapping, RestController, EncargadoControllerTest

### Community 41 - "UnidadMedida"
Cohesion: 0.18
Nodes (7): desdeValorDb(), UnidadMedida, BOLSA, CAJA, KG, L, PIEZA

### Community 42 - "CategoriaArticulo"
Cohesion: 0.18
Nodes (8): CategoriaArticulo, LIMPIEZA, MEDICAMENTO, NO_PERECEDERO, OTRO, PERECEDERO, ROPA, desdeValorDb()

### Community 43 - "org.springframework.http.ResponseEntity"
Cohesion: 0.33
Nodes (5): org.springframework.http.ResponseEntity, CoordinadorController, GetMapping, RequestMapping, RestController

## Knowledge Gaps
- **159 isolated node(s):** `com.hackaton:prog`, `NO_PERECEDERO`, `PERECEDERO`, `ROPA`, `LIMPIEZA` (+154 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **14 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Movimiento` connect `Movimiento` to `MovimientoService`, `MotivoMovimiento`, `Usuario`, `Campania`, `Transferencia`, `TipoMovimiento`, `InstitucionReceptora`, `Centro`, `Articulo`?**
  _High betweenness centrality (0.109) - this node is a cross-community bridge._
- **Why does `Centro` connect `Centro` to `MovimientoService`, `Movimiento`, `RolUsuario`, `Usuario`, `GuardarCentroRequest`, `Campania`, `Transferencia`, `.registrarCentro`, `CentroCampania`?**
  _High betweenness centrality (0.066) - this node is a cross-community bridge._
- **Why does `Usuario` connect `Usuario` to `MovimientoService`, `Movimiento`, `RolUsuario`, `GuardarCentroRequest`, `Campania`, `InstitucionReceptora`, `Centro`, `AuthController.java`?**
  _High betweenness centrality (0.065) - this node is a cross-community bridge._
- **What connects `com.hackaton:prog`, `NO_PERECEDERO`, `PERECEDERO` to the rest of the system?**
  _159 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `MovimientoService` be split into smaller, more focused modules?**
  _Cohesion score 0.11010284331518451 - nodes in this community are weakly interconnected._
- **Should `Movimiento` be split into smaller, more focused modules?**
  _Cohesion score 0.08925979680696662 - nodes in this community are weakly interconnected._
- **Should `CatalogosEncargadoDTO` be split into smaller, more focused modules?**
  _Cohesion score 0.06533776301218161 - nodes in this community are weakly interconnected._