function sendSolution() {
  var submitData = {taskId: $("#taskId").val(), token: $("#token").val(), sourceCode: $("#sourceCode").val()};

  $.ajax({
    type:"POST",
    contentType: "application/json",
    url: "/submit",
    data: JSON.stringify(submitData),
    dataType: 'html',
    success: function(responseText) {
      $("#errorBlock").empty();
      var html = $.parseHTML(responseText)[0];
      if (html["id"] == "resultlist")
        $("#resultBlock").html(responseText);
      if (html["id"] == "errorlist")
        $("#errorBlock").html(responseText);
    }
  })
}