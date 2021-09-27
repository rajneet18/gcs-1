<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>

  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
<div class="jumbotron">
<h1 class="text-secondary">Generate Signed Url</h1>

</br>
<form id="formName">
<input type="text" name="objectName" placeholder="enter object name">
&nbsp;&nbsp;
<input type="button" value="submit" id="submtibtn" class="btn btn-primary">
</form>

<br>

<textarea  id="myInput"  style="margin: 0px; width: 848px; height: 139px;"></textarea> &nbsp;&nbsp;&nbsp;
<button onclick="myFunction()" class="btn btn-success">Copy text</button>
</div>
</div>

<div class="container">

	<div class="row">
																	<div class="col-md-6">
																		<div class="form-group">
																			<label>Select Profile Image</label> <input
																				type="file" id="imgname" name="pictureImg"
																				class="file-upload-default"> <span
																				class="control-label spanCol" style="color: red;"
																				id="imgname-error"></span>
																			
																		</div>
																	</div>
																	
																	<div>
																	<button onclick="uploadData()" class="btn btn-success">Copy text</button>
																	</div>
</div>
</div>
<script>

$("#submtibtn").click(function(){  

	$.ajax({
				type : "POST",
				url : "generateurl",
				data : $("#formName").serialize(),
				
				beforeSend : function() {
					
				},
				success : function(data) {
					$("#myInput").text(data);
				},
				error : function(error) {
					alert("error");
				}
			});


 });
 
 
 function uploadData()
 {
 
 			var form = $('#uploadata')[0];
			var formData = new FormData(form);
			
 $.ajax({
				type : "POST",
				url : "uploaddoc",
				data : formData,
				enctype : 'multipart/form-data',
				contentType : false,
				processData : false,
				cache : false,
				beforeSend : function() {
					
				},
				success : function(data) {
					
				},
				error : function(error) {
					
				}
			});
 
 }
 
 function myFunction() {
  /* Get the text field */
  var copyText = document.getElementById("myInput");

  /* Select the text field */
  copyText.select();
  copyText.setSelectionRange(0, 99999); /* For mobile devices */

  /* Copy the text inside the text field */
  navigator.clipboard.writeText(copyText.value);
  
  /* Alert the copied text */
  alert("Copied the text: " + copyText.value);
}


</script>
</body>
</html>
