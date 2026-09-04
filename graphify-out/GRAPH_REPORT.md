# Graph Report - proyecto_hackaton_fsociety  (2026-09-03)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 789 nodes · 1574 edges · 36 communities (23 shown, 13 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 208 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `1edb4c5c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Community 0
- Community 1
- Community 2
- Community 3
- Community 4
- Community 5
- Community 6
- Community 7
- Community 8
- Community 9
- Community 10
- Community 11
- Community 12
- Community 13
- Community 14
- Community 15
- Community 16
- Community 17
- Community 18
- Community 19
- Community 20
- Community 21
- Community 22
- Community 23
- Community 24
- Community 25
- Community 26
- Community 27
- Community 28
- Community 29
- Community 30
- Community 31
- Community 32
- Community 33
- Community 34
- Community 35

## God Nodes (most connected - your core abstractions)
1. `Movimiento` - 62 edges
2. `Campania` - 44 edges
3. `Centro` - 42 edges
4. `Usuario` - 40 edges
5. `Transferencia` - 35 edges
6. `MovimientoService` - 29 edges
7. `RecepcionRequestDTO` - 29 edges
8. `UsuarioContextoDTO` - 28 edges
9. `Articulo` - 27 edges
10. `RegistroMovimientoRequest` - 27 edges

## Surprising Connections (you probably didn't know these)
- `ArticuloRepository` --references--> `Articulo`  [EXTRACTED]
  src/main/java/com/hackaton/prog/repository/ArticuloRepository.java → src/main/java/com/hackaton/prog/model/Articulo.java
- `MovimientoService` --references--> `ArticuloRepository`  [EXTRACTED]
  src/main/java/com/hackaton/prog/service/MovimientoService.java → src/main/java/com/hackaton/prog/repository/ArticuloRepository.java
- `RecepcionService` --references--> `ArticuloRepository`  [EXTRACTED]
  src/main/java/com/hackaton/prog/service/RecepcionService.java → src/main/java/com/hackaton/prog/repository/ArticuloRepository.java
- `CampaniaRepository` --references--> `Campania`  [EXTRACTED]
  src/main/java/com/hackaton/prog/repository/CampaniaRepository.java → src/main/java/com/hackaton/prog/model/Campania.java
- `MovimientoService` --references--> `CampaniaRepository`  [EXTRACTED]
  src/main/java/com/hackaton/prog/service/MovimientoService.java → src/main/java/com/hackaton/prog/repository/CampaniaRepository.java

## Import Cycles
- None detected.

## Communities (36 total, 13 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.08
Nodes (20): org.springframework.data.jpa.repository.JpaRepository, org.springframework.data.jpa.repository.Query, org.springframework.stereotype.Repository, org.springframework.stereotype.Service, org.springframework.transaction.annotation.Transactional, StockInsuficienteException, ArticuloRepository, CampaniaRepository (+12 more)

### Community 1 - "Community 1"
Cohesion: 0.06
Nodes (17): Donante, Entity, Table, desdeValorDb(), TipoMovimiento, AJUSTE_NEGATIVO, AJUSTE_POSITIVO, ENTREGA (+9 more)

### Community 2 - "Community 2"
Cohesion: 0.06
Nodes (4): ArticuloStockDTO, CatalogosCoordinadorDTO, CatalogosEncargadoDTO, OpcionSimpleDTO

### Community 3 - "Community 3"
Cohesion: 0.07
Nodes (3): PostMapping, RecepcionRequestDTO, RecepcionResponseDTO

### Community 6 - "Community 6"
Cohesion: 0.11
Nodes (10): org.springframework.http.ResponseEntity, org.springframework.web.bind.annotation.ExceptionHandler, org.springframework.web.bind.annotation.RestControllerAdvice, CoordinadorController, GetMapping, RequestMapping, RestController, ErrorResponse (+2 more)

### Community 7 - "Community 7"
Cohesion: 0.09
Nodes (5): EncargadoController, PostMapping, RequestMapping, RestController, RegistroMovimientoRequest

### Community 8 - "Community 8"
Cohesion: 0.08
Nodes (8): LoginResponse, desdeValorDb(), RolUsuario, COORDINADOR, ENCARGADO, INSTITUCION, LIDER, VOLUNTARIO

