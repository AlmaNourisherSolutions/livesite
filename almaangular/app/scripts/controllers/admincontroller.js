
/**
 * @ngdoc function
 * @name minovateApp.controller:DashboardCtrl
 * @description # DashboardCtrl Controller of the minovateApp
 */
app
	.controller(
				'DashboardCtrl',
				function($scope, $http, FileUploader, $rootScope) {
					$scope.page = {
						title : 'Dashboard',
						subtitle : 'Place subtitle here...'
					};

					
					$scope.getSchoolCount = function() {
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/getSchoolsCount'
						}).success(function(data, status, headers, config) {
							$scope.schoolCount = data;
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					};
					
					$scope.getStudentCount = function() {
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/getStudentsCount'
						}).success(function(data, status, headers, config) {
							$scope.studentCount = data;
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					};
					
					$scope.getArticleCount = function() {
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/getArticlesCount'
						}).success(function(data, status, headers, config) {
							$scope.articleCount = data;
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					};
					
					$scope.getRecipeCount = function() {
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/getRecipesCount'
						}).success(function(data, status, headers, config) {
							$scope.recipeCount = data;
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					};
					
					$scope.getQStats = function() {
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/questionaireStatisticsTotalCount'
						}).success(function(data, status, headers, config) {
							$scope.qStats = data;
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					};
					
					$scope.getSchoolList = function() {
						
						$scope.schoolList = [];
						
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/getSchoolList'
						}).success(function(data, status, headers, config) {
							$scope.schoolList = data;
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					};

					$scope.getSchoolCount();
					$scope.getStudentCount();
					$scope.getArticleCount();
					$scope.getRecipeCount();
					$scope.getQStats();
					$scope.getSchoolList();

					var uploaderQuestionaire= $scope.uploaderQuestionaire = new FileUploader({
						url: $rootScope.baseUrl + 'UploadDownloadFileService/uploadQuestionaireData'
					});
					uploaderQuestionaire.onCompleteAll = function() {
				         console.info('onCompleteAll');
				         alert("File upload completed");
				       };

					$scope.uploadQuestionaire = function(){
						$.each(uploaderQuestionaire.queue, function( index, item ) {
							uploaderQuestionaire.uploadAll();
						});
					}
					
					$scope.getBranchList = function(){
						$scope.branchList = [];
						
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/getSchoolBranchList/'+$scope.school
						}).success(function(data, status, headers, config) {
							$scope.branchList = data;
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					}
					
					$scope.bulkNutritionReport = function(){
		            	$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/bulkNutritionReport/'+$scope.branch
						})
								.success(
										function(data, status, headers, config) {
											$scope.nutritionreport = data;
										})
								.error(
										function(data, status, headers, config) {
											$window
													.alert("Something went wrong while adding school");
										});
		            
					}
					$scope.bulkFoodFreqReport = function(){
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/bulkFoodFreqReport/'+$scope.branch
						})
								.success(
										function(data, status, headers, config) {
											$scope.foodfreqreport = data;
										})
								.error(
										function(data, status, headers, config) {
											$window
													.alert("Something went wrong while adding school");
										});
					}
				})
		// Admin add school
		.controller(
				'AddSchoolCtrl',
				function($scope, $http, $state, $window,$rootScope,FileUploader) {
					$scope.page = {
						title : 'Add School',
						subtitle : 'Place subtitle here...'
					};
					$scope.gPlace;
					
					 var uploaderLogo = $scope.uploaderLogo = new FileUploader({
				            url: $rootScope.baseUrl + 'UploadDownloadFileService/upload'
				        });
					 var uploaderBanner = $scope.uploaderBanner = new FileUploader({
				            url: $rootScope.baseUrl + 'UploadDownloadFileService/upload'
				        });
					 
				        
					 	uploaderLogo.onAfterAddingFile = function(item) {
				            console.log('onAfterAddingFile', item);
				            var fileExtension = '.' + item.file.name.split('.').pop();
							item.file.name = Math.random().toString(36).substring(7) + new Date().getTime() + fileExtension;
				          };
				          
				          uploaderBanner.onAfterAddingFile = function(item) {
					            console.log('onAfterAddingFile', item);
					            var fileExtension = '.' + item.file.name.split('.').pop();
								item.file.name = Math.random().toString(36).substring(7) + new Date().getTime() + fileExtension;
					          };


					// function to submit the form after all validation has
					// occurred
					$scope.submitForm = function(isValid) {
						console.log('validate form');
						var user = this.user;
						user['schoolLogo'] = 'defaultlogo.png';
						user['schoolBanner'] = 'defaultbanner.png';
						console.log(JSON.stringify(user));
						$.each(uploaderLogo.queue, function( index, item ) {
							user['schoolLogo'] = item.file.name;
							uploaderLogo.uploadAll();
							});
						
						$.each(uploaderBanner.queue, function( index, item ) {
							user['schoolBanner'] = item.file.name;
							uploaderBanner.uploadAll();
							});
						// check to make sure the form is completely valid
						$http({
							method : "POST",
							url : $rootScope.baseUrl + 'admin/addSchool',
							data : JSON.stringify(user)
						})
								.success(
										function(data, status, headers, config) {
											$state
													.go('administrator.manageSchool.viewSchoolDetail', {'schoolId': data});
										})
								.error(
										function(data, status, headers, config) {
											$window
													.alert("Something went wrong while adding school");
										});

					};

					$scope.notBlackListed = function(value) {
						var blacklist = [ 'bad@domain.com',
								'verybad@domain.com' ];
						return blacklist.indexOf(value) === -1;
					};
				})
				
				// Admin edit school
		.controller(
				'EditSchoolCtrl',
				function($scope, $http, $state, $window,$rootScope,FileUploader, $stateParams) {
					$scope.page = {
						title : 'Edit School',
						subtitle : 'Place subtitle here...'
					};
					$scope.gPlace;
					
					var schoolJson = $stateParams.school;
					school = JSON.parse(schoolJson);
					$scope.user = school;
					var currentLogo = school.schoolLogo;
					
					 var uploaderLogo = $scope.uploaderLogo = new FileUploader({
				            url: $rootScope.baseUrl + 'UploadDownloadFileService/upload'
				        });
					 
				        
					 	uploaderLogo.onAfterAddingFile = function(item) {
				            console.log('onAfterAddingFile', item);
				            var fileExtension = '.' + item.file.name.split('.').pop();
							item.file.name = Math.random().toString(36).substring(7) + new Date().getTime() + fileExtension;
				          };
				          


					// function to submit the form after all validation has
					// occurred
					$scope.submitForm = function(isValid) {
						console.log('validate form');
						var user = this.user;
						
						console.log(JSON.stringify(user));
						
						delete user["title"];
						delete user["subtitle"];
						$.each(uploaderLogo.queue, function( index, item ) {
							user['schoolLogo'] = item.file.name;
							uploaderLogo.uploadAll();
							});
						// check to make sure the form is completely valid
						$http({
							method : "POST",
							url : $rootScope.baseUrl + 'admin/editSchool',
							data : JSON.stringify(user)
						})
								.success(
										function(data, status, headers, config) {
											$state
													.go('administrator.manageSchool.viewSchoolDetail', {'schoolId': data});
										})
								.error(
										function(data, status, headers, config) {
											$window
													.alert("Something went wrong while adding school");
										});

					};

					$scope.notBlackListed = function(value) {
						var blacklist = [ 'bad@domain.com',
								'verybad@domain.com' ];
						return blacklist.indexOf(value) === -1;
					};
				})

		// Admin add school Branch
		.controller(
				'AddSchoolBranchCtrl',
				function($scope, $stateParams, $http, $state, $window,$rootScope) {
					$scope.page = {
						title : 'Add School Branch',
						subtitle : 'Place subtitle here...'
					};
					$scope.gPlace;
					$scope.user = {
						schoolName : $stateParams.schoolName,
						schoolId : $stateParams.schoolId
					};

					$scope.classLookup = [];
					$scope.getClassLookup = function(branchId) {
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/getClassLookup'
						}).success(function(data, status, headers, config) {
							$scope.classLookup = data;
							console.log($scope.classLookup);
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					};

					$scope.getClassLookup();
					// function to submit the form after all validation has
					// occurred
					$scope.submitForm = function(isValid) {
						console.log('validate form');
						var user = this.user;
						// check to make sure the form is completely valid
						$http({
							method : "POST",
							url : $rootScope.baseUrl + 'admin/addSchoolBranch',
							data : JSON.stringify(user)
						})
								.success(
										function(data, status, headers, config) {
											$state
													.go('administrator.manageSchool.viewSchoolDetail',{'schoolId': data});
										})
								.error(
										function(data, status, headers, config) {
											$window
													.alert("Something went wrong while adding school branch");
										});
					};

					$scope.notBlackListed = function(value) {
						var blacklist = [ 'bad@domain.com',
								'verybad@domain.com' ];
						return blacklist.indexOf(value) === -1;
					};
				})
				
				// Admin edit school Branch
		.controller(
				'EditSchoolBranchCtrl',
				function($scope, $stateParams, $http, $state, $window,$rootScope) {
					$scope.page = {
						title : 'Edit School Branch',
						subtitle : 'Place subtitle here...'
					};
					$scope.gPlace;
					var branchJson = $stateParams.branch;
					$scope.user = JSON.parse(branchJson);

					$scope.classLookup = [];
					$scope.getClassLookup = function(branchId) {
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/getClassLookup'
						}).success(function(data, status, headers, config) {
							$scope.classLookup = data;
							console.log($scope.classLookup);
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					};

					$scope.getClassLookup();
					// function to submit the form after all validation has
					// occurred
					$scope.submitForm = function(isValid) {
						console.log('validate form');
						var user = this.user;
						// check to make sure the form is completely valid
						$http({
							method : "POST",
							url : $rootScope.baseUrl + 'admin/addSchoolBranch',
							data : JSON.stringify(user)
						})
								.success(
										function(data, status, headers, config) {
											$state
													.go('administrator.manageSchool.viewSchoolDetail',{'schoolId': data});
										})
								.error(
										function(data, status, headers, config) {
											$window
													.alert("Something went wrong while adding school branch");
										});
					};

					$scope.notBlackListed = function(value) {
						var blacklist = [ 'bad@domain.com',
								'verybad@domain.com' ];
						return blacklist.indexOf(value) === -1;
					};
				})

		// Admin edit Students
		.controller(
				'EditStudentCtrl',
				function($scope, $http, $state, $window,$rootScope,FileUploader,$stateParams) {
					$scope.page = {
						title : 'Edit Student',
						subtitle : 'Place subtitle here...'
					};
					 $scope.gPlace;
					 
					 
					 
					 var uploader= $scope.uploader = new FileUploader({
				            url: $rootScope.baseUrl + 'UploadDownloadFileService/uploadProfilePic'
				        });
					 uploader.onAfterAddingFile = function(item) {
				            console.log('onAfterAddingFile', item);
				            var fileExtension = '.' + item.file.name.split('.').pop();
							item.file.name = Math.random().toString(36).substring(7) + new Date().getTime() + fileExtension;
				          };
					 
				     var uploaderStudent= $scope.uploaderStudent = new FileUploader({
					            url: $rootScope.baseUrl + 'UploadDownloadFileService/uploadStudentData'
					        });
					 var malL = [-2.01118107, -1.982373595, -1.924100169, -1.86549793, -1.807261899, -1.750118905, -1.69481584, -1.642106779, -1.592744414, -1.547442391, -1.506902601, -1.471770047, -1.442628957, -1.419991255, -1.404277619, -1.39586317, -1.394935252, -1.401671596, -1.416100312, -1.438164899, -1.467669032, -1.504376347, -1.547942838, -1.597896397, -1.653732283, -1.714869347, -1.780673181, -1.850468473, -1.923551865, -1.999220429, -2.076707178, -2.155348017, -2.234438552, -2.313321723, -2.391381273, -2.468032491, -2.542781541, -2.61516595, -2.684789516, -2.751316949, -2.81445945, -2.87402476, -2.92984048, -2.981796828, -3.029831343, -3.073924224, -3.114093476, -3.15039004, -3.182893018, -3.21170511, -3.23694834, -3.25876011, -3.277281546, -3.292683774, -3.305124073, -3.314768951, -3.321785992, -3.326345795, -3.328602731, -3.328725277, -3.32687018, -3.323188896, -3.317827016, -3.310923871, -3.302612272, -3.293018361, -3.282260813, -3.270454609, -3.257703616, -3.244108214, -3.229761713, -3.214751287, -3.199158184, -3.18305795, -3.166520664, -3.1496103, -3.132389637, -3.114911153, -3.097226399, -3.079383079, -3.061423765, -3.043386071, -3.025310003, -3.007225737, -2.989164598, -2.971148225, -2.953208047, -2.935363951, -2.917635157, -2.900039803, -2.882593796, -2.865311266, -2.848204697, -2.831285052, -2.81456189, -2.79804347, -2.781736856, -2.765648008, -2.749782197, -2.734142443, -2.718732873, -2.703555506, -2.688611957, -2.673903164, -2.659429443, -2.645190534, -2.631185649, -2.617413511, -2.603872392, -2.590560148, -2.577474253, -2.564611831, -2.551969684, -2.539539972, -2.527325681, -2.515320235, -2.503519447, -2.491918934, -2.480514136, -2.469300331, -2.458272656, -2.447426113, -2.436755595, -2.426255887, -2.415921689, -2.405747619, -2.395728233, -2.385858029, -2.376131459, -2.366542942, -2.357086871, -2.347757625, -2.338549576, -2.3294571, -2.320474586, -2.311596446, -2.302817124, -2.294131107, -2.285532933, -2.277017201, -2.268578584, -2.260211837, -2.251911809, -2.243673453, -2.235491842, -2.227362173, -2.21927979, -2.211240187, -2.203239029, -2.195272161, -2.187335625, -2.179425674, -2.171538789, -2.163671689, -2.155821357, -2.147985046, -2.140160305, -2.132344989, -2.124537282, -2.116735712, -2.108939167, -2.10114692, -2.093358637, -2.085574403, -2.077794735, -2.070020599, -2.062253431, -2.054495145, -2.046748156, -2.039015385, -2.031300282, -2.023606828, -2.015942013, -2.008305745, -2.000706389, -1.993150137, -1.985643741, -1.97819451, -1.970810308, -1.96349954, -1.956271141, -1.949134561, -1.942099744, -1.935177101, -1.92837748, -1.921712136, -1.915192685, -1.908831065, -1.902639482, -1.896630358, -1.890816268, -1.885209876, -1.879823505, -1.874670324, -1.869760299, -1.865113245, -1.860734944, -1.85663384, -1.852827186, -1.849323204, -1.846131607, -1.843261294, -1.840720248, -1.83851544, -1.83665586, -1.835138046, -1.833972004, -1.833157751, -1.83269562, -1.832584342, -1.832820974, -1.833400825, -1.834317405, -1.83555752, -1.837119466, -1.838987063, -1.841146139, -1.843580575],
				        malM = [16.57502768, 16.54777487, 16.49442763, 16.44259552, 16.3922434, 16.34333654, 16.29584097, 16.24972371, 16.20495268, 16.16149871, 16.11933258, 16.07842758, 16.03875896, 16.00030401, 15.96304277, 15.92695418, 15.89202582, 15.85824093, 15.82558822, 15.79405728, 15.76364255, 15.73433668, 15.70613566, 15.67904062, 15.65305192, 15.62817269, 15.604408, 15.58176458, 15.56025067, 15.5398746, 15.52064993, 15.50258427, 15.48568973, 15.46997718, 15.45545692, 15.44213961, 15.43003207, 15.41914163, 15.40947356, 15.40103139, 15.39381785, 15.38783094, 15.38306945, 15.37952958, 15.37720582, 15.37609107, 15.37617677, 15.37745304, 15.37990886, 15.38353217, 15.38831005, 15.39422883, 15.40127496, 15.40943252, 15.41868691, 15.42902273, 15.44042439, 15.45287581, 15.46636218, 15.48086704, 15.49637465, 15.51286936, 15.53033563, 15.54875807, 15.56812143, 15.58841065, 15.60961101, 15.63170735, 15.65468563, 15.67853139, 15.70323052, 15.72876911, 15.75513347, 15.78231007, 15.8102856, 15.83904708, 15.86858123, 15.89887562, 15.92991765, 15.96169481, 15.99419489, 16.02740607, 16.0613159, 16.09591292, 16.13118532, 16.16712234, 16.20371168, 16.24094239, 16.27880346, 16.31728385, 16.35637267, 16.39605916, 16.43633265, 16.47718256, 16.51859843, 16.56056987, 16.60308661, 16.64613844, 16.68971518, 16.73380695, 16.77840363, 16.82349538, 16.86907238, 16.91512487, 16.96164317, 17.00861766, 17.05603879, 17.10389705, 17.15218302, 17.20088732, 17.25000062, 17.29951367, 17.34941726, 17.39970308, 17.45036072, 17.50138161, 17.55275674, 17.60447714, 17.6565339, 17.70891811, 17.76162094, 17.81463359, 17.86794729, 17.92155332, 17.97544299, 18.02960765, 18.08403868, 18.1387275, 18.19366555, 18.24884431, 18.3042553, 18.35989003, 18.41574009, 18.47179706, 18.52805255, 18.5844982, 18.64112567, 18.69792663, 18.75489278, 18.81201584, 18.86928753, 18.92669959, 18.98424378, 19.04191185, 19.09969557, 19.15758672, 19.21557707, 19.27365839, 19.33182247, 19.39006106, 19.44836594, 19.50672885, 19.56514153, 19.62359571, 19.6820831, 19.74059538, 19.7991242, 19.85766121, 19.916198, 19.97472615, 20.03323719, 20.09172262, 20.15017387, 20.20858236, 20.26693944, 20.32523642, 20.38346455, 20.44161501, 20.49967894, 20.5576474, 20.6155114, 20.67326189, 20.73088905, 20.7883851, 20.84574003, 20.90294449, 20.95998909, 21.01686433, 21.07356067, 21.1300685, 21.18637813, 21.24247982, 21.29836376, 21.35402009, 21.40943891, 21.46461026, 21.51952414, 21.57417053, 21.62853937, 21.68262062, 21.73640419, 21.78988003, 21.84303819, 21.8958685, 21.94836168, 22.00050569, 22.05229242, 22.10371305, 22.15475603, 22.20541249, 22.255673, 22.30552831, 22.3549693, 22.40398706, 22.45257182, 22.50071778, 22.54841437, 22.59565422, 22.64242956, 22.68873292, 22.73455713, 22.7798953, 22.82474087, 22.86908912, 22.91293151, 22.95626373, 22.99908062, 23.041377338],
				        malS = [.080592465, .080127429, .079233994, .078389356, .077593501, .076846462, .076148308, .075499126, .074898994, .074347997, .073846139, .07339337, .072989551, .072634432, .072327649, .07206864, .071856805, .071691278, .071571093, .071495113, .071462106, .071470646, .071519218, .071606277, .071730167, .071889214, .072081737, .072306081, .072560637, .07284384, .073154324, .073490667, .073851672, .074236235, .074643374, .075072264, .075522104, .07599225, .076482128, .076991232, .077519149, .07806539, .078629592, .079211369, .079810334, .080426086, .081058206, .081706249, .082369741, .083048178, .083741021, .0844477, .085167651, .085900184, .086644667, .087400421, .088166744, .088942897, .089728202, .090521875, .091323162, .092131305, .092945544, .093765118, .09458927, .095417247, .096248301, .097081694, .097916698, .098752593, .099588675, .100424251, .101258643, .102091189, .102921245, .103748189, .104571386, .105390269, .106204258, .107012788, .107815327, .108611374, .109400388, .110181915, .110955478, .111720691, .112477059, .1132242, .113961734, .114689291, .115406523, .116113097, .116808702, .117493042, .11816584, .118826835, .119475785, .120112464, .120736656, .121348181, .121946849, .122532501, .123104991, .123664186, .124209969, .124742239, .125260905, .125765895, .126257147, .126734613, .12719826, .127648067, .128084023, .128506192, .128914497, .129309001, .129689741, .130056765, .130410133, .130749913, .131076187, .131389042, .131688579, .131974905, .132248138, .132508403, .132755834, .132990575, .133212776, .133422595, .133620197, .133805756, .133979452, .13414147, .134292005, .134431256, .134559427, .134676731, .134783385, .134879611, .134965637, .135041695, .135108024, .135164867, .135212469, .135251083, .135280963, .135302371, .135315568, .135320824, .135318407, .135308594, .135291662, .135267891, .135237567, .135200976, .135158409, .135110159, .135056522, .134997797, .134934285, .134866291, .134794121, .134718085, .134638494, .134555663, .13446991, .134381553, .134290916, .134198323, .134104101, .134008581, .133912066, .133814954, .133717552, .1336202, .133523244, .133427032, .133331914, .133238245, .133146383, .13305669, .132969531, .132885274, .132804292, .132726962, .132653664, .132584784, .132520711, .132461838, .132408563, .132361289, .132320427, .132286382, .1322596, .132240418, .13222933, .132226801, .132233201, .132248993, .132274625, .132310549, .132357221, .132415103, .132484631, .132566359, .132660699, .132768153, .132889211, .133024368, .133174129, .133338999, .133519496, .133716192, .133929525, .134160073, .134408381, .1346750014],
				        femL = [-.98660853, -1.024496827, -1.102698353, -1.18396635, -1.268071036, -1.354751525, -1.443689692, -1.53454192, -1.626928093, -1.720434829, -1.814635262, -1.909076262, -2.003296102, -2.096828937, -2.189211877, -2.279991982, -2.368732949, -2.455021314, -2.538471972, -2.618732901, -2.695488973, -2.768464816, -2.837426693, -2.902178205, -2.962580386, -3.018521987, -3.069936555, -3.116795864, -3.159107331, -3.196911083, -3.230276759, -3.259300182, -3.284099963, -3.30481415, -3.321596954, -3.334615646, -3.344047622, -3.35007771, -3.352893805, -3.352691376, -3.34966438, -3.343998803, -3.335889574, -3.325522491, -3.31307846, -3.298732648, -3.282653831, -3.265003896, -3.245937506, -3.225606516, -3.204146115, -3.181690237, -3.158363475, -3.134282833, -3.109557879, -3.084290931, -3.058577292, -3.032505499, -3.0061576, -2.979609448, -2.952930993, -2.926186592, -2.899435307, -2.872731211, -2.846123683, -2.819657704, -2.793374145, -2.767310047, -2.741498897, -2.715970894, -2.690753197, -2.665870146, -2.641343436, -2.617192204, -2.593430614, -2.570076037, -2.547141473, -2.524635245, -2.502569666, -2.48095189, -2.459785573, -2.439080117, -2.418838304, -2.399063683, -2.379756861, -2.360920527, -2.342557728, -2.324663326, -2.307240716, -2.290287663, -2.273803847, -2.257782149, -2.242227723, -2.227132805, -2.212495585, -2.19831275, -2.184580762, -2.171295888, -2.158454232, -2.146051754, -2.134084303, -2.122547629, -2.111437411, -2.100749266, -2.090478774, -2.080621484, -2.071172932, -2.062128649, -2.053484173, -2.045235058, -2.03737688, -2.029906684, -2.022817914, -2.016107084, -2.009769905, -2.003802134, -1.998199572, -1.992958064, -1.988073505, -1.983541835, -1.979359041, -1.975521156, -1.972024258, -1.968864465, -1.966037938, -1.963540872, -1.961369499, -1.959520079, -1.9579889, -1.956772271, -1.95586652, -1.955267984, -1.954973011, -1.954977947, -1.955279136, -1.955872909, -1.956755579, -1.957923436, -1.959372737, -1.9610997, -1.963100496, -1.96537124, -1.967907983, -1.970706706, -1.973763307, -1.977073595, -1.980633277, -1.984437954, -1.988483106, -1.992764085, -1.997276103, -2.002014224, -2.00697335, -2.012148213, -2.017533363, -2.023123159, -2.028911755, -2.034893091, -2.041060881, -2.047408604, -2.05392949, -2.060616513, -2.067462375, -2.074459502, -2.081600029, -2.088875793, -2.096278323, -2.103798828, -2.111428194, -2.119156972, -2.126975375, -2.134873266, -2.142840157, -2.150865204, -2.158937201, -2.167044578, -2.175176987, -2.183317362, -2.191457792, -2.199583649, -2.207681525, -2.215737645, -2.223739902, -2.231667995, -2.239511942, -2.247257081, -2.254885145, -2.26238209, -2.269731517, -2.276917229, -2.283925442, -2.290731442, -2.29732427, -2.303687802, -2.309799971, -2.315651874, -2.32121731, -2.326481911, -2.331428139, -2.336038473, -2.34029545, -2.344181703, -2.34768, -2.350773286, -2.353444725, -2.355677743, -2.35745607, -2.358763788, -2.359585369, -2.359905726, -2.359710258, -2.358980464, -2.357714508, -2.355892424, -2.353501353, -2.350528726, -2.346962247, -2.342796948],
				        femM = [16.42339664, 16.38804056, 16.3189719, 16.25207985, 16.18734669, 16.12475448, 16.06428762, 16.00593001, 15.94966631, 15.89548197, 15.84336179, 15.79329146, 15.7452564, 15.69924188, 15.65523282, 15.61321371, 15.57316843, 15.53508019, 15.49893145, 15.46470384, 15.43237817, 15.40193436, 15.37335154, 15.34660842, 15.32168181, 15.29854897, 15.27718618, 15.2575692, 15.23967338, 15.22347371, 15.20894491, 15.19606152, 15.18479799, 15.17512871, 15.16702811, 15.16047068, 15.15543107, 15.15188405, 15.14980479, 15.14916825, 15.14994984, 15.15212585, 15.15567186, 15.16056419, 15.16677947, 15.17429464, 15.18308694, 15.1931339, 15.20441335, 15.21690296, 15.2305815, 15.24542745, 15.26141966, 15.27853728, 15.29675967, 15.31606644, 15.33643745, 15.35785274, 15.38029261, 15.40373754, 15.42816819, 15.45356545, 15.47991037, 15.50718419, 15.53536829, 15.56444426, 15.5943938, 15.6251988, 15.65684126, 15.68930333, 15.7225673, 15.75661555, 15.79143062, 15.82699517, 15.86329241, 15.90030484, 15.93801545, 15.97640787, 16.01546483, 16.05516984, 16.09550688, 16.13645881, 16.17800955, 16.22014281, 16.26284277, 16.30609316, 16.34987759, 16.39418118, 16.43898741, 16.48428082, 16.53004554, 16.57626713, 16.62292864, 16.67001572, 16.71751288, 16.76540496, 16.81367689, 16.86231366, 16.91130036, 16.96062216, 17.0102643, 17.06021213, 17.11045106, 17.16096656, 17.21174424, 17.26276973, 17.31402878, 17.3655072, 17.4171909, 17.46906585, 17.52111811, 17.57333347, 17.62569869, 17.67819987, 17.7308234, 17.78355575, 17.83638347, 17.88929321, 17.94227168, 17.9953057, 18.04838216, 18.10148804, 18.15461039, 18.20773639, 18.26085325, 18.31394832, 18.36700902, 18.42002284, 18.47297739, 18.52586035, 18.57865951, 18.63136275, 18.68395801, 18.73643338, 18.788777, 18.84097713, 18.89302212, 18.94490041, 18.99660055, 19.04811118, 19.09942105, 19.15051899, 19.20139397, 19.25203503, 19.30243131, 19.35257209, 19.40244671, 19.45204465, 19.50135548, 19.55036888, 19.59907464, 19.64746266, 19.69552294, 19.7432456, 19.79062086, 19.83763907, 19.88429066, 19.9305662, 19.97645636, 20.02195192, 20.06704377, 20.11172291, 20.15598047, 20.19980767, 20.24319586, 20.28613648, 20.32862109, 20.37064138, 20.41218911, 20.45325617, 20.49383457, 20.5339164, 20.57349387, 20.61255929, 20.65110506, 20.6891237, 20.72660728, 20.76355011, 20.79994337, 20.83578051, 20.87105449, 20.90575839, 20.93988477, 20.97342858, 21.00638171, 21.0387374, 21.07048996, 21.10163241, 21.13215845, 21.16206171, 21.1913351, 21.21997472, 21.24797262, 21.27532239, 21.30201933, 21.32805489, 21.35342563, 21.37812462, 21.40214589, 21.42548351, 21.44813156, 21.47008412, 21.49133529, 21.51187918, 21.53170989, 21.55082155, 21.56920824, 21.58686406, 21.60378309, 21.61995939, 21.635387, 21.65006126, 21.6639727, 21.67711736, 21.68948935, 21.70108288, 21.71189225, 21.721909734],
				        femS = [.085451785, .085025838, .084214052, .083455124, .082748284, .082092737, .081487717, .080932448, .080426175, .079968176, .079557735, .079194187, .078876895, .078605255, .078378696, .078196674, .078058667, .077964169, .077912684, .077903716, .077936763, .078011309, .078126817, .078282739, .078478449, .078713325, .078986694, .079297841, .079646006, .080030389, .080450145, .080904391, .081392203, .081912623, .082464661, .083047295, .083659478, .084300139, .0849682, .085662539, .086382035, .087125591, .087892047, .088680264, .089489106, .090317434, .091164117, .092028028, .092908048, .093803033, .094711916, .095633595, .096566992, .097511046, .09846471, .099426955, .100396769, .101373159, .10235515, .103341788, .104332139, .105325289, .106320346, .10731644, .108312721, .109308364, .110302563, .111294537, .112283526, .113268793, .114249622, .115225321, .116195218, .117158667, .118115073, .119063807, .12000429, .120935994, .121858355, .12277087, .123673085, .124564484, .125444639, .126313121, .127169545, .128013515, .128844639, .129662637, .130467138, .131257852, .132034479, .132796819, .133544525, .134277436, .134995324, .135697996, .136385276, .137057004, .137713039, .138353254, .138977537, .139585795, .140177947, .140753927, .141313686, .141857186, .142384404, .142895332, .143389972, .143868341, .144330469, .144776372, .145206138, .145619819, .146017491, .146399239, .146765161, .147115364, .147449967, .147769097, .148072891, .148361495, .148635067, .148893769, .149137776, .14936727, .149582439, .149783482, .149970604, .15014402, .15030395, .150450621, .15058427, .150705138, .150813475, .150909535, .150993582, .151065883, .151126714, .151176355, .151215094, .151243223, .151261042, .151268855, .151266974, .151255713, .151235395, .151206347, .151168902, .151123398, .15107018, .151009595, .150942, .150867753, .150787221, .150700774, .150608788, .150511645, .150409731, .15030344, .150193169, .150079322, .149962308, .14984254, .149720441, .149596434, .149470953, .149344433, .149217319, .14909006, .14896311, .148836931, .148711989, .148588757, .148467715, .148349348, .14823412, .148122614, .148015249, .147912564, .147815078, .147723315, .147637768, .147559083, .147487716, .14742421, .147369174, .147323144, .147286698, .147260415, .147244828, .147240683, .147248467, .14726877, .147302299, .147349514, .147411215, .147487979, .147580453, .147689289, .14781515, .147958706, .148120633, .148301619, .148502355, .148723546, .148965902, .149230142, .149516994, .149827195, .150161492, .150520734, .150905439, .151316531, .151754808, .152221086, .152716206, .1532408716];
				   
					 
					 var studentJson = $stateParams.student;
						$scope.user = JSON.parse(studentJson);
					 
					 $scope.uploadStudent = function(){
						 $.each(uploaderStudent.queue, function( index, item ) {
									uploaderStudent.uploadAll();
								});
						}
					 $scope.uploadStudent.onCompleteAll = function() {
			               alert("Upload Completed");
			            };
					 
					 $scope.getSchoolList = function() {
							$scope.schoolList = [];
							$scope.schoolBranchList = [];
							$scope.date = new Date();
							$http({
								method : "GET",
								url : $rootScope.baseUrl + 'admin/getSchoolList'
							}).success(function(data, status, headers, config) {
								$scope.schoolList = data;
							}).error(function(data, status, headers, config) {
								$window.alert("Something went wrong");
							});
						};

					$scope.getSchoolList();
					
					$scope.calculateAge = function(dob){
						var birthDate = new Date(dob);
						var curDate = new Date();
						var days = Math.floor((curDate-birthDate)/(1000*60*60*24));
						var ageYears = Math.floor(days/365);
						var ageMonths = Math.floor((days%365)/31);
						var yrs = ((curDate-birthDate) / (1000 * 60 * 60 * 24)) / 365.25;
						$scope.exactYear = parseFloat(yrs).toFixed(1);
						var age = "" + ageYears + " years and " + ageMonths + (ageMonths > 1 ? " months." : " month.");
						
						var mon = Math.round(ageMonths);
						if(mon < 10){
							mon = '0'+mon;
						}
						age = ageYears+'.'+mon;
						$scope.user.studentsHealth.age = age;
						
						$scope.calculateBMI();
					}
					
					$scope.calculateBMI = function(){
						var dob = $scope.user.dob;
				        var bmiValue; 
				        var weight = $scope.user.studentsHealth.weight;
				        var height = $scope.user.studentsHealth.height;
				        var gender = $scope.user.gender;
				        var weightUnit = $scope.user.studentsHealth.weightUnit; 
				        var heightUnit = $scope.user.studentsHealth.heightUnit;
				        
				        if(heightUnit === "Centimeter"){
				        	height = height * 0.01;
				        }
				        else if(heightUnit === "Inches"){
				        	height = height * 0.0254;
				        }
				        else if(heightUnit === "Feet"){
				        	height = height * 0.3048;
				        }
				        if(weightUnit === "LBS"){
				        	weight = weight/ 2.2;
				        }
				        if (weight > 0 && height > 0) {
				            var finalBmi = weight / (height * height);
				            bmiValue = parseFloat(finalBmi).toFixed(2);
				            $scope.user.studentsHealth.bmi = bmiValue;
				            if(dob!=null){
				            	$scope.user.studentsHealth.ibw = $scope.getIbw(gender, $scope.exactYear);
				            	$scope.user.studentsHealth.bmiPercentile =$scope.getBMIPercentile(dob, bmiValue, gender);
				            }
				        } 
				        else {
				        	$scope.user.studentsHealth.bmi = 0;
				        }
					}
					
					$scope.getIbw = function(gender, exactYear) {
				        if (exactYear >= 0 && exactYear < 1) return "Not Applicable";
				        if (exactYear >= 1 && exactYear < 1.5) return "9.5 - 10.2 kgs";
				        if (exactYear >= 1.5 && exactYear < 2) return "10.8 - 11.5 kgs";
				        if (exactYear >= 2 && exactYear < 2.5) return "11.8 - 12.3 kgs";
				        if (exactYear >= 2.5 && exactYear < 3) return "13 - 13.5 kgs";
				        if (exactYear >= 3 && exactYear < 3.5) return "14.1 - 15.7 kgs";
				        if (exactYear >= 3.5 && exactYear < 4) return "15.1 - 15.7 kgs";
				        if (exactYear >= 4 && exactYear < 4.5) return "16 - 16.7 kgs";
				        if (exactYear >= 4.5 && exactYear < 5) return "16.8 - 17.7 kgs";
				        if (exactYear >= 5 && exactYear < 5.5) return "17.7 - 18.7 kgs";
				        if (exactYear >= 5.5 && exactYear < 6) return "18.6 - 19.7";
				        if (exactYear >= 6 && exactYear < 6.5) return "19.5 - 20.7 kgs";
				        if (exactYear >= 6.5 && exactYear < 7) return "20.6 - 21.7 kgs";
				        if (exactYear >= 7 && exactYear < 7.5) return "21.8 - 22.9 kgs";
				        if (exactYear >= 7.5 && exactYear < 8) return "23.3 - 24 kgs";
				        if (exactYear >= 8 && exactYear < 9) return "24.8 - 25.3 kgs";
				        if ("Male" == gender) {
				            if (exactYear >= 9 && exactYear < 10) return "28 kgs";
				            if (exactYear >= 10 && exactYear < 11) return "30.8 kgs";
				            if (exactYear >= 11 && exactYear < 12) return "34.1 kgs";
				            if (exactYear >= 12 && exactYear < 13) return "38 kgs";
				            if (exactYear >= 13 && exactYear < 14) return "43.3 kgs";
				            if (exactYear >= 14 && exactYear < 15) return "48 kgs";
				            if (exactYear >= 15 && exactYear < 16) return "51.5 kgs";
				            if (exactYear >= 16 && exactYear < 17) return "54.3 kgs";
				            if (exactYear >= 17 && exactYear < 18) return "56.5 kgs";
				            if (exactYear >= 18) return "58.4 kgs"
				        } else {
				            if ("Female" != gender) return "Not Applicable";
				            if (exactYear >= 9 && exactYear < 10) return "27.6 kgs";
				            if (exactYear >= 10 && exactYear < 11) return "31.2 kgs";
				            if (exactYear >= 11 && exactYear < 12) return "34.8 kgs";
				            if (exactYear >= 12 && exactYear < 13) return "39 kgs";
				            if (exactYear >= 13 && exactYear < 14) return "43.4 kgs";
				            if (exactYear >= 14 && exactYear < 15) return "47.1 kgs";
				            if (exactYear >= 15 && exactYear < 16) return "49.4 kgs";
				            if (exactYear >= 16 && exactYear < 17) return "51.3 kgs";
				            if (exactYear >= 17 && exactYear < 18) return "52.8 kgs";
				            if (exactYear >= 18) return "53.8 kgs"
				        }
				    }
					
					$scope.getBMIPercentile = function (dob, bmiValue, gender) {
				        var bmiLMS = new Array(3);
				        var age = $scope.exactYear
				        if ( 0 !== bmiValue && 0 !== age) {
				            bmiLMS = "Female" == gender ? $scope.fLMS(age) : $scope.mLMS(age);
				            var pP, pcnt, L = bmiLMS[0],
				                M = bmiLMS[1],
				                S = bmiLMS[2],
				                z = (Math.pow(bmiValue / M, L) - 1) / (L * S);
				            pP = 1 - 1 / Math.sqrt(6.2831853) * Math.exp(-Math.pow(Math.abs(z), 2) / 2) * (.4361836 * (1 / (1 + .33267 * Math.abs(z))) - .1201676 * Math.pow(1 / (1 + .33267 * Math.abs(z)), 2) + .937298 * Math.pow(1 / (1 + .33267 * Math.abs(z)), 3)), pcnt = z > 0 ? 100 * pP : 100 - 100 * pP, z = Math.round(100 * z) / 100, pcnt = Math.round(100 * pcnt) / 100;
				            var pcntout = Math.round(10 * pcnt) / 10;
				            return pcntout;
				        }
				        return 0;
				    }
					
					$scope.mLMS = function (a) {
				        var b = Array(3);
				        a *= 12;
				        var c = Math.round(a),
				            d = c - 24,
				            e = c - 23;
				        return a = a - c + .5, b[0] = a * malL[e] + (1 - a) * malL[d], b[1] = a * malM[e] + (1 - a) * malM[d], b[2] = a * malS[e] + (1 - a) * malS[d], b
				    }

				    $scope.fLMS = function (a) {
				        var b = Array(3);
				        a *= 12;
				        var c = Math.round(a),
				            d = c - 24,
				            e = c - 23;
				        return a = a - c + .5, b[0] = a * femL[e] + (1 - a) * femL[d], b[1] = a * femM[e] + (1 - a) * femM[d], b[2] = a * femS[e] + (1 - a) * femS[d], b
				    }
					
					$scope.getSchoolBranch = function(schoolId) {
						
						$scope.schoolBranchList = [];
						
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/getSchoolBranchList/'+ schoolId
						}).success(function(data, status, headers, config) {
							$scope.schoolBranchList = data;
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					};
					
						$scope.getClassList = function(branchId) {
						
						$scope.schoolClassList = [];
						
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/getClassList/'+ branchId
						}).success(function(data, status, headers, config) {
							$scope.schoolClassList = data;
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					};
					

					$scope.getSchoolBranch($scope.user.schoolId);
					$scope.getClassList($scope.user.branchId);

					// function to submit the form after all validation has
					// occurred
					$scope.submitForm = function(isValid) {
						console.log('validate form');
						var user = this.user;
						user['profileImage'] = 'default.png';
						$.each(uploader.queue, function( index, item ) {
							user['profileImage'] = item.file.name;
							uploader.uploadAll();
							});
						// check to make sure the form is completely valid
						$http({
							method : "POST",
							url : $rootScope.baseUrl + 'admin/editStudent',
							data : JSON.stringify(user)
						})
								.success(
										function(data, status, headers, config) {
											$state
													.go('administrator.manageStudent.viewStudentList');
										})
								.error(
										function(data, status, headers, config) {
											$window
													.alert("Something went wrong while adding students");
										});

					};

				})
				
						// Admin add Students
		.controller(
				'AddStudentCtrl',
				function($scope, $http, $state, $window,$rootScope,FileUploader) {
					$scope.page = {
						title : 'Add Student',
						subtitle : 'Place subtitle here...'
					};
					 $scope.gPlace;
					 
					 var uploader= $scope.uploader = new FileUploader({
				            url: $rootScope.baseUrl + 'UploadDownloadFileService/uploadProfilePic'
				        });
					 uploader.onAfterAddingFile = function(item) {
				            console.log('onAfterAddingFile', item);
				            var fileExtension = '.' + item.file.name.split('.').pop();
							item.file.name = Math.random().toString(36).substring(7) + new Date().getTime() + fileExtension;
				          };
					 
				     var uploaderStudent= $scope.uploaderStudent = new FileUploader({
					            url: $rootScope.baseUrl + 'UploadDownloadFileService/uploadStudentData'
					        });
				    
				       uploaderStudent.onCompleteAll = function() {
				         console.info('onCompleteAll');
				         alert("File upload completed");
				       };
					 var malL = [-2.01118107, -1.982373595, -1.924100169, -1.86549793, -1.807261899, -1.750118905, -1.69481584, -1.642106779, -1.592744414, -1.547442391, -1.506902601, -1.471770047, -1.442628957, -1.419991255, -1.404277619, -1.39586317, -1.394935252, -1.401671596, -1.416100312, -1.438164899, -1.467669032, -1.504376347, -1.547942838, -1.597896397, -1.653732283, -1.714869347, -1.780673181, -1.850468473, -1.923551865, -1.999220429, -2.076707178, -2.155348017, -2.234438552, -2.313321723, -2.391381273, -2.468032491, -2.542781541, -2.61516595, -2.684789516, -2.751316949, -2.81445945, -2.87402476, -2.92984048, -2.981796828, -3.029831343, -3.073924224, -3.114093476, -3.15039004, -3.182893018, -3.21170511, -3.23694834, -3.25876011, -3.277281546, -3.292683774, -3.305124073, -3.314768951, -3.321785992, -3.326345795, -3.328602731, -3.328725277, -3.32687018, -3.323188896, -3.317827016, -3.310923871, -3.302612272, -3.293018361, -3.282260813, -3.270454609, -3.257703616, -3.244108214, -3.229761713, -3.214751287, -3.199158184, -3.18305795, -3.166520664, -3.1496103, -3.132389637, -3.114911153, -3.097226399, -3.079383079, -3.061423765, -3.043386071, -3.025310003, -3.007225737, -2.989164598, -2.971148225, -2.953208047, -2.935363951, -2.917635157, -2.900039803, -2.882593796, -2.865311266, -2.848204697, -2.831285052, -2.81456189, -2.79804347, -2.781736856, -2.765648008, -2.749782197, -2.734142443, -2.718732873, -2.703555506, -2.688611957, -2.673903164, -2.659429443, -2.645190534, -2.631185649, -2.617413511, -2.603872392, -2.590560148, -2.577474253, -2.564611831, -2.551969684, -2.539539972, -2.527325681, -2.515320235, -2.503519447, -2.491918934, -2.480514136, -2.469300331, -2.458272656, -2.447426113, -2.436755595, -2.426255887, -2.415921689, -2.405747619, -2.395728233, -2.385858029, -2.376131459, -2.366542942, -2.357086871, -2.347757625, -2.338549576, -2.3294571, -2.320474586, -2.311596446, -2.302817124, -2.294131107, -2.285532933, -2.277017201, -2.268578584, -2.260211837, -2.251911809, -2.243673453, -2.235491842, -2.227362173, -2.21927979, -2.211240187, -2.203239029, -2.195272161, -2.187335625, -2.179425674, -2.171538789, -2.163671689, -2.155821357, -2.147985046, -2.140160305, -2.132344989, -2.124537282, -2.116735712, -2.108939167, -2.10114692, -2.093358637, -2.085574403, -2.077794735, -2.070020599, -2.062253431, -2.054495145, -2.046748156, -2.039015385, -2.031300282, -2.023606828, -2.015942013, -2.008305745, -2.000706389, -1.993150137, -1.985643741, -1.97819451, -1.970810308, -1.96349954, -1.956271141, -1.949134561, -1.942099744, -1.935177101, -1.92837748, -1.921712136, -1.915192685, -1.908831065, -1.902639482, -1.896630358, -1.890816268, -1.885209876, -1.879823505, -1.874670324, -1.869760299, -1.865113245, -1.860734944, -1.85663384, -1.852827186, -1.849323204, -1.846131607, -1.843261294, -1.840720248, -1.83851544, -1.83665586, -1.835138046, -1.833972004, -1.833157751, -1.83269562, -1.832584342, -1.832820974, -1.833400825, -1.834317405, -1.83555752, -1.837119466, -1.838987063, -1.841146139, -1.843580575],
				        malM = [16.57502768, 16.54777487, 16.49442763, 16.44259552, 16.3922434, 16.34333654, 16.29584097, 16.24972371, 16.20495268, 16.16149871, 16.11933258, 16.07842758, 16.03875896, 16.00030401, 15.96304277, 15.92695418, 15.89202582, 15.85824093, 15.82558822, 15.79405728, 15.76364255, 15.73433668, 15.70613566, 15.67904062, 15.65305192, 15.62817269, 15.604408, 15.58176458, 15.56025067, 15.5398746, 15.52064993, 15.50258427, 15.48568973, 15.46997718, 15.45545692, 15.44213961, 15.43003207, 15.41914163, 15.40947356, 15.40103139, 15.39381785, 15.38783094, 15.38306945, 15.37952958, 15.37720582, 15.37609107, 15.37617677, 15.37745304, 15.37990886, 15.38353217, 15.38831005, 15.39422883, 15.40127496, 15.40943252, 15.41868691, 15.42902273, 15.44042439, 15.45287581, 15.46636218, 15.48086704, 15.49637465, 15.51286936, 15.53033563, 15.54875807, 15.56812143, 15.58841065, 15.60961101, 15.63170735, 15.65468563, 15.67853139, 15.70323052, 15.72876911, 15.75513347, 15.78231007, 15.8102856, 15.83904708, 15.86858123, 15.89887562, 15.92991765, 15.96169481, 15.99419489, 16.02740607, 16.0613159, 16.09591292, 16.13118532, 16.16712234, 16.20371168, 16.24094239, 16.27880346, 16.31728385, 16.35637267, 16.39605916, 16.43633265, 16.47718256, 16.51859843, 16.56056987, 16.60308661, 16.64613844, 16.68971518, 16.73380695, 16.77840363, 16.82349538, 16.86907238, 16.91512487, 16.96164317, 17.00861766, 17.05603879, 17.10389705, 17.15218302, 17.20088732, 17.25000062, 17.29951367, 17.34941726, 17.39970308, 17.45036072, 17.50138161, 17.55275674, 17.60447714, 17.6565339, 17.70891811, 17.76162094, 17.81463359, 17.86794729, 17.92155332, 17.97544299, 18.02960765, 18.08403868, 18.1387275, 18.19366555, 18.24884431, 18.3042553, 18.35989003, 18.41574009, 18.47179706, 18.52805255, 18.5844982, 18.64112567, 18.69792663, 18.75489278, 18.81201584, 18.86928753, 18.92669959, 18.98424378, 19.04191185, 19.09969557, 19.15758672, 19.21557707, 19.27365839, 19.33182247, 19.39006106, 19.44836594, 19.50672885, 19.56514153, 19.62359571, 19.6820831, 19.74059538, 19.7991242, 19.85766121, 19.916198, 19.97472615, 20.03323719, 20.09172262, 20.15017387, 20.20858236, 20.26693944, 20.32523642, 20.38346455, 20.44161501, 20.49967894, 20.5576474, 20.6155114, 20.67326189, 20.73088905, 20.7883851, 20.84574003, 20.90294449, 20.95998909, 21.01686433, 21.07356067, 21.1300685, 21.18637813, 21.24247982, 21.29836376, 21.35402009, 21.40943891, 21.46461026, 21.51952414, 21.57417053, 21.62853937, 21.68262062, 21.73640419, 21.78988003, 21.84303819, 21.8958685, 21.94836168, 22.00050569, 22.05229242, 22.10371305, 22.15475603, 22.20541249, 22.255673, 22.30552831, 22.3549693, 22.40398706, 22.45257182, 22.50071778, 22.54841437, 22.59565422, 22.64242956, 22.68873292, 22.73455713, 22.7798953, 22.82474087, 22.86908912, 22.91293151, 22.95626373, 22.99908062, 23.041377338],
				        malS = [.080592465, .080127429, .079233994, .078389356, .077593501, .076846462, .076148308, .075499126, .074898994, .074347997, .073846139, .07339337, .072989551, .072634432, .072327649, .07206864, .071856805, .071691278, .071571093, .071495113, .071462106, .071470646, .071519218, .071606277, .071730167, .071889214, .072081737, .072306081, .072560637, .07284384, .073154324, .073490667, .073851672, .074236235, .074643374, .075072264, .075522104, .07599225, .076482128, .076991232, .077519149, .07806539, .078629592, .079211369, .079810334, .080426086, .081058206, .081706249, .082369741, .083048178, .083741021, .0844477, .085167651, .085900184, .086644667, .087400421, .088166744, .088942897, .089728202, .090521875, .091323162, .092131305, .092945544, .093765118, .09458927, .095417247, .096248301, .097081694, .097916698, .098752593, .099588675, .100424251, .101258643, .102091189, .102921245, .103748189, .104571386, .105390269, .106204258, .107012788, .107815327, .108611374, .109400388, .110181915, .110955478, .111720691, .112477059, .1132242, .113961734, .114689291, .115406523, .116113097, .116808702, .117493042, .11816584, .118826835, .119475785, .120112464, .120736656, .121348181, .121946849, .122532501, .123104991, .123664186, .124209969, .124742239, .125260905, .125765895, .126257147, .126734613, .12719826, .127648067, .128084023, .128506192, .128914497, .129309001, .129689741, .130056765, .130410133, .130749913, .131076187, .131389042, .131688579, .131974905, .132248138, .132508403, .132755834, .132990575, .133212776, .133422595, .133620197, .133805756, .133979452, .13414147, .134292005, .134431256, .134559427, .134676731, .134783385, .134879611, .134965637, .135041695, .135108024, .135164867, .135212469, .135251083, .135280963, .135302371, .135315568, .135320824, .135318407, .135308594, .135291662, .135267891, .135237567, .135200976, .135158409, .135110159, .135056522, .134997797, .134934285, .134866291, .134794121, .134718085, .134638494, .134555663, .13446991, .134381553, .134290916, .134198323, .134104101, .134008581, .133912066, .133814954, .133717552, .1336202, .133523244, .133427032, .133331914, .133238245, .133146383, .13305669, .132969531, .132885274, .132804292, .132726962, .132653664, .132584784, .132520711, .132461838, .132408563, .132361289, .132320427, .132286382, .1322596, .132240418, .13222933, .132226801, .132233201, .132248993, .132274625, .132310549, .132357221, .132415103, .132484631, .132566359, .132660699, .132768153, .132889211, .133024368, .133174129, .133338999, .133519496, .133716192, .133929525, .134160073, .134408381, .1346750014],
				        femL = [-.98660853, -1.024496827, -1.102698353, -1.18396635, -1.268071036, -1.354751525, -1.443689692, -1.53454192, -1.626928093, -1.720434829, -1.814635262, -1.909076262, -2.003296102, -2.096828937, -2.189211877, -2.279991982, -2.368732949, -2.455021314, -2.538471972, -2.618732901, -2.695488973, -2.768464816, -2.837426693, -2.902178205, -2.962580386, -3.018521987, -3.069936555, -3.116795864, -3.159107331, -3.196911083, -3.230276759, -3.259300182, -3.284099963, -3.30481415, -3.321596954, -3.334615646, -3.344047622, -3.35007771, -3.352893805, -3.352691376, -3.34966438, -3.343998803, -3.335889574, -3.325522491, -3.31307846, -3.298732648, -3.282653831, -3.265003896, -3.245937506, -3.225606516, -3.204146115, -3.181690237, -3.158363475, -3.134282833, -3.109557879, -3.084290931, -3.058577292, -3.032505499, -3.0061576, -2.979609448, -2.952930993, -2.926186592, -2.899435307, -2.872731211, -2.846123683, -2.819657704, -2.793374145, -2.767310047, -2.741498897, -2.715970894, -2.690753197, -2.665870146, -2.641343436, -2.617192204, -2.593430614, -2.570076037, -2.547141473, -2.524635245, -2.502569666, -2.48095189, -2.459785573, -2.439080117, -2.418838304, -2.399063683, -2.379756861, -2.360920527, -2.342557728, -2.324663326, -2.307240716, -2.290287663, -2.273803847, -2.257782149, -2.242227723, -2.227132805, -2.212495585, -2.19831275, -2.184580762, -2.171295888, -2.158454232, -2.146051754, -2.134084303, -2.122547629, -2.111437411, -2.100749266, -2.090478774, -2.080621484, -2.071172932, -2.062128649, -2.053484173, -2.045235058, -2.03737688, -2.029906684, -2.022817914, -2.016107084, -2.009769905, -2.003802134, -1.998199572, -1.992958064, -1.988073505, -1.983541835, -1.979359041, -1.975521156, -1.972024258, -1.968864465, -1.966037938, -1.963540872, -1.961369499, -1.959520079, -1.9579889, -1.956772271, -1.95586652, -1.955267984, -1.954973011, -1.954977947, -1.955279136, -1.955872909, -1.956755579, -1.957923436, -1.959372737, -1.9610997, -1.963100496, -1.96537124, -1.967907983, -1.970706706, -1.973763307, -1.977073595, -1.980633277, -1.984437954, -1.988483106, -1.992764085, -1.997276103, -2.002014224, -2.00697335, -2.012148213, -2.017533363, -2.023123159, -2.028911755, -2.034893091, -2.041060881, -2.047408604, -2.05392949, -2.060616513, -2.067462375, -2.074459502, -2.081600029, -2.088875793, -2.096278323, -2.103798828, -2.111428194, -2.119156972, -2.126975375, -2.134873266, -2.142840157, -2.150865204, -2.158937201, -2.167044578, -2.175176987, -2.183317362, -2.191457792, -2.199583649, -2.207681525, -2.215737645, -2.223739902, -2.231667995, -2.239511942, -2.247257081, -2.254885145, -2.26238209, -2.269731517, -2.276917229, -2.283925442, -2.290731442, -2.29732427, -2.303687802, -2.309799971, -2.315651874, -2.32121731, -2.326481911, -2.331428139, -2.336038473, -2.34029545, -2.344181703, -2.34768, -2.350773286, -2.353444725, -2.355677743, -2.35745607, -2.358763788, -2.359585369, -2.359905726, -2.359710258, -2.358980464, -2.357714508, -2.355892424, -2.353501353, -2.350528726, -2.346962247, -2.342796948],
				        femM = [16.42339664, 16.38804056, 16.3189719, 16.25207985, 16.18734669, 16.12475448, 16.06428762, 16.00593001, 15.94966631, 15.89548197, 15.84336179, 15.79329146, 15.7452564, 15.69924188, 15.65523282, 15.61321371, 15.57316843, 15.53508019, 15.49893145, 15.46470384, 15.43237817, 15.40193436, 15.37335154, 15.34660842, 15.32168181, 15.29854897, 15.27718618, 15.2575692, 15.23967338, 15.22347371, 15.20894491, 15.19606152, 15.18479799, 15.17512871, 15.16702811, 15.16047068, 15.15543107, 15.15188405, 15.14980479, 15.14916825, 15.14994984, 15.15212585, 15.15567186, 15.16056419, 15.16677947, 15.17429464, 15.18308694, 15.1931339, 15.20441335, 15.21690296, 15.2305815, 15.24542745, 15.26141966, 15.27853728, 15.29675967, 15.31606644, 15.33643745, 15.35785274, 15.38029261, 15.40373754, 15.42816819, 15.45356545, 15.47991037, 15.50718419, 15.53536829, 15.56444426, 15.5943938, 15.6251988, 15.65684126, 15.68930333, 15.7225673, 15.75661555, 15.79143062, 15.82699517, 15.86329241, 15.90030484, 15.93801545, 15.97640787, 16.01546483, 16.05516984, 16.09550688, 16.13645881, 16.17800955, 16.22014281, 16.26284277, 16.30609316, 16.34987759, 16.39418118, 16.43898741, 16.48428082, 16.53004554, 16.57626713, 16.62292864, 16.67001572, 16.71751288, 16.76540496, 16.81367689, 16.86231366, 16.91130036, 16.96062216, 17.0102643, 17.06021213, 17.11045106, 17.16096656, 17.21174424, 17.26276973, 17.31402878, 17.3655072, 17.4171909, 17.46906585, 17.52111811, 17.57333347, 17.62569869, 17.67819987, 17.7308234, 17.78355575, 17.83638347, 17.88929321, 17.94227168, 17.9953057, 18.04838216, 18.10148804, 18.15461039, 18.20773639, 18.26085325, 18.31394832, 18.36700902, 18.42002284, 18.47297739, 18.52586035, 18.57865951, 18.63136275, 18.68395801, 18.73643338, 18.788777, 18.84097713, 18.89302212, 18.94490041, 18.99660055, 19.04811118, 19.09942105, 19.15051899, 19.20139397, 19.25203503, 19.30243131, 19.35257209, 19.40244671, 19.45204465, 19.50135548, 19.55036888, 19.59907464, 19.64746266, 19.69552294, 19.7432456, 19.79062086, 19.83763907, 19.88429066, 19.9305662, 19.97645636, 20.02195192, 20.06704377, 20.11172291, 20.15598047, 20.19980767, 20.24319586, 20.28613648, 20.32862109, 20.37064138, 20.41218911, 20.45325617, 20.49383457, 20.5339164, 20.57349387, 20.61255929, 20.65110506, 20.6891237, 20.72660728, 20.76355011, 20.79994337, 20.83578051, 20.87105449, 20.90575839, 20.93988477, 20.97342858, 21.00638171, 21.0387374, 21.07048996, 21.10163241, 21.13215845, 21.16206171, 21.1913351, 21.21997472, 21.24797262, 21.27532239, 21.30201933, 21.32805489, 21.35342563, 21.37812462, 21.40214589, 21.42548351, 21.44813156, 21.47008412, 21.49133529, 21.51187918, 21.53170989, 21.55082155, 21.56920824, 21.58686406, 21.60378309, 21.61995939, 21.635387, 21.65006126, 21.6639727, 21.67711736, 21.68948935, 21.70108288, 21.71189225, 21.721909734],
				        femS = [.085451785, .085025838, .084214052, .083455124, .082748284, .082092737, .081487717, .080932448, .080426175, .079968176, .079557735, .079194187, .078876895, .078605255, .078378696, .078196674, .078058667, .077964169, .077912684, .077903716, .077936763, .078011309, .078126817, .078282739, .078478449, .078713325, .078986694, .079297841, .079646006, .080030389, .080450145, .080904391, .081392203, .081912623, .082464661, .083047295, .083659478, .084300139, .0849682, .085662539, .086382035, .087125591, .087892047, .088680264, .089489106, .090317434, .091164117, .092028028, .092908048, .093803033, .094711916, .095633595, .096566992, .097511046, .09846471, .099426955, .100396769, .101373159, .10235515, .103341788, .104332139, .105325289, .106320346, .10731644, .108312721, .109308364, .110302563, .111294537, .112283526, .113268793, .114249622, .115225321, .116195218, .117158667, .118115073, .119063807, .12000429, .120935994, .121858355, .12277087, .123673085, .124564484, .125444639, .126313121, .127169545, .128013515, .128844639, .129662637, .130467138, .131257852, .132034479, .132796819, .133544525, .134277436, .134995324, .135697996, .136385276, .137057004, .137713039, .138353254, .138977537, .139585795, .140177947, .140753927, .141313686, .141857186, .142384404, .142895332, .143389972, .143868341, .144330469, .144776372, .145206138, .145619819, .146017491, .146399239, .146765161, .147115364, .147449967, .147769097, .148072891, .148361495, .148635067, .148893769, .149137776, .14936727, .149582439, .149783482, .149970604, .15014402, .15030395, .150450621, .15058427, .150705138, .150813475, .150909535, .150993582, .151065883, .151126714, .151176355, .151215094, .151243223, .151261042, .151268855, .151266974, .151255713, .151235395, .151206347, .151168902, .151123398, .15107018, .151009595, .150942, .150867753, .150787221, .150700774, .150608788, .150511645, .150409731, .15030344, .150193169, .150079322, .149962308, .14984254, .149720441, .149596434, .149470953, .149344433, .149217319, .14909006, .14896311, .148836931, .148711989, .148588757, .148467715, .148349348, .14823412, .148122614, .148015249, .147912564, .147815078, .147723315, .147637768, .147559083, .147487716, .14742421, .147369174, .147323144, .147286698, .147260415, .147244828, .147240683, .147248467, .14726877, .147302299, .147349514, .147411215, .147487979, .147580453, .147689289, .14781515, .147958706, .148120633, .148301619, .148502355, .148723546, .148965902, .149230142, .149516994, .149827195, .150161492, .150520734, .150905439, .151316531, .151754808, .152221086, .152716206, .1532408716];
				   
					 
					 $scope.user = {
							 	studentsHealth: {
							 		age: 0,
							 		bmi: 0,
							 		ibw: 0,
							 		bmiPercentile: 0,
							 		height: 0,
							 		weight: 0,
							 		weightUnit: "Kilograms",
							 		heightUnit: "Centimeter"
							 			
							 	},
							 	gender: "Male",
							 	dob: null,
							 	active: true
							};
					 
					 $scope.uploadStudent = function(){
						 $.each(uploaderStudent.queue, function( index, item ) {
									uploaderStudent.uploadAll();
								});
						}
					 
					 
					 $scope.getSchoolList = function() {
							$scope.schoolList = [];
							$scope.schoolBranchList = [];
							$scope.date = new Date();
							$http({
								method : "GET",
								url : $rootScope.baseUrl + 'admin/getSchoolList'
							}).success(function(data, status, headers, config) {
								$scope.schoolList = data;
							}).error(function(data, status, headers, config) {
								$window.alert("Something went wrong");
							});
						};

					$scope.getSchoolList();
					
					$scope.calculateAge = function(dob){
						var birthDate = new Date(dob);
						var curDate = new Date();
						var days = Math.floor((curDate-birthDate)/(1000*60*60*24));
						var ageYears = Math.floor(days/365);
						var ageMonths = Math.floor((days%365)/31);
						var yrs = ((curDate-birthDate) / (1000 * 60 * 60 * 24)) / 365.25;
						$scope.exactYear = parseFloat(yrs).toFixed(1);
						var age = "" + ageYears + " years and " + ageMonths + (ageMonths > 1 ? " months." : " month.");
						
						var mon = Math.round(ageMonths);
						if(mon < 10){
							mon = '0'+mon;
						}
						age = ageYears+'.'+mon;
						$scope.user.studentsHealth.age = age;
						
						$scope.calculateBMI();
					}
					
					$scope.calculateBMI = function(){
						var dob = $scope.user.dob;
				        var bmiValue; 
				        var weight = $scope.user.studentsHealth.weight;
				        var height = $scope.user.studentsHealth.height;
				        var gender = $scope.user.gender;
				        var weightUnit = $scope.user.studentsHealth.weightUnit; 
				        var heightUnit = $scope.user.studentsHealth.heightUnit;
				        
				        if(heightUnit === "Centimeter"){
				        	height = height * 0.01;
				        }
				        else if(heightUnit === "Inches"){
				        	height = height * 0.0254;
				        }
				        else if(heightUnit === "Feet"){
				        	height = height * 0.3048;
				        }
				        if(weightUnit === "LBS"){
				        	weight = weight/ 2.2;
				        }
				        if (weight > 0 && height > 0) {
				            var finalBmi = weight / (height * height);
				            bmiValue = parseFloat(finalBmi).toFixed(2);
				            $scope.user.studentsHealth.bmi = bmiValue;
				            if(dob!=null){
				            	$scope.user.studentsHealth.ibw = $scope.getIbw(gender, $scope.exactYear);
				            	$scope.user.studentsHealth.bmiPercentile =$scope.getBMIPercentile(dob, bmiValue, gender);
				            }
				        } 
				        else {
				        	$scope.user.studentsHealth.bmi = 0;
				        }
					}
					
					$scope.getIbw = function(gender, exactYear) {
				        if (exactYear >= 0 && exactYear < 1) return "Not Applicable";
				        if (exactYear >= 1 && exactYear < 1.5) return "9.5 - 10.2 kgs";
				        if (exactYear >= 1.5 && exactYear < 2) return "10.8 - 11.5 kgs";
				        if (exactYear >= 2 && exactYear < 2.5) return "11.8 - 12.3 kgs";
				        if (exactYear >= 2.5 && exactYear < 3) return "13 - 13.5 kgs";
				        if (exactYear >= 3 && exactYear < 3.5) return "14.1 - 15.7 kgs";
				        if (exactYear >= 3.5 && exactYear < 4) return "15.1 - 15.7 kgs";
				        if (exactYear >= 4 && exactYear < 4.5) return "16 - 16.7 kgs";
				        if (exactYear >= 4.5 && exactYear < 5) return "16.8 - 17.7 kgs";
				        if (exactYear >= 5 && exactYear < 5.5) return "17.7 - 18.7 kgs";
				        if (exactYear >= 5.5 && exactYear < 6) return "18.6 - 19.7";
				        if (exactYear >= 6 && exactYear < 6.5) return "19.5 - 20.7 kgs";
				        if (exactYear >= 6.5 && exactYear < 7) return "20.6 - 21.7 kgs";
				        if (exactYear >= 7 && exactYear < 7.5) return "21.8 - 22.9 kgs";
				        if (exactYear >= 7.5 && exactYear < 8) return "23.3 - 24 kgs";
				        if (exactYear >= 8 && exactYear < 9) return "24.8 - 25.3 kgs";
				        if ("Male" == gender) {
				            if (exactYear >= 9 && exactYear < 10) return "28 kgs";
				            if (exactYear >= 10 && exactYear < 11) return "30.8 kgs";
				            if (exactYear >= 11 && exactYear < 12) return "34.1 kgs";
				            if (exactYear >= 12 && exactYear < 13) return "38 kgs";
				            if (exactYear >= 13 && exactYear < 14) return "43.3 kgs";
				            if (exactYear >= 14 && exactYear < 15) return "48 kgs";
				            if (exactYear >= 15 && exactYear < 16) return "51.5 kgs";
				            if (exactYear >= 16 && exactYear < 17) return "54.3 kgs";
				            if (exactYear >= 17 && exactYear < 18) return "56.5 kgs";
				            if (exactYear >= 18) return "58.4 kgs"
				        } else {
				            if ("Female" != gender) return "Not Applicable";
				            if (exactYear >= 9 && exactYear < 10) return "27.6 kgs";
				            if (exactYear >= 10 && exactYear < 11) return "31.2 kgs";
				            if (exactYear >= 11 && exactYear < 12) return "34.8 kgs";
				            if (exactYear >= 12 && exactYear < 13) return "39 kgs";
				            if (exactYear >= 13 && exactYear < 14) return "43.4 kgs";
				            if (exactYear >= 14 && exactYear < 15) return "47.1 kgs";
				            if (exactYear >= 15 && exactYear < 16) return "49.4 kgs";
				            if (exactYear >= 16 && exactYear < 17) return "51.3 kgs";
				            if (exactYear >= 17 && exactYear < 18) return "52.8 kgs";
				            if (exactYear >= 18) return "53.8 kgs"
				        }
				    }
					
					$scope.getBMIPercentile = function (dob, bmiValue, gender) {
				        var bmiLMS = new Array(3);
				        var age = $scope.exactYear
				        if ( 0 !== bmiValue && 0 !== age) {
				            bmiLMS = "Female" == gender ? $scope.fLMS(age) : $scope.mLMS(age);
				            var pP, pcnt, L = bmiLMS[0],
				                M = bmiLMS[1],
				                S = bmiLMS[2],
				                z = (Math.pow(bmiValue / M, L) - 1) / (L * S);
				            pP = 1 - 1 / Math.sqrt(6.2831853) * Math.exp(-Math.pow(Math.abs(z), 2) / 2) * (.4361836 * (1 / (1 + .33267 * Math.abs(z))) - .1201676 * Math.pow(1 / (1 + .33267 * Math.abs(z)), 2) + .937298 * Math.pow(1 / (1 + .33267 * Math.abs(z)), 3)), pcnt = z > 0 ? 100 * pP : 100 - 100 * pP, z = Math.round(100 * z) / 100, pcnt = Math.round(100 * pcnt) / 100;
				            var pcntout = Math.round(10 * pcnt) / 10;
				            return pcntout;
				        }
				        return 0;
				    }
					
					$scope.mLMS = function (a) {
				        var b = Array(3);
				        a *= 12;
				        var c = Math.round(a),
				            d = c - 24,
				            e = c - 23;
				        return a = a - c + .5, b[0] = a * malL[e] + (1 - a) * malL[d], b[1] = a * malM[e] + (1 - a) * malM[d], b[2] = a * malS[e] + (1 - a) * malS[d], b
				    }

				    $scope.fLMS = function (a) {
				        var b = Array(3);
				        a *= 12;
				        var c = Math.round(a),
				            d = c - 24,
				            e = c - 23;
				        return a = a - c + .5, b[0] = a * femL[e] + (1 - a) * femL[d], b[1] = a * femM[e] + (1 - a) * femM[d], b[2] = a * femS[e] + (1 - a) * femS[d], b
				    }
					
					$scope.getSchoolBranch = function(schoolId) {
						
						$scope.schoolBranchList = [];
						
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/getSchoolBranchList/'+ schoolId
						}).success(function(data, status, headers, config) {
							$scope.schoolBranchList = data;
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					};
					
						$scope.getClassList = function(branchId) {
						
						$scope.schoolClassList = [];
						
						$http({
							method : "GET",
							url : $rootScope.baseUrl + 'admin/getClassList/'+ branchId
						}).success(function(data, status, headers, config) {
							$scope.schoolClassList = data;
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					};

					// function to submit the form after all validation has
					// occurred
					$scope.submitForm = function(isValid) {
						console.log('validate form');
						var user = this.user;
						user['profileImage'] = 'default.png';
						$.each(uploader.queue, function( index, item ) {
							user['profileImage'] = item.file.name;
							uploader.uploadAll();
							});
						// check to make sure the form is completely valid
						$http({
							method : "POST",
							url : $rootScope.baseUrl + 'admin/addStudent',
							data : JSON.stringify(user)
						})
								.success(
										function(data, status, headers, config) {
											$state
													.go('administrator.manageStudent.viewStudentList');
										})
								.error(
										function(data, status, headers, config) {
											$window
													.alert("Something went wrong while adding students");
										});

					};

				})

		// Admin add Nutritionist
		.controller(
				'addNutritionistCtrl',
				function($scope, $http, $state, $window,$rootScope) {
					$scope.page = {
						title : 'Add Nutritionist',
						subtitle : 'Place subtitle here...'
					};
					$scope.myImage = '';
					$scope.myCroppedImage = '';
					$scope.cropType = 'square';

					var handleFileSelect = function(evt) {
						var file = evt.currentTarget.files[0];
						var reader = new FileReader();
						reader.onload = function(evt) {
							$scope.$apply(function($scope) {
								$scope.myImage = evt.target.result;
							});
						};
						reader.readAsDataURL(file);
					};
					angular.element(document.querySelector('#fileInput')).on(
							'change', handleFileSelect);

					// function to submit the form after all validation has
					// occurred
					$scope.submitForm = function(isValid) {
						console.log('validate form');
						var user = this.user;
						// check to make sure the form is completely valid
						$http({
							method : "POST",
							url : $rootScope.baseUrl + 'admin/addNutritionist',
							data : JSON.stringify(user)
						})
								.success(
										function(data, status, headers, config) {
											$state
													.go('administrator.manageNutritionist.viewNutritionistList');
										})
								.error(
										function(data, status, headers, config) {
											$window
													.alert("Something went wrong while adding Nutritionist");
										});

					};

					$scope.notBlackListed = function(value) {
						var blacklist = [ 'bad@domain.com',
								'verybad@domain.com' ];
						return blacklist.indexOf(value) === -1;
					};
				})

		.controller('viewSchoolListCtrl', function($scope) {
			$scope.page = {
				title : 'View School List',
				subtitle : 'Place subtitle here...'
			};

		})
		// dataTable for list of schools
		.controller(
				'ViewSchoolListDataCtrl',
				function($scope, $compile, DTOptionsBuilder, DTColumnBuilder,$rootScope) {

					var vm = this;
					var id = 0;
					vm.dtOptions = DTOptionsBuilder
							.fromSource(
									$rootScope.baseUrl+'admin/getSchoolList')
							.withPaginationType('full_numbers').withBootstrap()
							// Active Responsive plugin
							.withOption('responsive', true).withOption(
									'bAutoWidth', false).withOption(
									'createdRow', createdRow);
					vm.dtColumns = [
							DTColumnBuilder.newColumn('schoolId').withTitle(
									'ID'),
							DTColumnBuilder
									.newColumn('schoolName')
									.withTitle('School Name')
									.renderWith(
											function(data, type, full, meta) {
												var rs = '<a ui-sref="administrator.manageSchool.viewSchoolDetail({schoolId: '
														+ full.schoolId
														+ '})">'
														+ data
														+ '</a>';
												return rs;
											}),
							DTColumnBuilder.newColumn('schoolContactPerson')
									.withTitle('Contact Person'),
							DTColumnBuilder.newColumn('schoolEmail').withTitle(
									'Email Id'),
							DTColumnBuilder.newColumn('mobileNumber')
									.withTitle('Phone No.'),
							DTColumnBuilder.newColumn('schoolActive')
									.withTitle('Status').renderWith(
											function(data) {
												var rs = '';
												if (data)
													rs = 'Active';
												else
													rs = 'Inactive';
												return rs;
											}),
							// .notVisible() does not work in this case. Use
							// .withClass('none') instead
							DTColumnBuilder.newColumn('schoolAddress')
									.withTitle('Address').withClass('none') ];

					function createdRow(row, data, dataIndex) {
						// Recompiling so we can bind Angular directive to the
						// DT
						$compile(angular.element(row).contents())($scope);
					}
				})

		.controller('viewSchoolDetailCtrl',
				function($rootScope, $scope, $stateParams, $http, $window) {
			
				$scope.deleteSchool = function(schoolId){
					
					$http({
						method : "POST",
						url : $rootScope.baseUrl + 'admin/deleteSchool/'+ schoolId
					}).success(function(data, status, headers, config) {
						$state
						.go('administrator.manageSchool.viewSchoolList');
					}).error(function(data, status, headers, config) {
						$window.alert("Something went wrong");
					});
				
				}

					$http({
						method : "POST",
						url : $rootScope.baseUrl + 'admin/getSchoolById',
						data : $stateParams
					}).success(function(data, status, headers, config) {
						
						$scope.page = {
							title : 'View School Detail',
							subtitle : 'Place subtitle here...',
							schoolId : data.schoolId,
							schoolName : data.schoolName,
							schoolAddress : data.schoolAddress,
							schoolContactPerson : data.schoolContactPerson,
							mobileNumber : data.mobileNumber,
							schoolEmail : data.schoolEmail,
							schoolActive : data.schoolActive,
							schoolLogo : data.schoolLogo,
							url : data.url,
							schoolAdmin : {
								firstName: data.schoolAdmin.firstName,
								email: data.schoolAdmin.email,
								contact: data.schoolAdmin.contact
							}
						};
						$scope.jsonSchool = JSON.stringify($scope.page);
					}).error(function(data, status, headers, config) {
						$window.alert("Something went wrong");
					});
				})

		// dataTable for list of school Branch in school detail page
		.controller(
				'ViewSchoolBranchListDataCtrl',
				function($scope, DTOptionsBuilder, DTColumnBuilder,
						$stateParams,$rootScope, $compile, $http, $window) {

					$scope.deleteSchoolBranch = function(branchId){
						
						$http({
							method : "POST",
							url : $rootScope.baseUrl + 'admin/deleteSchoolBranch/'+ branchId
						}).success(function(data, status, headers, config) {
							$window.location.reload();
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					
					}

					var vm = this;
					vm.dtOptions = DTOptionsBuilder.fromSource(
							$rootScope.baseUrl + 'admin/getSchoolBranchList/'
									+ $stateParams.schoolId)
							.withPaginationType('full_numbers').withBootstrap()
							// Active Responsive plugin
							.withOption('responsive', true).withOption(
									'bAutoWidth', false).withOption(
											'createdRow', createdRow);
					vm.dtColumns = [
							DTColumnBuilder.newColumn('branchId').withTitle(
									'ID'),
							DTColumnBuilder.newColumn('branchName').withTitle(
									'Branch Name'),
							DTColumnBuilder.newColumn('branchAddress')
									.withTitle('Address'),
							DTColumnBuilder.newColumn(null)
									.withTitle('Actions') .renderWith(function(data, type, full, meta) {
									    $scope.branchJson = JSON.stringify(data);
								        return '<a class="btn btn-success" ui-sref="administrator.manageSchool.editSchoolBranch({branch:branchJson})"' +
								                '   <i class="fa fa-pencil"/>' +" Edit"+
								                '</a>&nbsp;' +
								                '<a class="btn btn-danger" ng-click="deleteSchoolBranch('+data.branchId+')"' +
								                '   <i class="fa fa-close"/>' +" Delete"
								                '</a>';
								        }),
							// .notVisible() does not work in this case. Use
							// .withClass('none') instead
							DTColumnBuilder.newColumn('branchContactPerson')
									.withTitle('Contact Person').withClass(
											'none'),
							DTColumnBuilder.newColumn('branchEmail').withTitle(
									'Email Id').withClass('none'),
							DTColumnBuilder.newColumn('branchMobileNumber')
									.withTitle('Phone No.').withClass('none'),
							DTColumnBuilder.newColumn('branchIsActive')
									.withTitle('Status').withClass('none')
									.renderWith(function(data) {
										var rs = '';
										if (data)
											rs = 'Active';
										else
											rs = 'Inactive';
										return rs;
									}) ];
					
					function createdRow(row, data, dataIndex) {
						// Recompiling so we can bind Angular directive to the
						// DT
						$compile(angular.element(row).contents())($scope);
					}
				})

		// Student controller starts
		.controller('viewStudentCtrl', function($scope) {
			$scope.page = {
				title : 'View Student List',
				subtitle : 'Place subtitle here...'
			};
		})

		// dataTable for list of Student
		.controller(
				'ViewStudentListDataCtrl',
				function($scope, $compile, DTOptionsBuilder, DTColumnBuilder,$rootScope,$http,$window) {
					
					$scope.deleteStudent = function(studentId){
						
						$http({
							method : "POST",
							url : $rootScope.baseUrl + 'admin/deleteStudent/'+ studentId
						}).success(function(data, status, headers, config) {
							$window.location.reload();
						}).error(function(data, status, headers, config) {
							$window.alert("Something went wrong");
						});
					
					}

					var vm = this;
					var id = 0;
					vm.dtOptions = DTOptionsBuilder
							.fromSource(
									$rootScope.baseUrl + 'admin/getStudentList')
							.withPaginationType('full_numbers').withBootstrap()
							// Active Responsive plugin
							.withOption('responsive', true).withOption(
									'bAutoWidth', false).withOption(
									'createdRow', createdRow);
					vm.dtColumns = [
							DTColumnBuilder.newColumn('studentRollId').withTitle(
									'ID'),
							DTColumnBuilder
									.newColumn('firstName')
									.withTitle('Student Name')
									.renderWith(
											function(data, type, full, meta) {
												var rs = data;
												if(full.lastName!==null)
												 rs = data + ' ' + full.lastName;
												return rs;
											}),
							DTColumnBuilder
									.newColumn('schoolName')
									.withTitle('School Name'),
							DTColumnBuilder.newColumn('parent.email').withTitle(
									'Email Id'),
							DTColumnBuilder.newColumn('parent.contact')
									.withTitle('Phone No.'),
							DTColumnBuilder.newColumn('active')
									.withTitle('Status').renderWith(
											function(data) {
												var rs = '';
												if (data)
													rs = 'Active';
												else
													rs = 'Inactive';
												return rs;
											}),
							DTColumnBuilder.newColumn(null)
											.withTitle('Actions') .renderWith(function(data, type, full, meta) {
												 $scope.studentJson = JSON.stringify(data);
										        return '<a class="btn btn-success" ui-sref="administrator.manageStudent.editStudent({student:studentJson})"' +
								                '   <i class="fa fa-pencil"/>' +" Edit"+
								                '</a>&nbsp;' +
								                '<a class="btn btn-danger" ng-click="deleteStudent('+data.studentId+')"' +
								                '   <i class="fa fa-close"/>' +" Delete"
								                '</a>';
										        }),
							// .notVisible() does not work in this case. Use
							// .withClass('none') instead
							DTColumnBuilder.newColumn('studentRollId')
									.withTitle('Student Id.').withClass('none'),
							DTColumnBuilder.newColumn('gender')
									.withTitle('Gender').withClass('none'),
							DTColumnBuilder.newColumn('branchName')
									.withTitle('Branch').withClass('none'),
							DTColumnBuilder.newColumn('className')
									.withTitle('Class').withClass('none'),
							DTColumnBuilder.newColumn('sectionName')
									.withTitle('Section').withClass('none'),
							DTColumnBuilder.newColumn('dob')
									.withTitle('DOB').withClass('none'),
							DTColumnBuilder.newColumn('studentsHealth.age')
									.withTitle('Age').withClass('none'),
							DTColumnBuilder.newColumn('studentsHealth.height')
									.withTitle('Height').withClass('none')
									.renderWith(
											function(data, type, full, meta) {
												var rs = "NA";
												if(full.studentsHealth)
													rs= data + ' ' + full.studentsHealth.heightUnit;
												return rs;
											}),
							DTColumnBuilder.newColumn('studentsHealth.weight')
									.withTitle('Weight').withClass('none')
									.renderWith(
											function(data, type, full, meta) {
												var rs = "NA";
												if(full.studentsHealth)
													rs = data + ' ' + full.studentsHealth.weightUnit;
												return rs;
											}),
							DTColumnBuilder.newColumn('studentsHealth.bmi')
									.withTitle('BMI').withClass('none'),
							DTColumnBuilder.newColumn('studentsHealth.bmiPercentile')
									.withTitle('BMI Percentile').withClass(
											'none'),
							DTColumnBuilder.newColumn('studentsHealth.ibw')
									.withTitle('IBW').withClass('none'),
							DTColumnBuilder.newColumn('parent.firstName')
									.withTitle('Parent Name').withClass('none')];

					function createdRow(row, data, dataIndex) {
						// Recompiling so we can bind Angular directive to the
						// DT
						$compile(angular.element(row).contents())($scope);
					}
				})

		// Nutritionist controller starts
		.controller('viewNutritionistListCtrl', function($scope) {
			$scope.page = {
				title : 'View Nutritionist List',
				subtitle : 'Place subtitle here...'
			};
		})

		// dataTable for list of Nutritionist
		.controller(
				'ViewNutritionistListDataCtrl',
				function($scope, $compile, DTOptionsBuilder, DTColumnBuilder,$rootScope) {

					var vm = this;
					var id = 0;
					vm.dtOptions = DTOptionsBuilder
							.fromSource(
									$rootScope.baseUrl + 'admin/getSchoolList')
							.withPaginationType('full_numbers').withBootstrap()
							// Active Responsive plugin
							.withOption('responsive', true).withOption(
									'bAutoWidth', false).withOption(
									'createdRow', createdRow);
					vm.dtColumns = [
							DTColumnBuilder.newColumn('schoolId').withTitle(
									'ID'),
							DTColumnBuilder
									.newColumn('schoolName')
									.withTitle('Nutritionist Name')
									.renderWith(
											function(data, type, full, meta) {
												var rs = '<a ui-sref="administrator.manageSchool.viewSchoolDetail({schoolId: '
														+ full.schoolId
														+ '})">'
														+ data
														+ '</a>';
												return rs;
											}),
							DTColumnBuilder
									.newColumn('schoolName')
									.withTitle('School Assigned')
									.renderWith(
											function(data, type, full, meta) {
												var rs = '<a ui-sref="administrator.manageSchool.viewSchoolDetail({schoolId: '
														+ full.schoolId
														+ '})">'
														+ data
														+ '</a>';
												return rs;
											}),
							DTColumnBuilder.newColumn('schoolEmail').withTitle(
									'Email Id'),
							DTColumnBuilder.newColumn('mobileNumber')
									.withTitle('Phone No.'),
							DTColumnBuilder.newColumn('schoolActive')
									.withTitle('Status').renderWith(
											function(data) {
												var rs = '';
												if (data)
													rs = 'Active';
												else
													rs = 'Inactive';
												return rs;
											}),
							// .notVisible() does not work in this case. Use
							// .withClass('none') instead
							DTColumnBuilder.newColumn('schoolAddress')
									.withTitle('Address').withClass('none'),
							DTColumnBuilder.newColumn('schoolAddress')
									.withTitle('Password').withClass('none'),
							DTColumnBuilder.newColumn('schoolAddress')
									.withTitle('About Me').withClass('none') ];

					function createdRow(row, data, dataIndex) {
						// Recompiling so we can bind Angular directive to the
						// DT
						$compile(angular.element(row).contents())($scope);
					}
				})
				
						// createArticle controller starts
		.controller('createDocumentCtrl', function($scope, FileUploader, $rootScope, $http, $state, $window) {
			$scope.page = {
				title : 'Create article',
				subtitle : 'Place subtitle here...'
			};
			$scope.docType = 'ARTICLE';
			
			$scope.getSchoolList = function() {
				
				$scope.schoolList = [];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'admin/getSchoolList'
				}).success(function(data, status, headers, config) {
					$scope.schoolList = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			$scope.getSchoolList();
			
			 var uploader = $scope.uploader = new FileUploader({
		            url: $rootScope.baseUrl + 'UploadDownloadFileService/uploadArticleIcon'
		        });
			 uploader.onAfterAddingFile = function(item) {
		            console.log('onAfterAddingFile', item);
		            var fileExtension = '.' + item.file.name.split('.').pop();
					item.file.name = Math.random().toString(36).substring(7) + new Date().getTime() + fileExtension;
		          };
		          
		          $scope.submitForm = function(isValid) {
						console.log('validate form');
						var article = this.article;
						var docType = $scope.docType;
						var url = $rootScope.baseUrl;
						if(docType==='ARTICLE'){
							url += 'admin/addArticle';
						}
						else{
							url += 'admin/addRecipe';
						}
						
						var tagArr = [];
						var tags = $scope.article.tags;
						
						$.each(tags, function(index, item){
							tagArr.push(item.text);
						})
						
						$scope.article.tags = tagArr;
						
						article['icon'] = 'defaultIcon.png';
						console.log(JSON.stringify(article));
						$.each(uploader.queue, function( index, item ) {
							article['icon'] = item.file.name;
							uploader.uploadAll();
							});
						
						// check to make sure the form is completely valid
						$http({
							method : "POST",
							url : url,
							data : JSON.stringify(article)
						})
								.success(
										function(data, status, headers, config) {
											if(docType==='ARTICLE'){
												$state
														.go('administrator.viewArticleDetail', {'articleName': data});
											}
											else{
												$state
												.go('administrator.viewRecipeDetail', {'recipeName': data});
											}
										})
								.error(
										function(data, status, headers, config) {
											$window
													.alert("Something went wrong while adding article");
										});

					};
		})
		
		// View Article details starts
		.controller('viewArticleDetailCtrl', function($rootScope, $scope, $stateParams, $http, $window,$location) {

			$scope.page = {
					title : 'View Article Detail',
					subtitle : 'Place subtitle here...'
				}
			var heading = $stateParams["articleName"];
			 $scope.article = [];
			 
			 $scope.articleComment = {};
			$scope.articleComment['comment']='';
			$scope.absurl = $location.absUrl();
			
			var currentUser = $window.localStorage.getItem("currentUser");
            currentUser = $.parseJSON(currentUser);
            $scope.currentUserId = currentUser.userId;
				
			$http({
				method : "POST",
				url : $rootScope.baseUrl + 'admin/getArticleByName/'+heading
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
					url : $rootScope.baseUrl + 'admin/getRecentRecipeList'
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
					url : $rootScope.baseUrl + 'admin/getRecentArticleList'
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
					url : $rootScope.baseUrl + 'admin/getCommonTags'
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
					url : $rootScope.baseUrl + 'admin/getArticleComments/'+$scope.article.id
				}).success(function(data, status, headers, config) {
					$scope.comments = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			
			$scope.submitForm = function(isValid) {
				$scope.articleComment['articleId']= $scope.article.id;
				$scope.articleComment['userId']=currentUser.userId;
				$scope.articleComment['level']=1;
				var ac = $scope.articleComment;
				$http({
					method : "POST",
					url : $rootScope.baseUrl + 'admin/addCommentToArticle',
					data : JSON.stringify(ac)
				})
						.success(
								function(data, status, headers, config) {
									$scope.articleComment['comment'] = '';
									$scope.getComments();
								})
						.error(
								function(data, status, headers, config) {
									$window
											.alert("Something went wrong while adding comment");
						});
			}
		})
		
		// View Recipe details starts
		.controller('viewRecipeDetailCtrl', function($rootScope, $scope, $stateParams, $http, $window,$location) {
			var heading = $stateParams["recipeName"];
			$scope.page = {
					title : 'View Recipe Detail',
					subtitle : 'Place subtitle here...'
				};
			$scope.recipeComment = {};
			$scope.recipeComment['comment']='';
			$scope.absurl = $location.absUrl();
			
			var currentUser = $window.localStorage.getItem("currentUser");
            currentUser = $.parseJSON(currentUser);
			$scope.currentUserId = currentUser.userId;
			
			$http({
				method : "POST",
				url : $rootScope.baseUrl + 'admin/getRecipeByName/'+heading
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
					url : $rootScope.baseUrl + 'admin/getRecentRecipeList'
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
					url : $rootScope.baseUrl + 'admin/getRecentArticleList'
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
					url : $rootScope.baseUrl + 'admin/getCommonTags'
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
					url : $rootScope.baseUrl + 'admin/getRecipeComments/'+$scope.recipe.id
				}).success(function(data, status, headers, config) {
					$scope.comments = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			
			$scope.submitForm = function(isValid) {
				$scope.recipeComment['recipeId']= $scope.recipe.id;
				$scope.recipeComment['userId']=currentUser.userId;
				$scope.recipeComment['level']=1;
				var ac = $scope.recipeComment;
				$http({
					method : "POST",
					url : $rootScope.baseUrl + 'admin/addCommentToRecipe',
					data : JSON.stringify(ac)
				})
						.success(
								function(data, status, headers, config) {
									$scope.recipeComment['comment'] = '';
									$scope.getComments();
								})
						.error(
								function(data, status, headers, config) {
									$window
											.alert("Something went wrong while adding comment");
						});
			}
		})
		// list Article starts
		.controller('listArticleCtrl', function($scope,$http,$window,$rootScope,$stateParams) {
			$scope.page = {
				title : 'Article Listing',
				subtitle : 'Place subtitle here...'
			};
			
			$scope.getArticleList = function() {
				
				$scope.articleList = [];
				var tag = $stateParams["tag"];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'admin/getArticleList/'+tag
				}).success(function(data, status, headers, config) {
					console.log(data);
					$scope.articleList = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			$scope.getArticleList();
			
			$scope.publishArticle = function(id) {
				
				$http({
					method : "POST",
					url : $rootScope.baseUrl + 'admin/publishArticle/'+id
				}).success(function(data, status, headers, config) {
					$window.location.reload();
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
		})
		
		// list Recipe starts
		.controller('listRecipeCtrl', function($scope,$http,$window,$rootScope,$stateParams) {
			$scope.page = {
				title : 'Recipe Listing',
				subtitle : 'Place subtitle here...'
			};
			
			$scope.getRecipeList = function() {
				
				$scope.recipeList = [];
				var tag = $stateParams["tag"];
				
				$http({
					method : "GET",
					url : $rootScope.baseUrl + 'admin/getRecipeList/'+tag
				}).success(function(data, status, headers, config) {
					$scope.recipeList = data;
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
			
			$scope.getRecipeList();
			
			$scope.publishRecipe = function(id) {
				
				$http({
					method : "POST",
					url : $rootScope.baseUrl + 'admin/publishRecipe/'+id
				}).success(function(data, status, headers, config) {
					$window.location.reload();
				}).error(function(data, status, headers, config) {
					$window.alert("Something went wrong");
				});
			};
		})
		// statistics
		.controller('viewStatisticsCtrl', function($scope){
			$scope.page = {
					title : 'Statistics',
					subtitle : 'view questionaire stats'
				};
		})
		// dataTable for list of schools
		.controller(
				'ViewStatisticsDataCtrl',
				function($scope, $compile, DTOptionsBuilder, DTColumnBuilder,$rootScope) {

					var vm = this;
					var id = 0;
					vm.dtOptions = DTOptionsBuilder
							.fromSource(
									$rootScope.baseUrl+'admin/questionaireStatistics')
							.withPaginationType('full_numbers').withBootstrap()
							// Active Responsive plugin
							.withOption('responsive', true).withOption(
									'bAutoWidth', false).withOption(
									'createdRow', createdRow);
					vm.dtColumns = [
							DTColumnBuilder
									.newColumn('schoolName').withTitle('School Name'),
							DTColumnBuilder.newColumn('branchName')
									.withTitle('Branch Name'),
							DTColumnBuilder.newColumn('count').withTitle(
									'Questionaire Filled Count') ];

					function createdRow(row, data, dataIndex) {
						// Recompiling so we can bind Angular directive to the
						// DT
						$compile(angular.element(row).contents())($scope);
					}
				})