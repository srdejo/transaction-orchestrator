# Estrategia de CI/CD

Este proyecto sigue un enfoque moderno de DevOps para garantizar una alta calidad y ciclos de retroalimentación rápidos.

## Principios Core
- **Trunk-Based Development**: Trabajamos con ramas de corta duración que se integran frecuentemente a `main`.
- **Validación Automatizada**: No se permite la integración de código sin que pase la suite completa de pruebas y análisis estático.
- **Infraestructura Inmutable**: Utilizamos Docker para empaquetar la aplicación, asegurando la consistencia en todos los entornos.

## Pipeline de CI (GitHub Actions)
El pipeline se dispara con cada Pull Request y cada integración en `main`.

1.  **Build & Lint**: Verifica la compilación y hace cumplir los estándares de codificación (Checkstyle/SonarQube).
2.  **Pruebas Unitarias**: Ejecuta todas las pruebas unitarias con reporte de cobertura mediante JaCoCo.
3.  **Pruebas de Integración**: Utiliza **Testcontainers** para levantar instancias reales de PostgreSQL y Redis, verificando las migraciones de base de datos y la lógica de los repositorios.
4.  **Pruebas de Contrato**: Valida que el código generado siga coincidiendo con la especificación OpenAPI.
5.  **Generación de Artefactos**: Construye el shadow JAR y la imagen de Docker.

## Pipeline de CD
- **Staging**: Despliegue automático a un entorno de staging para validación de QA.
- **Producción**: Disparo manual o despliegue automático (Continuous Deployment) tras la validación exitosa en staging.

## Herramientas
- **GitHub Actions**: Orquestación.
- **Gradle**: Construcción y gestión de dependencias.
- **SonarCloud**: Análisis estático y escaneo de seguridad.
- **Docker**: Contenedores.
- **Flyway**: Evolución del esquema de base de datos.
