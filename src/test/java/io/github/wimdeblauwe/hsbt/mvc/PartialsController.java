package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PartialsController {

    private final TodoRepository todoRepository;

    public PartialsController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public interface TodoRepository {
        public int getNumberOfActiveItems();
    }

    public static class TodoItem{
        String name;

        public TodoItem(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @GetMapping("/partials/first")
    public HtmxPartials getFirstPartials() {
        return new HtmxPartials().addTemplate("users :: list");
    }

    @GetMapping("/partials/main-and-partial")
    public HtmxPartials getMainAndPartial(Model model) {
        model.addAttribute("userCountOob", true);
        model.addAttribute("userCount", 5);
        return new HtmxPartials()
                .addTemplate("users :: list")
                .addTemplate("users :: count");
    }

    @PostMapping("/partials/add-todo")
    public HtmxPartials htmxAddTodoItem(TodoItem item, Model model) {
        model.addAttribute("item", item);
        model.addAttribute("itemCountSwap", "true");
        model.addAttribute("numberOfActiveItems", todoRepository.getNumberOfActiveItems());
        return new HtmxPartials()
                .addTemplate("fragments :: todoItem")
                .addTemplate("fragments :: active-items-count");
    }

}
