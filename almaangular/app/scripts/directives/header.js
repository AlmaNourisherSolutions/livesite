'use strict';

/**
 * @ngdoc directive
 * @name minovateApp.directive:activateButton
 * @description
 * # activateButton
 */
app
.directive('header', function () {
    return {
        restrict: 'A', //This menas that it will be used as an attribute and NOT as an element. I don't like creating custom HTML elements
        replace: true,
        scope: {user: '='}, // This is one of the cool things :). Will be explained in post.
        templateUrl: "views/header.html",
        controller: ['$scope', '$filter', '$window', function ($scope, $filter, $window) {
            // Your behaviour goes here :)
        	console.log($scope);
        	
        }]
    }
});