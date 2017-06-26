function sendSolution() {
  var data = {taskId: $("#taskId").val(), token: $("#token").val(), sourceCode: $("#sourceCode").val()};
  $("#resultBlock").load("/submit", data, function() {
    $("#sourceCode").val("");
  });
}