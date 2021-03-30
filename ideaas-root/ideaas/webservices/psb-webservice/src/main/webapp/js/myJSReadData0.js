var data2 = [];
var keys = ["X", "Y"];

function getDataFromServer(url, isTest){

	var dateValues = [	"2016-07-01T00:00:00.000Z",
		"2016-07-01T01:00:00.000Z",
		"2016-07-01T02:00:00.000Z",
		"2016-07-01T03:00:00.000Z",
		"2016-07-01T04:00:00.000Z",
		"2016-07-01T05:00:00.000Z",
		"2016-07-01T06:00:00.000Z",
		"2016-07-01T07:00:00.000Z",
		"2016-07-01T08:00:00.000Z",
		"2016-07-01T09:00:00.000Z",
		"2016-07-01T10:00:00.000Z",
		"2016-07-01T11:00:00.000Z",
		"2016-07-01T12:00:00.000Z",
		"2016-07-01T13:00:00.000Z",
		"2016-07-01T14:00:00.000Z",
		"2016-07-01T15:00:00.000Z",
		"2016-07-01T16:00:00.000Z",
		"2016-07-01T17:00:00.000Z",
		"2016-07-01T18:00:00.000Z",
		"2016-07-01T19:00:00.000Z",
		"2016-07-01T20:00:00.000Z",
		"2016-07-01T21:00:00.000Z",
		"2016-07-01T22:00:00.000Z",
		"2016-07-01T23:00:00.000Z"
	];

	// var dateValues = [	"2016-07-01T00:00:00.000Z",
	// 	"2016-07-02T00:00:00.000Z",
	// 	"2016-07-03T00:00:00.000Z",
	// 	"2016-07-04T00:00:00.000Z",
	// 	"2016-07-05T00:00:00.000Z",
	// 	"2016-07-06T00:00:00.000Z",
	// 	"2016-07-07T00:00:00.000Z",
	// 	"2016-07-08T00:00:00.000Z",
	// 	"2016-07-09T00:00:00.000Z",
	// 	"2016-07-10T00:00:00.000Z"
	// ];


	asyncRequestData(url, dateValues, 0);

}

function asyncRequestData(url, queue, id){
	console.log("queue: " + queue.length);
	$("#loaderRequest").show();
	if($("#visualizzaDati").length != 0) {
		$("#visualizzaDati").hide();
	}
	if(queue.length>0){
		setTimeout(function() {
			var data = queue.shift();

			callAjax(url, data, id);
			asyncRequestData(url, queue, id+1);
		}, 5000);
	}
}

function callAjax(url, datetimeValue, id){
	console.log("ajax: " + datetimeValue);
	var experimentNumber = 0;
	if($("#select_experiment_number").length != 0) {
		experimentNumber = $("#select_experiment_number").val();
	}
	$.ajax({
		type:"POST",
		url: url,
		data: {
			select_features: 		"4",
			select_step: 			"-1",
			select_utensile: 		"-1",
			select_part_program: 	"-1",
			select_mode: 			"-1",
			select_macchina: 		"101170",
			select_componente: 		"Unit 1.0",
			intervalType: 			"hours",
			intervalValue:			1,
			experimentNumber:		experimentNumber,
			datetime: 				datetimeValue
		},
		async: false
	}).done(function(data){
		// $('#resultData').append(data);
		$("#loaderRequest").hide();


		$("#visualizzaDati").show();
		result=JSON.parse(data);
		var status=result["status"];

		var resultData=result["data"];
		if(status==="success"){
			readDataResult(resultData, id, datetimeValue);

		} else {
			console.log("Error: "+data);
		}

	});
}


function readDataResult(resultData, id, datetimeValue){

		var queue = [];
		// $('#resultData').append(resultData[0]);
		for (var i = 0; i < resultData.length; i++) {
			addElementsResultData(id+""+i, datetimeValue);

			var element = resultData[i];
			console.log("Result Data");
			// console.log(element[0]);
			if(element[0] != null && element[0]!='undefined'){
				keys = [element[0]["multi_dim_record"][0]["name"], element[0]["multi_dim_record"][1]["name"]];
				console.log("Result Data");
				// $('#resultData').append(element);
				let newData = buildData(element);
				queue.push({id: id+""+i, newData: newData});
			}
			// plotGraph("graph"+id+""+i, newData, data2, keys);
			// $('#loader'+id+""+i).hide();
		}

		plotAsync(queue, keys);
}

function plotAsync(queue, keys){

	$("#loaderRequest").hide();
	if(queue.length>0){
		setTimeout(function() {
			var data = queue.shift();

				console.log(queue.length+" -> data: "+data);

			// data2 = $.merge(data2, data.newData);
			// data.sintesiAda
			addElementsToInfoData(data.id, data.newData.length);

			plotGraph("graph"+data.id, data.newData, data2, keys);
			$('#loader'+data.id).hide();

			plotAsync(queue, keys);
		}, 2000);
	}else {

	}
}

function buildData(dataValue){
	var data = []
	dataValue.forEach(function(dataJson) {
		var numeroDati = 1;
		var raggio = 1;
		var X = parseFloat(dataJson["multi_dim_record"][0]["value"]);
		var Y = parseFloat(dataJson["multi_dim_record"][1]["value"]);
	//	console.log("X: "+X +" - Y: "+Y);
		// data.push({x: X, y: Y});
		data.push([X, Y]);
	}, this);
	return data;
}


function addElementsToInfoData(id, number_new_data){
	console.log("newData: "+number_new_data);
	$("#resultDataValues"+id).append("Nuovi dati: "+number_new_data+"<br>");
	$("#resultDataValues"+id).append("Totale dati: "+data2.length+"<br>");
}


function addElementsResultData(id, datetime){
	var resultRowID="resultData"+id;

	var resultGraphID="graph"+id;

	$('#resultData').append('<div class="row" style="height: 450px;"><div class="col-xs-12"> <div class="col-lg-4 col-md-4 col-sm-12" id='+ resultRowID +'>'+
								'<div class="col-xs-12" id="resultDataValues'+id+'"><br><br><p><strong>Data: ' + datetime + '</strong></p><br></div>'+'</div>'+
								'<div class="ccol-xs-12">'+
									'<div class="col-xs-6 loader" id="loader'+id+'"></div>' +
									'<div id="graph'+id+'" style="min-width: 310px; margin: 0 auto;"></div>'+
								'</div><hr></div></div>');
}

function plotGraph(id, data1, data2, keys, datetimeValue){
	console.log(keys);
	Highcharts.chart(id, {
		chart: {
        type: 'scatter',
        zoomType: 'xy'
    },
    title: {
        text: 'Dati del giorno '+datetimeValue
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
   plotOptions: {
        scatter: {
            marker: {
                radius: 5,
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
				useHTML: true,
				headerFormat: '<table>',
				pointFormat: '<tr><td>'+keys[0]+': {point.x:.2f}</td></tr>' +
						'<tr><td>'+keys[1]+': {point.y:.2f}</td></tr>',
			footerFormat: '</table>',
			followPointer: false
                // headerFormat: '<b>{series.name}</b><br>',
                // pointFormat: '{point.x} , {point.y} '
            }
        }
    },
    series: [
	// 		{
  //       name: 'Tutti i dati',
  //       color: 'rgba(223, 83, 83, .5)',
  //       data: data2
	//
  //   }
	// ,
		{

        name: 'Misure',
        color: 'rgba(119, 152, 191, .5)',
        data: data1
		}
		]
	});
}
