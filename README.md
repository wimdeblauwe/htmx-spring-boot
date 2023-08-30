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

### Request Headers

_See [Request Headers Reference](https://htmx.org/reference/#request_headers) for the related htmx documentation._

Methods can be annotated with `@HxRequest` to be selected only if it is an htmx-based request (e.g. `hx-get`).

```java
// Called when the request was made with htmx
@HxRequest 
@GetMapping("/users")
public String htmxRequest(HtmxRequest details){
    service.doSomething(details);
    return "partial";
}

// Called when a normal request was made
@GetMapping("/users")
public String normalRequest(HtmxRequest details){
    service.doSomething(details);
    return "users";
}
```

These annotations allow for composition if you wish to combine them,
so you could combine annotations to make a custom `@HxGetMapping`.

#### Using `HtmxRequest` to access HTTP request headers sent by htmx

The [HtmxRequest](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HtmxRequest.html) object can be used as controller method parameter to access the various [htmx Request Headers](https://htmx.org/reference/#request_headers).

```java
@GetMapping
public String htmxRequestDetails(HtmxRequest htmxRequest) {
    if(htmxRequest.isHistoryRestoreRequest()){
        // do something
    }
    return "partial";
}
```

### Response Headers

_See [Response Headers Reference](https://htmx.org/reference/#response_headers) for the related htmx documentation._

There are two ways to set htmx response headers on controller methods.
The first is to use annotations, e.g. `@HxTrigger`, and the second is to use the class [HtmxResponse](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HtmxResponse.html) as the return type of the controller method.

Here you can find a list of all available annotations:
* [@HxLocation](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxLocation.html)
* [@HxPushUrl](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxPushUrl.html)
* [@HxRedirect](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRedirect.html)
* [@HxRefresh](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRefresh.html)
* [@HxReplaceUrl](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxReplaceUrl.html)
* [@HxReselect](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxReselect.html)
* [@HxReswap](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxReswap.html)
* [@HxRetarget](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxRetarget.html)
* [@HxTrigger](https://javadoc.io/doc/io.github.wimdeblauwe/htmx-spring-boot/latest/io/github/wimdeblauwe/htmx/spring/boot/mvc/HxTrigger.html)

>**Note** Please check the corresponding Javadoc to learn about the available options.

#### Examples
If you want htmx to trigger an event by setting the response header [HX-Trigger](https://htmx.org/headers/hx-trigger/), you can use the annotation `@HxTrigger`.
```java
@GetMapping("/users")
@HxRequest
@HxTrigger("userUpdated") // 'userUpdated' event will be triggered by htmx
public String hxUpdateUser(){
    return "users";
}
```

If you want to do the same, but in a more flexible way, you can use `HtmxResponse` as the return type in the controller method instead.
```java
@GetMapping("/users")
@HxRequest
public HtmxResponse hxUpdateUser(){
    return HtmxResponse.builder()
        .template("users")
        .trigger("userUpdated") // 'userUpdated' event will be triggered by htmx
        .build();
}
```

### OOB Swap support

htmx supports updating multiple targets by returning multiple partials in a single response with
[hx-swap-oob](https://htmx.org/docs/#oob_swaps). Return partials using this library use the `HtmxResponse` as a return
type.
```java
@GetMapping("/partials/main-and-partial")
public HtmxResponse getMainAndPartial(Model model){
    model.addAttribute("userCount", 5);
    return HtmxResponse.builder()
        .template("users :: list")
        .template("users :: count")
        .build();
}
```

An `HtmxResponse` can be formed from view names, as above, or fully resolved `View` instances, if the controller knows how
to do that, or from `ModelAndView` instances (resolved or unresolved). For example:

```java
@GetMapping("/partials/main-and-partial")
public HtmxResponse getMainAndPartial(Model model){
    return HtmxResponse.builder()
        .template(new ModelAndView("users :: list")
        .template(new ModelAndView("users :: count", Map.of("userCount",5))
        .build();
}
```

Using `ModelAndView` means that each fragment can have its own model (which is merged with the controller model before rendering).

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

_See [Attribute Reference](https://htmx.org/reference/#attributes) for the related htmx documentation._

The Thymeleaf dialect has appropriate processors that enable Thymeleaf to perform calculations and expressions
in htmx-related attributes.

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
| [3.0.0](https://github.com/wimdeblauwe/htmx-spring-boot-thymeleaf/releases/tag/3.0.0) | 3.1.x       | 17                   |
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
