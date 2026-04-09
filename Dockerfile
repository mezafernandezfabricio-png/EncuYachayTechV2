# Etapa 1: Construir la aplicación
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copiamos todo el contenido del repositorio
COPY . .

# Buscamos el archivo mvnw donde sea que esté y le damos permisos
RUN find . -name "mvnw" -exec chmod +x {} +

# Ejecutamos la construcción (esto genera el archivo .jar)
RUN find . -name "mvnw" -exec {} clean package -DskipTests \;

# Etapa 2: Ejecutar la aplicación
FROM eclipse-temurin:17-jre
WORKDIR /app

# Buscamos el archivo .jar generado y lo copiamos para ejecutarlo
COPY --from=build /app/**/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]