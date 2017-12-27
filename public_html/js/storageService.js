(function() {
        'use strict';

    angular
        .module('todoApp')
        .service('storageService', Service);

    const endpoint = "http://localhost:8080";


    function Service($window,$http,$filter) {
        this.add    = add;
        this.getAll = getAll;
        this.remove = remove;
        //this.open   = open;
        this.get    = get;

        this.auth   = btoa("");
        this.logged = false;
        this.admin  = false;

        function getAll(type) {
            return $http({
                method: 'GET',
                headers: {"Authorization": "Basic " + this.auth},
                url: endpoint + '/' + type
            }).then(function(response) {
                return Object.keys(response.data).map(function(key) {return response.data[key]; })
            });
        }

        function remove(item) {
            var url = endpoint + '/' + item.type + '/' + item._id;
            var config = {
                headers : {
                    'Authorization': 'Basic ' + this.auth
                }
            };
            return $http.delete(url, config);
        }


        /*function add(value) {
            var config = {
                headers : {
                    'Content-Type': 'application/json',
                    'Authorization': 'Basic ' + this.auth
                }
            }
            console.log(value);
            var url = endpoint + '/' + type;
            var a = $http.post(url, value, config);
            console.log(a);
            return a;
        }*/

        function get(type, id) {
           return $http({
                method: 'GET',
                headers: {"Authorization": "Basic " + this.auth},
                url: endpoint + '/' + type + '/' + id
            }).then(function(response) {
                return Object.keys(response.data).map(function(key) {return response.data[key]; })
            });
        }

        function add(type, value) {
            //TODO type da switchare 
            value.events.date = value.events.date.toISOString();
            value.events._id = "temp";
            console.log(value.events);
            var config = {
                headers : {
                    'Authorization': 'Basic ' + auth
                }
            };
            var url = endpoint + '/' + type;
            var a = $http.post(url, value.events, config);
            console.log(a);
            return a;
        }
    }
})();
