
/**
 * @ngdoc function
 * @name minovateApp.controller:DashboardCtrl
 * @description # DashboardCtrl Controller of the minovateApp
 */
app
	.controller(
		'HomeCtrl',
		function($scope, $http, $window, $rootScope) {
			$scope.page = {
				title : 'Home page',
				subtitle : 'Place subtitle here...'
			};
			
			$scope.getRecentRecipeList = function() {
				
				$scope.recRecipeList = [];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'alma/getRecentRecipeList'
				}).success(function(data, status, headers, config) {
					$scope.recRecipeList = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			$scope.getRecentRecipeList();
			
			$scope.getRecentArticleList = function() {
				
				$scope.recArticleList = [];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'alma/getRecentArticleList'
				}).success(function(data, status, headers, config) {
					$scope.recArticleList = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			$scope.getRecentArticleList();
		})
		
	.directive('whenReady', ['$timeout', function ($timeout) {
    return {
        link: function ($scope, element, attrs) {
            $scope.$on('dataloaded', function () {
                $timeout(function () { // You might need this timeout to be sure its run after DOM render.
                  console.log("in directive");
				  MINOVATE.documentOnReady.init();
				//MINOVATE.documentOnLoad.init();
                }, 0, false);
            })
        }
    };
}])
		// About us
    .controller(
        'GuestHeaderCtrl',
        function($scope,$window,$http,$rootScope,$state) {
            $scope.page = {
                title: 'About us',
                subtitle: 'Everything about us'
            };
            $scope.loggedIn = false;
            $scope.url = '';
        	var logout = $window.localStorage.getItem('logout');
        	if(logout){
        		$window.localStorage.removeItem("logout");
        	}
        	else{
                var currentUser = $window.localStorage.getItem('currentUser');
            	currentUser = $.parseJSON(currentUser);
	        	if(currentUser){
	        		$scope.loggedIn = true;
	        		if(currentUser.roleName === "Alma")
	        			$scope.url = 'administrator.dashboard';
	          		 else if(currentUser.roleName === "SchoolAdmin")
	          			$scope.url = "schoolUrl.schoolarea.dashboard({schoolUrl:'".concat(currentUser.schoolUrl).concat("'})");
	          		 else if(currentUser.roleName === "Parent")
	          			$scope.url = "schoolUrl.parentarea.dashboard({schoolUrl:'".concat(currentUser.schoolUrl).concat("'})");
	        	}
        	}
        	$scope.logout= function() { 
        		var currentUser = $window.localStorage.getItem('currentUser');
            	currentUser = $.parseJSON(currentUser);
            	if(currentUser){
      		  $http({method: "POST", url: $rootScope.baseUrl+'admin/invalidateSession/'+currentUser.userId
      	         }).success(function(data, status, headers, config) {
      	        	 $window.localStorage.removeItem("currentUser");
      	        	$scope.loggedIn = false;
      	        	$scope.url = '';
      	        	 $state.go('app.home');
      	        	 }).
      	             error(function(data, status, headers, config) {
      	                 $window.alert("Something went wrong");
      	             });
            	}
            	else{
            		$scope.loggedIn = false;
      	        	$scope.url = '';
            	}
      		  
      	  }
           
        })        
	// About us
    .controller(
        'AboutUsCtrl',
        function($scope) {
            $scope.page = {
                title: 'About us',
                subtitle: 'Everything about us'
            }
        })        
        
        // Our Team
    .controller(
        'OurTeamCtrl',
        function($scope) {
            $scope.page = {
                title: 'Our Team',
                subtitle: 'We are here'
            }
        })
        
        // Services
    .controller(
        'ServicesCtrl',
        function($scope) {
            $scope.page = {
                title: 'Services',
                subtitle: 'What we offer to you'
            }
        })
        
        // Contact us
    .controller(
        'ContactUsCtrl',
        function($scope,$http,$window,$rootScope) {
            $scope.page = {
                title: 'Contact us',
                subtitle: 'Stay in Touch with Us!'
            }
            
            $scope.addRecord = function() {
                 //Try to log in to account
                 $http({method: "POST", url: $rootScope.baseUrl+'alma/addContactUsRecord',
                     data: $scope.contactUs
                 }).success(function(data, status, headers, config) {
                	 $scope.contactUs = {
                			 name: '',
                			 email:'',
                			 phone:'',
                			 subject:'',
                			 message:''
                	 }
                	 $window.alert("Thankyou for your interest in AlmaNourisher. We will get back to you shortly");
                 }).
                     error(function(data, status, headers, config) {
                         $window.alert("Something went wrong");
                     });
            };
        })
        
           // Faq
    .controller(
        'FaqCtrl',
        function($scope) {
            $scope.page = {
                title: 'Frequently Asked Questions',
                subtitle: 'Ask your queries'
            }
        })
		
		       // Disclaimer
    .controller(
        'DisclaimerCtrl',
        function($scope) {
            $scope.page = {
                title: 'Disclaimer',
                subtitle: 'Ask your queries'
            }
        })
		
		       // Privacy Policy
    .controller(
        'PrivacyPolicyCtrl',
        function($scope) {
            $scope.page = {
                title: 'Privacy Policy',
                subtitle: 'Ask your queries'
            }
        })
		
		       // Terms & Conditions
    .controller(
        'TermsConditionsCtrl',
        function($scope) {
            $scope.page = {
                title: 'Terms & Conditions',
                subtitle: 'Ask your queries'
            }
        })
		
			       // Web Login
    .controller(
        'WebLoginCtrl',
        function($scope) {
            $scope.page = {
                title: 'Login',
                subtitle: 'Access Your Account'
            }
        })
		
			       // Article 1
    .controller(
        'Article1Ctrl',
        function($scope) {
            $scope.page = {
                title: 'Article',
                subtitle: 'Read more about this article'
            }
        })
		
			       // Article 2
    .controller(
        'Article2Ctrl',
        function($scope) {
            $scope.page = {
                title: 'Article',
                subtitle: 'Read more about this article'
            }
        })
		
			       // Article 3
    .controller(
        'Article3Ctrl',
        function($scope) {
            $scope.page = {
                title: 'Article',
                subtitle: 'Read more this article'
            }
        })
		
			       // Article 4
    .controller(
        'Article4Ctrl',
        function($scope) {
            $scope.page = {
                title: 'Article',
                subtitle: 'Read more this article'
            }
        })

		
		
			       // Recipe 1
    .controller(
        'Recipe1Ctrl',
        function($scope) {
            $scope.page = {
                title: 'Recipe',
                subtitle: 'Read more this recipe'
            }
        })
		
			       // Recipe 2
    .controller(
        'Recipe2Ctrl',
        function($scope) {
            $scope.page = {
                title: 'Recipe',
                subtitle: 'Read more this recipe'
            }
        })
		
			       // Recipe 3
    .controller(
        'Recipe3Ctrl',
        function($scope) {
            $scope.page = {
                title: 'Recipe',
                subtitle: 'Read more this recipe'
            }
        })
		
			       // Recipe 4
    .controller(
        'Recipe4Ctrl',
        function($scope) {
            $scope.page = {
                title: 'Recipe',
                subtitle: 'Read more this recipe'
            }
        })
        
        // list Article starts
		.controller('guestListArticleCtrl', function($scope,$http,$window,$rootScope,$stateParams) {
			$scope.page = {
				title : 'Article Listing',
				subtitle : 'Place subtitle here...'
			};
			
			$scope.getArticleList = function() {
				
				$scope.articleList = [];
				var tag = $stateParams["tag"];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'alma/getArticleList/'+tag
				}).success(function(data, status, headers, config) {
					console.log(data);
					$scope.articleList = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			$scope.getArticleList();
			
		})
		
		// list Recipe starts
		.controller('guestListRecipeCtrl', function($scope,$http,$window,$rootScope,$stateParams,$location) {
			$scope.page = {
				title : 'Recipe Listing',
				subtitle : 'Place subtitle here...'
			};
			
			$scope.getRecipeList = function() {
				
				$scope.recipeList = [];
				var tag = $stateParams["tag"];
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'alma/getRecipeList/'+tag
				}).success(function(data, status, headers, config) {
					$scope.recipeList = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			$scope.getRecipeList();
			
		})
		// View Article details starts
		.controller('guestViewArticleDetailCtrl', function($rootScope, $scope, $stateParams, $http, $window,$location) {

			$scope.page = {
					title : 'View Article Detail',
					subtitle : 'Place subtitle here...'
				}
			var heading = $stateParams["articleName"];
			 $scope.article = [];
			 $scope.absurl = $location.absUrl();
			$http({
				method : "POST",
				url : $rootScope.baseUrl + 'alma/getArticleByName/'+heading
			}).success(function(data, status, headers, config) {
				 $scope.article = data;
				 $scope.getComments();
			}).error(function(data, status, headers, config) {
				$window.alert("Something went wrong");
			});
			
			$scope.getRecentRecipeList = function() {
				
				$scope.recRecipeList = [];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'alma/getRecentRecipeList'
				}).success(function(data, status, headers, config) {
					$scope.recRecipeList = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			$scope.getRecentRecipeList();
			
			$scope.getRecentArticleList = function() {
				
				$scope.recArticleList = [];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'alma/getRecentArticleList'
				}).success(function(data, status, headers, config) {
					$scope.recArticleList = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			$scope.getRecentArticleList();
			
			$scope.getTopTags = function() {
				
				$scope.topTags = [];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'alma/getCommonTags'
				}).success(function(data, status, headers, config) {
					$scope.topTags = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			$scope.getTopTags();
			
			$scope.getComments = function(){
				$scope.comments = [];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'alma/getArticleComments/'+$scope.article.id
				}).success(function(data, status, headers, config) {
					$scope.comments = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
		})
		// View Recipe details starts
		.controller('guestViewRecipeDetailCtrl', function($rootScope, $scope, $stateParams, $http, $window,$location) {
			var heading = $stateParams["recipeName"];
			$scope.page = {
					title : 'View Recipe Detail',
					subtitle : 'Place subtitle here...'
				};
			$scope.absurl = $location.absUrl();
			$http({
				method : "POST",
				url : $rootScope.baseUrl + 'alma/getRecipeByName/'+heading
			}).success(function(data, status, headers, config) {
				 $scope.recipe= data;
				 $scope.getComments();
			}).error(function(data, status, headers, config) {
				$window.alert("Something went wrong");
			});
			
			$scope.getRecentRecipeList = function() {
				
				$scope.recRecipeList = [];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'alma/getRecentRecipeList'
				}).success(function(data, status, headers, config) {
					$scope.recRecipeList = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			$scope.getRecentRecipeList();
			
			$scope.getRecentArticleList = function() {
				
				$scope.recArticleList = [];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'alma/getRecentArticleList'
				}).success(function(data, status, headers, config) {
					$scope.recArticleList = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			$scope.getRecentArticleList();
			
			$scope.getTopTags = function() {
				
				$scope.topTags = [];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'alma/getCommonTags'
				}).success(function(data, status, headers, config) {
					$scope.topTags = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			$scope.getTopTags();
			
			$scope.getComments = function(){
				$scope.comments = [];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'alma/getRecipeComments/'+$scope.article.id
				}).success(function(data, status, headers, config) {
					$scope.comments = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
		});
