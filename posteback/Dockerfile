# Use a imagem oficial do Maven com OpenJDK 17 para o build
FROM maven:3.9-eclipse-temurin-17 AS build

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Maven primeiro (para cache de dependências)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Baixar dependências (será cacheado se o pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Fazer o build da aplicação
RUN mvn clean package -DskipTests

# Estágio final - imagem mínima para execução
FROM eclipse-temurin:17-jre-alpine

# Criar diretório para a aplicação
WORKDIR /app

# Copiar o JAR gerado do estágio de build
COPY --from=build /app/target/vendas-postes-1.0.0.jar app.jar

# Expor a porta 8080
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]