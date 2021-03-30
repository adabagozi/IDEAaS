var numData = [];
var valuesTimeExec = [];
var valuesTimeX=[];
var valuesTimeY=[];
var valuesDistancesX=[];
var valuesDistancesY=[];

var dateFormat = "DD/MM/YYYY";


var dateFormatHours = "DD/MM/YYYY HH:mm";

function materializeInit(){
	$('select').material_select();
	$('.datepicker').pickadate({
		selectMonths : true, // Creates a dropdown to control month
		selectYears : 16,
		labelMonthNext: 'Mese Pros',
		labelMonthPrev: 'Mese Prec',
		//The title label to use for the dropdown selectors
		labelMonthSelect: 'Seleziona Mese',
		labelYearSelect: 'Seleziona Anno',
		//Months and weekdays
		monthsFull: [ 'Gennaio', 'Febbraio', 'Marzo', 'Aprile', 'Maggio', 'Giugno', 'Luglio', 'Agosto', 'Settembre', 'Ottobre', 'Novembre', 'Dicembre' ],
		monthsShort: [ 'Gen', 'Feb', 'Mar', 'Apr', 'Mag', 'Giu', 'Lug', 'Ago', 'Set', 'Ott', 'Nov', 'Dic' ],
		weekdaysFull: [ 'Domenica', 'Lunedi', 'Martedi', 'Mercoledi', 'Giovi', 'Venerdi', 'Sabato' ],
		weekdaysShort: [ 'Dom', 'Lun', 'Mar', 'Mer', 'Gio', 'Ven', 'Sab' ],
		//Materialize modified
		weekdaysLetter: [ 'D', 'L', 'M', 'M', 'G', 'V', 'S' ],
		//Today and clear
		today: 'Oggi',
		clear: 'Annulla',
		close: 'Chiudi',
		//The format to show on the `input` element
		format: 'dd/mm/yyyy'
			// Creates a dropdown of 15 years to control year
	});
	var $inputStart = $('#start_date').pickadate()
	// Use the picker object directly.
	var pickerStart = $inputStart.pickadate('picker')
	// Using arrays formatted as [YEAR, MONTH, DATE].
	pickerStart.set('select', [2016, 7, 1])

	var $inputEnd = $('#end_date').pickadate()
	var pickerEnd = $inputEnd.pickadate('picker')
	pickerEnd.set('select', [2016, 7, 2])
	
	$(':input').removeAttr('readonly');
}

function selectChange(options){
	$("#select_macchina").on('change', function() {
		clearSelectionOptions("select_part_program");
		clearSelectionOptions("select_step");
		clearSelectionOptions("select_utensile");

		options["part_programs"][$("#select_macchina").val()].forEach(function(entry) {
			$("#select_part_program").append($(entry));
		});
		
		options["steps"][$("#select_macchina").val()].forEach(function(entry) {
			$("#select_step").append($(entry));
		});
		
		options["utensili"][$("#select_macchina").val()].forEach(function(entry) {
			$("#select_utensile").append($(entry));
		})
		
		$("#select_part_program").material_select();
		$("#select_step").material_select();
		$("#select_utensile").material_select();

	
	});
}

function clearSelectionOptions(id){
	$("#"+id+" option").each(function() {
		$(this).remove();
	});
}


function newResearch(){
	$("#newResearchButton").click(function() {
		$("#form").show();
		$("#spinner").hide();
		$("#newResearch").hide();
		$("#requestData").hide();

		$("#requestData").html("");

		$("#result").hide();
		$("#resultValue").html("");
		$("#errorAlert").hide();
		$("#errorMessage").html("");
		
		$("#num_record").html("");
		$("#temp_clustering").html("");
		$("#temp_extraction").html("");
		$("#temp_distances").html("");
		$("#date_value").html("");
		$("#num_cluster").html("");
		$("#dist_euclidea").html("");
		$("#dist_euclidea_sint").html("");
		
		location.reload();
	});
	
}

