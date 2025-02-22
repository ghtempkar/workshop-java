package my.w250222b.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@Configuration
class MyConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper = jacksonObjectMapper()

}

@ControllerAdvice
class ControllerExceptionHandler(
    private val messageSource: MessageSource,
) {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleValidationException(ex: MethodArgumentNotValidException): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val locale = LocaleContextHolder.getLocale()
//        val errors = mutableMapOf<String, String>()
        ex.bindingResult.fieldErrors.forEach { error ->
            val message = messageSource.getMessage(error, locale)
            errors[error.field] = message
//            errors[error.field] = error.defaultMessage ?: "Invalid value"
        }
        return errors
    }
}