'use strict';

var tmfNotificationServices = angular.module('tmfNotificationServices', ['ngResource']);


tmfNotificationServices.service('historyService', function($http) {
    this.getHistoryNotification = function(urlHistory, callback) {
        console.log("URL HISTORY = " + urlHistory);
        $http({
            method: 'GET',
            url: urlHistory,
            isArray: true
        }).success(function(notifications) {
            console.log("SUCCESS HISTORY");
            callback(notifications);
        }).error(function() {
            console.log("ERROR HISTORY!");
        });
    };
});

tmfNotificationServices.service('currentService', function($http) {
    this.getCurrentNotification = function(urlCurrent, callback) {
        console.log("URL CURRENT = " + urlCurrent);
        $http({
            method: 'GET',
            url: urlCurrent,
            isArray: false
        }).success(function(notification) {
            console.log("SUCCESS CURRENT" + notification.eventType);
            callback(notification);
        }).error(function() {
            console.log("ERROR CURRENT!");
        });
    };
});

tmfNotificationServices.service('Config', function($http) {
    this.getSettings = function(callback) {
        console.log("lecture du fichier properties");
        $http({
            method: 'GET',
            url: 'properties/app.properties',
            isArray: false
        }).success(function(response) {
            console.log("PROPERTIES SUCCESS : " + response.url);
            callback(response);
        }).error(function() {
            console.log("PROPERTIES ERROR!");
        });
    };
});

