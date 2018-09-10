
/**
 * @ngdoc function
 * @name minovateApp.controller:PagesLoginCtrl
 * @description
 * # PagesLoginCtrl
 * Controller of the minovateApp
 */

app
  .controller('LoginCtrl', function ($scope, $state, $http, $window, $rootScope, $stateParams) {
	  if($state.current.name === 'admin.login') {
		  $scope.page = {
			      title: 'Alma Admin'
			    };
	  }
	  else if($state.current.name === 'parent.login'){
		  $scope.page = {
			      title: 'Parent Login'
			    };
	  }
	  else {
		  $scope.page = {
			      title: 'School Login'
			    };
		 
	  }
	
	  $scope.getSchoolFromUrl = function() {
			$scope.school = [];
			
			$http({
				method : "GET",
				url : $rootScope.baseUrl + 'alma/getSchoolFromUrl/'+$stateParams['schoolName']
			}).success(function(data, status, headers, config) {
				$scope.school = data;
			}).error(function(data, status, headers, config) {
				$window.alert("Something went wrong");
			})
		};
    if($stateParams['schoolName'])
    	$scope.getSchoolFromUrl();
	
    $scope.login = function(role) {
    	var username = this.user.email;
    	var password = this.user.password;
    	
    	if(role===''){
	    	if($state.current.name === 'admin.login') {
	    		role = 'Admin';
	    	}else if($state.current.name === 'parent.login') {
	    		role = 'Parent';
	    	}else if($state.current.name === 'school.login') {
	    		role = 'SchoolAdmin';
	    	}
    	}

         //Try to log in to account
         $http({method: "POST", url: $rootScope.baseUrl+'admin/validateLogin',
             data: {'username': username, 'password': password, 'role': role, 'schoolUrl':$stateParams['schoolName']}
         }).success(function(data, status, headers, config) {
        	 if(data.userId) {
        		 if(data.roleName === "Alma" && role==="Admin"){
            		 $window.localStorage.setItem("currentUser",JSON.stringify(data));
        			 $state.go('administrator.dashboard');
        		 }
        		 else if(data.roleName === "SchoolAdmin" && role==="SchoolAdmin"){
            		 $window.localStorage.setItem("currentUser",JSON.stringify(data));
        			 var val = {};
        			 val['schoolUrl']=data.schoolUrl;
        			 $state.go('schoolUrl.schoolarea.dashboard', val);
        		 }
        		 else if(data.roleName === "Parent" && role==="Parent"){
            		 $window.localStorage.setItem("currentUser",JSON.stringify(data));
        			 var val = {};
        			 val['schoolUrl']=$stateParams['schoolName'];
        			 if($rootScope.previousState){
        				 if($rootScope.previousParams.articleName){
        					 val['articleName'] = $rootScope.previousParams.articleName;
        				 }
        				 else if($rootScope.previousParams.recipeName){
        					 val['recipeName'] = $rootScope.previousParams.recipeName;
        				 }
        				 $state.go($rootScope.previousState, val);
        			 }
        			 else
        			 $state.go('schoolUrl.parentarea.dashboard',val);
        		 }
        		 else
        			 $window.alert("Wrong username/password. Please try again");
        	 }
        	 else {
        		 $window.alert("Wrong username/password. Please try again");
        	 }
             }).
             error(function(data, status, headers, config) {
                 $window.alert("Something went wrong");
             });
    };
  })
  .controller('HeaderCtrl', function ($scope, $state, $http, $window, $rootScope, $stateParams) {
	  var currentUser = $window.localStorage.getItem('currentUser');
  	$scope.userObj = $.parseJSON(currentUser);
  	$scope.wait = function(ms){
   	   var start = new Date().getTime();
   	   var end = start;
   	   while(end < start + ms) {
   	     end = new Date().getTime();
   	  }
   	};
	  $scope.logout= function() { 
		  $window.localStorage.setItem("logout","performed");
		  if($scope.userObj){
			  $http({method: "POST", url: $rootScope.baseUrl+'admin/invalidateSession/'+$scope.userObj.userId
		         }).success(function(data, status, headers, config) {
		        	 $window.localStorage.removeItem("currentUser");
		        	 $scope.loggedIn = false;
			        	$scope.url = '';
		        	 $state.go('app.home');
		        	 }).
		             error(function(data, status, headers, config) {
		                 $window.alert("Something went wrong");
		             });
		  }else{
			  $scope.loggedIn = false;
	        	$scope.url = '';
		  }
		  
	  }
 })