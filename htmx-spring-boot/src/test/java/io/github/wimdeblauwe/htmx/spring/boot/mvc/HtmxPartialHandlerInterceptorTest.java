package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.support.PartialXpathResultMatchers.partialXpath;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@WebMvcTest(PartialsController.class)
@WithMockUser
class HtmxPartialHandlerInterceptorTest {

    @MockBean
    private PartialsController.TodoRepository todoRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testASinglePartialCanBeReturned() throws Exception {
        mockMvc.perform(get("/partials/first"))
                .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
               .andExpect(xpath("/ul").exists())
               .andExpect(xpath("/ul[@hx-swap-oob='true']").doesNotExist());
    }

    @Test
    public void testASingleViewCanBeReturned() throws Exception {
        mockMvc.perform(get("/partials/view"))
                .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
               .andExpect(xpath("/ul").exists());
    }

    @Test
    public void testASingleModelAndViewCanBeReturned() throws Exception {
        mockMvc.perform(get("/partials/mav"))
                .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
               .andExpect(partialXpath("/span[@id='item']").exists())
               .andExpect(content().string(containsString("Foo")));
    }

    @Test
    public void testAMainChange() throws Exception {
        mockMvc.perform(get("/partials/main-and-partial"))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
                //Note xpath can't be used on partials because the document doesn't have a single root
               .andExpect(partialXpath("//*[@id='userCount'][@hx-swap-oob]").exists())
               .andExpect(partialXpath("/ul").exists())
               .andExpect(partialXpath("/ul[@hx-swap-oob]").doesNotExist());
    }

    @Test
    public void testHeaders() throws Exception {
        mockMvc.perform(get("/partials/triggers"))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger", "usersCounted"))
               .andExpect(header().string("HX-Trigger-After-Settle", "{\"usersCountedSettle1\":\"aDetail\",\"usersCountedSettle2\":null}"))
               .andExpect(header().string("HX-Trigger-After-Swap", "{\"usersCountedSwap\":\"swap detail\"}"))
               .andExpect(header().string("HX-Push-Url", "/a/newHistory"))
               .andExpect(header().string("HX-Redirect", "/a/redirect"))
               .andExpect(header().string("HX-Refresh", "true"))
               .andExpect(header().string("HX-Retarget", "#newTarget"));
    }
    @Test
    public void testExtension() throws Exception {
        mockMvc.perform(get("/partials/extension"))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
               .andExpect(partialXpath("//*[@id='alert'][@hx-swap-oob]").exists())
               .andExpect(header().string("HX-Trigger", "alertSent"));
    }

    @Test
    public void testPostTodo() throws Exception {
        when(todoRepository.getNumberOfActiveItems()).thenReturn(0);

        mockMvc.perform(post("/partials/add-todo")
                                .param("name", "A Todo Name")
                                .with(csrf()))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
                //Note xpath can't be used on partials because the document doesn't have a single root
               .andExpect(partialXpath("//*[@id='active-items-count'][@hx-swap-oob]").exists())
               .andExpect(partialXpath("/span[@id='item']").exists())
               .andExpect(partialXpath("/span[@id='item'][@hx-swap-oob]").doesNotExist());
    }

}
