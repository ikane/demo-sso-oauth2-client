angular.module('app', [ 'ngRoute' ]).config(function($routeProvider, $httpProvider) {

	$routeProvider.when('/', {
		templateUrl : 'home.html',
		controller : 'HomeController',
		controllerAs : 'controller'
	}).otherwise('/');

	$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
	$httpProvider.defaults.headers.common['Accept'] = 'application/json';

}).controller('MainController', MainController)
.controller('HomeController', HomeController);

function HomeController ($http) {
	var self = this;
	$http.get('resource/').then(function(response) {
		self.greeting = response.data;
	})
}

function MainController($rootScope, $scope, $http, $location, $route) {
	
	$scope.logout = function() {
		console.log("post logout to uaa using ajax...");

	   $.ajax({
	        url: "http://localhost:9999/uaa/logout",
	        method: "POST",
	        xhrFields: {withCredentials: true},
	        success: function(data) {
				console.log("post logout to ui-gateway...");

				$http.post('logout', {}).finally(function() {
					console.log("redirect to root...");
							
					$rootScope.authenticated = false;
					$location.path("/");
				});
	        }
    	})		
	}
	
}