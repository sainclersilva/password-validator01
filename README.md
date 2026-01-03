# Password Validator Service (Kotlin / Ktor)

Projeto mínimo em Kotlin usando Ktor que expõe uma API para validar senhas segundo as regras fornecidas.

**Regras de validação implementadas**
- Pelo menos 9 caracteres
- Ao menos um dígito
- Ao menos uma letra minúscula
- Ao menos uma letra maiúscula
- Ao menos um caractere especial do conjunto: `!@#$%^&*()-+`
- Não conter espaços em branco
- Nenhum caractere pode se repetir (comparação sensível a maiúsculas/minúsculas)

**Endpoints**
- POST `/validate` — corpo JSON: `{ "password": "..." }` → resposta JSON `{ "valid": true|false }`
- GET `/openapi.json` — OpenAPI 3.0 JSON (gerado dinamicamente pela aplicação)
- GET `/docs` — Swagger UI (carrega `/openapi.json`)

```powershell
curl -X POST http://localhost:8080/validate -H "Content-Type: application/json" -d '{"password":"AbTp9!fok"}'
```

Execução (Windows / PowerShell)

Pré-requisitos:
- JDK 17+
- VS Code com extensões recomendadas (há um arquivo `.vscode/extensions.json` no repositório)

Executar localmente com o wrapper Gradle:

```powershell
cd <repo-root>
.\gradlew.bat :app:run
```

Executar testes:

```powershell
.\gradlew.bat :app:test
```

Ao iniciar a aplicação, abra `http://localhost:8080/docs` para ver a documentação interativa (Swagger UI).

## OpenAPI gerado (visão completa)

O endpoint `/openapi.json` é construído dinamicamente. Abaixo está a especificação completa gerada pela aplicação (OpenAPI 3.0.1):

```json
{
	"openapi": "3.0.1",
	"info": {
		"title": "Password Validator API",
		"version": "1.0.0"
	},
	"paths": {
		"/validate": {
			"post": {
				"summary": "Validate password",
				"requestBody": {
					"required": true,
					"content": {
						"application/json": {
							"schema": { "$ref": "#/components/schemas/PasswordRequest" }
						}
					}
				},
				"responses": {
					"200": {
						"description": "OK",
						"content": {
							"application/json": {
								"schema": { "$ref": "#/components/schemas/PasswordResponse" }
							}
						}
					}
				}
			}
		}
	},
	"components": {
		"schemas": {
			"PasswordRequest": {
				"type": "object",
				"properties": {
					"password": {
						"type": "string",
						"description": "Regras: mínimo 9 caracteres; ao menos 1 dígito; 1 letra minúscula; 1 letra maiúscula; 1 caractere especial entre !@#$%^&*()-+; sem espaços; sem caracteres repetidos.",
						"minLength": 9,
						"pattern": "^(?=.{9,}$)(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\\$%\\^&\\*()\\-\\+])(?!.*\\s).*$",
						"example": "Abcdef1!2"
					}
				},
				"required": ["password"]
			},
			"PasswordResponse": {
				"type": "object",
				"properties": {
					"valid": { "type": "boolean" }
				}
			}
		}
	}
}
```

## Decisões de design e observações

 - A validação principal é feita em `PasswordValidator.isValid(password: String)` no arquivo `app/src/main/kotlin/org/example/App.kt`.
 - A verificação de caracteres repetidos é feita com `password.toSet().size == password.length`, o que garante que todos os caracteres sejam distintos.
 - Testes automatizados cobrem os casos positivos/negativos e incluem um teste de integração que usa o servidor em memória.

## Próximos passos sugeridos

 - Integrar um gerador OpenAPI mais rico (ex.: `com.papsign:ktor-openapi-generator`) para gerar docs a partir de rotas e DTOs automaticamente — eu posso integrar essa biblioteca se desejar.
 - Adicionar `launch.json` para depurar no VS Code.
 - Publicar um pequeno container Docker com a aplicação.

Quer que eu adicione algum desses itens agora (por exemplo, integração com `ktor-openapi-generator`)?

## Example responses (success & failure)

Exemplo de resposta válida (HTTP 200):

```json
{
	"valid": true,
	"message": "Senha OK, todos requisitos foram contemplados",
	"details": []
}
```

Exemplo de resposta inválida (HTTP 200):

```json
{
	"valid": false,
	"message": "Password validation failed",
	"details": ["mínimo 9 caracteres", "deve conter ao menos um dígito"]
}
```

## Curl examples with expected responses

Valid password example:

```bash
curl -s -X POST http://localhost:8080/validate \
	-H "Content-Type: application/json" \
	-d '{"password":"AbTp9!fok"}' | jq
```
Expected JSON:

```json
{
	"valid": true,
	"message": "Senha OK, todos requisitos foram contemplados",
	"details": []
}
```

Invalid password example (short + missing digit):

```bash
curl -s -X POST http://localhost:8080/validate \
	-H "Content-Type: application/json" \
	-d '{"password":"abcd"}' | jq
```
Expected JSON:

```json
{
	"valid": false,
	"message": "Password validation failed",
	"details": [
		"mínimo 9 caracteres",
		"deve conter ao menos um dígito",
		"deve conter ao menos uma letra maiúscula",
		"deve conter ao menos um caractere especial entre !@#$%^&*()-+"
	]
}
```

## PowerShell examples (Windows)

Valid password example (PowerShell - Invoke-RestMethod):

```powershell
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/validate' \
	-ContentType 'application/json' \
	-Body '{"password":"AbTp9!fok"}' | ConvertTo-Json -Depth 5
```

