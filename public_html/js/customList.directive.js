(function() {
    'use strict';

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
            templateUrl: '/admin/List_md.html?' + V

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
            templateUrl: '/admin/List_md_offers.html?' + V
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
            templateUrl: '/admin/List_md_coupons.html?' + V

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
            templateUrl: '/admin/List_md_contacts.html?' + V

        };
    }   

    //Directive controller
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


})();
