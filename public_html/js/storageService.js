(function() {
        'use strict';

    angular
        .module('todoApp')
        .service('storageService', Service);

    const endpoint = "http://aleskandro.lucylaika.ovh:8080";

    function Service($window,$http,$filter) {
        this.endpoint = endpoint;
        this.add    = add;
        this.getAll = getAll;
        this.remove = remove;
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
            var config = {
                headers : {'Authorization': 'Basic ' + this.auth}
            };
            var url = endpoint + '/' + type;
            var a = null;
            value[type]._id = "temp";

            switch (type) {
                case 'events':
                    value['events'].date = value['events'].date.toISOString();
                    a = $http.post(url, value['events'], config);
                    break;
                case 'users':
                    // this is to make sure that isAdmin isn't undefined
                    if (value['users'].isAdmin !== true)
                        value['users'].isAdmin = false;
                    a = $http.post(url, value['users'], config);
                    break;
                case 'tickets':
                    // TODO mettere altre robe se servono
                    a = $http.post(url, value['tickets'], config);
                    break;
            }
            //console.log(value[type]);
            //console.log(a);
            return a;
        }
    }
})();
