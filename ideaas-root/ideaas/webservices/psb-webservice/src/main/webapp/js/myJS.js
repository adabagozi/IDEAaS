function getDataFromServer(){
	var timeType = $("#timeType").val();
	timeType = "hours";
	// var dateValues = [	"2016-08-01T00:00:00.000Z",
	// 		"2016-08-02T00:00:00.000Z",
	// 		"2016-08-03T00:00:00.000Z",
	// 		"2016-08-04T00:00:00.000Z",
	// 		"2016-08-05T00:00:00.000Z",
	// 		"2016-08-06T00:00:00.000Z",
	// 		"2016-08-07T00:00:00.000Z",
	// 		"2016-08-08T00:00:00.000Z"
	// 	];
	// if(timeType=="hours"){
	// 	dateValues = [	"2016-08-01T05:00:00.000Z",
	// 		"2016-08-01T06:00:00.000Z",
	// 		"2016-08-01T07:00:00.000Z",
	// 		"2016-08-01T08:00:00.000Z",
	// 		"2016-08-01T09:00:00.000Z",
	// 		"2016-08-01T10:00:00.000Z",
	// 		"2016-08-01T11:00:00.000Z",
	// 		"2016-08-01T12:00:00.000Z",
	// 		"2016-08-01T13:00:00.000Z",
	// 		"2016-08-01T14:00:00.000Z",
	// 		"2016-08-01T15:00:00.000Z"
	// 	];
	// }else if (timeType == "minutes"){
	// 	dateValues = ["2016-08-01T05:30:00.000Z",
	// 		"2016-08-01T06:00:00.000Z",
	// 		"2016-08-01T06:30:00.000Z",
	// 		"2016-08-01T07:00:00.000Z",
	// 		"2016-08-01T07:30:00.000Z",
	// 		"2016-08-01T08:00:00.000Z",
	// 		"2016-08-01T08:30:00.000Z",
	// 		"2016-08-01T09:00:00.000Z",
	// 		"2016-08-01T09:30:00.000Z",
	// 		"2016-08-01T10:00:00.000Z",
	// 		"2016-08-01T10:30:00.000Z",
	// 		"2016-08-01T11:00:00.000Z",
	// 		"2016-08-01T11:30:00.000Z",
	// 		"2016-08-01T12:00:00.000Z",
	// 		"2016-08-01T12:30:00.000Z",
	// 		"2016-08-01T13:00:00.000Z",
	// 		"2016-08-01T13:30:00.000Z",
	// 		"2016-08-01T14:00:00.000Z",
	// 		"2016-08-01T14:30:00.000Z",
	// 		"2016-08-01T15:00:00.000Z",
	// 		"2016-08-01T15:30:00.000Z"
	// 	];
	// }

}


function ajaxCall(){

	$("#loaderRequest").show();
	$("#visualizzaSintesi").hide();
	var featureSpace = $("#select_feature_space").val();

	var part_program = $("#select_part_program").val();

	var mode = $("#select_mode").val();

	var macchina = $("#select_macchina").val();

	var componente = $("#select_componente").val();

	var intervalType = $("#select_interval_type").val();

	var intervalValue = $("#select_interval_value").val();

	var experimentNumber = $("#select_experiment_number").val();
	$.ajax({
		type:"POST",
		url: "./requests/service/getSummarisedData",
		data: {
			select_features: 		featureSpace,
			select_utensile: 		"0",
			select_part_program: 	part_program,
			select_mode: 			mode,
			select_macchina: 		macchina,
			select_componente: 		componente,
			intervalType: 			intervalType,
			intervalValue:			intervalValue,
			experimentNumber:		experimentNumber,
			datetime: 				"2016-08-01T00:00:00.000Z"
		},
		async: false
	}).done(function(data){

		$("#loaderRequest").hide();

		// $('#resultData').append("<p>"+data+"</p>");
		result=JSON.parse(data);
		if(result.length!=""){
			readDataResult(result);
		}else {
			$('#resultData').append('<div class="alert alert-warning"><strong>Nessuna Sintesi</strong></div>');
		}
	});
}

function readDataResult(result){
	console.log("result: "+result['ideaas_synthesis']);
	var ideaasSynthesis = result['ideaas_synthesis'];

	var keys = ["X", "Y"];


	if(ideaasSynthesis.length){
		keys = [ideaasSynthesis[0]["synthesis"][0]["synthesi_element"][0]["feature_name"], ideaasSynthesis[0]["synthesis"][0]["synthesi_element"][1]["feature_name"]];
	}
	var queue = [];
	for (var i = 0; i < ideaasSynthesis.length; i++) {

		var sintesiIdeaas = ideaasSynthesis[i];
		// var sintesiCioce = cioceSynthesis[i];

		addElementsResultData(i, sintesiIdeaas["timestamp_initial"]);


		addElementsToInfoSintesi("resultDataIdeaas"+i, sintesiIdeaas["number_new_data"], sintesiIdeaas["total_synthesis"], sintesiIdeaas["synthesis_discarted"], sintesiIdeaas["old_synthesis"], sintesiIdeaas["number_data"], keys);
		// addElementsToInfoSintesi("resultDataCioce"+i, sintesiCioce["number_new_data"], sintesiCioce["total_synthesis"], sintesiCioce["synthesis_discarted"], sintesiCioce["old_synthesis"], sintesiCioce["number_data"], keys);
		queue.push({sintesiIdeaas: buildData(sintesiIdeaas["synthesis"]), clusterIdeaas: buildDataCluster(sintesiIdeaas["clusters"])});
	}
	plotAsync(queue, 0, keys);

}

