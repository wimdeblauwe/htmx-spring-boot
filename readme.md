[![Discord](https://img.shields.io/discord/725789699527933952)](https://htmx.org/discord)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wimdeblauwe/htmx-spring-boot-thymeleaf/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.wimdeblauwe/htmx-spring-boot-thymeleaf)

# Spring Boot and Thymeleaf library for htmx

This library provides helper classes and a [Thymeleaf](https://www.thymeleaf.org/) dialect to make it easy to work
with [htmx](www.htmx.org) in a [Spring Boot](https://spring.io/projects/spring-boot) application.

More information about htmx can be viewed on [their website](www.htmx.org).

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

## Usage

### Configuration

The included Spring Boot autoconfiguration will enable the htmx integrations.

### Request Headers

_See [Request Headers Reference](https://htmx.org/reference/#request_headers) for the related htmx documentation._

Methods can be annotated with `@HxRequest` to be selected when an htmx-based request (ie `hx-get`) is made.

```java
@GetMapping("/users")
@HxRequest                  // Called when hx-get request to '/users/' is made 
public String htmxRequest(HtmxRequest details){
        service.doSomething(details);

        return"partial";
        }

@GetMapping("/users")        // Only called on a full page refresh, not an htmx request
public String normalRequest(HtmxRequest details){
    service.doSomething(details);

    return "users";
}
```

These annotations allow for composition if you wish to combine them, so you could 
combine annotations to make a custom `@HxGetMapping`.

#### Using `HtmxRequest` to inspect HTML request headers

The `HtmxRequest` object can be injected into controller methods to check the various htmx request headers.

```java
@GetMapping
@ResponseBody
public String htmxRequestDetails(HtmxRequest htmxReq) { // HtmxRequest is injected
        if(htmxReq.isHistoryRestoreRequest()){
        // ...    
        }

        return"";
        }
```

### Response Headers

_See [Response Headers Reference](https://htmx.org/reference/#response_headers) for the related htmx documentation._

Setting the `hx-trigger` header triggers an event when the response is swapped in by htmx. The `@HxTrigger` annotation
supports doing that for you:

```java
@GetMapping("/users")
@HxRequest
@HxTrigger("userUpdated") // 'userUpdated' event will be triggered by htmx
public String hxUpdateUser(){
        return"users";
        }
```

### Processors

_See [Attribute Reference](https://htmx.org/reference/#attributes) for the related htmx documentation._

Thymeleaf processors are provided to allow Thymeleaf to be able to perform calculations and expressions in htmx-related
attributes. Note the `:` colon instead of the typical hyphen.

* `hx:get`: This is a Thymeleaf processing enabled attribute
* `hx-get`: This is just a static attribute if you don't need the Thymeleaf processing

For example, this Thymeleaf template:

```html

<div hx:get="@{/users/{id}(id=$userId}" hx-target="#otherElement">Load user details</div>
```

Will be rendered as:

```html

<div hx-get="/users/123" hx-target="#otherElement">Load user details</div>
```

The included Thymeleaf dialect has corresponding processors for most of the `hx-*` attributes.
Please [open an issue](https://github.com/wimdeblauwe/htmx-spring-boot-thymeleaf/issues) if something is missing.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[Apache 2.0](https://choosealicense.com/licenses/apache-2.0/)

## Release

To release a new version of the project, follow these steps:

1. Update `pom.xml` with the new version and commit
2. Tag the commit with the version (e.g. `1.0.0`) and push the tag.
3. Create a new release in GitHub via https://github.com/wimdeblauwe/htmx-spring-boot-thymeleaf/releases/new
    - Select the newly pushed tag
    - Update the release notes. This should automatically start
      the [release action](https://github.com/wimdeblauwe/htmx-spring-boot-thymeleaf/actions).
4. Update `pom.xml` again with the next `SNAPSHOT` version.
5. Close the milestone in the GitHub issue tracker.
