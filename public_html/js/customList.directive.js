(function() {
    'use strict';

    angular
        .module('todoApp')
        .directive('customList', directive);

    angular
        .module('todoApp')
        .directive('customListEvents', directiveEvents);

    angular
        .module('todoApp')
        .directive('customListTickets', directiveTickets);

    angular
        .module('todoApp')
        .directive('customListUsers', directiveUsers);


    function directive() {
        return {
            scope: {},
            bindToController: {
                items: '=',
                selectedItem: '=',
                filterFunction: '=',
               
            },
            controller: customListController,
            controllerAs: 'customListCtrl',
            transclude: true,
            restrict: 'E',
            templateUrl: '/List_md.html?' + V

        };
    }

    function directiveEvents() {
        return {
            scope: {},
            bindToController: {
                items: '=',
                selectedItem: '=',
            },
            controller: customListControllerEvents,
            controllerAs: 'customListCtrlEvents',
            transclude: true,
            restrict: 'E',
            templateUrl: '/List_md_events.html?'

        };
    }

    function directiveTickets() {
        return {
            scope: {},
            bindToController: {
                items: '=',
                selectedItem: '=',
            },
            controller: customListControllerTickets,
            controllerAs: 'customListCtrlTickets',
            transclude: true,
            restrict: 'E',
            templateUrl: '/List_md_tickets.html?'

        };
    }

    function directiveUsers() {
        return {
            scope: {},
            bindToController: {
                items: '=',
                selectedItem: '=',
            },
            controller: customListControllerUsers,
            controllerAs: 'customListCtrlUsers',
            transclude: true,
            restrict: 'E',
            templateUrl: '/List_md_users.html?'

        };
    }

    function customListController(storageService) {
        var vm = this;
        //Select or deselect the given item
        vm.toggleSelection = function(item) {
            item.type = 'services';
            if (vm.selectedItem.indexOf(item) == -1)
                vm.selectedItem.push(item);
            else
            {
                var index = vm.selectedItem.indexOf(item);
                vm.selectedItem.splice(index, 1);
            }
        }
        
    }


    function customListControllerEvents(storageService, $mdDialog) {
        var vm = this;
        //Select or deselect the given item

        vm.toggleSelection = function(item) {
            item.type = 'events';
            if (vm.selectedItem.indexOf(item) == -1)
                vm.selectedItem.push(item);
            else
            {
                var index = vm.selectedItem.indexOf(item);
                vm.selectedItem.splice(index,1);
            }
        }

        vm.buyTicket = function(ev, eventId) {

            var confirm = $mdDialog.confirm()
                .textContent('Vuoi acquistare un biglietto per questo evento?')
                .ariaLabel('Buy')
                .targetEvent(ev)
                .ok('Si')
                .cancel('No');

            var userId = null;
            var userName = null;

            $mdDialog.show(confirm).then(function(answer) {
                if (answer) {
                    storageService.getAll("me").then(function(response) {
                        userName = response.name;
                        userId = response._id;
                        console.log(userId + ' ' + userName);
                    });

                    var ciao = {};
                    ciao['tickets'].event = eventId;
                    ciao['tickets'].ticketHolder = userName;
                    ciao['tickets'].boughtFrom = userId;
                    storageService.add('tickets', ciao);

                }
            });
        }


    }

    
    function customListControllerTickets(storageService) {
        var vm = this;
        //Select or deselect the given item
        vm.toggleSelection = function(item) {
            item.type = 'tickets';
            if (vm.selectedItem.indexOf(item) == -1)
                vm.selectedItem.push(item);
            else
            {
                var index = vm.selectedItem.indexOf(item);
                vm.selectedItem.splice(index,1);
            }
        }

    }

    function customListControllerUsers(storageService) {
        var vm = this;
        //Select or deselect the given item
        vm.toggleSelection = function(item) {
            item.type = 'users';
            if (vm.selectedItem.indexOf(item) == -1)
                vm.selectedItem.push(item);
            else
            {
                var index = vm.selectedItem.indexOf(item);
                vm.selectedItem.splice(index,1);
            }
        }

    }

})();
