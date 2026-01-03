package org.example.openapi

// Construção manual de um objeto JSON que representa a especificação OpenAPI 3.0.1
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

// Gera a especificação OpenAPI em formato JSON (String). A construção usa builders imutáveis.
fun buildOpenApi(): String {
    // `json` é o objeto raiz que conterá a spec completa
    val json = buildJsonObject {
        // Versão OpenAPI
        put("openapi", JsonPrimitive("3.0.1"))

        // Informações da API (título e versão)
        put("info", buildJsonObject {
            put("title", JsonPrimitive("Password Validator API"))
            put("version", JsonPrimitive("1.0.0"))
        })

        // Paths: define o endpoint /validate com método POST
        put("paths", buildJsonObject {
            put("/validate", buildJsonObject {
                put("post", buildJsonObject {
                    // Pequena descrição da operação
                    put("summary", JsonPrimitive("Validate password"))

                    // Corpo de requisição: obrigatório e com schema de application/json
                    put("requestBody", buildJsonObject {
                        put("required", JsonPrimitive(true))
                        put("content", buildJsonObject {
                            put("application/json", buildJsonObject {
                                put("schema", buildJsonObject {
                                    put("\$ref", JsonPrimitive("#/components/schemas/PasswordRequest"))
                                })
                            })
                        })
                    })

                    // Respostas possíveis: aqui definimos 200 com o schema de PasswordResponse
                    put("responses", buildJsonObject {
                        put("200", buildJsonObject {
                                    put("description", JsonPrimitive("OK"))
                                    put("content", buildJsonObject {
                                        put("application/json", buildJsonObject {
                                            put("schema", buildJsonObject {
                                                put("\$ref", JsonPrimitive("#/components/schemas/PasswordResponse"))
                                            })

                                            // Exemplos embutidos para documentação (válido e inválido)
                                            put("examples", buildJsonObject {
                                                put("validExample", buildJsonObject {
                                                    put("value", buildJsonObject {
                                                            put("valid", JsonPrimitive(true))
                                                            put("message", JsonPrimitive("Senha OK, todos requisitos foram contemplados"))
                                                        })
                                                })
                                                put("invalidExample", buildJsonObject {
                                                    put("value", buildJsonObject {
                                                        put("valid", JsonPrimitive(false))
                                                        put("message", JsonPrimitive("Password validation failed"))
                                                        put("details", buildJsonArray { add(JsonPrimitive("mínimo 9 caracteres")) })
                                                    })
                                                })
                                            })
                                        })
                                    })
                                })
                    })
                })
            })
        })

        // Components: definição dos schemas usados acima
        put("components", buildJsonObject {
            put("schemas", buildJsonObject {
                // Schema para a requisição que contém a senha
                put("PasswordRequest", buildJsonObject {
                    put("type", JsonPrimitive("object"))
                    put("properties", buildJsonObject {
                        put("password", buildJsonObject {
                            put("type", JsonPrimitive("string"))
                            put("description", JsonPrimitive("Regras: mínimo 9 caracteres; ao menos 1 dígito; 1 letra minúscula; 1 letra maiúscula; 1 caractere especial entre !@#$%^&*()-+; sem espaços; sem caracteres repetidos."))
                            put("minLength", JsonPrimitive(9))
                            put("pattern", JsonPrimitive("^(?=.{9,}$)(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\\$%\\^&\\*()\\-\\+])(?!.*\\s).*$"))
                            put("example", JsonPrimitive("Abcdef1!2"))
                        })
                    })
                    put("required", buildJsonArray { add(JsonPrimitive("password")) })
                })

                // Schema para a resposta: atualmente definimos apenas a propriedade `valid`
                put("PasswordResponse", buildJsonObject {
                    put("type", JsonPrimitive("object"))
                    put("properties", buildJsonObject {
                        put("valid", buildJsonObject { put("type", JsonPrimitive("boolean")) })
                    })
                    put("required", buildJsonArray { add(JsonPrimitive("valid")) })
                })
            })
        })
    }

    // Serializa o objeto JSON para uma string formatada (pretty print)
    return Json { prettyPrint = true }.encodeToString(JsonObject.serializer(), json)
}
