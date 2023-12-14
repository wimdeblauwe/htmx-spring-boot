[![Discord](https://img.shields.io/discord/725789699527933952)](https://htmx.org/discord)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wimdeblauwe/htmx-spring-boot-thymeleaf/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.wimdeblauwe/htmx-spring-boot-thymeleaf)
[![javadoc](https://javadoc.io/badge2/io.github.wimdeblauwe/htmx-spring-boot/javadoc.svg)](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot)

# Spring Boot and Thymeleaf library for htmx

This project provides annotations, helper classes and a [Thymeleaf](https://www.thymeleaf.org/) dialect
to make it easy to work with [htmx](https://htmx.org/)
in a [Spring Boot](https://spring.io/projects/spring-boot) application.

More information about htmx can be viewed on [their website](https://htmx.org/).

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

### Mapping controller methods to htmx requests

Controller methods can be annotated with
[HxRequest](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRequest.html)
to be selected only if it is a htmx-based request (e.g. `hx-get`).
This annotation allows composition if you want to combine them.
For example, you can combine annotations to create a custom `@HxGetMapping`.

The following method is called only if the request was made by htmx.
```java
@HxRequest
@GetMapping("/users")
public String htmxRequest(){
    return "partial";
}
```

In addition, if you want to restrict the invocation of a controller method to a 
specific triggering element, you can set [HxRequest#value](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRequest.html#value())
to the ID or name of the element. If you want to be explicit use [HxRequest#triggerId](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRequest.html#triggerId())
or [HxRequest#triggerName](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRequest.html#triggerName())

```java
@HxRequest("my-element")
@GetMapping("/users")
public String htmxRequest(){
    return "partial";
}
```
If you want to restrict the invocation of a controller method to having a specific target element defined,
use [HxRequest#target](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRequest.html#target())

```java
@HxRequest(target = "my-target")
@GetMapping("/users")
public String htmxRequest(){
    return "partial";
}
```

#### Using HtmxRequest to access HTTP request headers sent by htmx

The [HtmxRequest](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HtmxRequest.html) object can be used as controller method parameter to access the various [htmx Request Headers](https://htmx.org/reference/#request_headers).

```java
@HxRequest
@GetMapping("/users")
public String htmxRequest(HtmxRequest htmxRequest) {
    if(htmxRequest.isHistoryRestoreRequest()){
        // do something
    }
    return "partial";
}
```

### Response Headers

There are two ways to set [htmx Response Headers](https://htmx.org/reference/#response_headers) on controller methods.
The first is to use annotations, e.g. `@HxTrigger`, and the second is to use the class [HtmxResponse](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HtmxResponse.html) as the return type of the controller method.

See [Response Headers Reference](https://htmx.org/reference/#response_headers) for the related htmx documentation.

The following annotations are currently supported:
* [@HxRefresh](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRefresh.html)
* [@HxTrigger](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxTrigger.html)

>**Note** Please refer to the related Javadoc to learn more about the available options.

#### Examples

If you want htmx to trigger an event after the response is processed, you can use the annotation `@HxTrigger` which sets the necessary response header [HX-Trigger](https://htmx.org/headers/hx-trigger/).

```java
@HxRequest
@HxTrigger("userUpdated") // the event 'userUpdated' will be triggered by htmx
@GetMapping("/users")
public String hxUpdateUser(){
    return "partial";
}
```

If you want to do the same, but in a more flexible way, you can use `HtmxResponse` as the return type in the controller method instead.
```java
@HxRequest
@GetMapping("/users")
public HtmxResponse hxUpdateUser(){
    return HtmxResponse.builder()
        .trigger("userUpdated") // the event 'userUpdated' will be triggered by htmx
        .view("partial")
        .build();
}
```

### Out Of Band Swaps

htmx supports updating multiple targets by returning multiple partials in a single response, which is called [Out Of Band Swaps](https://htmx.org/docs/#oob_swaps).
For this purpose, use [HtmxResponse](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HtmxResponse.html)
as the return type of a controller method, where you can add multiple templates.

```java
@HxRequest
@GetMapping("/partials/main-and-partial")
public HtmxResponse getMainAndPartial(Model model){
    model.addAttribute("userCount", 5);
    return HtmxResponse.builder()
        .view("users-list")
        .view("users-count")
        .build();
}
```

An `HtmxResponse` can be formed from view names, as above, or fully resolved `View` instances, if the controller knows how
to do that, or from `ModelAndView` instances (resolved or unresolved). For example:

```java
@HxRequest
@GetMapping("/partials/main-and-partial")
public HtmxResponse getMainAndPartial(Model model){
    return HtmxResponse.builder()
        .view(new ModelAndView("users-list")
        .view(new ModelAndView("users-count", Map.of("userCount",5))
        .build();
}
```

Using `ModelAndView` means that each fragment can have its own model (which is merged with the controller model before rendering).

### Error handlers

It is possible to use `HtmxResponse` as a return type from error handlers.
This makes it quite easy to declare a global error handler that will show a message somewhere whenever there is an error
by declaring a global error handler like this:

```java

@ExceptionHandler(Exception.class)
public HtmxResponse handleError(Exception ex) {
    return HtmxResponse.builder()
                       .reswap(HtmxReswap.none())
                       .view(new ModelAndView("fragments :: error-message", Map.of("message", ex.getMessage())))
                       .build();
}
```

This will override the normal swapping behaviour of any htmx request that has an exception to avoid swapping to occur.
If the `error-message` fragment is declared as an Out Of Band Swap and your page layout has an empty div to "receive"
that piece of HTML, then only that will be placed on the screen.

### Spring Security

The library has an `HxRefreshHeaderAuthenticationEntryPoint` that you can use to have htmx force a full page browser
refresh in case there is an authentication failure.
If you don't use this, then your login page might be appearing in place of a swap you want to do somewhere.
See [htmx-authentication-error-handling](https://www.wimdeblauwe.com/blog/2022/10/04/htmx-authentication-error-handling/)
blog post for detailed information.

To use it, add it to your security configuration like this:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
    // probably some other configurations here

    var entryPoint = new HxRefreshHeaderAuthenticationEntryPoint();
    var requestMatcher = new RequestHeaderRequestMatcher("HX-Request");
    http.exceptionHandling(exception ->
        exception.defaultAuthenticationEntryPointFor(entryPoint, requestMatcher));
    return http.build();
}
```

### Thymeleaf

#### Markup Selectors and Out Of Band Swaps

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
@GetMapping("/partials/main-and-partial")
public HtmxResponse getMainAndPartial(Model model){
    model.addAttribute("userCount", 5);
    return HtmxResponse.builder()
        .view("users :: list")
        .view("users :: count")
        .build();
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

## Articles

Links to articles and blog posts about this library:

* [Release 1.0.0 and 2.0.0 of htmx-spring-boot-thymeleaf](https://www.wimdeblauwe.com/blog/2022/12/11/release-1.0.0-and-2.0.0-of-htmx-spring-boot-thymeleaf/)
* [Htmx authentication error handling](https://www.wimdeblauwe.com/blog/2022/10/04/htmx-authentication-error-handling/)
* [Thymeleaf and htmx with out of band swaps](https://www.wimdeblauwe.com/blog/2022/06/15/thymeleaf-and-htmx-with-out-of-band-swaps/)

## Spring Boot compatibility

| Library version                                                                       | Spring Boot | Minimum Java version |
|---------------------------------------------------------------------------------------|-------------|----------------------|
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
