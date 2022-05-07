[![Discord](https://img.shields.io/discord/725789699527933952)](https://htmx.org/discord)

## Spring Boot integration with HTMX and Thymeleaf
[HTMX](www.htmx.org) does a great job of making declarative HTML interactive. Thymeleaf is
good fit since it also focuses on HTML. This project adds processors and other helpers to
make using both a pleasure.

## Installation

### Warning: this artifact has not yet been published to maven central! 

```xml
<dependency>
  <groupId>io.github.wimdeblauwe</groupId>
  <artifactId>htmx-spring-boot-thymeleaf</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Configuration
The included Spring Boot autoconfiguration will enable the HTMX integrations. 

## Request Headers

Methods can be annotated with `@HxRequest` to be selected when an HTMX-based request (ie `hx-get`) is made. 

```java
@GetMapping("/users")
@HxRequest                  // Called when hx-get request to '/users/' is made 
public String htmxRequest(HtmxRequest details) {
    service.doSomething(details);

    return "partial";
}

@GetMapping("/users")        // Only called on a full page refresh, not an HTMX request
public String normalRequest(HtmxRequest details) {
    service.doSomething(details);

    return "users";
}
```

These annotations allow for composition if you wish to combine them, so you could 
combine annotations to make a custom `@HxGetMapping`.

### Using `HtmxRequest` to inspect HTML request headers

The `HtmxRequest` object can be injected into controller methods to check the various HTMX request headers.

```java
@GetMapping
@ResponseBody
public String htmxRequestDetails(HtmxRequest htmxReq) { // HtmxRequest is injected
    if(htmxReq.isHistoryRestoreRequest()) {
        // ...    
    }

    return "";
}
```

## Response Headers

Setting the `hx-trigger` header triggers an event when the response is swapped in by HTMX.
The `@HxTrigger` annotation supports doing that for you:

```java
@GetMapping("/users")
@HxRequest
@HxTrigger("userUpdated") // 'userUpdated' event will be triggered by HTMX
public String hxUpdateUser() {
    return "users";
}
```

## Processors

HTMX processors also are provided to allow ThymeLeaf to be able to perform calculations and expressions
in those fields. Note the `:` colon instead of the typical hyphen. 

* `hx:get`: Thymeleaf processing
* `hx-get`: static values


```html
<div hx:get="@{/users/{id}(id=$userId}" hx-target="#otherElement">Load user details</div>
```

Which will be rendered as:

```html
<div hx-get="/users/123" hx-target="#otherElement">Load user details</div>
```
