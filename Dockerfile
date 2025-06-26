# Use uma imagem base do OpenJDK 17
FROM openjdk:17-jdk-slim

# Instalar Maven, curl e ferramentas de encoding
RUN apt-get update && \
    apt-get install -y maven curl locales && \
    locale-gen en_US.UTF-8 && \
    rm -rf /var/lib/apt/lists/*

# Definir variáveis de ambiente para codificação
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US:en
ENV LC_ALL=en_US.UTF-8
ENV MAVEN_OPTS="-Dfile.encoding=UTF-8 -Dproject.build.sourceEncoding=UTF-8 -Dproject.reporting.outputEncoding=UTF-8"

# Definir diretório de trabalho
WORKDIR /app

# Copiar apenas o pom.xml primeiro para cache de dependências
COPY posteback/pom.xml ./

# Baixar dependências (será cacheado se pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copiar o código fonte
COPY posteback/src ./src

# Criar application.properties se não existir ou estiver corrompido
RUN echo "# Configuracao de producao" > src/main/resources/application.properties && \
    echo "server.port=8080" >> src/main/resources/application.properties && \
    echo "spring.application.name=vendas-postes" >> src/main/resources/application.properties && \
    echo "spring.jpa.hibernate.ddl-auto=update" >> src/main/resources/application.properties && \
    echo "spring.jpa.show-sql=false" >> src/main/resources/application.properties

# Limpar e construir o projeto
RUN mvn clean package -DskipTests \
    -Dfile.encoding=UTF-8 \
    -Dproject.build.sourceEncoding=UTF-8 \
    -Dproject.reporting.outputEncoding=UTF-8

# Expor porta
EXPOSE 8080

# Comando para executar a aplicação
CMD ["java", "-jar", "-Dfile.encoding=UTF-8", "-Dspring.profiles.active=production", "target/vendas-postes-1.0.0.jar"]