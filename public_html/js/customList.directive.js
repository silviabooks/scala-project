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


    function customListControllerEvents(storageService) {
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