function validateForm(){
	var message = "";
	if($("#select_features").val()=="-1"){
		message += "Selezionare Feature Space.<br>";
	}
	
//	if($("#select_macchina").val()=="-1"){
//		message += "Selezionare Macchina.<br>";
//	}
//	if($("#select_componente").val()=="-1"){
//		message += "Selezionare Componente.<br>";
//	}
//	if($("#select_utensile").val()=="-1"){
//		message += "Selezionare Utensile.<br>";
//	}
//	if($("#select_part_program").val()=="-1"){
//		message += "Selezionare Part Program.<br>";
//	}
//	if($("#select_mode").val()=="-1"){
//		message += "Selezionare Mode.<br>";
//	}
//	if($("#select_step").val()=="-1"){
//		message += "Selezionare Step.<br>";
//	}
	
	if(message!=""){
		$("#validationAlert").show();
		$("#validationMessage").html(message);
		window.scrollTo(0,0);
		
		return false;
	}else {

		$("#validationAlert").hide();
		$("#validationMessage").html("");
		return true;
	}
	
}
function scannerDates(){
	$("#calculateClustering").click(function() {
		if(validateForm()){
			$("#requestData").show();
			$("#form").hide();
			$("#spinner").show();
			$("#requestData").append("<div class=\"col m4\"> Feature Space: "+$("#select_features option:selected").text()+"</div>");
			$("#requestData").append("<div class=\"col m4\"> Macchina: "+$("#select_macchina option:selected").text()+"</div>");
			$("#requestData").append("<div class=\"col m4\"> Componente: "+$("#select_componente option:selected").text()+"</div>");
			$("#requestData").append("<div class=\"col m4\"> Utensile: "+$("#select_utensile option:selected").text()+"</div>");
//			$("#requestData").append("<div class=\"col m4\"> Step: "+$("#select_step option:selected").text()+"</div>");
			$("#requestData").append("<div class=\"col m4\"> Part Program: "+$("#select_part_program option:selected").text()+"</div>");
			$("#requestData").append("<div class=\"col m4\"> Mode: "+$("#select_mode option:selected").text()+"</div>");
			window.scrollTo(0,0);
	
			//var startDate =  moment($('#start_date').val() , dateFormat).add(1, 'days').toDate();
			var startDate = convertStringDateToTwixDate($('#start_date').val());
			
			var endDate = convertStringDateToTwixDate($('#end_date').val());
			//var endDate =  moment($('#end_date').val() , dateFormat).add(1, 'days').toDate();
			
			var timeInterval = $("#date_interval").val();
			//var itr = moment.twix(startDate, endDate).iterate(timeInterval);
			//see https://github.com/icambron/twix.js/issues/72
			var itr = startDate.twix(endDate).iterate(timeInterval);
			var i=0;
			console.log(startDate);
			console.log(endDate);
			var dateValues = ["2016-08-01T00:00:00.000Z","2016-08-02T00:00:00.000Z","2016-08-03T00:00:00.000Z","2016-08-04T00:00:00.000Z",
					"2016-08-05T00:00:00.000Z", "2016-08-06T00:00:00.000Z", "2016-08-07T00:00:00.000Z"];
//			for (let datetime of dateValues) {
//				console.log(datetime);
//
//				//var iterVal = itr.next();
//				ajaxCall(null, i, datetime);
//			}
			while(itr.hasNext()){
				var iterVal = itr.next();
				
				//console.log(i+"qerty1 " + iterVal.utc().format());
				console.log("timestamp " + iterVal.valueOf());
				//ajaxCall(iterVal, i);
				ajaxCall(iterVal, i);
				
				i+=1;
			}
			$("#spinner").hide();
			$("#newResearch").show();

		}
	});
}

/**
 * Converte una stringa rappresentante una data in una data che può essere letta da twix
 * 
 * @param date una stringa rappresentante una data seguente il pattern "dd/mm/yyyy"
 * @returns un oggetto data che può essere iniettato in moment.twix
 */
