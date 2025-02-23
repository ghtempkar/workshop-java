package my.w250223s1.sample1

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.json.JsonCompareMode
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(
    controllers = [Sample1OrderController::class],
    excludeAutoConfiguration = [
//        SecurityAutoConfiguration::class,
    ]
)
@Import(
    Sample1BasicAuthSecurityConfig::class,
)
class Sample1OrderControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `forbidden when no user`() {
        mockMvc.perform(
            post("/sample1/orders")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `forbidden when mock user is _user_`() {
        mockMvc.perform(
            post("/sample1/orders")
        )
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `ok when mock user is _admin_`() {
        mockMvc.perform(
            post("/sample1/orders")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `ok when basic auth is user _admin_`() {
        mockMvc.perform(
            post("/sample1/orders")
                .with(httpBasic("admin", "adminpassword"))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `unauthorized when basic auth is user _user_ with wrong password`() {
        mockMvc.perform(
            post("/sample1/orders")
                .with(httpBasic("user", "XXX"))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `forbidden when basic auth is user _user_`() {
        mockMvc.perform(
            post("/sample1/orders")
                .with(httpBasic("user", "userpassword"))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `returns user details for basic auth user`() {
        mockMvc.perform(
            post("/sample1/orders/2/json")
                .with(httpBasic("admin", "adminpassword"))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect {
//                println(it)
            }
            .andExpect(content().json("""["ROLE_ADMIN"]""", JsonCompareMode.STRICT))
    }

    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN", "USER"])
    fun `returns user details for mock user`() {
        mockMvc.perform(
            post("/sample1/orders/2/json")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect {
//                println(it)
            }
            .andExpect(content().json("""["ROLE_ADMIN", "ROLE_USER"]""", JsonCompareMode.STRICT))
    }
}
