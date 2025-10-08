$(document).ready(function () {
  $("#myForm").on("submit", function (event) {
    event.preventDefault();
    let formData = {};
    $(this).find("input").each(function () {
      formData[this.name] = $(this).val();
    });

    $.ajax({
      type: "POST",
      url: "/auth/login",
      contentType: "application/json",
      data: JSON.stringify(formData),
      success: function (response, status, xhr) {
        window.location.href = "/home";
      },
      error: function (xhr, status, error) {
        console.error("Request failed: ", status, error);
      }
    });
  });
});
