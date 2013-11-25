var module = angular.module('MyApp', [ 'ngResource' ]);

module.config(function($locationProvider, $resourceProvider) {
	// configure html5 to get links working on jsfiddle
	$locationProvider.html5Mode(true);
});

module.directive('ngEnter', function() {
	return function(scope, element, attrs) {
		element.bind("keydown keypress", function(event) {
			if (event.which === 13) {
				scope.$apply(function() {
					scope.$eval(attrs.ngEnter);
				});
				event.preventDefault();
			}
		});
	};
});

module.factory('DataService', function($resource) {
	var model = {};

	var PropertiesDefinedInputs = $resource(window.contextpath
			+ '/base/propertiesDefinedInputs');
	model.propertiesDefinedInputs = PropertiesDefinedInputs.get(function() {
	});

	var DirectiveDefinedInputs = $resource(window.contextpath
			+ '/base/directiveDefinedInputs');
	model.directiveDefinedInputs = DirectiveDefinedInputs.get(function() {
	});

	var Strategies = $resource(window.contextpath + '/base/strategies');
	model.strategies = Strategies.query(function() {
	});

	return model;
});

module.controller('VsController', function($scope, DataService, $resource,
		$http) {
	$scope.selectedTab = 'profile';
	// 获取hash，设置给selectedTab
	var hash = window.location.hash;
	if (hash.length > 1) {// 去掉#号
		hash = hash.substring(1);
		if (hash == 'profile' || hash == 'pool' || hash == 'location') {
			$scope.selectedTab = hash;
		}
	}
	$scope.isActive = function(tabName) {
		var clazz = ($scope.selectedTab == tabName) ? 'active' : '';
		return clazz;
	};
	var vsChanged = false;
	$scope.vs = null;
	$scope.getVs = function(vsName) {
		$http({
			method : 'GET',
			url : window.contextpath + '/' + vsName + '/get'
		}).success(function(data, status, headers, config) {
			if (data.errorCode == 0) {
				if (data.virtualServer == null) {// 新建vs
					$scope.vs = new Object();
					$scope.vs.name = vsName;
					$scope.vs.dynamicAttributes = new Object();
					$scope.vs.instances = [];
					$scope.vs.instances.push(new Object());
					$scope.vs.pools = [];
					$scope.newVs = true;
				} else {
					$scope.vs = data.virtualServer;
					$scope.newVs = false;
				}
				// 开始监听vs的修改
				$scope.$watch('vs', function(newValue,oldValue) { 
					if(newValue != oldValue){
						vsChanged=true;
					}
				}, true);
			} else {
				app.alertError("获取失败: " + data.errorMessage);
			}
		}).error(function(data, status, headers, config) {
			app.appError("响应错误", data);
		});
	};
	// 保存
	$scope.save = function() {
		$http({
			method : 'POST',
			data : $scope.vs,
			url : window.contextpath + '/' + $scope.vs.name + '/save'
		}).success(
				function(data, status, headers, config) {
					if (data.errorCode == 0) {
						app.alertSuccess("保存成功！ 即将刷新页面...");
						vsChanged = false;//保存成功，修改标识重置
						setTimeout(function() {
							window.location = window.contextpath + "/"
									+ $scope.vs.name + window.location.hash;
						}, 700);
					} else {
						app.alertError("保存失败: " + data.errorMessage);
					}
				}).error(function(data, status, headers, config) {
			app.appError("响应错误", data);
		});
	};
	// 删除
	$scope.removeVirtualServer = function() {
		$http({
			method : 'POST',
			data : $scope.vs,
			url : window.contextpath + '/' + $scope.vs.name + '/remove'
		}).success(
				function(data, status, headers, config) {
					if (data.errorCode == 0) {
						app.alertSuccess("删除成功！ 即将刷新页面...",
								"removeVirtualServerAlertDiv");
						setTimeout(function() {
							window.location = window.contextpath + "/";
						}, 700);
					} else {
						app.alertError("删除失败: " + data.errorMessage,
								"removeVirtualServerAlertDiv");
					}
				}).error(function(data, status, headers, config) {
			app.appError("响应错误", data);
		});
	};
	// 离开页面时，对比一下vs是否发生了修改
	var onunload = function() {
		if (vsChanged) {
			return "您的修改尚未保存，现在离开将丢失所有修改";
		}
	}
	window.onbeforeunload = onunload;

});