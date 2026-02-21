# Spring Boot Question Management System

Sistema de gestión de preguntas y evaluaciones desarrollado con Spring Boot, MySQL y MongoDB.

## Requisitos Previos

- Java 17 o superior
- Maven 3.6+
- Docker y Docker Compose (recomendado)
- **O** MySQL 8.0+ y MongoDB 7.0+ instalados localmente

## Inicio Rápido con Docker

### 1. Iniciar las bases de datos

```bash
docker-compose up -d
```

Esto iniciará:
- **MySQL** en `localhost:3306`
- **MongoDB** en `localhost:27017`
- **phpMyAdmin** en `http://localhost:8082` (usuario: `root`, contraseña: `root`)
- **Mongo Express** en `http://localhost:8081` (usuario: `admin`, contraseña: `admin123`)

### 2. Ejecutar la aplicación

```bash
# Con Maven Wrapper (Windows)
.\mvnw.cmd spring-boot:run

# O compilar y ejecutar el JAR
.\mvnw.cmd clean package
java -jar target/trabajoFinal-1-0.0.1-SNAPSHOT.jar
```

### 3. Acceder a la aplicación

- **Aplicación Web**: http://localhost:8080
- **API Swagger**: http://localhost:8080/swagger-ui.html

### 4. Credenciales por defecto

La aplicación crea automáticamente usuarios de prueba:

- **Admin**: username=`admin`, password=`admin123`
- **Usuario**: username=`user`, password=`user123`

## Detener las bases de datos

```bash
docker-compose down

# Para eliminar también los datos
docker-compose down -v
```

## Sin Docker (Instalación Manual)

### Configurar MySQL

```sql
CREATE DATABASE questiondb;
```

### Configurar MongoDB

MongoDB no requiere creación previa de base de datos.

### Actualizar application.properties

Si usas credenciales diferentes, edita `src/main/resources/application.properties`:

```properties
spring.datasource.password=TU_PASSWORD_MYSQL
```

## Estructura del Proyecto

```
trabajoFinal-1/
├── src/main/java/com/miempresa/miprimertfg/
│   ├── model/              # Entidades JPA y MongoDB
│   ├── repository/         # Repositorios de datos
│   ├── service/            # Lógica de negocio
│   ├── controller/         # Controladores web
│   │   └── api/           # Controladores REST API
│   ├── security/          # Configuración de seguridad
│   ├── config/            # Configuraciones
│   └── dto/               # Data Transfer Objects
├── src/main/resources/
│   ├── templates/         # Plantillas Thymeleaf
│   ├── static/           # Recursos estáticos (CSS, JS)
│   └── application.properties
├── docker-compose.yml     # Configuración Docker
└── pom.xml               # Dependencias Maven
```

## API REST Endpoints

### Autenticación

- `POST /api/auth/login` - Iniciar sesión (obtener JWT)
- `POST /api/auth/register` - Registrar usuario

### Preguntas

- `GET /api/questions` - Listar preguntas (paginado)
- `GET /api/questions/{id}` - Obtener pregunta por ID
- `POST /api/questions` - Crear pregunta (requiere autenticación)
- `PUT /api/questions/{id}` - Actualizar pregunta (requiere ADMIN)
- `DELETE /api/questions/{id}` - Eliminar pregunta (requiere ADMIN)
- `GET /api/questions/random` - Obtener preguntas aleatorias

### Tests

- `POST /api/tests/generate` - Generar nuevo test
- `POST /api/tests/submit` - Enviar respuestas
- `GET /api/tests/results` - Ver resultados
- `GET /api/tests/statistics` - Ver estadísticas

### Usuarios

- `GET /api/users/profile` - Ver perfil
- `PUT /api/users/profile` - Actualizar perfil
- `PUT /api/users/password` - Cambiar contraseña
- `GET /api/users` - Listar usuarios (requiere ADMIN)

## Características Implementadas

✅ Herencia en modelo de dominio (JOINED)  
✅ Configuración de base de datos dual (MySQL + MongoDB)  
✅ Paginación en listados  
✅ Seguridad con Spring Security  
✅ Autenticación JWT para API  
✅ Documentación Swagger/OpenAPI  
✅ Subida de archivos CSV/JSON  
✅ Integración con API externa  
✅ Fragmentos reutilizables en plantillas  
✅ Páginas de error personalizadas

## Tecnologías

- Spring Boot 3.5.10
- Spring Security
- Spring Data JPA
- Spring Data MongoDB
- Thymeleaf
- Bootstrap 5
- Swagger/OpenAPI
- JWT (jjwt)
- Lombok

## Desarrollo

Para más detalles sobre la implementación y próximos pasos, consulta los documentos de artifacts en `.gemini/antigravity/brain/`.

## Licencia

Proyecto académico - Universidad
