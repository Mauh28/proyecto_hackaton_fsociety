# Sistema de Registro y Coordinación de Centros de Acopio (<in>Hack)

> **Proyecto desarrollado por el equipo *fsociety* para el hackatón.**  
> Plataforma web para la gestión, trazabilidad y coordinación de centros de acopio en situaciones de emergencia y campañas benéficas.

---

## 🔗 Repositorio Oficial en GitHub

* **Repositorio:** [https://github.com/Mauh28/proyecto_hackaton_fsociety](https://github.com/Mauh28/proyecto_hackaton_fsociety)
* **Rama principal:** `main`

---

## 🛠️ Herramientas y Stack Tecnológico

| Componente | Tecnología / Herramienta | Uso en el Proyecto |
| :--- | :--- | :--- |
| **Backend** | **Java 17 & Spring Boot** | Lógica de negocio, API REST, validaciones y arquitectura de servicios. |
| **Persistencia / ORM** | **Spring Data JPA & Hibernate** | Mapeo y consultas a la base de datos relacional. |
| **Base de Datos (Cloud)** | **TiDB Cloud (Serverless)** | Base de datos MySQL distribuida de alta disponibilidad alojada en la nube para acceso colaborativo 24/7 y despliegue sin depender de `localhost`. |
| **Modelado de BD** | **MySQL Workbench** | Diseño relacional, creación y ejecución del script de tablas y procedimientos. |
| **Frontend** | **HTML5, CSS3, JavaScript & Bootstrap** | Interfaz de usuario responsiva para voluntarios y encargados de centro. |
| **Motor de Plantillas** | **Thymeleaf** | Renderizado de vistas dinámicas integrado con Spring Boot. |
| **Entorno & Asistencia IA** | **Antigravity IDE (Gemini 3.8 Flash)** | Asistente de pair programming, arquitectura y depuración de código. |
| **Control de Versiones** | **Git & GitHub** | Control de versiones y colaboración del equipo en tiempo real. |

---

## 🤖 Declaración de Uso de Herramientas de IA

* **Herramienta:** **Antigravity IDE** con el modelo **Gemini 3.8 Flash**.
* **Propósito:** Apoyo técnico en diseño de arquitectura, depuración de código y optimización de flujos. La lógica de negocio, requerimientos del evento y validaciones son dirigidas por el equipo.

---

## 🚀 Instrucciones de Instalación y Ejecución

### 1. Prerrequisitos
* **Java JDK 17** o superior instalado en el equipo.
* **Git** instalado.
* Conexión a internet (para descargar dependencias Maven y conectarse a Clever Cloud).

---

### 2. Clonar el repositorio desde GitHub
Abre tu terminal y ejecuta:
```bash
git clone https://github.com/Mauh28/proyecto_hackaton_fsociety.git
cd proyecto_hackaton_fsociety
```

---

### 3. Configuración de Base de Datos
La conexión a la base de datos en la nube ya se encuentra preconfigurada en el archivo `src/main/resources/application.properties`:
* **Host:** `gateway01.us-east-1.prod.aws.tidbcloud.com`
* **Base de datos:** `centros_acopio`
* **Puerto:** `4000`

*(Si necesitas cambiar credenciales, edita directamente `src/main/resources/application.properties`).*

---

### 4. Compilar y Ejecutar el Proyecto

* **En Windows (PowerShell / CMD):**
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```

* **En Linux / macOS:**
  ```bash
  ./mvnw spring-boot:run
  ```

---

### 5. Acceder a la Aplicación
Una vez que el servidor termine de iniciar en la consola (`Started ProgApplication in ...`), abre tu navegador web e ingresa a:

👉 **[http://localhost:8080](http://localhost:8080)**
