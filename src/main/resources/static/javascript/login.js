$(document).ready(function () {
    $("#myForm").on("submit", function (event) {
        event.preventDefault();
        var formData = {};
        $(this).find("input").each(function () {
            formData[this.name] = $(this).val();
        });

        $.ajax({
            type: "POST",
            url: "http://localhost:8080/auth/login",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function (response, status, xhr) {
                const username = document.getElementById('username').value;
                localStorage.setItem('username', username);
                var jwt = xhr.getResponseHeader('Set-Cookie');
                console.log("user logged in, jwt: ", jwt);
                window.location.href = "http://localhost:8080/home";
            },
            error: function (xhr, status, error) {
                console.error("Request failed: ", status, error);
            }
        });
    });
});
