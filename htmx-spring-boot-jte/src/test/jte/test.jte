@import io.github.wimdeblauwe.htmx.spring.boot.jte.EndpointController.UserForm
@param java.util.Optional<UserForm> userForm
@param io.github.wimdeblauwe.htmx.spring.boot.jte.EndpointController endpointController
<script src="https://unpkg.com/htmx.org@1.9.12"></script>

<form ${endpointController.createUserEndpoint.call()}>
    <label>
        username
        <input type="text" name="userName">
    </label>
    <label>
        password
        <input type="text" name="password">
    </label>
    <button type="submit">
        Save
    </button>
</form>

@if( userForm.isPresent())
    <div>
        Hello ${userForm.get().userName()}! Your password is: ${userForm.get().password()}
    </div>
@endif
