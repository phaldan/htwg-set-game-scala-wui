(function ($, l, a) {
    /////////////////////////////////
    // WebSocket
    /////////////////////////////////
    var ws = $.gracefulWebSocket('ws://' + l.host + '/ws');

    var updateAll = function () {
        function update($element) {
            try {
                a.element($element[0]).scope().loadData();
            } catch (e) {}
        }

        update($('.js-field .js-cards'));
        update($('.js-points'));
    };

    ws.onmessage = function (event) {
        updateAll();
    };

    /////////////////////////////////
    // Angular
    /////////////////////////////////
    var app = a.module('setGameApp', ['ngRoute']);

    app.config(function ($routeProvider) {
        $routeProvider.
        when('/', {
            templateUrl: '/assets/tpl/gamefield.html',
            controller: 'CardCtrl'
        }).
        when('/help', {
            templateUrl: '/assets/tpl/help.html'
        }).
        when('/about', {
            templateUrl: '/assets/tpl/about.html'
        }).
        otherwise({
            redirectTo: '/'
        });
    });

    app.controller('PointsCtrl', function ($scope, $http) {
        $scope.loadData = function () {
            $http.get('/points.json').success(function (data) {
                $scope.points = data;
            });
        };
        $scope.loadData();
    });

    app.controller('RestartCtrl', function ($scope, $http) {
        $scope.click = function () {
            $http.get('/reset.json');
        };
    });

    app.controller('KiCtrl', function ($scope, $http, $route) {
        $scope.mode = 'Medium';
        $scope.setKi = function () {
            if (!$scope.mode || $scope.mode === 'undefined') {
                window.alert('You have to choose the difficulty.');
            } else {
                $http.get('/setki/' + $scope.mode).success(function () {
                    console.log('KI now set to ' + $scope.mode);
                    location.reload();
                }).error(function (err) {
                    console.log(JSON.stringify(err));
                });
            }
        };
    });

    app.controller('SaveCtrl', function ($scope, $http) {
        $scope.saveGame = function () {
            var $main = $('.js-field');
            var $modalSave = $main.find('.js-save');
            $http.get('/save').success(function (gameId) {
                $('#modal-save').html("<b>Game saved under ID:</b><br>" + gameId);
                $modalSave.modal();
                console.log('Saved game with id:\n' + gameId);
            }).error(function (err) {
                console.log(JSON.stringify(err));
            });
        };
    });

    app.controller('LoadCtrl', function ($scope, $http) {
        $scope.loadGame = function () {
            $http.get('/load/' + $scope.gameId).success(function (res) {
                console.log('Game loaded.');
            }).error(function (err) {
                console.log(JSON.stringify(err));
            });
        };
    });

    app.controller('CardCtrl', function ($scope, $http) {
        $scope.loadData = function () {
            $http.get('/cards.json').success(function (data) {
                $scope.cards = data.cards;
                $scope.player = data.player;
            });
        };

        $scope.loadData();

        var mode = 1; // 1=Press button, 2=Select set

        var $main = $('.js-field');
        var $pressArea = $('body');
        var $modalTurn = $main.find('.js-turn');
        var $cards = $main.find('.js-cards');

        var selectClass = 'selected';
        var tipClass = 'highlight';

        var clearTip = function () {
            $cards.find('img.' + tipClass).removeClass(tipClass);
        };

        /////////////////////////////////
        // Player selection
        /////////////////////////////////
        $pressArea.keypress(function (e) {
            $main.find('button').each(function () {
                if ($(this).data('key') == e.which && mode === 1) {
                    mode = 2;
                    $modalTurn.modal();
                    clearTip();
                }
            });
        });

        /////////////////////////////////
        // Card selection
        /////////////////////////////////
        $cards.on('click', 'img', function () {
            if (mode == 2) {
                clearTip();
                $(this).addClass(selectClass);
            }

            var $selectedCards = $main.find('img.' + selectClass);

            if ($selectedCards.length == 3) {
                var url = '/set/' + get(0) + '/' + get(1) + '/' + get(2);
                $.get(url, function (data) {});

                $selectedCards.removeClass(selectClass);
                mode = 1;
            }

            function get(i) {
                return $selectedCards.eq(i).data('field');
            }
        });

        $scope.showTip = function () {
            $http.get('/solve.json').success(function (data) {
                $cards.find('img').each(function () {
                    var id = $(this).data('field');
                    if ($.inArray(id, data) > -1) {
                        $(this).addClass(tipClass);
                    }
                });
            });
        };
    });
})(jQuery, location, angular);