<html>
	<head>
		<title>Misure</title>
		<meta charset="utf-8">
		<!-- <link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"> -->
		<!-- <link rel="stylesheet" type="text/css" href="css/bootstrap.sandstone.min.css"> -->
		<link rel="stylesheet" type="text/css" href="css/bootstrap.cerulean.min.css">
		<link rel="stylesheet" type="text/css" href="css/myStyle.css">

		<script src="js/jquery.min.js"></script>

		<!--<script src="js/jsonToJS.js"></script>
		<script src="js/jsonHoursToJS.js"></script>-->

	</head>
	<body>
		<nav class="navbar navbar-inverse">
			<div class="container-fluid">
				<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-2">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#"><img alt=""
					src="./img/logo.png"></a>
				</div>

				<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-2">
					<ul class="nav navbar-nav">
						<li class="active"><a href="./index.html">Visualizza cluster <span class="sr-only">(current)</span></a></li>
						<!--<li class="active"><a href="#">Visualizza Cluster <span class="sr-only">(current)</span></a></li>#}
						{#<li><a href="#">Link</a></li>#}-->
						<li class="dropdown">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Visualizza dati <span class="caret"></span></a>
							<ul class="dropdown-menu" role="menu">
								<li><a href="./readDataPointsGenerated.html">Dati generati random</a></li>
								<li class="divider"></li>
								<li><a href="./readMeasures.html">Dati reali</a></li>
							</ul>
						</li>
					</ul>
					<!--<form class="navbar-form navbar-left" role="search">
						<div class="form-group">
						<input class="form-control" placeholder="Search" type="text">
						</div>
						<button type="submit" class="btn btn-default">Submit</button>
					</form>-->
					<ul class="nav navbar-nav navbar-right">
						<li><a href="./runSummarisation.html">Generete new clusters</a></li>
					</ul>
				</div>
			</div>
		</nav>

		<div class="container">

			<div class="page-header" id="banner">
				<div class="row">
					<div class="col-xs-12">
						<h1>Visualizza dati reali</h1>
						<p class="lead">Nei seguenti grafici sono plottati i dati nuovi (in azzurro) e tutti i dati (cumulativi) delle (in grigio).</p>
						<p>
							mode = 1 <br>
							idMachine = 101170 <br>
							idSpindle = Unit 1.0 <br>
							<!-- idPartProgram = 0171507370 GR6112 <br> -->
							featureSpaceID = Corrente - Velocit&agrave; asse Z <br>
						</p>
					</div>

				</div>
			</div>

			<div id="resultData">

			</div>

		</div>


		<script src="js/bootstrap.min.js"></script>
   		<script src="js/highcharts.js"></script>
		<script src="js/highcharts-more.js"></script>
		<script src="js/highcharts_exporting.js"></script>
		<script src="js/myJSReadData.js"></script>
		<!---->
		<script>
//			getDataFromServer();
			var day = "<?php echo $_GET['day']; ?>";
			$( document ).ready(function() {
				getDataFromServer("http://localhost:8080/ideaas/requests/service/readData", false, day);
			});
		</script>
	</body>
</html>
