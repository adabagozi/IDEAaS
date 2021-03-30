var data2 = [];
var keys = ["X", "Y"];

function getDataFromServer(url, isTest){
	
	var dateValues = [	"2016-08-01T05:00:00.000Z",
		"2016-08-01T06:00:00.000Z",
		"2016-08-01T07:00:00.000Z",
		"2016-08-01T08:00:00.000Z",
		"2016-08-01T09:00:00.000Z",
		"2016-08-01T10:00:00.000Z",
		"2016-08-01T11:00:00.000Z",
		"2016-08-01T12:00:00.000Z",
		"2016-08-01T13:00:00.000Z",
		"2016-08-01T14:00:00.000Z",
		"2016-08-01T15:00:00.000Z"
	];
	if(isTest){
		var dateValues = ["2016-08-01T05:00:00.000Z"];
	}

	asyncRequestData(url, dateValues, 0);

}

function asyncRequestData(url, queue, id){
	console.log(queue);
	$("#loaderRequest").show();
	if($("#visualizzaDati").length != 0) {
		$("#visualizzaDati").hide();
	}
	if(queue.length>0){
		setTimeout(function() {
			console.log("queue: " + queue.length);
			var data = queue.shift();
		
			callAjax(url, data, id);	
			asyncRequestData(queue, id+1);
		}, 5000);
	}
}

function callAjax(url, datetimeValue, id){
	console.log("ajax: "+datetimeValue);
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
			select_part_program: 	"0171507370 GR6112", 
			select_mode: 			"1",
			select_macchina: 		"101143",
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
			keys = [element[0]["multi_dim_record"][0]["feature_name"], element[0]["multi_dim_record"][1]["feature_name"]];
			console.log("Result Data");
			// $('#resultData').append(element);
			let newData = buildData(element);
			queue.push({id: id+""+i, newData: newData});
			// plotGraph("graph"+id+""+i, newData, data2, keys);	
			// $('#loader'+id+""+i).hide();
		}

		plotAsync(queue, keys);
}

function plotAsync(queue, keys){

	$("#loaderRequest").hide();
	if(queue.length>0){
		setTimeout(function() {
			console.log(queue.length);
			var data = queue.shift();

			data2 = $.merge(data2, data.newData);
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
								'<div class="col-lg-8 col-md-8 col-sm-12">'+
									'<div class="col-xs-6 loader" id="loader'+id+'"></div>' + 
									'<div id="graph'+id+'" style="min-width: 310px; max-width: 600px; height: 400px; margin: 0 auto;"></div>'+
								'</div><hr></div></div>');
}