function convertStringDateToTwixDate(dateStr) {
	//TODO rimpiazza il pattern con una costante
	//return moment(dateStr + " +0000", "DD/MM/YYYY Z").utc().toDate();
	//return moment(dateStr, "DD/MM/YYYY");
	//see https://momentjs.com/docs/#hour-minute-second-millisecond-and-offset-tokens
	return moment(dateStr+ " 00:00 +0000", "DD/MM/YYYY HH:mm Z").utc();
}

function ajaxCall(iterVal, id){
	//var date = iterVal.utc();

	var extractDataValue = $("#extractData:checked").val()?true:false;
	//18
	//if((date.toDate().getHours()>7 && date.toDate().getHours()<18) || $("#date_interval").val()=="days"){
		
		console.log("ajaxCall: " + extractDataValue);
		$.ajax({
			type:"POST",
			url: "./requests/service/readData",
//			url: "./requests/service/readData",
			data: {
				select_features: 		"4",
				select_step: 			"-1",
				select_utensile: 		"-1",
				select_part_program: 	"0171507370 GR6112", 
				select_mode: 			"1",
				select_macchina: 		"101143",
				select_componente: 		"Unit 1.0",
				intervalType: 			"hours",
				intervalValue:			1,
				datetime: 				iterVal.valueOf()
			},
			async: false
		}).done(function(data){
			$("#result").show();
	
			result=JSON.parse(data);
			var status=result["status"];
	
			var message=result["message"];
			if(status==="success"){ 
				resultDataSuccess(result, id, datetime);
	//			console.log(result);
				$(window).resize();
			} else {
				$("#errorAlert").show();
				message = message.replace(/error/g , "<br>"); 
				$("#errorMessage").append("<p><br>"+message+"</p><br>");
			}
			
		});
	//}
}

function resultDataSuccess(result, id, date){
	var resultRowID="resultRow"+id;
	var resultDataID="resultData"+id;

	//var dateITA = date.format(dateFormatHours);
	$("#newResearch").show();
	$("#resultValue").append("<h4 class=\"row center\">Dati del "+date+"</h4>")
	$("#resultValue").append("<div id=\""+resultRowID+"\" class=\"row\"><div id=\""+resultDataID+"\" class=\"col m6\"></div></div>")
	$("#"+resultDataID).append("<p> Stato: "+result["status"]+"</p>");

	var message = result['message'];

	if(message!=""){
		message = message.replace(/(?:\r\n|\r|\n)/g, '<br />');
		$("#"+resultDataID).append("<p> Messaggio: "+message+"</p>");
	}

	$("#"+resultDataID).append("<p id=\"numData"+id+"\"> Nuovi dati: "+ result["new_data_number"]+"</p>");

	$("#"+resultDataID).append("<p> Tempo Estrazione (ms): "+ result["time_extraction"]+"</p>");
	$("#"+resultDataID).append("<p> Tempo Clustering (ms): "+ result["time_total"]+"</p>");
	$("#"+resultDataID).append("<p> Distanza Cluster rispetto al caso Stabile: "+ result["cluster_distance"]["total_distance"]+"</p>");
	$("#"+resultDataID).append("<p> Tempo Calcolo Distanze: "+ result["cluster_distance"]["time_distance"]+"</p>");
	$("#"+resultDataID).append("<p> Numero Centroidi: "+ result["centroids_synthesis"]["centroids"]["centroids_number"]+"</p>");
	$("#"+resultDataID).append("<p> Numero Sintesi: "+ result["centroids_synthesis"]["synthesis"]["synthesis_number"]+"</p>");

	var centroidsValue = result["centroids_synthesis"]["centroids"]["centroidsValues"];

	var arrayCentroids = [];
	var keys = [];
	centroidsValue.forEach(function(entry) {
		var value = [];
		for (var key in entry) {
			if (!(key in arrayCentroids)){
				keys.push(key);
			}
			value.push(entry[key]);
		}
		arrayCentroids.push(value);
	});

	var synthesisValue = result["centroids_synthesis"]["synthesis"]["synthesisValues"];
	var arraySynthesis = [];
	synthesisValue.forEach(function(entry) {
		var value = [];

		for (var key in entry) {
			value.push(entry[key]);
		}
		arraySynthesis.push(value);
	});

	var oldValues = 0;
	if(valuesTimeX.length > 0){
		oldValues = valuesTimeX[valuesTimeX.length - 1] 
	}

	var numData = parseInt(result["new_data_number"]) + oldValues;

	console.log("Old Dati: "+oldValues);
	valuesTimeX.push(numData);
	console.log("Num Dati: "+numData);

	//updateGraphTime(result["new_data_number"], result["time_total"], result["time_extraction"], dateITA, result["cluster_distance"]["total_distance"]);
	updateGraphTime(numData, result["time_total"], result["time_extraction"], result["cluster_distance"]["time_distance"],  date, result["cluster_distance"]["total_distance"],  result["cluster_distance"]["diff_cluster_numbers"],  result["cluster_distance"]["centroid_best_match"],  result["cluster_distance"]["synthesis_best_match"]);
	graphGenerator(id, keys, arrayCentroids, arraySynthesis, date);
}