Expected JSON (pretty-printed):

```json
{
	"valid": true,
	"message": "Senha OK, todos requisitos foram contemplados",
	"details": []
}
```

### Usando `curl` no PowerShell / PowerShell Core (pwsh)

No Windows PowerShell `curl` é um alias para `Invoke-WebRequest`. Para usar a ferramenta `curl` real (quando instalada no sistema), chame `curl.exe` explicitamente. Em PowerShell/`pwsh` você pode então converter o JSON retornado para um objeto e re-serializar para impressão legível.

Exemplo válido (usa `curl.exe`):

```powershell
curl.exe -s -X POST http://localhost:8080/validate `
	-H "Content-Type: application/json" `
	-d '{"password":"AbTp9!fok"}' | ConvertFrom-Json | ConvertTo-Json -Depth 5
```

Exemplo inválido (usa `curl.exe`):

```powershell
curl.exe -s -X POST http://localhost:8080/validate `
	-H "Content-Type: application/json" `
	-d '{"password":"abcd"}' | ConvertFrom-Json | ConvertTo-Json -Depth 5
```

Se preferir usar `pwsh` em plataformas onde `curl` é o binário do sistema (Linux/macOS/WSL), os mesmos comandos `curl -s -X POST ...` funcionam sem precisar do sufixo `.exe`.

Invalid password example (PowerShell - short + missing digit):

```powershell
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/validate' \
	-ContentType 'application/json' \
	-Body '{"password":"abcd"}' | ConvertTo-Json -Depth 5
```

Expected JSON (pretty-printed):

```json
{
	"valid": false,
	"message": "Password validation failed",
	"details": [
		"mínimo 9 caracteres",
		"deve conter ao menos um dígito",
		"deve conter ao menos uma letra maiúscula",
		"deve conter ao menos um caractere especial entre !@#$%^&*()-+"
	]
}
```

## Testes

- Unitários: há testes para `DefaultPasswordValidator` cobrindo casos válidos e inválidos (`app/src/test/kotlin/org/example/PasswordValidatorTest.kt`).
- Integração: teste que sobe a aplicação em memória e consome `/validate` (`app/src/test/kotlin/org/example/ApiIntegrationTest.kt`).

## Abstração, Acoplamento, Extensibilidade e Coesão

- Abstração: `PasswordValidator` é uma interface que expõe apenas o necessário (`isValid` e `validate`).
- Acoplamento: o `App.module` depende da abstração (`PasswordValidator`), reduzindo acoplamento com implementações concretas.
- Extensibilidade: novas políticas/implementações podem ser adicionadas criando outra classe que implemente `PasswordValidator`.
- Coesão: cada módulo tem responsabilidade única (validação, geração OpenAPI, roteamento), aumentando coesão interna.

## Design de API

- Contrato simples e previsível: POST `/validate` recebe `{ password: string }` e sempre responde HTTP 200 com o resultado da validação no corpo (conteúdo determinístico).
- Erros de aplicação são mapeados via `StatusPages` para respostas JSON com chave `error`.
- Fornecemos exemplos e schema via `/openapi.json` e UI em `/docs` para facilitar integração com clientes.

## Clean Code

- Funções pequenas, nomes autoexplicativos e separação por responsabilidades (`validator`, `openapi`, `App`).
- Código escrito para ser legível e testável: validação encapsulada e testada isoladamente.

## SOLID

- Single Responsibility: cada classe/arquivo tem uma única responsabilidade.
- Open/Closed: novos validadores podem ser adicionados sem alterar consumidores.
- Liskov Substitution: implementações do validador seguem o contrato.
- Interface Segregation: consumidores usam apenas os métodos necessários.
- Dependency Inversion: módulos de alto nível dependem de abstrações, não de implementações.

**Clean Code**
- Código organizado por responsabilidade: `App.kt` (configuração e rotas), `validator/PasswordValidator.kt` (contrato e implementação), `openapi/OpenApiGenerator.kt` (geração de spec). Nomes claros e funções pequenas.

**SOLID**
- Single Responsibility: cada arquivo tem responsabilidade única (validação, geração OpenAPI, configuração do servidor).
- Open/Closed: o `PasswordValidator` é uma interface; novas políticas podem ser adicionadas sem modificar o consumidor.
- Liskov: implementações do validador respeitam o contrato da interface.
- Interface Segregation: o consumidor depende apenas do método `isValid`.
- Dependency Inversion: `App.module` aceita um `PasswordValidator` (injetado com padrão por omissão `DefaultPasswordValidator`).

**12-Factor (práticas principais adotadas)**
- Config via variáveis de ambiente: porta do servidor é lida de `PORT` (fallback 8080).
- Log para stdout: `CallLogging` registra para o logger da aplicação (console), adequado para plataformas como Docker/Heroku.
- Dependências: gerenciadas pelo Gradle (wrapper incluso) — não há dependências globais necessárias.
- Process: app é um processo stateless simples; adicionamos `/health` para checagem de disponibilidade.

**Resiliência & Disponibilidade**
- `StatusPages` captura exceções não tratadas e retorna uma resposta JSON padronizada em vez de crash silencioso.
- Health endpoint (`GET /health`) permite load balancers e orquestradores verificarem se a instância está UP.
- Logs e tratamento de erros tornam mais fácil detectar e reiniciar instâncias problemáticas.

Se quiser, eu posso adicionar camadas extras de resiliência (ex.: circuit-breaker via `resilience4j`, retry/backoff, timeouts específicos) e integração com métricas (Prometheus) e tracing (OpenTelemetry).
