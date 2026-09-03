# Sistema de Registro y Coordinación de Centros de Acopio (<in>Hack)

> **Proyecto desarrollado por el equipo *fsociety* para el hackatón.**  
> Plataforma centralizada para la gestión, trazabilidad y balance de inventario en tiempo real para centros de acopio durante contingencias humanitarias y campañas de beneficencia.

---

## Problemática y Solución

En situaciones de emergencia o contingencia, los centros de acopio suelen operar de forma aislada mediante registros manuales o dispersos, provocando:
* Falta de visibilidad en tiempo real del stock global de insumos.
* Cero trazabilidad sobre el destino, donantes o mermas de productos.
* Asimetría logística (centros saturados mientras otros sufren desabasto).

**Nuestra Solución:**
*(Pendiente de definir por el equipo)*

---

## 👥 Organización y Reparto de Trabajo del Equipo

| Integrante | Rol / Responsabilidad | Tareas Asignadas |
| :--- | :--- | :--- |
| *Por definir* | **Backend** | Lógica de negocio, servicios, API REST y conexión JPA/Hibernate. |
| *Por definir* | **Base de Datos** | Diseño del modelo EER en MySQL Workbench, scripts SQL y mantenimiento en TiDB Cloud. |
| *Por definir* | **Frontend / UI** | Diseño visual, maquetación HTML/CSS, integración con Bootstrap y experiencia de usuario. |

---

## 🛠️ Stack Tecnológico y Herramientas

| Capa / Área | Tecnología / Herramienta | Propósito |
| :--- | :--- | :--- |
| **Backend** | **Java 17 & Spring Boot** | Lógica de negocio, controladores REST/MVC, reglas de validación de inventario y auditoría. |
| **Persistencia / ORM** | **Spring Data JPA & Hibernate** | Mapeo objeto-relacional y operaciones seguras sobre la base de datos. |
| **Base de Datos (Cloud)**| **TiDB Cloud (Serverless)** | Base de datos MySQL distribuida de alta disponibilidad alojada en la nube para acceso colaborativo 24/7 y despliegue sin depender de `localhost`. |
| **Modelado de BD** | **MySQL Workbench** | Diseño conceptual, diagramación EER y generación de scripts relacionales del esquema. |
| **Frontend** | **HTML5, CSS3, JavaScript & Bootstrap** | Interfaz de usuario intuitiva, responsiva y accesible para voluntarios y encargados de sede. |
| **Motor de Vistas** | **Thymeleaf** | Renderizado de vistas del lado del servidor integrado con Spring Boot. |
| **Entorno & Asistencia IA** | **Antigravity IDE (Gemini 3.8 Flash)** | Pair programming, diseño de arquitectura, generación de scripts de automatización y optimización de código. |
| **Control de Versiones** | **Git & GitHub** | Repositorio central colaborativo con integración continua. |

---

## 🤖 Declaración de Uso de Herramientas de IA

En cumplimiento con las normas del evento y la transparencia técnica:
* **Herramienta utilizada:** **Antigravity IDE**, potenciado por el modelo **Gemini 3.8 Flash**.
* **Propósito y alcance:**
  * Apoyo en la arquitectura del backend y configuración del entorno de desarrollo.
  * Generación y revisión de código boilerplate, modelos JPA y pruebas unitarias.
  * Consultoría técnica para optimización de queries y validación de reglas de negocio.
  * Toda la lógica de dominio, requerimientos de negocio y arquitectura son supervisados y validados por el equipo humano.

---

## 👥 Matriz de Roles del Sistema (RBAC)

* **Coordinador General:** Gestión global de campañas y centros, acceso al dashboard general con comparativas y reportes de desperdicio.
* **Encargado de Centro:** Operación de sede específica (recepciones, entregas, mermas con motivo obligatorio, transferencias y ajustes).
* **Voluntario de Centro:** Operativa de campo para captura rápida de recepciones y entregas estándar.
* **Institución Receptora:** Consulta externa para verificar y confirmar insumos canalizados.
* **Donante:** Participación comunitaria sin necesidad de registro previo (donaciones nominales o anónimas).

---

## 📋 Checklist de Criterios de Aceptación del MVP

- [ ] **1. Registro Maestro:** Alta y configuración de centros de acopio y campañas.
- [ ] **2. Recepciones:** Captura de donaciones con actualización inmediata de stock.
- [ ] **3. Entregas:** Canalización de insumos a beneficiarios con validación de no stock negativo.
- [ ] **4. Mermas con motivo:** Registro justificado obligatorio (`CADUCIDAD`, `DAÑO`, `PERDIDA`).
- [ ] **5. Transferencias:** Traspaso atómico de insumos entre centros activos.
- [ ] **6. Ajustes de inventario:** Corrección justificada de discrepancias físicas.
- [ ] **7. Dashboard global:** Indicadores consolidados y reportes agregados para coordinación.
- [ ] **8. Despliegue externo:** Ejecución independiente accesible fuera de la red local.

---

## 🚀 Cómo Ejecutar el Proyecto Localmente

### Prerrequisitos
* Java JDK 17 o superior instalado.
* Conexión a internet (para sincronización con TiDB Cloud y dependencias Maven).

### Pasos
1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/Mauh28/proyecto_hackaton_fsociety.git
   cd proyecto_hackaton_fsociety
   ```

2. **Configurar las credenciales de base de datos:**
   Revisa el archivo `src/main/resources/application.properties` con los parámetros de conexión correspondientes a TiDB Cloud.

3. **Compilar y arrancar la aplicación:**
   * En Windows:
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
   * En Linux/macOS:
     ```bash
     ./mvnw spring-boot:run
     ```

4. **Acceder a la plataforma:**
   Abre tu navegador en `http://localhost:8080`.
