package com.fitwell.app.utils

/**
 * Production-grade form validator.
 * All methods return null on success, or a specific user-friendly error string.
 * Trimming happens inside each method silently — callers receive the trimmed value via
 * the companion trim helpers.
 */
object Validator {

    // ── Full Name ─────────────────────────────────────────────────────────────
    fun fullName(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "Name cannot be empty"
        if (v.any { it.isDigit() }) return "Numbers are not allowed in the name"
        if (v.any { !it.isLetter() && it != ' ' }) return "Special characters are not allowed in the name"
        if (v.contains("  ")) return "Multiple spaces are not allowed between words"
        if (v.length > 25) return "Name must be 25 characters or fewer"
        return null
    }

    // ── Email ─────────────────────────────────────────────────────────────────
    fun email(raw: String): String? {
        val v = raw.trim().lowercase()
        if (v.isEmpty()) return "Email cannot be empty"
        val atCount = v.count { it == '@' }
        if (atCount == 0) return "'@' is missing from the email"
        if (atCount > 1) return "Email cannot contain more than one '@'"
        if (v.startsWith('@')) return "Email cannot start with '@'"
        if (v.endsWith('@')) return "Email cannot end with '@'"
        val parts = v.split('@')
        val local  = parts[0]
        val domain = parts[1]
        if (local.isEmpty()) return "Email must have characters before '@'"
        if (domain.isEmpty()) return "Email must have a domain after '@'"
        if (domain.startsWith('.')) return "Domain cannot start with a '.' immediately after '@'"
        if (!domain.contains('.')) return "'.' is missing from the domain part"
        if (domain.endsWith('.')) return "Domain cannot end with a '.'"
        val domainParts = domain.split('.')
        if (domainParts.any { it.isEmpty() }) return "Email domain contains consecutive dots"
        // Basic local-part rules
        if (local.startsWith('.') || local.endsWith('.')) return "Email local part cannot start or end with a '.'"
        return null
    }

    // ── Password ──────────────────────────────────────────────────────────────
    fun password(raw: String): String? {
        if (raw.isEmpty()) return "Password cannot be empty"
        if (raw.contains(' ')) return "Spaces are not allowed in passwords"
        if (raw.length < 8) return "Password must be at least 8 characters long"
        if (!raw.any { it.isLetter() }) return "Password must contain at least one letter"
        if (!raw.any { it.isDigit() }) return "Password must contain at least one number"
        if (!raw.any { !it.isLetterOrDigit() }) return "Password must contain at least one special character (e.g. @, #, !)"
        return null
    }

    fun passwordsMatch(p1: String, p2: String): String? {
        if (p1 != p2) return "Passwords do not match"
        return null
    }

    // ── Age ───────────────────────────────────────────────────────────────────
    fun age(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "Age cannot be empty"
        val n = v.toIntOrNull() ?: return "Age must be a whole number (e.g. 25)"
        if (n < 4)   return "Age must be at least 4"
        if (n > 120) return "Age must be 120 or below"
        return null
    }

    // ── Height ────────────────────────────────────────────────────────────────
    // unitIndex: 0 = cm, 1 = ft/in
    fun heightCm(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "Height cannot be empty"
        val n = v.toFloatOrNull() ?: return "Height must be a number (e.g. 170)"
        if (n <= 0)   return "Height must be greater than 0"
        if (n > 300)  return "Height must be 300 cm or below"
        return null
    }

    fun heightFt(rawFt: String, rawIn: String): String? {
        val ft = rawFt.trim()
        val inches = rawIn.trim()
        if (ft.isEmpty()) return "Feet value cannot be empty"
        val ftVal = ft.toIntOrNull() ?: return "Feet must be a whole number"
        if (ftVal < 0)  return "Feet cannot be negative"
        if (ftVal > 10) return "Feet must be 10 or below"
        if (inches.isEmpty()) return "Inches value cannot be empty"
        val inVal = inches.toIntOrNull() ?: return "Inches must be a whole number"
        if (inVal < 0)  return "Inches must be between 0 and 11"
        if (inVal > 11) return "Inches must be between 0 and 11"
        if (ftVal == 0 && inVal == 0) return "Height must be greater than 0"
        return null
    }

    fun ftInToCm(ft: Int, inches: Int): Float = ft * 30.48f + inches * 2.54f

    // ── Weight ────────────────────────────────────────────────────────────────
    // unitIndex: 0 = kg, 1 = lbs
    fun weightKg(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "Weight cannot be empty"
        val n = v.toFloatOrNull() ?: return "Weight must be a number (e.g. 70)"
        if (n <= 0)   return "Weight must be greater than 0"
        if (n > 1000) return "Weight must be 1000 kg or below"
        return null
    }

