(function() {
        'use strict';

    angular
        .module('todoApp')
        .service('storageService', Service);
    // TODO change endpoint
    const endpoint = "http://localhost:8080";
    /*
    * TODO change with the right URLs given by our APIs */
    function Service($window,$http,$filter) {
        this.add    = add;
        this.getAll = getAll;
        this.remove = remove;
        this.open   = open;


        function getAll(type) {
            return $http({
                method: 'GET',
                // TODO change url
                url: endpoint + '/' + type
            }).then(function(response) {
                return Object.keys(response.data).map(function(key) {return response.data[key]; })
            });
        }

        function remove(item) {
            var data = { type: item.type, id: item._id.$id, delete: 1 };
            var config = {
                headers : {
                    'Content-Type': 'application/json'
                }
            } // change to DELETE?
            return $http.post(endpoint, data, config);
        }


        function add(value) {
            var config = {
                headers : {
                    'Content-Type': 'application/json'
                }
            }
            console.log(value);
            var a = $http.post(endpoint, value, config);
            console.log(a);
            return a;
        }

        // TODO che è?
        function open() {
            return $http({
                method: 'GET',
                url: endpoint + '?open=1'
            }).then(function(response) {
                return { status: 'ok' };
            });
        }
    }
})();
