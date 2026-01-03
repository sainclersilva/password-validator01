package org.example

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import org.example.validator.DefaultPasswordValidator

class PasswordValidatorTest {
    @Test
    fun sampleCases() {
        val v = DefaultPasswordValidator()
        assertFalse(v.isValid(""))
        assertFalse(v.isValid("aa"))
        assertFalse(v.isValid("ab"))
        assertFalse(v.isValid("AAAbbbCc"))
        assertFalse(v.isValid("AbTp9!foo"))
        assertFalse(v.isValid("AbTp9!foA"))
        assertFalse(v.isValid("AbTp9 fok"))
        assertTrue(v.isValid("AbTp9!fok"))
    }
}
