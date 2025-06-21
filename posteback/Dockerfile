# Use a imagem oficial do Maven com OpenJDK 17 para o build
FROM maven:3.9-eclipse-temurin-17 AS build

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos do projeto (agora estamos dentro da pasta posteback)
COPY pom.xml ./
COPY src ./src/

# Fazer o build da aplicação
RUN mvn clean package -DskipTests

# Debug: Mostrar o que foi gerado
RUN echo "=== ARQUIVOS GERADOS ===" && ls -la target/

# Estágio final - imagem mínima para execução
FROM eclipse-temurin:17-jre-alpine

# Instalar curl para health check
RUN apk add --no-cache curl

# Criar diretório para a aplicação
WORKDIR /app

# Copiar o JAR do estágio de build
COPY --from=build /app/target/vendas-postes-1.0.0.jar app.jar

# Expor a porta 8080
EXPOSE 8080

# Variáveis de ambiente padrão (serão sobrescritas pelo Render)
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/postes || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]