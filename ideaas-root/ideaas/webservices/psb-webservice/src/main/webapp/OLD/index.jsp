<!DOCTYPE script PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/highcharts-more.js"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
</head>

<body>
<div id="container" style="min-width: 310px; max-width: 600px; height: 400px; margin: 0 auto;"></div>

<script type="text/javascript">
	$.ajax({
			type:"GET",
			url: "./requests/service/getSummarisedData/minutes",
			async: false
		}).done(function(data){
			
			result=JSON.parse(data);
			console.log("success");
	// 		var status=result["status"];
	
	// 		var message=result["message"];
	// 		if(status==="success"){ 
	// 			resultDataSuccess(result, id, datetime);
	// //			console.log(result);
	// 			$(window).resize();
	// 		} else {
	// 			$("#errorAlert").show();
	// 			message = message.replace(/error/g , "<br>"); 
	// 			$("#errorMessage").append("<p><br>"+message+"</p><br>");
	// 		}
			
		});
	
	var data1 = [
        [9, 81, 63],
        [98, 5, 89],
    ];
	var data2 = [
        [42, 38, 20],
        [6, 18, 1]
    ];
	graphGenerator(1, data1, data2)
	function graphGenerator(id, data1, data2){
		var resultRowID="resultRow"+id;
		$('body').append("<div id=\"chart_"+id+"\" class=\"col m6\" style=\"margin: 0 auto\"></div>");

		Highcharts.chart('container', {

		    chart: {
		        type: 'bubble',
		        plotBorderWidth: 1,
		        zoomType: 'xy'
		    },

		    title: {
		        text: 'Highcharts bubbles with radial gradient fill'
		    },

		    xAxis: {
		        gridLineWidth: 1
		    },

		    yAxis: {
		        startOnTick: false,
		        endOnTick: false
		    },

		    series: [{
		        data: data1,
		        marker: {
		            fillColor: {
		                radialGradient: { cx: 0.4, cy: 0.3, r: 0.7 },
		                stops: [
		                    [0, 'rgba(255,255,255,0.5)'],
		                    [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0.5).get('rgba')]
		                ]
		            }
		        }
		    }, {
		        data: data2,
		        marker: {
		            fillColor: {
		                radialGradient: { cx: 0.4, cy: 0.3, r: 0.7 },
		                stops: [
		                    [0, 'rgba(255,255,255,0.5)'],
		                    [1, Highcharts.Color(Highcharts.getOptions().colors[1]).setOpacity(0.5).get('rgba')]
		                ]
		            }
		        }
		    }]

		});

	}
</script>
</body>
</html>
