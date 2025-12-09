# store-bff

## Descripcion

store-bff es un backend-for-frontend (Spring Boot + WebFlux) que realiza llamadas un servicio externo para obtener informacion de productos similares.

## Requisitos

- Java 21
- Maven (puedes usar el wrapper incluido `mvnw` / `mvnw.cmd`) o una instalación global de Maven (v3.9.9)

## Inicio rapido

1) Ejecutar desde la raiz del proyecto:

    .\mvnw.cmd spring-boot:run

   Alternativa (Maven global):

    mvn spring-boot:run

2) Compilar y empaquetar (sin tests)

    .\mvnw.cmd -DskipTests package

   Salida:

    target/store-bff-0.0.1-SNAPSHOT.jar

3) Ejecutar JAR generado

    java -jar target\store-bff-0.0.1-SNAPSHOT.jar

4) Ejecutar tests

    .\mvnw.cmd test

## Notas

Para el desarrollo de la aplicacion he creado una arquitectura escalable y limipia.
Me he decantado por una arquitectura hexagonal con vertical slicing, lo cual minimiza el acoplamiento permitiendo la separacion por features y dentro de la misma por dominio, aplicacion e infraestructura.
Tambien he creido conveniente separar aquella funcionalidad que seria compartida por otras features en un modulo shared como componentes para servicios externos o configuracion.

Para facilitar el escalado y mantenibilidad, he extraido al archivo configuracion algunas propiedades, evitando que esten harcoded en el codigo.
Esto facilita el ajuste de las mismas en un entorno con gestion de configuracion centralizada, comun en arquitecturas de microservicios.

Comencé analizando el problema y haciendome una idea inicial de los componentes que necesitaria y su organizacion.

- Primero me centré en la logica de negocio implementando el caso de uso, despues la api que consumiria el front y seguidamente la que consumiria la aplicacion.

- Segundo la conexion con el servicio externo.
Para ello utilicé webclient con una configuracion por defecto para centralizar la configuracion y mantener limpios los adaptadores de conexion a otros servicios.
En una primera version use el modelo bloqueante.

- Despues desarrollé la gestion de errores añadiendo un @ControllerAdvice para centralizar los errores.

- Seguidamente de realizar las pruebas de carga proporcionadas, cambie la implementacion a un modelo reactivo para mejorar el rendimiento ajustando los paramentros de concurrencia en llamadas y timeouts.

- Finalmente he simplificado el diseño y añadido varias mejoras para incrementar el rendimiento y la tolerancia a fallos:
  - Cache en memoria (por simplicidad) con caffeine para evitar llamadas repetidas al servicio externo en un corto periodo de tiempo.
  - Un pool de conexiones http para mejorar la gestion de conexiones con el servicio externo.
  - CircuitBreaker para evitar saturacion del servicio externo y mejorar la resiliencia.
  - Timelimiter para evitar esperas largas en el servicio externo.
  - Ratelimiter para limitar el numero de peticiones al servicio externo en un periodo de tiempo.

