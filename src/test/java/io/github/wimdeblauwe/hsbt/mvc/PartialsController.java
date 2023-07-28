package io.github.wimdeblauwe.hsbt.mvc;

import static io.github.wimdeblauwe.hsbt.mvc.CommonHtmxResponses.sendAlertPartial;

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
        return new HtmxResponse().addTemplate("users :: list");
    }

    @GetMapping("/partials/view")
    public HtmxResponse getFirstView() {
        return new HtmxResponse().addTemplate(new AbstractView() {
            @Override
            protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
                        response.getWriter().write("<ul><li>A list</li></ul>");
            }
            
        });
    }

    @GetMapping("/partials/mav")
    public HtmxResponse getFirstModelAndView() {
        return new HtmxResponse().addTemplate(new ModelAndView("fragments :: todoItem", Map.of("item", new TodoItem("Foo"))));
    }

    @GetMapping("/partials/main-and-partial")
    public HtmxResponse getMainAndPartial(Model model) {
        model.addAttribute("userCountOob", true);
        model.addAttribute("userCount", 5);
        return new HtmxResponse()
                .addTemplate("users :: list")
                .addTemplate("users :: count");
    }

    @PostMapping("/partials/add-todo")
    public HtmxResponse htmxAddTodoItem(TodoItem item, Model model) {
        model.addAttribute("item", item);
        model.addAttribute("itemCountSwap", "true");
        model.addAttribute("numberOfActiveItems", todoRepository.getNumberOfActiveItems());
        return new HtmxResponse()
                .addTemplate("fragments :: todoItem")
                .addTemplate("fragments :: active-items-count");
    }

    @GetMapping("/partials/triggers")
    public HtmxResponse getPartialsAndTriggers(Model model) {
        model.addAttribute("userCountOob", true);
        model.addAttribute("userCount", 5);

        return new HtmxResponse().addTemplate("users :: list")
                                 .addTemplate("users :: count")
                                 .addTrigger("usersCounted")
                                 .addTrigger("usersCountedSwap", "swap detail", HxTriggerLifecycle.SWAP)
                                 .addTrigger("usersCountedSettle1", "aDetail", HxTriggerLifecycle.SETTLE)
                                 .addTrigger("usersCountedSettle2", null, HxTriggerLifecycle.SETTLE)
                .retarget("#newTarget")
                .pushHistory("/a/newHistory")
                .browserRefresh(true)
                .browserRedirect("/a/redirect");

    }


    @GetMapping("/partials/extension")
    public HtmxResponse getPartialsViaExtension(Model model) {
        model.addAttribute("userCountOob", true);
        model.addAttribute("userCount", 5);

        return new HtmxResponse().addTemplate("users :: list")
                                 .addTemplate("users :: count")
                                 .and(sendAlertPartial(model, "Warning! Odium approaches!"));
    }


    @GetMapping("/partials/expressionUtility")
    public String getWithExpressionUtility() {
        return "htmxRequest";
    }


}
