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

# Criar application.properties atualizado
RUN echo "# Configuracao de producao" > src/main/resources/application.properties && \
    echo "server.port=\${PORT:8080}" >> src/main/resources/application.properties && \
    echo "spring.application.name=vendas-postes" >> src/main/resources/application.properties && \
    echo "spring.datasource.url=\${SPRING_DATASOURCE_URL:jdbc:h2:mem:testdb}" >> src/main/resources/application.properties && \
    echo "spring.datasource.username=\${SPRING_DATASOURCE_USERNAME:sa}" >> src/main/resources/application.properties && \
    echo "spring.datasource.password=\${SPRING_DATASOURCE_PASSWORD:}" >> src/main/resources/application.properties && \
    echo "spring.datasource.driver-class-name=\${SPRING_DATASOURCE_DRIVER:org.postgresql.Driver}" >> src/main/resources/application.properties && \
    echo "spring.jpa.hibernate.ddl-auto=\${SPRING_JPA_HIBERNATE_DDL_AUTO:validate}" >> src/main/resources/application.properties && \
    echo "spring.jpa.show-sql=\${SPRING_JPA_SHOW_SQL:false}" >> src/main/resources/application.properties && \
    echo "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect" >> src/main/resources/application.properties && \
    echo "spring.datasource.hikari.maximum-pool-size=10" >> src/main/resources/application.properties && \
    echo "spring.datasource.hikari.minimum-idle=2" >> src/main/resources/application.properties && \
    echo "logging.level.com.vendas.postes=INFO" >> src/main/resources/application.properties

# Limpar e construir o projeto
RUN mvn clean package -DskipTests \
    -Dfile.encoding=UTF-8 \
    -Dproject.build.sourceEncoding=UTF-8 \
    -Dproject.reporting.outputEncoding=UTF-8

# Expor porta (será definida pelo Render)
EXPOSE ${PORT:-8080}

# Comando para executar a aplicação com variáveis de ambiente
CMD ["sh", "-c", "java -jar -Dfile.encoding=UTF-8 -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-production} -Dserver.port=${PORT:-8080} target/vendas-postes-1.0.0.jar"]