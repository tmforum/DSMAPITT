'use strict';

tmfNotificationApp.controller('ListController', ['$scope', 'historyService', '$rootScope', function($scope, historyService, $rootScope) {
        $scope.url = $rootScope.urlName;
        $scope.urlHistory = $scope.url +'/history';
        historyService.getHistoryNotification($scope.urlHistory, function(callBack) {
            $scope.notifications = callBack;
        });
    }]);

tmfNotificationApp.controller('CurrentController', ['$scope', 'currentService', '$rootScope', function($scope, currentService, $rootScope) {
        $scope.url = $rootScope.urlName;
        $scope.urlCurrent = $scope.url +'/current';
        currentService.getCurrentNotification($scope.urlCurrent, function(callBack) {
            $scope.notification = callBack;
        });
    }]);

tmfNotificationApp.controller('SettingsController', ['$scope', '$rootScope', function($scope, $rootScope) {
        $scope.url = $rootScope.urlName;
    }]);

tmfNotificationApp.controller('UpdateSettingsController', ['$scope', '$rootScope', '$location', function($scope, $rootScope, $location) {
        $scope.updateSettings = function(newUrl) {
            if (newUrl != "" && newUrl != null) {
                $rootScope.urlName = newUrl;
            }
            console.log("NOUVELLE URL url= " + newUrl)
            console.log("NOUVEAU URL rootscope= " + $rootScope.urlName)
            $location.path("/list");
        };
        
        $scope.cancel = function(){
            $location.path("/list");
        }
    }]);



