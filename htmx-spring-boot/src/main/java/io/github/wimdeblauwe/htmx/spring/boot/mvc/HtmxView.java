package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * View that can be used to return multiple views as fragments to be rendered together.
 *
 * <p>In Spring MVC, view rendering typically involves specifying one view and one model.
 * However, in htmx a common capability is to send multiple HTML fragments that the browser
 * can use to update different parts of the page. For this, controller methods can return
 * this class.
 *
 * @since 3.6.0
 */
public class HtmxView {

    private final Set<ModelAndView> views = new LinkedHashSet<>();

    /**
     * Create a new HtmxView.
     */
    public HtmxView() {
    }

    /**
     * Create a new HtmxView with the given view names.
     *
     * @param viewNames the view names
     */
    public HtmxView(String... viewNames) {
        for (String name : viewNames) {
            this.views.add(new ModelAndView(name));
        }
    }

    /**
     * Create a new HtmxView with the given Views.
     *
     * @param views the views
     */
    public HtmxView(View... views) {
        for (View view : views) {
            this.views.add(new ModelAndView(view));
        }
    }

    /**
     * Create a new HtmxView with the given ModelAndViews
     *
     * @param mavs
     */
    public HtmxView(ModelAndView... mavs) {
        for (ModelAndView mav : mavs) {
            this.views.add(mav);
        }
    }

    /**
     * Add a ModelAndView with the given view name to the list of views to render.
     *
     * @param viewName name of the View to render, to be resolved by the DispatcherServlet's ViewResolver
     */
    public void add(String viewName) {
        this.views.add(new ModelAndView(viewName));
    }

    /**
     * Add a ModelAndView with the given view name and a model to the list of views to render.
     *
     * @param viewName name of the View to render, to be resolved by the DispatcherServlet's ViewResolver
     * @param model    a Map of model names (Strings) to model objects (Objects).
     *                 Model entries may not be {@code null}, but the model Map may be
     *                 {@code null} if there is no model data.
     */
    public void add(String viewName, @Nullable Map<String, ?> model) {
        this.views.add(new ModelAndView(viewName, model));
    }

    /**
     * Add a ModelAndView with the given View to the list of views to render.
     *
     * @param view the View object to render
     */
    public void add(View view) {
        this.views.add(new ModelAndView(view));
    }

    /**
     * Add a ModelAndView with the given View to the list of views to render.
     *
     * @param view  the View object to render
     * @param model a Map of model names (Strings) to model objects (Objects).
     *              Model entries may not be {@code null}, but the model Map may be
     *              {@code null} if there is no model data.
     */
    public void add(View view, @Nullable Map<String, ?> model) {
        this.views.add(new ModelAndView(view));
    }

    /**
     * Add a ModelAndView to the list of views to render.
     *
     * @param mav the ModelAndView
     */
    public void add(ModelAndView mav) {
        this.views.add(mav);
    }

    /**
     * Return the views to render.
     *
     * @return the views
     */
    public Set<ModelAndView> getViews() {
        return views;
    }

}
