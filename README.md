# Asaas Demo Checkout Store

![Java](https://img.shields.io/badge/Java-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=white)
![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?logo=vuedotjs&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)
![Asaas](https://img.shields.io/badge/Asaas-Checkout-0030B9)

Demonstração de um e-commerce básico, com apenas o fluxo de compra, integrado à plataforma de pagamentos **Asaas** pelo modelo **Asaas Checkout**.

O Asaas Checkout é uma **página de pagamento hospedada pelo Asaas**, com suporte a Pix, cartão de crédito, assinaturas, parcelamentos, callbacks e webhooks. Ele é indicado para aplicações que **já têm o fluxo de compra, mas não querem implementar a interface de pagamento**.

| Sem o Asaas Checkout | Com o Asaas Checkout |
| --- | --- |
| A aplicação constrói e mantém a tela de pagamento | A tela de pagamento é do Asaas |
| A aplicação recebe e trafega dados de cartão | Os dados de cartão ficam só no Asaas |
| A confirmação depende do que o navegador informa | O Asaas confirma o pagamento direto para o servidor, por webhook |

## Funcionalidades

- Vitrine de produtos
- Forma de pagamento: cartão de crédito à vista ou parcelado
- Redirecionamento para o checkout do Asaas
- Acompanhamento do pedido
- Lista de pedidos do cliente

> A demonstração usa **cartão de crédito**. A loja também oferece **Pix**, mas no sandbox o QR Code gerado não é aceito por nenhum banco, então esse pagamento não pode ser concluído.

---

## Como funciona

| Etapa&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | Onde&nbsp;acontece | O que acontece |
| :--- | :--- | :--- |
| **1.&nbsp;Compra** | Frontend | O comprador escolhe o produto e a forma de pagamento. O frontend envia só esses dados, **nunca o valor** |
| **2.&nbsp;Pedido** | Backend | O backend busca o **preço no banco de dados** e salva o pedido como `PENDING` |
| **3.&nbsp;Checkout** | Backend&nbsp;→&nbsp;Asaas | O backend cria o checkout no Asaas com o valor do pedido e devolve o link. O frontend redireciona o comprador para a página de pagamento |
| **4.&nbsp;Pagamento** | Asaas | O comprador paga com cartão **na página do Asaas**. A loja não recebe dados do cartão |
| **5.&nbsp;Webhook** | Asaas&nbsp;→&nbsp;Backend | O Asaas notifica o backend com um evento, aceito só com o header `asaas-access-token` válido. O backend atualiza o status do pedido |
| **6.&nbsp;Retorno** | Asaas&nbsp;→&nbsp;Frontend | O Asaas redireciona o comprador para o backend, que o envia à tela do pedido. A tela consulta o pedido a cada 5 segundos e exibe o novo status |

| Evento do webhook | Status do pedido | Quando ocorre |
| :--- | :--- | :--- |
| `CHECKOUT_PAID` | `PAID` | O pagamento foi confirmado, inclusive se o pedido já tinha expirado na loja |
| `CHECKOUT_EXPIRED` | `EXPIRED` | O checkout venceu sem pagamento (validade de 60 minutos) |
| `CHECKOUT_CANCELED` | `CANCELED` | O checkout foi cancelado antes do pagamento |
| `PAYMENT_CHARGEBACK_REQUESTED` | `CANCELED` | O comprador contestou no banco um pedido já pago (chargeback) |
| `PAYMENT_REFUNDED` | `CANCELED` | O pagamento de um pedido já pago foi estornado |

> O redirecionamento da etapa 6 **não altera o pedido**: a liberação acontece apenas pelo webhook, de servidor para servidor.

### Garantias da integração

- O valor cobrado vem sempre do banco de dados, nunca do frontend.
- Um pedido só é liberado pelo webhook autenticado com o token do Asaas.
- Eventos repetidos não alteram o pedido: cada evento só vale para os status permitidos.
- Retomar um pagamento pendente reabre o mesmo checkout, sem gerar uma nova cobrança.
- Cada cliente pode criar no máximo 3 pedidos por minuto.
- Pedidos pendentes expiram automaticamente após 70 minutos, caso o webhook não chegue.
- A forma de pagamento não pode ser alterada depois que o pedido é criado.

---

## Tecnologias

### Backend

| Tecnologia | Versão |
| --- | --- |
| Java | 21 |
| Spring Boot | 4.1.1 |
| Maven Wrapper | 3.10.0 |
| JUnit e Mockito | Gerenciadas pelo Spring Boot |

### Frontend

| Tecnologia | Versão |
| --- | --- |
| Vue.js | 3.5 |
| Pinia | 4.0 |
| Vue Router | 5.3 |
| axios | 1.20 |
| Vite | 8.2 |
| Vitest | 4.1 |
| Vue Test Utils | 2.5 |

### Infraestrutura

| Tecnologia | Versão / Plano |
| --- | --- |
| PostgreSQL (Docker) | 16 |
| ngrok | Plano gratuito, com domínio estático |
| Asaas | Sandbox |

---

## Como executar

### Pré-requisitos

| Requisito | Versão mínima |
| --- | --- |
| Java | 21 |
| Node.js | 24.12.0 (LTS) |
| Docker | Compose v2 (comando `docker compose`), com o Docker em execução |
| ngrok | Conta gratuita e ngrok instalado |
| Asaas | Conta no sandbox |

Confira a versão do Java:

```bash
java -version
```

Confira a versão do Node.js:

```bash
node -v
```

Confira a versão do Docker Compose:

```bash
docker compose version
```

Confira a instalação do ngrok:

```bash
ngrok version
```

### 1. Configure o ngrok

> O Asaas precisa acessar o backend pela internet para **enviar os webhooks** e **redirecionar o comprador** depois do pagamento. O ngrok cria um endereço público que encaminha essas requisições para o backend local.

1. Crie uma conta em [ngrok.com](https://ngrok.com) e instale o ngrok.
2. Configure o token de autenticação, disponível no painel do ngrok:

   ```bash
   ngrok config add-authtoken <SEU_TOKEN>
   ```

3. No painel do ngrok, em **Domains**, copie o seu domínio estático gratuito.

> Nos próximos passos, substitua `seu-dominio.ngrok-free.dev` por esse domínio em **todos** os lugares: site da conta e webhook no Asaas, variável `BACKEND_PUBLIC_URL` e comando do ngrok.

### 2. Configure o Asaas Sandbox

O sandbox é o ambiente de testes do Asaas: nenhuma cobrança é real.

1. Crie uma conta em [sandbox.asaas.com](https://sandbox.asaas.com).
2. Gere uma **chave de API** na área de integrações do painel. Ela começa com `$aact_` e será usada no `ASAAS_API_KEY`.
3. Em *Configurações da conta → Informações*, cadastre o site da conta com o valor `https://seu-dominio.ngrok-free.dev`. O Asaas só aceita URLs de retorno desse domínio.
4. Na área de webhooks do painel, crie um webhook:

   | Campo | Valor |
   | --- | --- |
   | URL | `https://seu-dominio.ngrok-free.dev/webhooks/asaas` |
   | Token de autenticação | Clique em **Gerar token** e copie o valor para o `ASAAS_WEBHOOK_TOKEN` |
   | Tipo de envio | Sequencial |
   | Eventos | `CHECKOUT_PAID`, `CHECKOUT_EXPIRED`, `CHECKOUT_CANCELED`, `PAYMENT_CHARGEBACK_REQUESTED` e `PAYMENT_REFUNDED` |

### 3. Clone o repositório

```bash
git clone https://github.com/thiagoDimon/interface-checkout-asaas.git
cd interface-checkout-asaas
```

### 4. Configure as variáveis de ambiente

Crie os arquivos `.env` a partir dos exemplos.

Linux / macOS:

```bash
cp backend/.env.example backend/.env
cp frontend/.env.example frontend/.env
```

Windows (PowerShell):

```powershell
Copy-Item backend\.env.example backend\.env
Copy-Item frontend\.env.example frontend\.env
```

Todas as variáveis do `backend/.env` são obrigatórias. **Altere apenas as variáveis em negrito**: as demais já vêm preenchidas no exemplo.

| Variável | Descrição | Valor |
| --- | --- | --- |
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` | Conexão com o PostgreSQL | Valores do exemplo |
| `CORS_ALLOWED_ORIGINS` | Origens permitidas para o frontend | `http://localhost:5173` |
| `FRONTEND_URL` | Destino do comprador ao voltar do checkout | `http://localhost:5173` |
| **`BACKEND_PUBLIC_URL`** | Domínio do ngrok, o mesmo cadastrado como site no Asaas | `https://seu-dominio.ngrok-free.dev` |
| `ASAAS_BASE_URL` | API do Asaas Sandbox | `https://api-sandbox.asaas.com/v3` |
| **`ASAAS_API_KEY`** | Chave de API do sandbox. Use **sem aspas**: com aspas, elas passam a fazer parte do valor e a autenticação falha | `$aact_...` |
| **`ASAAS_WEBHOOK_TOKEN`** | Token gerado no cadastro do webhook | Token gerado no Asaas |

O `frontend/.env` já vem preenchido com `VITE_API_BASE_URL=http://localhost:8080` e `VITE_CUSTOMER_ID=1`.

### 5. Execute a aplicação

Execute cada parte em um terminal separado, a partir da raiz do projeto.

1. Suba o banco de dados:

   ```bash
   cd backend
   docker compose up -d
   ```

2. Inicie o backend.

   Linux / macOS:

   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

   Windows (PowerShell):

   ```powershell
   cd backend
   .\mvnw.cmd spring-boot:run
   ```

3. Inicie o túnel do ngrok:

   ```bash
   ngrok http 8080 --url https://seu-dominio.ngrok-free.dev
   ```

4. Inicie o frontend:

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

| Serviço | URL |
| --- | --- |
| Loja | <http://localhost:5173> |
| Backend | <http://localhost:8080> |
| Inspetor do ngrok | <http://127.0.0.1:4040> |

> Na primeira execução, o backend cria as tabelas e cadastra 3 produtos e 1 cliente.

### 6. Faça uma compra de teste

Na loja, clique em **Comprar**, escolha **Cartão de crédito** e preencha o checkout do Asaas com:

| Campo | Valor |
| --- | --- |
| Número do cartão (aprovado) | Um número fictício válido, gerado no [gerador de cartões da 4Devs](https://www.4devs.com.br/gerador_de_numero_cartao_credito) |
| Número do cartão (recusado) | `5184 0197 4037 3151` |
| Validade | Qualquer data futura |
| CVV | `123` |

Depois do pagamento, a tela do pedido mostra **Pendente** e muda para **Pago** assim que o webhook do Asaas chega, porque ela consulta o pedido a cada 5 segundos. O ngrok precisa estar em execução para o webhook chegar.

> No plano gratuito, o ngrok exibe uma **página de aviso** quando o navegador passa pelo túnel, ao voltar do checkout. Clique em **Visit Site** para seguir até a tela do pedido.

### 7. Encerre a aplicação

Pare o backend, o frontend e o ngrok com `Ctrl + C` em cada terminal e, em seguida, pare o banco de dados:

```bash
cd backend
docker compose down
```

---

## Testes automatizados

A aplicação possui testes automatizados do fluxo de compra, com o Asaas simulado. Os testes do backend precisam do banco de dados em execução e do `backend/.env` preenchido.

Backend, Linux / macOS:

```bash
cd backend
./mvnw test
```

Backend, Windows (PowerShell):

```powershell
cd backend
.\mvnw.cmd test
```

Frontend:

```bash
cd frontend
npm run test:unit
```

---

## Problemas comuns

| Sintoma | Causa provável | Solução |
| --- | --- | --- |
| Erro ao iniciar o pagamento ("Não foi possível criar o checkout no Asaas") | Chave de API inválida ou domínio do ngrok não cadastrado como site da conta | Confira o `ASAAS_API_KEY` e o site da conta. O log do backend mostra o erro retornado pelo Asaas |
| Pedido parado em **Pendente** depois do pagamento | ngrok desligado, webhook com URL ou token diferentes, ou fila de webhooks interrompida no Asaas | Confira o inspetor do ngrok: resposta `401` indica token diferente do `ASAAS_WEBHOOK_TOKEN`. Reative a fila no painel do Asaas, se necessário |
| "Muitas tentativas de compra em pouco tempo" | Limite de 3 pedidos por minuto por cliente | Aguarde um minuto e tente novamente |
| Backend não inicia (`Could not resolve placeholder`) | Variável ausente no `backend/.env`, ou backend iniciado fora da pasta `backend` | Confira o `.env` e execute o backend a partir da pasta `backend` |
| Aviso `The "aact_..." variable is not set` no Docker Compose | O Docker Compose lê o mesmo `.env` e interpreta o `$` da chave de API | Pode ser ignorado: o banco sobe normalmente |

---

## Padrão de projeto utilizado: Ports and Adapters

A integração com o Asaas segue o padrão **Ports and Adapters**:

- **Porta:** `PaymentGateway` é a interface que o fluxo de compra usa para criar um checkout, sem conhecer o provedor.
- **Adaptador:** `AsaasCheckoutGateway` implementa a porta: monta o payload, chama a API do Asaas e traduz a resposta.

Com isso, as regras do pedido não dependem do Asaas, e trocar de provedor significa apenas criar um novo adaptador.

---

## Referências

- [Visão geral da documentação do Asaas](https://docs.asaas.com/docs/visao-geral)
- [Documentação do Asaas Checkout](https://docs.asaas.com/docs/checkout-asaas)
- [Como iniciar no Asaas Sandbox](https://docs.asaas.com/docs/sandbox)
- [Painel do Asaas Sandbox](https://sandbox.asaas.com)
