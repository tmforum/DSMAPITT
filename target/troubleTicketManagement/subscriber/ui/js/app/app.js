'use strict';

var tmfNotificationApp = angular.module('tmfNotificationApp', ['ngResource', 'ngRoute', 'ui.bootstrap', 'tmfNotificationServices', 'tmfNotificationFilters']);

tmfNotificationApp.config(function($routeProvider) {
    $routeProvider.when('/list', {templateUrl: 'view/list.html', controller: 'ListController'});
    $routeProvider.when('/viewCurrent', {templateUrl: 'view/viewCurrent.html', controller: 'CurrentController'});
    $routeProvider.when('/settings', {templateUrl: 'view/settings.html', controller: 'SettingsController'});
    $routeProvider.otherwise({redirectTo: '/list'});
});

tmfNotificationApp.run(function($rootScope, $location, Config) {
    $rootScope.location = $location;
    Config.getSettings(function(response) {
        console.log("INITIALISATION URL="+response.url);
        $rootScope.urlName = response.url;
    });

});
