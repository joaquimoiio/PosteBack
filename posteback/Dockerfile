# Use a imagem oficial do Maven com OpenJDK 17 para o build
FROM maven:3.9-eclipse-temurin-17 AS build

# Definir diretório de trabalho
WORKDIR /app

# Copiar tudo que está no contexto
COPY . .

# Debug: Mostrar o que foi copiado
RUN echo "=== ARQUIVOS COPIADOS ===" && find . -name "pom.xml" -o -name "src" -type d

# Tentar encontrar o pom.xml e entrar no diretório correto
RUN if [ -f "./pom.xml" ]; then \
        echo "✅ pom.xml encontrado na raiz"; \
    elif [ -f "./posteback/pom.xml" ]; then \
        echo "✅ pom.xml encontrado em posteback/"; \
        cd ./posteback; \
    else \
        echo "❌ pom.xml não encontrado em lugar nenhum!"; \
        ls -la; \
        exit 1; \
    fi

# Fazer o build - ajustar o diretório se necessário
RUN if [ -f "./pom.xml" ]; then \
        mvn clean package -DskipTests; \
    elif [ -f "./posteback/pom.xml" ]; then \
        cd ./posteback && mvn clean package -DskipTests; \
    else \
        echo "❌ Não foi possível fazer o build!"; \
        exit 1; \
    fi

# Encontrar o JAR gerado
RUN echo "=== PROCURANDO JAR ===" && find . -name "*.jar" -type f

# Estágio final - imagem mínima para execução
FROM eclipse-temurin:17-jre-alpine

# Criar diretório para a aplicação
WORKDIR /app

# Copiar o JAR do caminho correto (onde foi encontrado)
COPY --from=build /app/posteback/target/vendas-postes-1.0.0.jar app.jar

# Variáveis de ambiente padrão (podem ser sobrescritas)
ENV DATABASE_URL=jdbc:postgresql://dpg-d1bg2895pdvs73dsavg0-a:5432/ativaposte
ENV DATABASE_USERNAME=admin
ENV DATABASE_PASSWORD=aa303O04NteXaSLTrM0uOQ14Q2VnRKIw
ENV PORT=5432

# Expor a porta 8080
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]