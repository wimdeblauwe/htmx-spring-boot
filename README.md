[![Discord](https://img.shields.io/discord/725789699527933952)](https://htmx.org/discord)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wimdeblauwe/htmx-spring-boot-thymeleaf/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.wimdeblauwe/htmx-spring-boot-thymeleaf)
[![javadoc](https://javadoc.io/badge2/io.github.wimdeblauwe/htmx-spring-boot/javadoc.svg)](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot)

# Spring Boot and Thymeleaf library for htmx

The project simplifies the integration of [htmx](https://htmx.org/) with [Spring Boot](https://spring.io/projects/spring-boot) / [Spring Web MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html) applications.
It provides a set of views, annotations, and argument resolvers for controllers to easily handle htmx-related request and response headers.
This ensures seamless interaction between the frontend and backend, especially for dynamic content updates via htmx.

Additionally, the project includes a custom [Thymeleaf](https://www.thymeleaf.org/) dialect to enable smooth rendering of htmx-specific attributes within Thymeleaf templates.
With these tools, developers can quickly implement htmx-driven interactions, such as AJAX-based partial page updates, with minimal configuration.

## Maven configuration

The project provides the following libraries, which are available
on [Maven Central](https://mvnrepository.com/artifact/io.github.wimdeblauwe/htmx-spring-boot-thymeleaf),
so it is easy to add the desired dependency to your project.

### htmx-spring-boot

Provides annotations and helper classes.

```xml
<dependency>
    <groupId>io.github.wimdeblauwe</groupId>
    <artifactId>htmx-spring-boot</artifactId>
    <version>LATEST_VERSION_HERE</version>
</dependency>
```

### htmx-spring-boot-thymeleaf

Provides a [Thymeleaf](https://www.thymeleaf.org/) dialect to easily work with htmx attributes.
```xml
<dependency>
    <groupId>io.github.wimdeblauwe</groupId>
    <artifactId>htmx-spring-boot-thymeleaf</artifactId>
    <version>LATEST_VERSION_HERE</version>
</dependency>
```

## Usage

### Configuration

The included Spring Boot Auto-configuration will enable the htmx integrations.

### Mapping Requests

Controller methods can be annotated with
[HxRequest](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRequest.html)
to be selected only if it is a htmx-based request (e.g. `hx-get`).
This annotation allows composition if you want to combine them.
For example, you can combine annotations to create a custom `@HxGetMapping`.

The following method is called only if the request was made by htmx.
```java
@HxRequest
@GetMapping("/users")
public String users() {
    return "view";
}
```

In addition, if you want to restrict the invocation of a controller method to a 
specific triggering element, you can set [HxRequest#value](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRequest.html#value())
to the ID or name of the element. If you want to be explicit use [HxRequest#triggerId](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRequest.html#triggerId())
or [HxRequest#triggerName](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRequest.html#triggerName())

```java
@HxRequest("my-element")
@GetMapping("/users")
public String users() {
    return "view";
}
```
If you want to restrict the invocation of a controller method to having a specific target element defined,
use [HxRequest#target](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRequest.html#target())

```java
@HxRequest(target = "my-target")
@GetMapping("/users")
public String users() {
    return "view";
}
```

### Request Headers

To access the various [htmx Request Headers](https://htmx.org/reference/#request_headers) in a controller method, you can use the class [HtmxRequest](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HtmxRequest.html)
as a controller method argument.

```java
@HxRequest
@GetMapping("/users")
public String users(HtmxRequest htmxRequest) {
    if (htmxRequest.isHistoryRestoreRequest()) {
        // do something
    }
    return "view";
}
```

### Response Headers

There are two ways to set [htmx Response Headers](https://htmx.org/reference/#response_headers) in controller methods. The first is to use [HtmxResponse](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HtmxResponse.html)
as controller method argument in combination with different Views e.g. [HtmxRedirectView](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HtmxRedirectView.html)
as return value. The second is to use annotations, e.g. `@HxTrigger` to set the necessary response headers. The first method is more flexible and allows you to dynamically set the response headers based on the request.

#### HtmxResponse and Views

Most of the [htmx Response Headers](https://htmx.org/reference/#response_headers) can be set by using [HtmxResponse](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HtmxResponse.html) as controller method argument,
except for some control flow response headers such as [HX-Redirect](https://htmx.org/headers/hx-redirect/). For these response headers, you have to use a corresponding view as return value of the controller method.

* [HtmxRedirectView](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HtmxRedirectView.html) - sets the [HX-Redirect](https://htmx.org/headers/hx-redirect/) header to do a client-side redirect.
* [HtmxLocationRedirectView](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HtmxLocationRedirectView.html) - sets the [HX-Location](https://htmx.org/headers/hx-location/) header to do a client-side redirect without reloading the whole page.
* [HtmxRefreshView](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HtmxRefreshView.html) - sets the [HX-Refresh](https://htmx.org/headers/hx-refresh/) header to do a client-side refresh of the current page.

##### Special view name prefixes
For these views, there is also a special view name handling if you prefer to return a view name instead of a view instance.

* Redirect URLs can be specified via `redirect:htmx:`, e.g. `redirect:htmx:/path`, which causes htmx to perform a redirect to the specified URL.
* Location redirect URLs can be specified via `redirect:htmx:location:`, e.g. `redirect:htmx:location:/path`, which causes htmx to perform a client-side redirect without reloading the entire page.
* A refresh of the current page can be specified using `refresh:htmx`.

```java
@HxRequest
@PostMapping("/user/{id}")
public String user(@PathVariable Long id, @ModelAttribute @Valid UserForm form, 
                 BindingResult bindingResult, RedirectAttributes redirectAttributes,
                 HtmxResponse htmxResponse) {

    if (bindingResult.hasErrors()) {
        return "user/form";
    }

    // update user ...
    redirectAttributes.addFlashAttribute("successMessage", "User has been successfully updated.");
    htmxResponse.addTrigger("user-updated");
    
    return "redirect:htmx:/user/list";
}
```

#### Annotations

The following annotations can be used on controller methods to set the necessary response headers.

* [@HxLocation](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxLocation.html)
* [@HxPushUrl](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxPushUrl.html)
* [@HxRedirect](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRedirect.html)
* [@HxRefresh](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRefresh.html)
* [@HxReplaceUrl](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxReplaceUrl.html)
* [@HxReselect](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxReselect.html)
* [@HxReswap](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxReswap.html)
* [@HxRetarget](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRetarget.html)
* [@HxTrigger](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxTrigger.html)
* [@HxTriggerAfterSettle](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxTriggerAfterSettle.html)
* [@HxTriggerAfterSwap](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxTriggerAfterSwap.html)

>**Note** Please refer to the related Javadoc to learn more about the available options.

If you want htmx to trigger an event after the response is processed, you can use the annotation `@HxTrigger` which sets the necessary response header [HX-Trigger](https://htmx.org/headers/hx-trigger/).

```java
@HxRequest
@HxTrigger("userUpdated") // the event 'userUpdated' will be triggered by htmx
@GetMapping("/users")
public String users() {
    return "view";
}
```

### HTML Fragments

In Spring MVC, view rendering typically involves specifying one view and one model. However, in htmx a common capability is to send multiple HTML fragments that
htmx can use to update different parts of the page, which is called [Out Of Band Swaps](https://htmx.org/docs/#oob_swaps). For this, controller methods can return
[HtmxView](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HtmxView.html)

```java
@HxRequest
@GetMapping("/users")
public View users(Model model) {
    model.addAttribute("users", userRepository.findAll());
    model.addAttribute("count", userRepository.count());

    var view = new HtmxView();
    view.add("users/list");
    view.add("users/count");
    
    return view;
}
```

An `HtmxView` can be formed from view names, as above, or fully resolved `View` instances, if the controller knows how
to do that, or from `ModelAndView` instances (resolved or unresolved). Each fragment can have its own model, which is merged with the controller model before rendering.

```java
@HxRequest
@GetMapping("/users")
public View users(Model model) {
    var view = new HtmxView();
    view.add("users/list", Map.of("users", userRepository.findAll()));
    view.add("users/count", Map.of("count", userRepository.count()));

    return view;
}
```


### Exceptions

It is also possible to use `HtmxRequest` and `HtmxResponse` as method argument in handler methods annotated with `@ExceptionHandler`.

```java

@ExceptionHandler(Exception.class)
public String handleError(Exception ex, HtmxRequest htmxRequest, HtmxResponse htmxResponse) {
    if (htmxRequest.isHtmxRequest()) {
        htmxResponse.setRetarget("#error-message");
    }
    return "error";
}
```

### Spring Security

The library has an `HxRefreshHeaderAuthenticationEntryPoint` that you can use to have htmx force a full page browser
refresh in case there is an authentication failure.
If you don't use this, then your login page might be appearing in place of a swap you want to do somewhere.
See [htmx-authentication-error-handling](https://www.wimdeblauwe.com/blog/2022/10/04/htmx-authentication-error-handling/)
blog post for detailed information.

To use it, add it to your security configuration like this:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // probably some other configurations here
    var entryPoint = new HxRefreshHeaderAuthenticationEntryPoint();
    var requestMatcher = new RequestHeaderRequestMatcher("HX-Request");
    http.exceptionHandling(configurer -> configurer.defaultAuthenticationEntryPointFor(entryPoint, requestMatcher));
    return http.build();
}
```

### Thymeleaf

#### Markup Selectors and HTML Fragments

The Thymeleaf integration for Spring supports the specification of a [Markup Selector](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#appendix-c-markup-selector-syntax)
for views. The Markup Selector will be used for selecting the section
of the template that should be processed, discarding the rest of the template.
This is quite handy when it comes to htmx and for example [Out Of Band Swaps](https://htmx.org/docs/#oob_swaps),
where you only have to return parts of your template.

The following example combines two partials via `HtmxResponse` with a Markup Selector
that selects the fragment `list` (`th:fragment="list"`) and another that selects the
fragment `count` (th:fragment="count") from the template `users`.  

```java
@HxRequest
@GetMapping("/users")
public View users(Model model) {
    model.addAttribute("users", userRepository.findAll());
    model.addAttribute("count", userRepository.count());

    var view = new HtmxView();
    view.add("users :: list");
    view.add("users :: count");

    return view;
}
```

#### Dialect

The Thymeleaf dialect has appropriate processors that enable Thymeleaf to perform calculations and expressions
in htmx-related attributes.

_See [Attribute Reference](https://htmx.org/reference/#attributes) for the related htmx documentation._

>**Note** The `:` colon instead of the typical hyphen.

- `hx:get`: This is a Thymeleaf processing enabled attribute
- `hx-get`: This is just a static attribute if you don't need the Thymeleaf processing

For example, this Thymeleaf template:

```html
<div hx:get="@{/users/{id}(id=${userId})}" hx-target="#otherElement">Load user details</div>
```

Will be rendered as:

```html
<div hx-get="/users/123" hx-target="#otherElement">Load user details</div>
```

The Thymeleaf dialect has corresponding processors for most of the `hx-*` attributes.
Please [open an issue](https://github.com/wimdeblauwe/htmx-spring-boot-thymeleaf/issues) if something is missing.

> **Note**
> Be careful about using `#` in the value. If you do `hx:target="#mydiv"`, then this will not work as Thymeleaf uses
> the `#` symbol for translation keys. Either use `hx-target="#mydiv"` or `hx:target="${'#mydiv'}"`

#### Map support for hx:vals

The [hx-vals](https://htmx.org/attributes/hx-vals/) attribute allows to add to the parameters that will be submitted
with the AJAX request. The value of the attribute should be a JSON string.

The library makes it a bit easier to write such a JSON string by adding support for inline maps.

For example, this Thymeleaf expression:

```html
<div hx:vals="${ {id: user.id } }"></div>
```

will render as:

```html
<div hx-vals="{&amp;quot;id&amp;quot;: 1234 }"></div>
```

(Given `user.id` has the value `1234`)

You can use multiple values like this:

```html

<div hx:vals="${ {id: user.id, groupId: group.id } }"></div>
```

## Articles

Links to articles and blog posts about this library:

* [Release 1.0.0 and 2.0.0 of htmx-spring-boot-thymeleaf](https://www.wimdeblauwe.com/blog/2022/12/11/release-1.0.0-and-2.0.0-of-htmx-spring-boot-thymeleaf/)
* [Htmx authentication error handling](https://www.wimdeblauwe.com/blog/2022/10/04/htmx-authentication-error-handling/)
* [Thymeleaf and htmx with out of band swaps](https://www.wimdeblauwe.com/blog/2022/06/15/thymeleaf-and-htmx-with-out-of-band-swaps/)

## Spring Boot compatibility

| Library version                                                                       | Spring Boot | Minimum Java version |
|---------------------------------------------------------------------------------------|-------------|----------------------|
| [3.6.1](https://github.com/wimdeblauwe/htmx-spring-boot/releases/tag/3.6.1)           | 3.2.x       | 17                   |
| [3.5.1](https://github.com/wimdeblauwe/htmx-spring-boot/releases/tag/3.5.1)           | 3.2.x       | 17                   |
| [3.4.1](https://github.com/wimdeblauwe/htmx-spring-boot/releases/tag/3.4.1)           | 3.2.x       | 17                   |
| [3.3.0](https://github.com/wimdeblauwe/htmx-spring-boot/releases/tag/3.3.0)           | 3.1.x       | 17                   |
| [3.2.0](https://github.com/wimdeblauwe/htmx-spring-boot/releases/tag/3.2.0)           | 3.1.x       | 17                   |
| [2.2.0](https://github.com/wimdeblauwe/htmx-spring-boot-thymeleaf/releases/tag/2.2.0) | 3.0.x       | 17                   |
| [1.0.0](https://github.com/wimdeblauwe/htmx-spring-boot-thymeleaf/releases/tag/1.0.0) | 2.7.x       | 11                   |

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[Apache 2.0](https://choosealicense.com/licenses/apache-2.0/)

## Release

To release a new version of the project, follow these steps:

1. Update `pom.xml` with the new version (Use `mvn versions:set -DgenerateBackupPoms=false -DnewVersion=<VERSION>`)
2. Commit the changes locally.
3. Tag the commit with the version (e.g. `1.0.0`) and push the tag.
4. Create a new release in GitHub via https://github.com/wimdeblauwe/htmx-spring-boot/releases/new
    - Select the newly pushed tag
    - Update the release notes. This should automatically start
      the [release action](https://github.com/wimdeblauwe/htmx-spring-boot-thymeleaf/actions).
5. Update `pom.xml` again with the next `SNAPSHOT` version.
6. Close the milestone in the GitHub issue tracker.
