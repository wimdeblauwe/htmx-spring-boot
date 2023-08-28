package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.AbstractView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class PartialsController {

    private final TodoRepository todoRepository;

    public PartialsController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public interface TodoRepository {
        public int getNumberOfActiveItems();
    }

    public static class TodoItem {
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
    public HtmxResponse getFirstPartials() {
        return HtmxResponse.builder().template("users :: list").build();
    }

    @GetMapping("/partials/view")
    public HtmxResponse getFirstView() {
        var view = new AbstractView() {
            @Override
            protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
                response.getWriter().write("<ul><li>A list</li></ul>");
            }

        };

        return HtmxResponse.builder().template(view).build();
    }

    @GetMapping("/partials/mav")
    public HtmxResponse getFirstModelAndView() {
        return HtmxResponse.builder()
            .template(new ModelAndView("fragments :: todoItem", Map.of("item", new TodoItem("Foo"))))
            .build();
    }

    @GetMapping("/partials/main-and-partial")
    public HtmxResponse getMainAndPartial(Model model) {
        model.addAttribute("userCountOob", true);
        model.addAttribute("userCount", 5);

        return HtmxResponse.builder()
                .template("users :: list")
                .template("users :: count")
                .build();
    }

    @PostMapping("/partials/add-todo")
    public HtmxResponse htmxAddTodoItem(TodoItem item, Model model) {
        model.addAttribute("item", item);
        model.addAttribute("itemCountSwap", "true");
        model.addAttribute("numberOfActiveItems", todoRepository.getNumberOfActiveItems());

        return HtmxResponse.builder()
                .template("fragments :: todoItem")
                .template("fragments :: active-items-count")
                .build();
    }

    @GetMapping("/partials/triggers")
    public HtmxResponse getPartialsAndTriggers(Model model) {
        model.addAttribute("userCountOob", true);
        model.addAttribute("userCount", 5);

        return HtmxResponse.builder()
            .template("users :: list")
            .template("users :: count")
            .trigger("usersCounted")
            .trigger("usersCountedSwap", "swap detail", HxTriggerLifecycle.SWAP)
            .trigger("usersCountedSettle1", "aDetail", HxTriggerLifecycle.SETTLE)
            .trigger("usersCountedSettle2", null, HxTriggerLifecycle.SETTLE)
            .retarget("#newTarget")
            .pushUrl("/a/newHistory")
            .refresh()
            .redirect("/a/redirect")
            .build();
    }


    @GetMapping("/partials/extension")
    public HtmxResponse getPartialsViaExtension(Model model) {
        model.addAttribute("userCountOob", true);
        model.addAttribute("userCount", 5);

        return HtmxResponse.builder()
            .template("users :: list")
            .template("users :: count")
            .and(CommonHtmxResponses.sendAlertPartial(model, "Warning! Odium approaches!"))
            .build();
    }


    @GetMapping("/partials/expressionUtility")
    public String getWithExpressionUtility() {
        return "htmxRequest";
    }

}
