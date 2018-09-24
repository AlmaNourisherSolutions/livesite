
/**
 * @ngdoc function
 * @name minovateApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the minovateApp
 */
app
  .controller('MainCtrl', function ($scope, $http, $translate) {

    $scope.main = {
      title: 'AlmaNourisher Admin',
      settings: {
        navbarHeaderColor: 'scheme-default',
        sidebarColor: 'scheme-default',
        brandingColor: 'scheme-default',
        activeColor: 'default-scheme-color',
        headerFixed: true,
        asideFixed: true,
        rightbarShow: false
      }
    };

    $scope.ajaxFaker = function(){
      $scope.data=[];
      var url = 'http://www.filltext.com/?rows=10&fname={firstName}&lname={lastName}&delay=5&callback=JSON_CALLBACK';

      $http.jsonp(url).success(function(data){
        $scope.data=data;
        angular.element('.tile.refreshing').removeClass('refreshing');
      });
    };

    $scope.changeLanguage = function (langKey) {
      $translate.use(langKey);
      $scope.currentLanguage = langKey;
    };
    $scope.currentLanguage = $translate.proposedLanguage() || $translate.use();
  })
   .controller('DaterangepickerCtrl', function ($scope, $moment) {
    $scope.startDate = $moment().subtract(1, 'days').format('MMMM D, YYYY');
    $scope.endDate = $moment().add(31, 'days').format('MMMM D, YYYY');
    $scope.rangeOptions = {
      ranges: {
        Today: [$moment(), $moment()],
        Yesterday: [$moment().subtract(1, 'days'), $moment().subtract(1, 'days')],
        'Last 7 Days': [$moment().subtract(6, 'days'), $moment()],
        'Last 30 Days': [$moment().subtract(29, 'days'), $moment()],
        'This Month': [$moment().startOf('month'), $moment().endOf('month')],
        'Last Month': [$moment().subtract(1, 'month').startOf('month'), $moment().subtract(1, 'month').endOf('month')]
      },
      opens: 'left',
      startDate: $moment().subtract(29, 'days'),
      endDate: $moment(),
      parentEl: '#content'
    };
  })
   .controller('NavCtrl', function ($scope) {
    $scope.oneAtATime = false;

    $scope.status = {
      isFirstOpen: true,
      isSecondOpen: true,
      isThirdOpen: true
    };

  })
  .controller('ModalDemoCtrl', function ($scope, $uibModal, $log) {

    $scope.items = ['item1', 'item2', 'item3'];

    $scope.open = function(size) {

      var modalInstance = $uibModal.open({
        templateUrl: 'myModalContent.html',
        controller: 'ModalInstanceCtrl',
        size: size,
        resolve: {
          items: function () {
            return $scope.items;
          }
        }
      });
    }
})
.controller('ModalInstanceCtrl', function ($scope, $uibModalInstance, items, $http, $window,$rootScope,$state) {

    $scope.items = items;
    $scope.selected = {
      item: $scope.items[0]
    };
    
    $scope.getSchoolList = function() {
		$scope.schoolList = [];
		
		$http({
			method : "GET",
			url : $rootScope.baseUrl + 'alma/getSchoolList'
		}).success(function(data, status, headers, config) {
			$scope.schoolList = data;
		}).error(function(data, status, headers, config) {
			$window.alert("Something went wrong");
		})
	};

	$scope.getSchoolList();

    $scope.ok = function () {
      $uibModalInstance.close();
      var school = $scope.school;
      $rootScope.school = school;
      console.log(school);
      var obj = {};
      obj['schoolName']=school.url;
      $state.go('schoolName.weblogin', obj);
    };

    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };
  })
.controller('ButtonsCtrl', function ($scope) {
    $scope.singleModel = 1;

    $scope.radioModel = 'Middle';

    $scope.checkModel = {
      left: false,
      middle: true,
      right: false
    };
  })
    .controller('TodoWidgetCtrl', function($scope) {
    $scope.todos = [{
      text: 'Release update',
      completed: false
    },{
      text: 'Make a backup',
      completed: false
    },{
      text: 'Send e-mail to Ici',
      completed: true
    },{
      text: 'Buy tickets',
      completed: false
    },{
      text: 'Resolve issues',
      completed: false
    },{
      text: 'Compile new version',
      completed: false
    }];

    var todos = $scope.todos;

    $scope.addTodo = function() {
      $scope.todos.push({
        text: $scope.todo,
        completed: false
      });
      $scope.todo = '';
    };

    $scope.removeTodo = function(todo) {
      todos.splice(todos.indexOf(todo), 1);
    };

    $scope.editTodo = function(todo) {
      $scope.editedTodo = todo;
      // Clone the original todo to restore it on demand.
      $scope.originalTodo = angular.extend({}, todo);
    };

    $scope.doneEditing = function(todo) {
      $scope.editedTodo = null;

      todo.text = todo.text.trim();

      if (!todo.text) {
        $scope.removeTodo(todo);
      }
    };

    $scope.revertEditing = function(todo) {
      todos[todos.indexOf(todo)] = $scope.originalTodo;
      $scope.doneEditing($scope.originalTodo);
    };

  })
  .controller('tagsInputCtrl', function($scope, $http) {
    // $scope.movies = [
      // 'The Dark Knight',
      // 'Heat',
      // 'Inception',
      // 'The Dark Knight Rises',
      // 'Kill Bill: Vol. 1',
      // 'Terminator 2: Judgment Day',
      // 'The Matrix',
      // 'Minority Report',
      // 'The Bourne Ultimatum'
    // ];

    // $scope.loadMovies = function(query) {
      // return $http.get('scripts/jsons/movies.json');
    // }; 
  });