package my.w250303s4

import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.ui.Model
import jakarta.validation.Valid

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserForm(
    @field:NotBlank(message = "Nazwa użytkownika jest wymagana")
    @field:Size(min = 3, message = "Nazwa użytkownika musi mieć co najmniej 3 znaki")
    var username: String = "",

    @field:NotBlank(message = "Email jest wymagany")
    @field:Email(message = "Niepoprawny format adresu email")
    var email: String = "",

    @field:NotBlank(message = "Hasło jest wymagane")
    @field:Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków")
    var password: String = ""
)

@Controller
@RequestMapping("/register")
class RegisterController {

    @GetMapping
    fun showForm(model: Model): String {
        model.addAttribute("userForm", UserForm())
        return "register"
    }

    @PostMapping
    fun processForm(
        @ModelAttribute("userForm") @Valid userForm: UserForm,
        bindingResult: BindingResult
    ): String {
        if (bindingResult.hasErrors()) {
            return "fragments/my-form2 :: form-content" // Zwracamy tylko fragment formularza
        }
        // Jeśli walidacja się powiodła, przekieruj do sukcesu
        return "redirect:/success"
    }
}
