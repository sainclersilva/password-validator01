package org.example.validator

// Resultado da validação: indica se é válido, mensagem opcional e lista de detalhes (motivos de falha)
data class ValidationResult(
    val valid: Boolean, // true se a senha passou em todas as regras
    val message: String? = null, // mensagem resumida sobre o resultado
    val details: List<String> = emptyList() // lista de motivos quando inválida
)

// Contrato do validador de senha. Implementações devem fornecer checagem booleana e resultado detalhado.
interface PasswordValidator {
    fun isValid(password: String): Boolean // retorno simplificado (apenas boolean)
    fun validate(password: String): ValidationResult // retorno detalhado (mensagem + motivos)
}

// Implementação padrão do validador de senha.
class DefaultPasswordValidator : PasswordValidator {
    // Conjunto de caracteres especiais permitidos exigidos pela regra
    private val SPECIAL = "!@#\$%^&*()-+"

    // isValid delega para validate e retorna somente o campo `valid` do resultado
    override fun isValid(password: String): Boolean = validate(password).valid

    // Executa todas as checagens e acumula motivos de falha em `reasons`.
    override fun validate(password: String): ValidationResult {
        val reasons = mutableListOf<String>() // lista mutável para coletar mensagens de erro

        // Regra: pelo menos 9 caracteres
        if (password.length < 9) reasons.add("mínimo 9 caracteres")

        // Regra: não pode conter espaços em branco (tabs, espaços, quebras de linha)
        if (password.any { it.isWhitespace() }) reasons.add("não pode conter espaços em branco")

        // Regra: deve conter ao menos um dígito (0-9)
        if (!password.any { it.isDigit() }) reasons.add("deve conter ao menos um dígito")

        // Regra: deve conter ao menos uma letra minúscula (a-z)
        if (!password.any { it.isLowerCase() }) reasons.add("deve conter ao menos uma letra minúscula")

        // Regra: deve conter ao menos uma letra maiúscula (A-Z)
        if (!password.any { it.isUpperCase() }) reasons.add("deve conter ao menos uma letra maiúscula")

        // Regra: deve conter ao menos um caractere especial do conjunto definido em SPECIAL
        if (!password.any { SPECIAL.contains(it) }) reasons.add("deve conter ao menos um caractere especial entre !@#\$%^&*()-+")

        // Regra: nenhum caractere pode se repetir — compara tamanho do Set com o tamanho da string
        if (password.toSet().size != password.length) reasons.add("não pode conter caracteres repetidos")

        // Se não houver motivos, a senha é válida — retorna ValidationResult com mensagem de sucesso
        return if (reasons.isEmpty())
            ValidationResult(true, "Senha OK, todos requisitos foram contemplados")
        else // Caso contrário, retorna falso com mensagem genérica e lista de motivos
            ValidationResult(false, "Password validation failed", reasons)
    }
}

