# Use uma imagem base do OpenJDK 17
FROM openjdk:17-jdk-slim

# Instalar Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Definir variáveis de ambiente para codificação
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV MAVEN_OPTS="-Dfile.encoding=UTF-8 -Dproject.build.sourceEncoding=UTF-8"

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos do projeto
COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn ./.mvn

# Dar permissão de execução para o mvnw
RUN chmod +x mvnw

# Limpar e construir o projeto
RUN mvn clean package -DskipTests -Dfile.encoding=UTF-8

# Expor porta
EXPOSE 8080

# Comando para executar a aplicação
CMD ["java", "-jar", "-Dfile.encoding=UTF-8", "target/vendas-postes-1.0.0.jar"]