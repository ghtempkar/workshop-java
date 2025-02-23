package my.w250223s1.sample2

import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(
    controllers = [Sample2OrderController::class],
)
@Import(
    Sample2BasicAuthSecurityConfig::class,
)
class Sample2OrderControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @MockitoBean lateinit var orderService: Sample2OrderService

    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `ok when mock user is _admin_`() {
        whenever(orderService.add(any())).thenReturn("AAA")

        mockMvc.perform(
            post("/sample2/orders")
        )
            .andExpect(status().isOk)
            .andExpect {
                println(it)
            }
    }
}
