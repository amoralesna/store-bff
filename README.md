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
