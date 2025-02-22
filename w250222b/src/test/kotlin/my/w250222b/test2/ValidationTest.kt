package my.w250222b.test2

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


data class TestRequest(
    @field:NotBlank
    val name: String? = null
)

@RestController
@RequestMapping("/testing")
class TestController {

    @PostMapping
    fun test1(@RequestBody @Valid request: TestRequest) {

    }

}

@WebMvcTest(
    controllers = [TestController::class],
//    excludeFilters = [
//        ComponentScan.Filter(
//            type = FilterType.ANNOTATION,
//            classes = [Configuration::class]
//        )
//    ]
    )

class ValidationTest {

    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun test1() {
        mockMvc.perform(
            post("/testing")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language",  "pl")
                .content(objectMapper.writeValueAsString(TestRequest(name = "")))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.name").value("must not be blank PL XXX"))
    }

}