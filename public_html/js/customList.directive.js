(function() {
    'use strict';

    // TODO add all the directives that we need

    angular
        .module('todoApp')
        .directive('customList', directive);

    angular
        .module('todoApp')
        .directive('customListOffers', directiveOffers);

    angular
        .module('todoApp')
        .directive('customListCoupons', directiveCoupons);
 
    angular
        .module('todoApp')
        .directive('customListContacts', directiveContacts);

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

    function directiveOffers() {
        return {
            scope: {},
            bindToController: {
                items: '=',
                selectedItem: '=',
            },
            controller: customListControllerOffers,
            controllerAs: 'customListCtrlOffers',
            transclude: true,
            restrict: 'E',
            templateUrl: '/List_md_offers.html?' + V
        };
    }

    function directiveCoupons() {
        return {
            scope: {},
            bindToController: {
                items: '=',
                selectedItem: '=',
            },
            controller: customListControllerCoupons,
            controllerAs: 'customListCtrlCoupons',
            transclude: true,
            restrict: 'E',
            templateUrl: '/List_md_coupons.html?' + V

        };
    }

    function directiveContacts() {
        return {
            scope: {},
            bindToController: {
                items: '=',
                selectedItem: '=',
            },
            controller: customListControllerContacts,
            controllerAs: 'customListCtrlContacts',
            transclude: true,
            restrict: 'E',
            templateUrl: '/List_md_contacts.html?' + V

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
            templateUrl: '/List_md_events.html?' + V

        };
    }

    // TODO add tickets and users
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
            templateUrl: '/List_md_tickets.html?' + V

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
            templateUrl: '/List_md_users.html?' + V

        };
    }

    /* ********** Directive controllers ************* */
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

    function customListControllerOffers(storageService) {
        var vm = this;
        //Select or deselect the given item
        vm.toggleSelection = function(item) {
            item.type = 'offers';
            if (vm.selectedItem.indexOf(item) == -1)
                vm.selectedItem.push(item);
            else
            {
                var index = vm.selectedItem.indexOf(item);
                vm.selectedItem.splice(index, 1);
            }
        }
    }

    function customListControllerCoupons(storageService) {
        var vm = this;
        //Select or deselect the given item
        vm.toggleSelection = function(item) {
            item.type = 'offers';
            if (vm.selectedItem.indexOf(item) == -1)
                vm.selectedItem.push(item);
            else
            {
                var index = vm.selectedItem.indexOf(item);
                vm.selectedItem.splice(index,1);
            }
        }
        
    }

    function customListControllerContacts(storageService) {
        var vm = this;
        //Select or deselect the given item
        vm.toggleSelection = function(item) {
            item.type = 'contacts';
            if (vm.selectedItem.indexOf(item) == -1)
                vm.selectedItem.push(item);
            else
            {
                var index = vm.selectedItem.indexOf(item);
                vm.selectedItem.splice(index,1);
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
        // TODO prendere l'evento qui?
        console.log("cazzomerda");
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
