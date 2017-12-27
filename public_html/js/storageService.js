(function() {
        'use strict';

    angular
        .module('todoApp')
        .service('storageService', Service);

    const endpoint = "http://localhost:8080";
    /*
    * TODO change with the right URLs given by our APIs */
    function Service($window,$http,$filter) {
        this.add    = add;
        this.getAll = getAll;
        this.remove = remove;
        this.open   = open;
        this.get    = get;

        const auth = btoa("silvia@scala:anypass");

        function getAll(type) {
            return $http({
                method: 'GET',
                headers: {"Authorization": "Basic " + auth},
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

        function get(type, id) {
           return $http({
                method: 'GET',
                headers: {"Authorization": "Basic " + auth},
                url: endpoint + '/' + type + '/' + id
            }).then(function(response) {
                return Object.keys(response.data).map(function(key) {return response.data[key]; })
            });
           
        }
    }
})();