function graphGenerator(id, keys, centroids, synthesis, dateITA){
	console.log(centroids);
	console.log(synthesis);
	console.log(keys);
	console.log(dateITA);
	var resultRowID="resultRow"+id;
	$("#"+resultRowID).append("<div id=\"chart_"+id+"\" class=\"col m6\" style=\"margin: 0 auto\"></div>");
	Highcharts.chart('chart_'+id, {
		chart: {
			type: 'scatter',
		},
		title: {
			text: 'Centroidi e Sinstesi'
		},
		subtitle: {
			text: dateITA
		},
		xAxis: {
			title: {
				enabled: true,
				text: keys[0]
			},
			startOnTick: true,
			endOnTick: true,
			showLastLabel: true
		},
		yAxis: {
			title: {
				text: keys[1]
			}
		},
		legend: {
			layout: 'vertical',
			align: 'left',
			verticalAlign: 'top',
			x: 10,
			y: 1,
			floating: true,
			backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF',
			borderWidth: 1
		},
		plotOptions: {
			scatter: {
				marker: {
					radius: 3,
					states: {
						hover: {
							enabled: true,
							lineColor: 'rgb(100,100,100)'
						}
					}
				},
				states: {
					hover: {
						marker: {
							enabled: false
						}
					}
				},
				tooltip: {
					headerFormat: '<b>{series.name}</b><br>',
					pointFormat: '{point.x}, {point.y}'
				}
			}
		},
		series: [{
			name: 'Sintesi',
			color: 'rgba(0,0,255,0.5)',
			data: synthesis

		}, {
			name: 'Centroids',
			color: 'rgb(255,0,0)',
			data: centroids
		}]
	});
}

var timeCanvas = $('#chart_time');

var distanceCanvas = $('#chart_distances');

var distanceNumCanvas = $('#chart_diff_cluster_numbers');

var distanceCentroidCanvas = $('#chart_centroid_best_match');

var distanceSynthesisCanvas = $('#chart_synthesis_best_match');


