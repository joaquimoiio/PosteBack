# Use a imagem oficial do Maven com OpenJDK 17 para o build
FROM maven:3.9-eclipse-temurin-17 AS build

# Definir diretório de trabalho
WORKDIR /app

# Copiar todos os arquivos do contexto de build
COPY . .

# Verificar se o pom.xml existe (para debug)
RUN ls -la && echo "Verificando se pom.xml existe..." && test -f pom.xml || (echo "❌ pom.xml não encontrado!" && exit 1)

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