# Etapa 1: Construir el proyecto
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copiamos todo el contenido
COPY . .

# Damos permisos y compilamos
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: Ejecutar el proyecto
FROM eclipse-temurin:17-jre
WORKDIR /app

# Esta es la linea clave: 
# Copiamos el archivo generado desde la carpeta 'target' al nombre 'app.jar'
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto y encendemos
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]