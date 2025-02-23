package my.w250223s1.sample2

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(
    controllers = [Sample2OrderController::class],
)
@Import(
    Sample2BasicAuthSecurityConfig::class,
    Sample2OrderService::class,
)
class Sample2OrderControllerIntTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `forbidden when no user`() {
        mockMvc.perform(
            post("/sample2/orders")
        )
            .andExpect(status().isUnauthorized)
    }
}
