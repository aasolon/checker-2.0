function sendSolution() {
  var submitData = {taskId: $("#taskId").val(), token: $("#token").val(), sourceCode: $("#sourceCode").val()};
  // $("#resultBlock").load("/submit", data, function() {
  //   $("#sourceCode").val("");
  // });

  $.ajax({
    type:"POST",
    contentType: "application/json",
    url: "/submit",
    data: JSON.stringify(submitData),
    dataType: 'html',
    success: function(responseText) {
      $("#resultBlock").html(responseText);
    }
  })
}