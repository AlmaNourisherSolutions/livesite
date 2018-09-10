
/**
 * @ngdoc overview
 * @name minovateApp
 * @description
 * # minovateApp
 *
 * Main module of the application.
 */

  /*jshint -W079 */

var app = angular
  .module('minovateApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngTouch',
    'ngMessages',
    'picardy.fontawesome',
    'ui.bootstrap',
    'ui.router',
    'ui.utils',
    'angular-loading-bar',
    'angular-momentjs',
    'FBAngular',
    'lazyModel',
    'toastr',
    'angularBootstrapNavTree',
    'oc.lazyLoad',
    'ui.select',
    'ui.tree',
    'textAngular',
    'colorpicker.module',
    'angularFileUpload',
    'ngImgCrop',
    'datatables',
    'datatables.bootstrap',
    'datatables.colreorder',
    'datatables.colvis',
    'datatables.tabletools',
    'datatables.scroller',
    'datatables.columnfilter',
    'ui.grid',
    'ui.grid.resizeColumns',
    'ui.grid.edit',
    'ui.grid.moveColumns',
    'ngTable',
    'smart-table',
    'angular-flot',
    'angular-rickshaw',
    'easypiechart',
    'uiGmapgoogle-maps',
    'ui.calendar',
    'ngTagsInput',
    'pascalprecht.translate',
    'ngMaterial',
    'localytics.directives',
    'leaflet-directive',
    'wu.masonry',
    'ipsum',
    'angular-intro',
    'dragularModule',
    '720kb.datepicker',
    'angularCSS',
    'ng.ckeditor',
    '720kb.socialshare'
  ])
  .factory('authProvider', function() {
    var user;
      return {
        setUser : function(aUser){
          user = aUser;
        },
        isLoggedIn : function(){
          return(user)? user : false;
        }
      };
  })
  .run(['$rootScope', '$state', '$stateParams','$window','$location','$moment', function($rootScope, $state, $stateParams, $window,$location, $moment) {
    $rootScope.$state = $state;
    $rootScope.$stateParams = $stateParams;
   //$rootScope.baseUrl = "http://198.1.106.132:8080/AlmaNourisher/rest/";
   $rootScope.baseUrl = "http://localhost:8080/AlmaNourisher/rest/";
   $rootScope.today = $moment().format('MMMM D, YYYY');
    $rootScope.$on('$stateChangeSuccess', function(event, toState) {

      event.targetScope.$watch('$viewContentLoaded', function () {

        angular.element('html, body, #content').animate({ scrollTop: 0 }, 200);

        setTimeout(function () {
          angular.element('#wrap').css('visibility','visible');

          if (!angular.element('.dropdown').hasClass('open')) {
            angular.element('.dropdown').find('>ul').slideUp();
          }
        }, 200);
      });
      $rootScope.containerClass = toState.containerClass;
    });

    $rootScope.$on('$stateChangeStart', function (event, toState, toParams) {

    	var requireLogin = toState.data.requireLogin;
    	$rootScope.errormessage = "";
    	$rootScope.successmessage = "";
    	
    	var currentUser = $window.localStorage.getItem('currentUser');
    	currentUser = $.parseJSON(currentUser);
    	if((toState.name == 'admin.login'||toState.name == 'parent.login'||toState.name == 'school.login') && currentUser){
    		console.log('Logged in');
            event.preventDefault();
            $('#pageloader').toggleClass('hide animate');
            if(currentUser.roleName === "Alma")
   			 $state.go('administrator.dashboard');
   		 else if(currentUser.roleName === "SchoolAdmin"){
   			 var val = {};
   			 val['schoolUrl'] = currentUser.url;
   			 $state.go('schoolUrl.schoolarea.dashboard', val);
   		 }
   		 else if(currentUser.roleName === "Parent"){
   			 var val = {};
   			 val['schoolUrl'] = currentUser.url;
   			 $state.go('schoolUrl.parentarea.dashboard',val);
   		 }
   		 else{
   			$window.localStorage.removeItem('currentUser');
   			$state.go('admin.login');
   		 }
    	}
    	else if (requireLogin && $window.localStorage.getItem('currentUser') === null) {
          console.log('DENY : Redirecting to Login');
          event.preventDefault();
          $('#pageloader').toggleClass('hide animate');
          if(toState.name.indexOf("parent") >= 0 || toState.name.indexOf("school") >= 0){
        	  var schoolUrl = toParams.schoolUrl;
        	  var url =  $location.$$absUrl.replace( $location.$$url,'');
        	  $rootScope.previousState = toState.name;
        	  $rootScope.previousParams = toParams;
        	  $window.location.href = url.concat('/').concat(schoolUrl).concat('/login');
          }
          else if(toState.name.indexOf("admin") >= 0){
              $state.go('admin.login');
          }
          else{
              $state.go('home');
          }
        }
        else {
          console.log('ALLOW');
        }
  }); 

  }])

  .config(['uiSelectConfig', function (uiSelectConfig) {
    uiSelectConfig.theme = 'bootstrap';
  }])

  //angular-language
  .config(['$translateProvider', function($translateProvider) {
    $translateProvider.useStaticFilesLoader({
      prefix: 'languages/',
      suffix: '.json'
    });
    $translateProvider.useLocalStorage();
    $translateProvider.preferredLanguage('en');
    $translateProvider.useSanitizeValueStrategy(null);
  }])

  .config(['$stateProvider', '$urlRouterProvider','$locationProvider', function($stateProvider, $urlRouterProvider,$locationProvider) {

	
    $urlRouterProvider.otherwise('/'); 

    $stateProvider
    //main page
    // .state('app', {
    // abstract: true,
    // url: '/app',
    // template: '<div ui-view></div>',
        // data: {
            // requireLogin: false // this property will apply to all children of 'admin'
          // }// parent dashboard url
  // })
  
   .state('app', {
      abstract: true,
      url: '',
      templateUrl: 'views/guest/app.html',
      data: {
          requireLogin: false 
        }
    })
  
  // guest homepage
    .state('app.home', {
      url: '/',
      controller: 'HomeCtrl',
      templateUrl: 'views/guest/homepage.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/js/vendor/animsition/css/animsition.min.css',
            'assets/css/style.css']
    })
    
     //Guest about us 
    .state('app.aboutus', {
      url: '/about-us',
      controller: 'AboutUsCtrl',
      templateUrl: 'views/guest/about-us.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
    
     //Guest our team 
    .state('app.ourteam', {
      url: '/our-team',
      controller: 'OurTeamCtrl',
      templateUrl: 'views/guest/our-team.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
    
     //Guest services 
    .state('app.services', {
      url: '/services',
      controller: 'ServicesCtrl',
      templateUrl: 'views/guest/services.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
    
     //Guest contact-us 
    .state('app.contactus', {
      url: '/contact-us',
      controller: 'ContactUsCtrl',
      templateUrl: 'views/guest/contact-us.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
    
    //Guest FAQ 
    .state('app.faq', {
      url: '/faq',
      controller: 'FaqCtrl',
      templateUrl: 'views/guest/faq.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
	
	//Guest Disclaimer 
    .state('app.disclaimer', {
      url: '/disclaimer',
      controller: 'DisclaimerCtrl',
      templateUrl: 'views/guest/disclaimer.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
	
	//Guest Privacy Policy 
    .state('app.privacypolicy', {
      url: '/privacy-policy',
      controller: 'PrivacyPolicyCtrl',
      templateUrl: 'views/guest/privacy-policy.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
	
	//Guest Terms & Conditions 
    .state('app.termsconditions', {
      url: '/terms-conditions',
      controller: 'TermsConditionsCtrl',
      templateUrl: 'views/guest/terms-conditions.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
	
	//Article 1
    .state('app.article1', {
      url: '/how_to_protect_your_kids_from_monsoon_ailments',
      controller: 'Article1Ctrl',
      templateUrl: 'views/guest/article1.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
	
	//Article 2
    .state('app.article2', {
      url: '/best_foods_for_kids_to_eat',
      controller: 'Article2Ctrl',
      templateUrl: 'views/guest/article2.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
	
	//Article 3
    .state('app.article3', {
      url: '/tips_to_a_healthy_summer_vacation',
      controller: 'Article3Ctrl',
      templateUrl: 'views/guest/article3.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
	
	//Article 4
    .state('app.article4', {
      url: '/find_out_your_childs_ideal_weekly_menu',
      controller: 'Article4Ctrl',
      templateUrl: 'views/guest/article4.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
	
	
	//Recipe 1
    .state('app.recipe1', {
      url: '/brown_poha_dosa',
      controller: 'Recipe1Ctrl',
      templateUrl: 'views/guest/recipe1.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
	
	//Recipe 2
    .state('app.recipe2', {
      url: '/fruity_sandwich',
      controller: 'Recipe2Ctrl',
      templateUrl: 'views/guest/recipe2.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
	
	//Recipe 3
    .state('app.recipe3', {
      url: '/fruity_sabudana_kheer',
      controller: 'Recipe3Ctrl',
      templateUrl: 'views/guest/recipe3.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
	
	//Recipe 4
    .state('app.recipe4', {
      url: '/fruits_and_walnut_pesto',
      controller: 'Recipe4Ctrl',
      templateUrl: 'views/guest/recipe4.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })
    
    //parent article List
	.state('app.articleList', {
         url: '/articles/:tag',
         controller: 'guestListArticleCtrl',
         templateUrl: 'views/guest/articleListP.html',
         css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
             'assets/js/vendor/flexslider/flexslider.css',
             'assets/js/vendor/rs-plugin/css/settings.css',
             'assets/css/style.css']
    })
    
    //parent recipes List
	.state('app.recipeList', {
         url: '/recipes/:tag',
         controller: 'guestListRecipeCtrl',
         templateUrl: 'views/guest/recipeListP.html',
         css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
             'assets/js/vendor/flexslider/flexslider.css',
             'assets/js/vendor/rs-plugin/css/settings.css',
             'assets/css/style.css']
    })
    
    //parent article Detail
	.state('app.articleDetailP', {
         url: '/article/:articleName',
         controller: 'guestViewArticleDetailCtrl',
         templateUrl: 'views/guest/articleDetailP.html',
         css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
             'assets/js/vendor/flexslider/flexslider.css',
             'assets/js/vendor/rs-plugin/css/settings.css',
             'assets/css/style.css']
    })
    
    //parent recipes Detail
	.state('app.recipesDetailP', {
         url: '/recipe/:recipeName',
         controller: 'guestViewRecipeDetailCtrl',
         templateUrl: 'views/guest/recipesDetailP.html',
         css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
             'assets/js/vendor/flexslider/flexslider.css',
             'assets/js/vendor/rs-plugin/css/settings.css',
             'assets/css/style.css']
    })
    
    .state('administrator', {
      abstract: true,
      url: '/administrator',
      templateUrl: 'views/admin/app.html',
      data: {
          requireLogin: true // this property will apply to all children of 'administrator'
        }
    })

	//app core pages (errors, login,signup)
      .state('admin', {
      abstract: true,
      url: '/admin',
      template: '<div ui-view></div>',
      data: {
          requireLogin: false // this property will apply to all children of 'admin'
        }
    })
    
    //admin login
    .state('admin.login', {
      url: '/login',
      controller: 'LoginCtrl',
      templateUrl: 'views/admin/pages/login.html'
    })
    
	//admin forgot password
    .state('admin.forgotpass', {
      url: '/forgotpass',
      controller: 'ForgotPasswordCtrl',
      templateUrl: 'views/admin/pages/forgotpass.html'
    })
    
        //admin dashboard
    .state('administrator.dashboard', {
      url: '/dashboard',
      controller: 'DashboardCtrl',
      templateUrl: 'views/admin/dashboard.html',
	    containerClass: 'hz-menu',
      resolve: {
        plugins: ['$ocLazyLoad', function($ocLazyLoad) {
          return $ocLazyLoad.load([
            'scripts/vendor/datatables/datatables.bootstrap.min.css',
            'scripts/vendor/datatables/datatables.bootstrap.min.css'
          ]);
        }]
      }
    })

      .state('administrator.manageSchool', {
        url: '',
        template: '<div ui-view></div>'
      })
    //Admin Add School
      .state('administrator.manageSchool.addSchool', {
        url: '/addSchool',
        controller: 'AddSchoolCtrl',
        templateUrl: 'views/admin/addSchool.html',
        containerClass: 'hz-menu',
        resolve: {
          plugins: ['$ocLazyLoad', function($ocLazyLoad) {
            return $ocLazyLoad.load([
              'scripts/vendor/filestyle/bootstrap-filestyle.min.js'
            ]);
          }]
        }
      })
       //Admin Edit School
      .state('administrator.manageSchool.editSchool', {
        url: '/editSchool/:school',
        controller: 'EditSchoolCtrl',
        templateUrl: 'views/admin/editSchool.html',
        containerClass: 'hz-menu',
        resolve: {
          plugins: ['$ocLazyLoad', function($ocLazyLoad) {
            return $ocLazyLoad.load([
              'scripts/vendor/filestyle/bootstrap-filestyle.min.js'
            ]);
          }]
        }
      })
      //Admin Add School Branch
      .state('administrator.manageSchool.addSchoolBranch', {
        url: '/addSchoolBranch/:schoolName/:schoolId',
        controller: 'AddSchoolBranchCtrl',
        templateUrl: 'views/admin/addSchoolBranch.html',
        containerClass: 'hz-menu',
        resolve: {
          plugins: ['$ocLazyLoad', function($ocLazyLoad) {
            return $ocLazyLoad.load([
              'scripts/vendor/filestyle/bootstrap-filestyle.min.js'
            ]);
          }]
        }
      })
      //Admin Edit School Branch
      .state('administrator.manageSchool.editSchoolBranch', {
        url: '/editSchoolBranch/:branch',
        controller: 'EditSchoolBranchCtrl',
        templateUrl: 'views/admin/editSchoolBranch.html',
        containerClass: 'hz-menu',
        resolve: {
          plugins: ['$ocLazyLoad', function($ocLazyLoad) {
            return $ocLazyLoad.load([
              'scripts/vendor/filestyle/bootstrap-filestyle.min.js'
            ]);
          }]
        }
      })
	  //Admin view School List
      .state('administrator.manageSchool.viewSchoolList', {
        url: '/viewSchoolList',
        controller: 'viewSchoolListCtrl',
        templateUrl: 'views/admin/viewSchoolList.html',
        containerClass: 'hz-menu',
        resolve: {
          plugins: ['$ocLazyLoad', function($ocLazyLoad) {
            return $ocLazyLoad.load([
              'scripts/vendor/datatables/Responsive/dataTables.responsive.css',
              'scripts/vendor/datatables/Responsive/dataTables.responsive.js',
              'scripts/vendor/datatables/datatables.bootstrap.min.css'
            ]);
          }]
        }
      })

	  //Admin view School Detail
      .state('administrator.manageSchool.viewSchoolDetail', {
        url: '/viewSchoolDetail/:schoolId',
        controller: 'viewSchoolDetailCtrl',
        templateUrl: 'views/admin/viewSchoolDetail.html',
        containerClass: 'hz-menu',
        resolve: {
          plugins: ['$ocLazyLoad', function($ocLazyLoad) {
            return $ocLazyLoad.load([
              'scripts/vendor/datatables/Responsive/dataTables.responsive.css',
              'scripts/vendor/datatables/Responsive/dataTables.responsive.js',
              'scripts/vendor/datatables/datatables.bootstrap.min.css'
            ]);
          }]
        }
      })

    .state('administrator.manageStudent', {
      url: '',
      template: '<div ui-view></div>'
    })
    
    //Admin Add Student
      .state('administrator.manageStudent.addStudent', {
        url: '/addStudent',
        controller: 'AddStudentCtrl',
        templateUrl: 'views/admin/addStudent.html',
        containerClass: 'hz-menu',
        resolve: {
          plugins: ['$ocLazyLoad', function($ocLazyLoad) {
            return $ocLazyLoad.load([
              'scripts/vendor/filestyle/bootstrap-filestyle.min.js'
            ]);
          }]
        }
      })
      //Admin Add Student
      .state('administrator.manageStudent.editStudent', {
        url: '/editStudent/:student',
        controller: 'EditStudentCtrl',
        templateUrl: 'views/admin/editStudent.html',
        containerClass: 'hz-menu',
        resolve: {
          plugins: ['$ocLazyLoad', function($ocLazyLoad) {
            return $ocLazyLoad.load([
              'scripts/vendor/filestyle/bootstrap-filestyle.min.js'
            ]);
          }]
        }
      })

    //Admin view Student List
      .state('administrator.manageStudent.viewStudentList', {
        url: '/viewStudentList',
        controller: 'viewStudentCtrl',
        templateUrl: 'views/admin/viewStudentList.html',
        containerClass: 'hz-menu',
        resolve: {
          plugins: ['$ocLazyLoad', function($ocLazyLoad) {
            return $ocLazyLoad.load([
              'scripts/vendor/datatables/Responsive/dataTables.responsive.css',
              'scripts/vendor/datatables/Responsive/dataTables.responsive.js',
              'scripts/vendor/datatables/datatables.bootstrap.min.css'
            ]);
          }]
        }
      })
      
      .state('administrator.manageNutritionist', {
        url: '',
        template: '<div ui-view></div>'
      })
    //Admin Add Nutritionist
      .state('administrator.manageNutritionist.addNutritionist', {
        url: '/addNutritionist',
        controller: 'addNutritionistCtrl',
        templateUrl: 'views/admin/addNutritionist.html',
        containerClass: 'hz-menu'
      })

    //Admin view Nutritionist List
      .state('administrator.manageNutritionist.viewNutritionistList', {
        url: '/viewNutritionistList',
        controller: 'viewNutritionistListCtrl',
        templateUrl: 'views/admin/viewNutritionistList.html',
        containerClass: 'hz-menu',
        resolve: {
          plugins: ['$ocLazyLoad', function($ocLazyLoad) {
            return $ocLazyLoad.load([
              'scripts/vendor/datatables/Responsive/dataTables.responsive.css',
              'scripts/vendor/datatables/Responsive/dataTables.responsive.js',
              'scripts/vendor/datatables/datatables.bootstrap.min.css'
            ]);
          }]
        }
      })
      
      //Admin view Nutritionist List
      .state('administrator.createDocument', {
        url: '/createDocument',
        controller: 'createDocumentCtrl',
        templateUrl: 'views/admin/createArticle.html',
        containerClass: 'hz-menu'
      })
	  
	  .state('administrator.listArticle', {
        url: '/listArticle/:tag',
        controller: 'listArticleCtrl',
        templateUrl: 'views/admin/listArticle.html',
        containerClass: 'hz-menu'
      })
	  
	  .state('administrator.listRecipe', {
        url: '/listRecipe/:tag',
        controller: 'listRecipeCtrl',
        templateUrl: 'views/admin/listRecipe.html',
        containerClass: 'hz-menu'
      })
      
      .state('administrator.viewArticleDetail', {
        url: '/article/:articleName',
        controller: 'viewArticleDetailCtrl',
        templateUrl: 'views/admin/viewArticleDetail.html',
        containerClass: 'hz-menu'
      })
      
      .state('administrator.viewRecipeDetail', {
        url: '/recipe/:recipeName',
        controller: 'viewRecipeDetailCtrl',
        templateUrl: 'views/admin/viewRecipeDetail.html',
        containerClass: 'hz-menu'
      })
    
      //Admin view Statistics List
      .state('administrator.viewStatistics', {
        url: '/viewStatistics',
        controller: 'viewStatisticsCtrl',
        templateUrl: 'views/admin/viewStatistics.html',
        containerClass: 'hz-menu',
        resolve: {
          plugins: ['$ocLazyLoad', function($ocLazyLoad) {
            return $ocLazyLoad.load([
              'scripts/vendor/datatables/Responsive/dataTables.responsive.css',
              'scripts/vendor/datatables/Responsive/dataTables.responsive.js',
              'scripts/vendor/datatables/datatables.bootstrap.min.css'
            ]);
          }]
        }
      })
        //school login
    .state('schoolName', {
      url: '/:schoolName', 
      templateUrl: 'views/guest/app.html',
      data: {
          requireLogin: false 
        }
    })
    
    	//Login
    .state('schoolName.weblogin', {
      url: '/login',
      controller: 'LoginCtrl',
      templateUrl: 'views/guest/login.html',
      css: ['http://fonts.googleapis.com/css?family=Lato:300,400,400italic,700,700italic|Raleway:300,400,500,600,700',
            'assets/js/vendor/flexslider/flexslider.css',
            'assets/js/vendor/rs-plugin/css/settings.css',
            'assets/css/style.css']
    })

        //postlogin
    .state('schoolUrl', {
      url: '/:schoolUrl', 
      abstract: true,
      template: '<div ui-view></div>'
    })
    .state('schoolUrl.parentarea', {
    	 abstract: true,
         url: '/parentarea',
         templateUrl: 'views/parent/app.html',
         data: {
             requireLogin: true // this property will apply to all children of 'admin'
         }  // header; menu for parent
    })
    
    // Parent dashboard
	.state('schoolUrl.parentarea.dashboard', {
         url: '/dashboard',
         controller: 'ParentDashboardCtrl',
         templateUrl: 'views/parent/dashboard.html',
         containerClass: 'hz-menu',// parent dashboard url
         resolve: {
               plugins: ['$ocLazyLoad', function($ocLazyLoad) {
                 return $ocLazyLoad.load([
                   'scripts/vendor/flot/jquery.flot.resize.js',
                   'scripts/vendor/flot/jquery.flot.orderBars.js',
                   'scripts/vendor/flot/jquery.flot.axislabels.js'
                 ]);
               }]
             }
    })
    
    //Recipe 1
    .state('schoolUrl.parentarea.recipe1', {
      url: '/brown_poha_dosa',
      controller: 'Recipe1Ctrl',
      templateUrl: 'views/parent/recipe1.html',
      containerClass: 'hz-menu'// parent dashboard url
    })
	
	//Recipe 2
    .state('schoolUrl.parentarea.recipe2', {
      url: '/fruity_sandwich',
      controller: 'Recipe2Ctrl',
      templateUrl: 'views/parent/recipe2.html',
      containerClass: 'hz-menu'// parent dashboard url
    })
    // Parent download Welness Report
	.state('schoolUrl.parentarea.downloadWelnessReportP', {
         url: '/downloadWelnessReport',
         controller: 'downloadWelnessReportCtrl',
         templateUrl: 'views/parent/downloadWelnessReportP.html',
         containerClass: 'hz-menu'
    })
    //Article 1
    .state('schoolUrl.parentarea.article1', {
      url: '/how_to_protect_your_kids_from_monsoon_ailments',
      controller: 'Article1Ctrl',
      templateUrl: 'views/parent/article1.html',
      containerClass: 'hz-menu'// parent dashboard url
    })
	
	//Article 2
    .state('schoolUrl.parentarea.article2', {
      url: '/best_foods_for_kids_to_eat',
      controller: 'Article2Ctrl',
      templateUrl: 'views/parent/article2.html',
      containerClass: 'hz-menu'// parent dashboard url
    })
    
    // Parent download Food Frequency Report
	.state('schoolUrl.parentarea.downloadFoodFrequencyReportP', {
         url: '/downloadFoodFrequencyReport',
         controller: 'downloadFoodFrequencyReportCtrl',
         templateUrl: 'views/parent/downloadFoodFrequencyReportP.html',
         containerClass: 'hz-menu'
    })
    
	//parent Questionnaire
	.state('schoolUrl.parentarea.questionnaireP', {
         url: '/questionnaire',
         controller: 'questionnairePCtrl',
         templateUrl: 'views/parent/questionnaireP.html',
         containerClass: 'hz-menu',
         resolve: {
			plugins: ['$ocLazyLoad', function($ocLazyLoad) {
			  return $ocLazyLoad.load([
				'scripts/vendor/slider/bootstrap-slider.js',
				'scripts/vendor/touchspin/jquery.bootstrap-touchspin.js',
				'scripts/vendor/touchspin/jquery.bootstrap-touchspin.css',
				'scripts/vendor/filestyle/bootstrap-filestyle.min.js'
			  ]);
			}]
		  }
		  
    })
    
    	//parent post question to admin
	.state('schoolUrl.parentarea.postQueryP', {
         url: '/postQuestion',
         controller: 'postQuestionPCtrl',
         templateUrl: 'views/parent/postQuestionP.html',
         containerClass: 'hz-menu'
		  
    })
    
     //parent article List
	.state('schoolUrl.parentarea.articleList', {
         url: '/articles/:tag',
         controller: 'parentListArticleCtrl',
         templateUrl: 'views/parent/articleListP.html',
         containerClass: 'hz-menu'
    })
    
    //parent recipes List
	.state('schoolUrl.parentarea.recipeList', {
         url: '/recipes/:tag',
         controller: 'parentListRecipeCtrl',
         templateUrl: 'views/parent/recipeListP.html',
         containerClass: 'hz-menu'
    })
    
    //parent article Detail
	.state('schoolUrl.parentarea.articleDetailP', {
         url: '/article/:articleName',
         controller: 'parentViewArticleDetailCtrl',
         templateUrl: 'views/parent/articleDetailP.html',
         containerClass: 'hz-menu'
    })
    
    //parent recipes Detail
	.state('schoolUrl.parentarea.recipesDetailP', {
         url: '/recipe/:recipeName',
         controller: 'parentViewRecipeDetailCtrl',
         templateUrl: 'views/parent/recipesDetailP.html',
         containerClass: 'hz-menu'
    })
    
    //parent Nutritional Product Reviews
	.state('schoolUrl.parentarea.newslettersP', {
         url: '/newsletters',
         controller: 'newslettersListPCtrl',
         templateUrl: 'views/parent/newslettersListP.html',
         containerClass: 'hz-menu'
    })
    
    //parent Nutritional Product Reviews
	.state('schoolUrl.parentarea.newslettersDetailP', {
         url: '/newslettersDetail',
         controller: 'newslettersDetailPCtrl',
         templateUrl: 'views/parent/newslettersDetailP.html',
         containerClass: 'hz-menu'
    })
    
    //parent Nutritional Product Reviews
	.state('schoolUrl.parentarea.nutritional_product_reviewsP', {
         url: '/nutritional_product_reviews',
         controller: 'nutritionalProductReviewsCtrl',
         templateUrl: 'views/parent/nutritional_product_reviewsP.html',
         containerClass: 'hz-menu'
    })
    
    //parent Nutritional Product Reviews
	.state('schoolUrl.parentarea.nutritional_product_reviewsDetailP', {
         url: '/nutritional_product_reviewsDetail',
         controller: 'nutritionalProductReviewsDetailCtrl',
         templateUrl: 'views/parent/nutritional_product_reviewsDetailP.html',
         containerClass: 'hz-menu'
    })
    
    // parent settings page
    .state('schoolUrl.parentarea.settings', {
    	url: '/settings',
        controller: 'settingsCtrl',
        templateUrl: 'views/parent/settings.html',
        containerClass: 'hz-menu'
    })
    
	
    // School Login
    .state('schoolUrl.schoolarea', {
    	 abstract: true,
         url: '/schoolarea',
         templateUrl: 'views/school/app.html',
         data: {
             requireLogin: true // this property will apply to all children of 'admin'
           }// parent dashboard url  // header; menu for parent
    })
    
    // School Login Dashboard
    .state('schoolUrl.schoolarea.dashboard', {
         url: '/dashboard',
         controller: 'SchoolDashboardCtrl',
 	    containerClass: 'hz-menu',
         templateUrl: 'views/school/dashboard.html' // parent dashboard url
    });

  }]);

