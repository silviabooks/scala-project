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


    function TodoController($scope, storageService, $mdDialog,$http,$filter,$q) {
        var vm = this;
        //vm.weekDays = [ 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun' ];
        vm.items = {};
        vm.cazzo = {};
        vm.createTabSelectedIndex = 0;
        vm.load = function() {
            vm.get('events');
            vm.get('tickets');
            vm.get('users');
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
                if (what === 'events') {
                    vm.cazzo = response;
                }
            });
        }

        //Delete the current selected item, if any
        vm.deleteItem = function(ev) {
            if (vm.selectedItem != null) {
                var confirm = $mdDialog.confirm()

                .textContent(vm.selectedItem.length + ' element(s) will be deleted. Are you sure?')
                    .ariaLabel('Delete contacts')
                    .targetEvent(ev)
                    .ok('Yes')
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

        vm.openDoor = function(ev) {
            if (vm.selectedItem != null) {
                var confirm = $mdDialog.confirm()

                .textContent('Opening door. Are you sure?')
                    .ariaLabel('Opening door')
                    .targetEvent(ev)
                    .ok('Yes')
                    .cancel('No');

                $mdDialog.show(confirm).then(function(result) {
                    if (result) {
                        var promise = storageService.open().then(function() {
                            var message = $mdDialog.alert()
                                .textContent('Opening...')
                                    .ariaLabel('Opening door').ok('Ok');
                            $mdDialog.show(message);
                        });
                    }
                });
            }
        }

        //Creates a new item with the given parameters
        vm.create = function (item, type) {
            var obj = { };
            obj[type] = item;
            storageService.add(obj).then(vm.load);
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
                    templateUrl: '/form_' + vm.tabSelected() + '.html?' + V
            });
            $mdDialog.show(confirm).then(function(answer){
                if(answer)
                {
                    vm.create(answer.newItem, vm.tabSelected());
                }
            });
            return confirm;
        }

        $scope.answer = function(answer, surplus){
            if (typeof surplus !== 'undefined')
                answer.newItem[surplus] = 1;
            $mdDialog.hide(answer);
        };

        $scope.cancel = function(ev) {
             $mdDialog.cancel();
        };

        vm.logout = function(ev) {
            var out = window.location.href.replace(/:\/\//, '://log:out@');
            var baseurl = window.location.origin;

            var confirm = $mdDialog.confirm()
            .textContent('Logout. Are you sure?')
                .ariaLabel('Logout')
                .targetEvent(ev)
                .ok('Yes')
                .cancel('No');

            $mdDialog.show(confirm).then(function(result) {
                if (result) {
                    $http.get(out);
                    window.location = baseurl;
                }
            });

        }

        vm.load();
    }
})(window.angular);