var data = {
		labels: [],
		datasets: [
			{
				label: "Tempo Clustering",
				fill: false,
				lineTension: 0.1,
				backgroundColor: "rgba(255,0,0,0.4)",
				borderColor: "rgba(255,0,0,1)",
				borderCapStyle: 'butt',
				borderDash: [],
				borderDashOffset: 0.0,
				borderJoinStyle: 'miter',
				pointBorderColor: "rgba(255,0,0,1)",
				pointBackgroundColor: "rgba(255,0,0,1)",
				pointBorderWidth: 1,
				pointHoverRadius: 5,
				pointHoverBackgroundColor: "rgba(255,0,0,1)",
				pointHoverBorderColor: "rgba(220,220,220,1)",
				pointHoverBorderWidth: 2,
				pointRadius: 5,
				pointHitRadius: 10,
				data: [],
			}, {
				label: "Tempo Estrazione",
				fill: false,
				lineTension: 0.1,
				backgroundColor: "rgba(0,255,0,0.4)",
				borderColor: "rgba(0,255,0,1)",
				borderCapStyle: 'butt',
				borderDash: [],
				borderDashOffset: 0.0,
				borderJoinStyle: 'miter',
				pointBorderColor: "rgba(0,255,0,1)",
				pointBackgroundColor: "rgba(0,255,0,1)",
				pointBorderWidth: 1,
				pointHoverRadius: 5,
				pointHoverBackgroundColor: "rgba(0,255,0,1)",
				pointHoverBorderColor: "rgba(220,220,220,1)",
				pointHoverBorderWidth: 2,
				pointRadius: 5,
				pointHitRadius: 10,
				data: [],
			},{
				label: "Tempo Calcolo Distanze",
				fill: false,
				lineTension: 0.1,
				backgroundColor: "rgba(0,0,255,0.4)",
				borderColor: "rgba(0,0,255,1)",
				borderCapStyle: 'butt',
				borderDash: [],
				borderDashOffset: 0.0,
				borderJoinStyle: 'miter',
				pointBorderColor: "rgba(0,0,255,1)",
				pointBackgroundColor: "rgba(0,0,255,1)",
				pointBorderWidth: 1,
				pointHoverRadius: 5,
				pointHoverBackgroundColor: "rgba(0,0,255,1)",
				pointHoverBorderColor: "rgba(220,220,220,1)",
				pointHoverBorderWidth: 2,
				pointRadius: 5,
				pointHitRadius: 10,
				data: [],
			}
			]
};
var dataDistance = {
		labels: [],
		datasets: [{
			label: "Distanza Cluster",
			fill: false,
			lineTension: 0.1,
			backgroundColor: "rgba(75,192,192,0.4)",
			borderColor: "rgba(75,192,192,1)",
			borderCapStyle: 'butt',
			borderDash: [],
			borderDashOffset: 0.0,
			borderJoinStyle: 'miter',
			pointBorderColor: "rgba(75,192,192,1)",
			pointBackgroundColor: "rgba(75,192,192,1)",
			pointBorderWidth: 1,
			pointHoverRadius: 5,
			pointHoverBackgroundColor: "rgba(75,192,192,1)",
			pointHoverBorderColor: "rgba(220,220,220,1)",
			pointHoverBorderWidth: 2,
			pointRadius: 5,
			pointHitRadius: 10,
			data: [],
		}]
};
var dataDistanceNum = {
		labels: [],
		datasets: [{
			label: "Distanza numero cluster",
			fill: false,
			lineTension: 0.1,
			backgroundColor: "rgba(75,192,192,0.4)",
			borderColor: "rgba(75,192,192,1)",
			borderCapStyle: 'butt',
			borderDash: [],
			borderDashOffset: 0.0,
			borderJoinStyle: 'miter',
			pointBorderColor: "rgba(75,192,192,1)",
			pointBackgroundColor: "rgba(75,192,192,1)",
			pointBorderWidth: 1,
			pointHoverRadius: 5,
			pointHoverBackgroundColor: "rgba(75,192,192,1)",
			pointHoverBorderColor: "rgba(220,220,220,1)",
			pointHoverBorderWidth: 2,
			pointRadius: 5,
			pointHitRadius: 10,
			data: [],
		}]
};
var dataDistanceCentroids = {
		labels: [],
		datasets: [{
			label: "Distanza euclidea centroidi cluster",
			fill: false,
			lineTension: 0.1,
			backgroundColor: "rgba(75,192,192,0.4)",
			borderColor: "rgba(75,192,192,1)",
			borderCapStyle: 'butt',
			borderDash: [],
			borderDashOffset: 0.0,
			borderJoinStyle: 'miter',
			pointBorderColor: "rgba(75,192,192,1)",
			pointBackgroundColor: "rgba(75,192,192,1)",
			pointBorderWidth: 1,
			pointHoverRadius: 5,
			pointHoverBackgroundColor: "rgba(75,192,192,1)",
			pointHoverBorderColor: "rgba(220,220,220,1)",
			pointHoverBorderWidth: 2,
			pointRadius: 5,
			pointHitRadius: 10,
			data: [],
		}]
};
var dataDistanceCentroidSynthesi = {
		labels: [],
		datasets: [{
			label: "Distanza euclidea centroidi sintesi",
			fill: false,
			lineTension: 0.1,
			backgroundColor: "rgba(75,192,192,0.4)",
			borderColor: "rgba(75,192,192,1)",
			borderCapStyle: 'butt',
			borderDash: [],
			borderDashOffset: 0.0,
			borderJoinStyle: 'miter',
			pointBorderColor: "rgba(75,192,192,1)",
			pointBackgroundColor: "rgba(75,192,192,1)",
			pointBorderWidth: 1,
			pointHoverRadius: 5,
			pointHoverBackgroundColor: "rgba(75,192,192,1)",
			pointHoverBorderColor: "rgba(220,220,220,1)",
			pointHoverBorderWidth: 2,
			pointRadius: 5,
			pointHitRadius: 10,
			data: [],
		}]
};

