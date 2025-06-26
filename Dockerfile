# Use uma imagem base do OpenJDK 17
FROM openjdk:17-jdk-slim

# Instalar Maven e curl
RUN apt-get update && apt-get install -y maven curl && rm -rf /var/lib/apt/lists/*

# Definir variáveis de ambiente para codificação
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV MAVEN_OPTS="-Dfile.encoding=UTF-8 -Dproject.build.sourceEncoding=UTF-8"

# Definir diretório de trabalho
WORKDIR /app

# Primeiro, copiar apenas o pom.xml para cache de dependências
COPY posteback/pom.xml .

# Baixar dependências (será cacheado se pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copiar o código fonte
COPY posteback/src ./src

# Copiar arquivos do Maven Wrapper (se existirem)
COPY posteback/mvnw* ./
COPY posteback/.mvn ./.mvn

# Dar permissão de execução para o mvnw (se existir)
RUN if [ -f "./mvnw" ]; then chmod +x mvnw; fi

# Limpar e construir o projeto usando Maven instalado
RUN mvn clean package -DskipTests -Dfile.encoding=UTF-8

# Expor porta
EXPOSE 8080

# Comando para executar a aplicação
CMD ["java", "-jar", "-Dfile.encoding=UTF-8", "target/vendas-postes-1.0.0.jar"]