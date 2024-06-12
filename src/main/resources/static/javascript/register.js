
$(document).ready(function () {
    $("#myForm").on("submit", function (event) {
        event.preventDefault();
        var formData = {};
        $(this).find("input").each(function () {
            formData[this.name] = $(this).val();
        });

        $.ajax({
            type: "POST",
            url: "http://localhost:8080/users",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function (response, status, xhr) {
                console.log("user registered");
                window.location.href = "http://localhost:8080/login";
            },
            error: function (xhr, status, error) {
                console.error("failed to register a user", status, error);
            }
        });
    });
});