var optionTime = {
		showLines: true,
		responsive: true,
		title:{
			display: true,
			text:'Tempi di estrazione dati, calcolo set di cluster e calcolo delle distanze'
		},
		scales: {
			yAxes: [{
				scaleLabel: {
					display: true,
					labelString: 'Tempo (ms)'
				}
			}],
			xAxes: [{
				scaleLabel: {
					display: true,
					labelString: 'Numero recod'
				}
			}]
		}
};
var optionDistance = {
		showLines: true,
		responsive: true,
		title:{
			display: true,
			text:'Distanze rispetto al primo valore sull\'asse X'
		},
		scales: {
			yAxes: [{
				scaleLabel: {
					display: true,
					labelString: 'Distanza'
				}
			}],
			xAxes: [{
				scaleLabel: {
					display: true,
					labelString: 'Data Clustering'
				}
			}]
		}
};
var optionDistanceNum = {
		showLines: true,
		responsive: true,
		title:{
			display: true,
			text:'Distanze rispetto al primo valore sull\'asse X'
		},
		scales: {
			yAxes: [{
				scaleLabel: {
					display: true,
					labelString: 'Distanza numero clusters'
				}
			}],
			xAxes: [{
				scaleLabel: {
					display: true,
					labelString: 'Data e ora Clustering'
				}
			}]
		}
};

var optionDistanceCentroids = {
		showLines: true,
		responsive: true,
		title:{
			display: true,
			text:'Distanze rispetto al primo valore sull\'asse X'
		},
		scales: {
			yAxes: [{
				scaleLabel: {
					display: true,
					labelString: 'Distanza eucliedea centroidi clusters'
				}
			}],
			xAxes: [{
				scaleLabel: {
					display: true,
					labelString: 'Data e ora clustering'
				}
			}]
		}
};

var optionDistanceCentroidSynthesis = {
		showLines: true,
		responsive: true,
		title:{
			display: true,
			text:'Distanze rispetto al primo valore sull\'asse X'
		},
		scales: {
			yAxes: [{
				scaleLabel: {
					display: true,
					labelString: 'Distanza eucliedea centroidi sintesi'
				}
			}],
			xAxes: [{
				scaleLabel: {
					display: true,
					labelString: 'Data e ora clustering'
				}
			}]
		}
};
var timeChart = Chart.Line(timeCanvas,{
	data:data,
	options:optionTime
});

var distanceChart = Chart.Line(distanceCanvas,{
	data:dataDistance,
	options:optionDistance
});

var distanceNumChart = Chart.Line(distanceNumCanvas,{
	data:dataDistanceNum,
	options:optionDistanceNum
});

