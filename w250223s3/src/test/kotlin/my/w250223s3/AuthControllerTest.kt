package my.w250223s3

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AuthController::class)
internal class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `auth_me is forbidden for not logged in user`() {
        mockMvc.perform(
            get("/auth/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `auth_me is ok for for logged in user`() {
        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/auth/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }
}