### Community 9 - "Community 9"
Cohesion: 0.18
Nodes (9): org.junit.jupiter.api.BeforeEach, org.junit.jupiter.api.DisplayName, org.junit.jupiter.api.Test, org.springframework.boot.test.context.SpringBootTest, org.springframework.test.web.servlet.MockMvc, EncargadoControllerTest, MenuControllerTest, ProgApplicationTests (+1 more)

### Community 15 - "Community 15"
Cohesion: 0.12
Nodes (5): Entity, PrePersist, Table, Transferencia, TransferenciaRepository

### Community 17 - "Community 17"
Cohesion: 0.18
Nodes (5): desdeValorDb(), EstadoTransferencia, CANCELADA, COMPLETADA, PENDIENTE

### Community 18 - "Community 18"
Cohesion: 0.11
Nodes (17): jakarta.persistence.AttributeConverter, jakarta.persistence.Converter, EnumsConverters, EstadoTransferenciaConverter, Override, MotivoMovimientoConverter, RolUsuarioConverter, TipoMovimientoConverter (+9 more)

### Community 19 - "Community 19"
Cohesion: 0.31
Nodes (6): org.slf4j.Logger, org.springframework.jdbc.core.JdbcTemplate, RequestMapping, RestController, RecepcionController, RecepcionService

### Community 20 - "Community 20"
Cohesion: 0.15
Nodes (3): Campania, Entity, Table

### Community 21 - "Community 21"
Cohesion: 0.18
Nodes (9): CategoriaArticuloConverter, CategoriaArticulo, LIMPIEZA, MEDICAMENTO, NO_PERECEDERO, OTRO, PERECEDERO, ROPA (+1 more)

### Community 22 - "Community 22"
Cohesion: 0.13
Nodes (3): InstitucionReceptora, Entity, Table

### Community 23 - "Community 23"
Cohesion: 0.18
Nodes (3): jakarta.persistence.Embeddable, CentroCampaniaId, Override

### Community 26 - "Community 26"
Cohesion: 0.17
Nodes (8): AuthController, RequestMapping, RestController, GetMapping, RequestMapping, RestController, MenuController, MenuService

### Community 27 - "Community 27"
Cohesion: 0.15
Nodes (3): Centro, Entity, Table

### Community 28 - "Community 28"
Cohesion: 0.17
Nodes (3): Entity, Table, Usuario

### Community 29 - "Community 29"
Cohesion: 0.20
Nodes (3): Articulo, Entity, Table

### Community 30 - "Community 30"
Cohesion: 0.18
Nodes (7): desdeValorDb(), UnidadMedida, BOLSA, CAJA, KG, L, PIEZA

### Community 31 - "Community 31"
Cohesion: 0.18
Nodes (3): CentroCampania, Entity, Table

### Community 32 - "Community 32"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

## Knowledge Gaps
- **33 isolated node(s):** `CANCELADA`, `COMPLETADA`, `PENDIENTE`, `CADUCIDAD`, `CORRECCION_CONTEO` (+28 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **13 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Movimiento` connect `Community 1` to `Community 0`, `Community 15`, `Community 18`, `Community 19`, `Community 20`, `Community 22`, `Community 25`, `Community 27`, `Community 28`, `Community 29`?**
  _High betweenness centrality (0.193) - this node is a cross-community bridge._
- **Why does `Usuario` connect `Community 28` to `Community 0`, `Community 1`, `Community 8`, `Community 11`, `Community 12`, `Community 20`, `Community 22`, `Community 24`, `Community 27`?**
  _High betweenness centrality (0.098) - this node is a cross-community bridge._
- **Why does `Campania` connect `Community 20` to `Community 0`, `Community 1`, `Community 13`, `Community 15`, `Community 17`, `Community 25`, `Community 28`, `Community 31`?**
  _High betweenness centrality (0.094) - this node is a cross-community bridge._
- **What connects `CANCELADA`, `COMPLETADA`, `PENDIENTE` to the rest of the system?**
  _33 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.07506584723441616 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.06126331811263318 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.06262626262626263 - nodes in this community are weakly interconnected._