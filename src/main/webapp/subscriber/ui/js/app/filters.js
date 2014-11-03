'use strict';

var tmfNotificationApp = angular.module('tmfNotificationFilters', ['ui.unique']);

tmfNotificationApp.filter('fuzzyFilter', function() {
    return function(contacts, search, threshold) {
        if (!search) {
            return contacts;
        }

        var fuse = new Fuse(contacts, {
            keys: ['firstName', 'lastName'],
            threshold: threshold
        });

        return fuse.search(search);
    }
});

tmfNotificationApp.filter('prettify', function() {
    function syntaxHighlight(json) {
//        var jsonString = '{"some":"json"}';
//        var jsonString = $scope.notification.resource;
//        var jsonPretty;
//        if (typeof jsonString !== 'string') {
//            jsonPretty = JSON.stringify(jsonString, undefined, 2);
//        } else {
//            jsonPretty = JSON.stringify(JSON.parse(jsonString), null, 2);
//        }
//        return jsonPretty;$sce.trustAsHtml
        json = JSON.stringify(json, undefined, 2);
        json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        var response = json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function(match) {
            var cls = 'number';
            if (/^"/.test(match)) {
                if (/:$/.test(match)) {
                    cls = 'key';
                } else {
                    cls = 'string';
                }
            } else if (/true|false/.test(match)) {
                cls = 'boolean';
            } else if (/null/.test(match)) {
                cls = 'null';
            }
            return '<span class="' + cls + '">' + match + '</span>';
        });
        return response;
    }

    return syntaxHighlight;
});


