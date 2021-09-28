<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container">
		<div class="jumbotron">
			<h1 class="text-secondary">Generate Signed Url</h1>

			</br>
			<form id="formName">


				<select id="getbucket" name="bucketname" onchange="loadValues()">
					<option value="">---- select your bucket ----</option>
				</select> &nbsp;&nbsp; <select id="objectValues" name="objectName">
					<option value="">---- select your object ----</option>
				</select> &nbsp;&nbsp; <input type="button" value="submit" id="submtibtn"
					class="btn btn-primary">
			</form>

			<br>

			<textarea id="myInput"
				style="margin: 0px; width: 848px; height: 139px;"></textarea>
			&nbsp;&nbsp;&nbsp;
			<button onclick="myFunction()" class="btn btn-success">Copy
				text</button>
		</div>
	</div>
	<br />
	<br />
	<div class="container">
		<div class="jumbotron">
			<h1 class="text-secondary">Upload file with Signed URL</h1>

			</br>
			<form id="uploadata">
				<div class="row">
					<div class="col-md-6">

						<select id="getbucketforupload" name="bucketname">
							<option value="">---- select your bucket ----</option>
						</select>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label>Select File</label> <input type="file" id="imgname"
								name="files" class="file-upload-default"> <span
								class="control-label spanCol" style="color: red;"
								id="imgname-error"></span>

						</div>
					</div>

					<div>
						<input type="button" onclick="uploadData()"
							class="btn btn-primary" value="upload" />
					</div>
				</div>
			</form>
		</div>
	</div>
	<script>
		$("#submtibtn").click(function() {

			var valueOfobj = $("#objectValues").val();
			if (valueOfobj == "") {
				alert("Error! Please Select An Object");
				$("#myInput").val("");
				return false;
			}

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

		function uploadData() {

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
					alert("errro");
				}
			});

			location.reload();
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

		function loadValues() {

			var values = "<option value=''>---- select your object ----</option>";

			$.ajax({
				type : "GET",
				url : "getobject",
				data : $("#formName").serialize(),
				beforeSend : function() {

				},
				success : function(data) {
					if (data != "" && data.length > 0) {
						for (var i = 0; i < data.length; i++) {

							values = values + "<option value='"+data[i]+"'>"
									+ data[i] + "</option>";
						}
					}
					$("#objectValues").html(values);
				},
				error : function(error) {

				}
			});
		}

		$(document)
				.ready(
						function() {
							var values = "<option value=''>---- select your bucket ----</option>";

							$
									.ajax({
										type : "GET",
										url : "getbucket",
										data : $("#formName").serialize(),
										beforeSend : function() {

										},
										success : function(data) {
											if (data != "" && data.length > 0) {
												for (var i = 0; i < data.length; i++) {

													values = values
															+ "<option value='"+data[i]+"'>"
															+ data[i]
															+ "</option>";
												}
											}
											$("#getbucket").html(values);
											$("#getbucketforupload").html(
													values);
										},
										error : function(error) {

										}
									});

						});
	</script>
</body>
</html>
