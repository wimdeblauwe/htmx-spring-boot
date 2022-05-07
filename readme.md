[![Discord](https://img.shields.io/discord/725789699527933952)](https://htmx.org/discord)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wimdeblauwe/htmx-spring-boot-thymeleaf/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.wimdeblauwe/htmx-spring-boot-thymeleaf)

# Spring Boot integration with HTMX and Thymeleaf

[HTMX](www.htmx.org) does a great job of making declarative HTML interactive. Thymeleaf is good fit since it also
focuses on HTML. This project adds processors and other helpers to make using both a pleasure.

## Installation

The library is available
on [Maven Central](https://mvnrepository.com/artifact/io.github.wimdeblauwe/htmx-spring-boot-thymeleaf), so it is easy
to add the dependency to your project.

```xml

<dependency>
  <groupId>io.github.wimdeblauwe</groupId>
    <artifactId>htmx-spring-boot-thymeleaf</artifactId>
    <version>LATEST_VERSION_HERE</version>
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

# Release

To release a new version of the project, follow these steps:

1. Update `pom.xml` with the new version and commit
2. Tag the commit with the version (e.g. `1.0.0`) and push the tag.
3. Create a new release in GitHub via https://github.com/wimdeblauwe/htmx-spring-boot-thymeleaf/releases/new
    - Select the newly pushed tag
    - Update the release notes. This should automatically start
      the [release action](https://github.com/wimdeblauwe/htmx-spring-boot-thymeleaf/actions).
4. Update `pom.xml` again with the next `SNAPSHOT` version.
