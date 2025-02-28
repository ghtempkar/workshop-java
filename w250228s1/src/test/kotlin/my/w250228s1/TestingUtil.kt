package my.w250228s1

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

fun prettyPrintJson(jsonString: String): String {
    val objectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val jsonNode = objectMapper.readTree(jsonString)
    return objectMapper.writeValueAsString(jsonNode)
}
