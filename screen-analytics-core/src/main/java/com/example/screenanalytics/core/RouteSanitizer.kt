package com.example.screenanalytics.core

object RouteSanitizer {
    
    // Patterns can be registered to resolve complex routes to base screen names
    // Example: "profile/{id}" -> "Profile"
    private val routePatterns = mutableMapOf<Regex, String>()

    fun registerRoute(pattern: String, screenName: String) {
        // Convert a path like "profile/{id}" into a regex like "^profile/[^/]+$"
        val regexPattern = pattern.replace(Regex("\\{[^/]+\\}"), "[^/]+")
        routePatterns[Regex("^$regexPattern$")] = screenName
    }

    fun sanitize(route: String): String {
        // Find matching registered pattern
        for ((regex, screenName) in routePatterns) {
            if (regex.matches(route)) {
                return screenName
            }
        }
        
        // Default sanitization: Strip query parameters and dynamic-looking segments
        val withoutQuery = route.substringBefore("?")
        return withoutQuery.split("/").joinToString("/") { segment ->
            if (segment.any { it.isDigit() }) "{id}" else segment
        }
    }
}
