# GA.EVENT - Event Management System

## Descricao do Projeto
GA.EVENT e uma plataforma de gestao de eventos e controle de presenca, desenvolvida com foco em seguranca, gamificacao e usabilidade. O projeto foi arquitetado para suportar o gerenciamento do ciclo de vida completo de um evento academico ou corporativo, integrando um aplicativo movel (frontend) a uma API RESTful robusta (backend).

O principal diferencial da aplicacao e a substituicao da tradicional lista de presenca por um sistema de validacao via leitura de QR Codes dinamicos, mitigando fraudes e automatizando o calculo de horas complementares.

## Arquitetura e Estrutura de Dominios
O backend foi estruturado utilizando o padrao arquitetural MVC, respeitando os principios de separacao de responsabilidades (SoC). O sistema e dividido nos seguintes dominios principais:

1. **Gestao de Usuarios (User):** Responsavel pelo controle de perfis de acesso e dados cadastrais.
2. **Gestao de Eventos (Evento):** Gerenciamento do ciclo de vida dos eventos, incluindo definicao de data, local e carga horaria.
3. **Controle de Presenca (Presenca/Inscricao):** Engine central de negocios responsavel por gerenciar as inscricoes, controlar o check-in/check-out e calcular a permanencia do usuario.
4. **Validacao (Scanner):** Modulo responsavel pela interceptacao e validacao das credenciais dinamicas (QR Code).

## Tecnologias Utilizadas
A aplicacao backend foi construida utilizando o ecossistema Java moderno e as melhores praticas de mercado:
* **Java 21:** Versao LTS atual da linguagem.
* **Spring Boot (3.x/4.x):** Framework base para a aplicacao.
* **Spring Data JPA & Hibernate:** ORM e persistencia de dados.
* **Spring Security & BCrypt:** Controle de seguranca e hash de senhas criptografadas.
* **Bean Validation:** Garantia de integridade dos dados na camada de entrada (Controllers).
* **PostgreSQL:** Banco de dados relacional hospedado na nuvem (Supabase).
* **Docker:** Conteinerizacao do ambiente de producao.
* **Railway:** Hospedagem em nuvem e integracao continua (CI/CD).

## Regras de Negocio e Features Principais

### Seguranca Anti-Fraude com QR Code Dinamico
A geracao de presenca e controlada via credenciais temporarias. O aplicativo gera um QR Code contendo o identificador do usuario atrelado a um *timestamp* Unix. 
O servidor possui uma trava de seguranca de 5 minutos; QR Codes apresentados apos esse periodo sao rejeitados, impedindo que usuarios compartilhem capturas de tela (*print screens*) para forjar presenca.

### Calculo de Permanencia
Para que uma presenca seja validada e contabilizada, o sistema realiza um calculo automatico no momento do check-out. O tempo total decorrido entre o primeiro scan (entrada) e o segundo scan (saida) e validado contra a carga horaria total do evento. A presenca so atinge o status "CONCLUIDO" caso o usuario permaneca em, no minimo, 80% do tempo estipulado para a atividade.

### Gamificacao (Badge Ouro)
Como mecanismo de engajamento, a plataforma conta com uma flag de conquista (`badgeOuro`). O backend monitora o historico do aluno a cada check-out e, ao atingir a conclusao completa de 3 eventos distintos, o sistema atualiza o perfil do usuario, concedendo-lhe o merito de Ouro, o qual pode ser interpretado pelo aplicativo para liberar beneficios ou certificados exclusivos.

## Estrutura da API (Endpoints)

A comunicacao entre os sistemas e feita inteiramente no padrao REST consumindo e retornando JSON.

### Autenticacao e Usuarios
* `POST /auth/login` - Validacao de credenciais.
* `POST /auth/registrar` - Cadastro de novos usuarios.
* `GET /api/usuarios/{id}` - Consulta em tempo real do perfil do usuario (incluindo status da Badge Ouro).

### Gestao de Eventos
* `GET /api/eventos` - Listagem de todos os eventos.
* `POST /api/eventos` - Criacao de evento (Organizador).
* `PUT /api/eventos/{id}` - Edicao de evento.
* `DELETE /api/eventos/{id}` - Exclusao logica/fisica do evento.

### Fluxo de Inscricao e Presenca
* `POST /api/inscricoes` - Inscricao previa em um evento.
* `GET /api/inscricoes/aluno/{alunoId}` - Historico de eventos do aluno (Meus Eventos).
* `GET /api/inscricoes/evento/{eventoId}/inscritos` - Listagem gerencial de inscritos.

### Validacao (Porteiro)
* `POST /scanner/validar` - Endpoint hibrido (Check-in/Check-out) que processa a string extraida do QR Code, valida o tempo de expiracao, define o status da presenca e calcula as variaveis de gamificacao.

## Setup e Execucao Local

### Pre-requisitos
* Java 21+
* Maven 3.8+
* Instancia do PostgreSQL rodando localmente ou em nuvem.

### Configuracao
Configure as variaveis de ambiente na sua maquina ou na sua IDE antes de executar o projeto:
* `SPRING_DATASOURCE_URL` (ex: jdbc:postgresql://localhost:5432/gaevent)
* `SPRING_DATASOURCE_USERNAME`
* `SPRING_DATASOURCE_PASSWORD`
* `JWT_SECRET_KEY`

### Executando
Para compilar e executar o projeto via linha de comando:
```bash
mvn clean package -DskipTests
java -jar target/ga.event-0.0.1-SNAPSHOT.jar
```

A aplicacao iniciara na porta `8080` e, caso o banco esteja vazio, uma rotina de injecao de dados (`DataInitializer`) ira configurar automaticamente os perfis de teste iniciais (Administrador, Porteiro e Aluno).