function plotAsync(queue, id, keys){
	if(queue.length>0){
		setTimeout(function() {
			console.log(queue.length);
			var data = queue.shift();
			// data.sintesiIdeaas
			plotGraph("graph"+id, data.sintesiIdeaas, data.clusterIdeaas, keys);
			$('#loader'+id).hide();

			plotAsync(queue, id+1, keys);
		}, 2000);
	}
}

function buildDataCluster(clusters){
	var data = []
	clusters.forEach(function(cluster) {
		// $('#resultData').append("<p> " + cluster + "</p>");
		var numeroDati = 0;
		var raggio = 100;
		// if(cluster["radius"] != "NaN"){
		// 		raggio = parseFloat(sintesi["radius"]);
		// }
		var SS = 0;
		var X = parseFloat(cluster["centroid"][0]["value"]);
		var Y = parseFloat(cluster["centroid"][1]["value"]);
		// data.push([X, Y, raggio]);
		data.push({x: X, y: Y, z: raggio, numero: numeroDati, ss: SS});
	}, this);
	return data;
}

function buildData(sintesis){
	var data = []
	sintesis.forEach(function(sintesi) {
		var numeroDati = parseFloat(sintesi["num_data"]);
		var raggio = 0;
		if(sintesi["radius"] != "NaN"){
			raggio = parseFloat(sintesi["radius"]);
		}
		var SS = parseFloat(sintesi["getSS_synthesi"]);
		var X = parseFloat(sintesi["synthesi_element"][0]["center"]);
		var Y = parseFloat(sintesi["synthesi_element"][1]["center"]);
		// data.push([X, Y, raggio]);
		data.push({x: X, y: Y, z: raggio, numero: numeroDati, ss: SS});
	}, this);
	return data;
}


function addElementsToInfoSintesi(id, number_new_data, total_synthesis, synthesis_discarted, old_synthesis, number_data, key){
	$("#"+id).append("Feature Space: "+ key[0] + "-" + key[1] + "<br>");
	$("#"+id).append("Totale dati: "+number_data+"<br>");
	$("#"+id).append("Nuovi dati: "+number_new_data+"<br>");
	$("#"+id).append("Sintesi Output: "+(total_synthesis-synthesis_discarted)+"<br>");
	$("#"+id).append("Sintesi totali: "+total_synthesis+"<br>");
	$("#"+id).append("Sintesi scartati: "+synthesis_discarted+"<br>");
	$("#"+id).append("Vecchie sintesi: "+old_synthesis+"<br>");

}


function addElementsResultData(id, dateIdeaas){
	var resultRowID="resultData"+id;

	var resultRowIdeaasID="resultDataIdeaas"+id;
	var resultRowCioceID="resultDataCioce"+id;
	var resultGraphID="graph"+id;

	$('#resultData').append('<div class="row" style="height: 450px;"><div class="col-xs-12"> <div class="col-lg-4 col-md-5 col-sm-12" id='+ resultRowID +'>'+
								'<div class="col-xs-12" id='+ resultRowIdeaasID +'><p><strong>Sintesi Nuove: ' + dateIdeaas + '</strong></p><br></div></div>'+
								'<div class="col-lg-8 col-md-7 col-sm-12">'+
									'<div class="col-xs-6 loader" id="loader'+id+'"></div>' +
									'<div id="graph'+id+'" style="min-width: 310px; max-width: 600px; height: 400px; margin: 0 auto;"></div>'+
								'</div><hr></div></div>');
}

function plotGraph(id, data1, clusters1, keys){
	Highcharts.chart(id, {
		chart : {
			type : 'bubble',
			plotBorderWidth : 1,
			zoomType : 'xy'
		},
		title : {
			text : 'Confronto sintesi - giorno 01/08/2016'
		},
		tooltip: {
			useHTML: true,
			headerFormat: '<table>',
			pointFormat: '<tr><td>'+keys[0]+': {point.x:.2f}</td></tr>' +
						'<tr><td>'+keys[1]+': {point.y:.2f}</td></tr>' +
				'<tr><td>Raggio: {point.z:.2f}</td></tr>' +
				'<tr><td>Numero Dati: {point.numero}</td></tr>' +
				'<tr><td>SS: {point.ss:.2f}</td></tr>',
			footerFormat: '</table>',
			followPointer: false
		},

//TODO (remove max e min sugli assi)
		xAxis : {
			min: -11,
			max: 11,
			gridLineWidth : 1,
			title: {
				text: keys[0]
			}
		},
		yAxis : {
			min: -11,
			max: 11,
			startOnTick : false,
			endOnTick : false,
			title: {
				text: keys[1]
			}
		},
		series : [{
			name: 'Sintesi CluStream',
			data : data1,
			marker : {
				fillColor : {
					radialGradient : {
						cx : 0.4,
						cy : 0.3,
						r : 0.7
					},
					stops : [
							[ 0, 'rgba(255,255,255,0.5)' ],
							[1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0.5).get('rgba')]
						]
				}
			}
		},
		{
			name: 'Centroidi X-Means',
			data : clusters1,
			marker : {
				fillColor : {
					radialGradient : {
						cx : 0.4,
						cy : 0.3,
						r : 0.7
					},
					stops : [
						[0, 'rgba(255,255,255,0.5)'],
						[1, Highcharts.Color(Highcharts.getOptions().colors[1]).setOpacity(0.5).get('rgba') ]
					]
				}
			}
		}]
	});
}
