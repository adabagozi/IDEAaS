// js/app.js
var app = angular.module('clusteringDistances');

app.config(function () {
	
//	$interpolateProvider.startSymbol('{[{').endSymbol('}]}');
//	
//    $translateProvider.useStaticFilesLoader({
//	    prefix: 'languages/locale-',
//	    suffix: '.json'
//    });
//	var userLang = navigator.language || navigator.userLanguage; 
//	var languageDefaultKey="en";
//	if(userLang == "it"){
//		languageDefaultKey = userLang;
//	}
//
//    $translateProvider.preferredLanguage(languageDefaultKey);
//   	$translateProvider.fallbackLanguage(languageDefaultKey);
//   	//$translateProvider.useCookieStorage();
//    //$translateProvider.useSanitizeValueStrategy(null);
//        
//    $translateProvider.useLocalStorage();
//
//    
//	changeFlagLanguage(localStorage.getItem('NG_TRANSLATE_LANG_KEY'), languageDefaultKey);
	
    
});



//app.controller('myController', function ($scope, $translate) {
////	$scope.changeLanguage = function (key) {
////	  $translate.use(key);
////	  changeFlagLanguage(key)
////	};
//
//    
//});

// create angular controller
app.controller('formController', function($scope, $http) {
	// function to submit the form after all validation has occurred		

	$scope.formData = {};	
	$scope.submitForm = function(isValid) {
		// $scope.user;
		//console.log("Dati form: "+$scope.formData);
		$scope.showDanger=false;
		$scope.showInfo=false;
		if (isValid) { 
			$scope.loading = true;
			$http({
		        method  : 'POST',
		        url     : 'http://sibylsystem.microsi.it:8080/bsb/requests/service/clusteringTest', //TODO modificare il link... dovrebbe essere relativo.
		        data    : $.param($scope.formData),  // pass in data as strings
		        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  // set the headers so angular passing info as form data (not request payload)
		    })
	        .success(function(data) {
	            //console.log(data);
	            if (!data.success) {
	            	//console.log(data.message);
	            	$scope.showDanger=true;
	            } else {
	            	// if successful, bind success message to message
	                //console.log(data.message)
	                $scope.showInfo=true;
		            // $scope.formData.subject = "";
	             //    $scope.formData.message = "";

	            }
	        })    
	        .error(function(error){
			    //console.log(error)
			    $scope.showDanger=true;
			})
	        .catch(function (err) {
				// Log error somehow.
				$scope.showDanger=true;
			})
			.finally(function () {
				// Hide loading spinner whether our call succeeded or failed.
				$scope.loading = false;
		    });
		}else {
			console.log("Dati non validi");
		}
	};

});
