(function(angular) {

    'use strict';
    var module = angular.module('todoApp', ['ngMaterial','md.data.table']);

    angular.module('todoApp').config(function($mdDateLocaleProvider) {
        $mdDateLocaleProvider.formatDate = function(date) {
            if (!date)
                return "";
            return moment(date).format('DD/MM/YYYY');
        };
    });

    angular.module('todoApp').controller('TodoController', TodoController);

    angular.module('todoApp').filter('capitalize', function() {
        return function(input) {
          return (!!input) ? input.charAt(0).toUpperCase() + input.substr(1).toLowerCase() : '';
        }
    });

    angular.module('todoApp').filter('toLocaleTime', function() {
        return function(dateStr) {
            return new Date(dateStr).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit', hour12: false});
        }
    });

    angular.module('todoApp').filter('toLocaleDate', function() {
        return function(dateStr) {
            return new Date(dateStr).toLocaleDateString();
        }
    });

    angular.module('todoApp').filter('yesNo', function() {
        return function(input) {
            return input ? 'Si' : 'No';
        }
    });


    function TodoController($scope, storageService, $mdDialog, $http, $filter, $q) {
        var vm = this;
        vm.items = {};
        vm.createTabSelectedIndex = 0;
        vm.ws = undefined;
        vm.websocketMessages = [];
        vm.load = function() {
            storageService.auth = vm.getCookie("auth");
            vm.doLogin()
            vm.get('events');
            vm.selectedItem = [];
        }

        vm.get = function(what) {
            if (what === "tickets") {
                storageService.getAll(what).then(function (response) {
                    vm.items[what] = response;
                    vm.items[what].forEach(function(t, i) {
                        $q.all([
                        storageService.get("users", t.boughtFrom),
                        storageService.get("events", t.event)])
                        .then(function(data) {
                            vm.items[what][i].userDetails = data[0][0];
                            vm.items[what][i].eventDetails = data[1][0];
                        });
                    });
                    return;
                });
            }
            storageService.getAll(what).then(function (response) {
                vm.items[what] = response;
            });
        }

        //Delete the current selected item, if any
        vm.deleteItem = function(ev) {
            if (vm.selectedItem != null) {
                var confirm = $mdDialog.confirm()

                .textContent(vm.selectedItem.length + ' elementi verranno cancellati. Sei sicuro?')
                    .ariaLabel('Delete contacts')
                    .targetEvent(ev)
                    .ok('Si')
                    .cancel('No');

                $mdDialog.show(confirm).then(function(result) {
                    if (result) {
                        angular.forEach(vm.selectedItem, function(v, k) {
                            var promise = storageService.remove(v);
                            if (vm.selectedItem.length > k)
                                promise.then(vm.load);
                        });
                    }
                });
            }
        }

        //Creates a new item with the given parameters
        vm.create = function (item, type) {
            var obj = { };
            obj[type] = item;
            storageService.add(type, obj).then(vm.load);
        }

        // View
        vm.tabSelected = function(tab) {
            if (typeof tab !== 'undefined') {
                vm.selectedItem = [];
                vm.tab = tab;
                vm.get(tab);
            }
            return vm.tab;
        }

        vm.atLeastOneEnabled = function (item) {
            // Not set required field if item is just not defined (still no inputs) 
            // or if the schedule definition is not needed
            //
            if (!item || item.forever)
                return true;

            // If the execution come here the scheduling is needed but it still could
            // be undefined: in this case it is sure that no weekday was still selected
            // So all weekdays switches are set as required
            //
            var schedule = item.schedule;
            if (schedule === undefined)
                return false;

            var ret = false;

            // Loop thruogh the schedule and stop if it is found an enabled weekday
            // In this case ret is changed to true so that weekdays for the schedule
            // will not be set as required fields
            //
            Object.keys(schedule).forEach((x) => {
                if (schedule[x].enabled == true)
                    return ret = true;
                });

            return ret;
        }

        vm.helperAdd = function(ev) {

            var confirm =  $mdDialog.prompt({
                    targetEvent: ev,
                    controller: 'TodoController',
                    controllerAs: 'vm',
                    clickOutsideToClose: true,
                    templateUrl: '/form_' + vm.tabSelected() + '.html?'
            });

            $mdDialog.show(confirm).then(function(answer){
                if(answer)
                {
                    vm.create(answer.newItem, vm.tabSelected());
                }
            });
            return confirm;
        }

        $scope.answer = function(answer){
            $mdDialog.hide(answer);
        };

        $scope.cancel = function(ev) {
             $mdDialog.cancel();
        };

        vm.logout = function(ev) {
            var confirm = $mdDialog.confirm()
            .textContent('Logout. Sei sicuro?')
                .ariaLabel('Logout')
                .targetEvent(ev)
                .ok('Yes')
                .cancel('No');

            $mdDialog.show(confirm).then(function(result) {
                if (result) {
                    storageService.logged = false;
                    storageService.admin  = false;
                    storageService.auth   = undefined;
                    vm.setCookie("auth", "", -1);
                    console.log("Logout");
                }
            });

        };

        vm.isAdmin = function() {
            return storageService.admin;
        };

        vm.isLoggedIn = function() {
            return storageService.logged;
        };

        vm.login = function(ev) {
            var confirm =  $mdDialog.prompt({
                    targetEvent: ev,
                    controller: 'TodoController',
                    controllerAs: 'vm',
                    clickOutsideToClose: true,
                    templateUrl: '/form_login.html'
            });
            var that = vm;
            $mdDialog.show(confirm).then(function(answer){
                storageService.auth = btoa(answer.user.email + ":" + answer.user.password);
                var wrong = $mdDialog.alert({
                    title: 'Errore',
                    textContent: 'Credenziali errate',
                    ok: 'Ok'
                });
                that.doLogin(wrong);
            });
            return confirm;
        };

        vm.doLogin = function(wrong) {
            return storageService.me().then(function(response) {
                if (response.isAdmin) {
                    console.log("Admin logged in");
                    storageService.admin = true;
                } else { // user
                    console.log("user logged in");
                    storageService.admin = false;
                }
                storageService.user  = response;
                storageService.logged = true;
                vm.setCookie("auth", storageService.auth, 15);
                vm.get('tickets');
                vm.get('users');
            }).catch(function() {
                console.log("Wrong credentials");
                if (wrong !== undefined) {
                    $mdDialog.show(wrong)
                }
                storageService.auth = undefined;
                storageService.user = false;
                storageService.admin= false;
            });
        };

        vm.setCookie = function(cname, cvalue, exdays) {
            var d = new Date();
            d.setTime(d.getTime() + (exdays*24*60*60*1000));
            var expires = "expires="+ d.toUTCString();
            document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
        };

        vm.getCookie = function(cname) {
            var name = cname + "=";
            var decodedCookie = decodeURIComponent(document.cookie);
            var ca = decodedCookie.split(';');
            for(var i = 0; i <ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) == ' ') {
                    c = c.substring(1);
                }
                if (c.indexOf(name) == 0) {
                    return c.substring(name.length, c.length);
                }
            }
            return "";
        };


        vm.wss = function () {
            vm.ws = new WebSocket("ws://" + storageService.endpoint.replace(/http:\/\//, '') + "/ws");

            if ("WebSocket" in window)
                console.log("WebSocket is supported by your Browser!");
            else {
                console.log("WebSocket not supported");
                return;
            }
            // Let us open a web socket

            vm.ws.onopen = function() {
                // Web Socket is connected, send data using send()
                vm.ws.send("Alive...");
                console.log("Message is sent...");
                setTimeout(vm.keepAlive, 10000);
            };

            vm.ws.onmessage = function (evt) {
                var received_msg = JSON.parse(evt.data);
                vm.websocketMessages.push(received_msg);
                console.log("Message is received...");
            };

            vm.ws.onclose = function() {
                // websocket is closed.
                console.log("Connection is closed..."); 
            };

            window.onbeforeunload = function(event) {
                vm.ws.close();
            };
        }

        vm.keepAlive = function () {
            vm.ws.send("Keep-alive");
            console.log("Keep alive to keep webSocket up");
            setTimeout(vm.keepAlive, 10000);
        };

        vm.load();
        window.onload = vm.wss;
    }
})(window.angular);