    fun weightLbs(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "Weight cannot be empty"
        val n = v.toFloatOrNull() ?: return "Weight must be a number (e.g. 154)"
        if (n <= 0)   return "Weight must be greater than 0"
        if (n > 2500) return "Weight must be 2500 lbs or below"
        return null
    }

    fun lbsToKg(lbs: Float): Float = lbs * 0.453592f
    fun kgToLbs(kg: Float): Float  = kg  * 2.20462f

    // Target weight — same rules but optional (empty = skip)
    fun targetWeightKg(raw: String): String? {
        if (raw.trim().isEmpty()) return null // optional
        return weightKg(raw)
    }

    fun targetWeightLbs(raw: String): String? {
        if (raw.trim().isEmpty()) return null // optional
        return weightLbs(raw)
    }

    // ── Step Goal ─────────────────────────────────────────────────────────────
    fun stepGoal(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "Step goal cannot be empty"
        val n = v.toIntOrNull() ?: return "Step goal must be a whole number"
        if (n < 1)       return "Step goal must be at least 1 step"
        if (n > 1000000) return "Please enter a step goal between 1 and 1,000,000 steps"
        return null
    }

    // ── Exercise fields ───────────────────────────────────────────────────────
    fun exerciseName(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "Exercise name cannot be empty"
        if (v.isBlank()) return "Exercise name cannot be only spaces"
        if (v.any { it.isDigit() }) return "Numbers are not allowed in exercise names"
        if (v.any { !it.isLetter() && it != ' ' }) return "Special characters are not allowed in exercise names"
        return null
    }

    fun setsOrReps(raw: String, fieldName: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "$fieldName cannot be empty"
        val n = v.toIntOrNull() ?: return "$fieldName must be a whole number"
        if (n < 1)    return "$fieldName must be at least 1"
        if (n > 1000) return "$fieldName cannot exceed 1000"
        return null
    }

    fun durationMinutes(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return null // optional
        val n = v.toIntOrNull() ?: return "Duration must be a whole number of minutes"
        if (n < 0)    return "Duration cannot be negative"
        if (n > 1440) return "Duration cannot exceed 1440 minutes (24 hours)"
        return null
    }

    // ── Food fields ───────────────────────────────────────────────────────────
    fun foodName(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "Food name cannot be empty"
        if (v.isBlank()) return "Food name cannot be only spaces"
        if (v.any { it.isDigit() }) return "Numbers are not allowed in food names"
        if (v.any { !it.isLetter() && it != ' ' }) return "Special characters are not allowed in food names"
        return null
    }

    fun foodQuantity(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "Quantity cannot be empty"
        val n = v.toIntOrNull() ?: return "Quantity must be a whole number"
        if (n < 1)  return "Quantity must be at least 1"
        if (n > 99) return "Quantity cannot exceed 99"
        return null
    }

    fun servingSize(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "Serving size cannot be empty"
        val n = v.toIntOrNull() ?: return "Serving size must be a whole number of grams"
        if (n < 1)    return "Serving size must be at least 1g"
        if (n > 5000) return "Serving size cannot exceed 5000g"
        return null
    }

    fun macroGrams(raw: String, fieldName: String): Float {
        val v = raw.trim()
        if (v.isEmpty()) return 0f // default to 0, no error
        return v.toFloatOrNull()?.coerceIn(0f, 1000000f) ?: 0f
    }

    // ── Calorie goal ──────────────────────────────────────────────────────────
    fun calorieGoal(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "Daily calorie goal cannot be empty"
        val n = v.toIntOrNull() ?: return "Daily calorie goal must be a whole number"
        if (n < 1500) return "Eating below 1500 kcal a day is unsafe"
        if (n > 5000) return "Eating above 5000 kcal a day is unsafe"
        return null
    }

    // ── Log weight ────────────────────────────────────────────────────────────
    fun logWeightKg(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "Weight cannot be empty"
        val n = v.toFloatOrNull() ?: return "Weight must be a number"
        if (n <= 0)   return "Weight must be greater than 0"
        if (n > 1000) return "Weight must be 1000 kg or below"
        return null
    }

    fun logWeightLbs(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return "Weight cannot be empty"
        val n = v.toFloatOrNull() ?: return "Weight must be a number"
        if (n <= 0)   return "Weight must be greater than 0"
        if (n > 2500) return "Weight must be 2500 lbs or below"
        return null
    }
}
