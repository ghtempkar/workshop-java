package my.w250303s4

import java.time.Instant
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

data class Task(
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var description: String
)

@Controller
@RequestMapping("/")
class TaskController(private val taskRepository: MutableList<Task>) {

    var c: Long = 0

    @GetMapping
    fun showTasks(model: Model): String {
        model.addAttribute("tasks", taskRepository.toList())
        model.addAttribute("userForm", UserForm())
        return "index"
    }

    @PostMapping("/add")
    fun addTask(@RequestParam description: String, model: Model): String {
        val task = Task(description = description, id = c++)
        taskRepository.add(task)
        model.addAttribute("tasks", taskRepository.toList())
        return "fragments/task-list :: task-list"
    }

    @PostMapping("/delete/{id}")
    fun deleteTask(@PathVariable id: Long, model: Model): String {
        taskRepository.removeIf { it.id == id }
        model.addAttribute("tasks", taskRepository.toList())
        return "fragments/task-list :: task-list"
    }

    @PostMapping("/action1")
    fun action1(): String {
        return "fragments/my-frag1 :: frag2"
    }

    @PostMapping("/action2")
    @ResponseBody
    fun action2(): String {
        return "<div>${c++} ${Instant.now()}<div>"
    }

    @PostMapping("/longAction")
    @ResponseBody
    fun longAction(): String {
        Thread.sleep(2_000)
        return "<div>it is a long action</div>"
    }

}