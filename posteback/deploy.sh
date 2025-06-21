#!/bin/bash

echo "🚀 Iniciando processo de deploy do Sistema de Vendas de Postes..."

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para verificar se comando existe
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Verificar se Maven está instalado
if ! command_exists mvn; then
    echo -e "${RED}❌ Maven não encontrado. Por favor, instale o Maven primeiro.${NC}"
    echo "Para instalar no Ubuntu/Debian: sudo apt install maven"
    echo "Para instalar no macOS: brew install maven"
    exit 1
fi

# Verificar se Docker está instalado
if ! command_exists docker; then
    echo -e "${RED}❌ Docker não encontrado. Por favor, instale o Docker primeiro.${NC}"
    echo "Visite: https://docs.docker.com/get-docker/"
    exit 1
fi

# Verificar se estamos no diretório correto (dentro de posteback)
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}❌ Arquivo pom.xml não encontrado.${NC}"
    echo "Certifique-se de estar dentro da pasta 'posteback'."
    exit 1
fi

# Verificar se src existe
if [ ! -d "src" ]; then
    echo -e "${RED}❌ Diretório 'src' não encontrado.${NC}"
    echo "Certifique-se de estar dentro da pasta 'posteback'."
    exit 1
fi

echo -e "${BLUE}📦 Fazendo build da aplicação Maven...${NC}"

# Limpar e compilar
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Falha no build do Maven${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Build Maven concluído com sucesso!${NC}"

# Verificar se o JAR foi criado
if [ ! -f "target/vendas-postes-1.0.0.jar" ]; then
    echo -e "${RED}❌ JAR não foi gerado. Verificando arquivos...${NC}"
    ls -la target/
    exit 1
fi

echo -e "${BLUE}🐳 Construindo imagem Docker...${NC}"
docker build -t vendas-postes-api .

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Falha na construção da imagem Docker${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Imagem Docker criada com sucesso!${NC}"

echo -e "${YELLOW}🧪 Testando a imagem localmente...${NC}"

# Parar container anterior se existir
docker stop vendas-postes-test 2>/dev/null || true
docker rm vendas-postes-test 2>/dev/null || true

# Executar container de teste
docker run --rm -d -p 8080:8080 \
  -e DATABASE_URL="jdbc:postgresql://dpg-d1bg2895pdvs73dsavg0-a:5432/ativaposte" \
  -e DATABASE_USERNAME="admin" \
  -e DATABASE_PASSWORD="aa303O04NteXaSLTrM0uOQ14Q2VnRKIw" \
  -e SPRING_PROFILES_ACTIVE="production" \
  --name vendas-postes-test vendas-postes-api

echo -e "${YELLOW}⏳ Aguardando aplicação inicializar (30s)...${NC}"
sleep 30

# Testar se a aplicação está respondendo
echo -e "${BLUE}🔍 Testando endpoint da API...${NC}"
if curl -f -s http://localhost:8080/api/postes > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Aplicação está funcionando localmente!${NC}"
    echo -e "${GREEN}🌐 API disponível em: http://localhost:8080/api${NC}"

    # Mostrar alguns endpoints de teste
    echo -e "${BLUE}📋 Endpoints disponíveis:${NC}"
    echo "  GET  http://localhost:8080/api/postes"
    echo "  GET  http://localhost:8080/api/vendas"
    echo "  GET  http://localhost:8080/api/estoque"
    echo "  GET  http://localhost:8080/api/despesas"

    docker stop vendas-postes-test
else
    echo -e "${RED}❌ Aplicação não está respondendo${NC}"
    echo -e "${YELLOW}📋 Logs do container:${NC}"
    docker logs vendas-postes-test
    docker stop vendas-postes-test
    exit 1
fi

echo ""
echo -e "${GREEN}🎉 Deploy local concluído com sucesso!${NC}"
echo ""
echo -e "${BLUE}📋 Próximos passos para deploy no Render:${NC}"
echo "1. Faça commit das alterações (a partir da raiz do projeto):"
echo "   cd .."
echo "   git add ."
echo "   git commit -m 'Configuração para deploy no Render'"
echo "   git push origin main"
echo ""
echo "2. No Render.com:"
echo "   - Clique em 'New +' → 'Web Service'"
echo "   - Conecte seu repositório Git"
echo "   - Selecione 'Docker' como Environment"
echo "   - Configure o Root Directory como: posteback"
echo "   - Use o arquivo render.yaml para configuração automática"
echo ""
echo "3. Variáveis de ambiente já configuradas no render.yaml"
echo ""
echo -e "${GREEN}🚀 Sua aplicação está pronta para o deploy!${NC}"

# Mostrar informações sobre a estrutura
echo ""
echo -e "${BLUE}📁 Estrutura de arquivos criada:${NC}"
echo "posteback/"
echo "├── Dockerfile"
echo "├── render.yaml"
echo "├── deploy.sh"
echo "├── pom.xml"
echo "├── src/"
echo "└── target/"