var distanceCentroidChart = Chart.Line(distanceCentroidCanvas,{
	data:dataDistanceCentroids,
	options:optionDistanceCentroids
});
var distanceSynthesisChart = Chart.Line(distanceSynthesisCanvas,{
	data:dataDistanceCentroidSynthesi,
	options:optionDistanceCentroidSynthesis
});




//updateGraphTime(numData, result["time_total"], result["time_extraction"], dateITA, result["cluster_distance"]["total_distance"],  result["cluster_distance"]["diff_cluster_numbers"],  result["cluster_distance"]["centroid_best_match"],  result["cluster_distance"]["synthesis_best_match"]);

function updateGraphTime(newValueTimeX, newValueTimeY, newValueTimeExec, newValueTimeDistances, newValueDistancesX, newValueDistancesY, newValueDistanceNum, newValueDistanceCentroid, newValueDistanceSynhesis){
	
//	$("#num_record").html($("#num_record").val()+"<br>"+newValueTimeX);
//	$("#temp_clustering").html($("#temp_clustering").val()+"<br>"+newValueTimeY);
//	$("#temp_extraction").html($("#temp_extraction").val()+"<br>"+newValueTimeExec);
//	$("#temp_distances").html($("#temp_distances").val()+"<br>"+newValueTimeDistances);
//	$("#date_value").html($("#date_value").val()+"<br>"+newValueDistancesX);
//	$("#num_cluster").html($("#num_cluster").val()+"<br>"+newValueDistanceNum);
//	$("#dist_euclidea").html($("#dist_euclidea").val()+"<br>"+newValueDistanceCentroid);
//	$("#dist_euclidea_sint").html($("#dist_euclidea_sint").val()+"<br>"+newValueDistanceSynhesis);

	$("#num_record").append("<br>"+newValueTimeX);
	$("#temp_clustering").append("<br>"+newValueTimeY);
	$("#temp_extraction").append("<br>"+newValueTimeExec);
	$("#temp_distances").append("<br>"+newValueTimeDistances);
	$("#date_value").append("<br>"+newValueDistancesX);
	$("#num_cluster").append("<br>"+newValueDistanceNum);
	$("#dist_euclidea").append("<br>"+newValueDistanceCentroid);
	$("#dist_euclidea_sint").append("<br>"+newValueDistanceSynhesis);
	
	
	
	var index = timeChart.data.labels.length 

	timeChart.data.datasets[0].data[index] = newValueTimeY;
	timeChart.data.datasets[1].data[index] = newValueTimeExec;
	timeChart.data.datasets[2].data[index] = newValueTimeDistances;
	timeChart.data.labels[index] = newValueTimeX;
	timeChart.update();
	var indexDistance = distanceChart.data.labels.length 

	
	
	distanceChart.data.datasets[0].data[indexDistance] = newValueDistancesY;
	distanceChart.data.labels[indexDistance] = newValueDistancesX;
	distanceChart.update();

//	var indexDistanceNum = distanceNumChart.data.labels.length 
//
//	distanceNumChart.data.datasets[0].data[indexDistanceNum] = newValueDistanceNum;
//	distanceNumChart.data.labels[indexDistanceNum] = newValueDistancesX;
//	distanceNumChart.update();
//	
//	var indexDistanceCentroid = distanceCentroidChart.data.labels.length 
//
//	distanceCentroidChart.data.datasets[0].data[indexDistanceCentroid] = newValueDistanceCentroid;
//	distanceCentroidChart.data.labels[indexDistanceCentroid] = newValueDistancesX;
//	distanceCentroidChart.update();
//	
	var indexDistanceSynthesisCentroid = distanceSynthesisChart.data.labels.length 

	distanceSynthesisChart.data.datasets[0].data[indexDistanceSynthesisCentroid] = newValueDistanceSynhesis;
	distanceSynthesisChart.data.labels[indexDistanceSynthesisCentroid] = newValueDistancesX;
	distanceSynthesisChart.update();

}
