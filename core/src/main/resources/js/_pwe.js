var PwE;
(function (PwE) {
    var RequestHeader = (function () {
        function RequestHeader() {
        }
        RequestHeader.AJAX_FORM_POST_CONTENT_TYPE = "application/x-www-form-urlencoded";
        return RequestHeader;
    })();
    PwE.RequestHeader = RequestHeader;
})(PwE || (PwE = {}));
var PwE;
(function (PwE) {
    var HTTPVerbs = (function () {
        function HTTPVerbs() {
        }
        HTTPVerbs.POST = "POST";
        HTTPVerbs.GET = "GET";
        HTTPVerbs.PUT = "PUT";
        HTTPVerbs.DELETE = "DELETE";
        return HTTPVerbs;
    })();
    PwE.HTTPVerbs = HTTPVerbs;
})(PwE || (PwE = {}));
var PwE;
(function (PwE) {
    (function (Arrays) {
        function zipWith(l1, l2, what) {
            return l1.map(function (v1, idx) {
                return [[v1, l2[idx]].join(what)];
            });
        }
        Arrays.zipWith = zipWith;
    })(PwE.Arrays || (PwE.Arrays = {}));
    var Arrays = PwE.Arrays;
})(PwE || (PwE = {}));
var PwE;
(function (PwE) {
    var AjaxCallback = (function () {
        function AjaxCallback(onSuccess, onFailure) {
            this.onSuccess = onSuccess;
            this.onFailure = onFailure;
        }
        AjaxCallback.prototype.failure = function (d) {
            this.failure(d);
        };

        AjaxCallback.prototype.success = function (d) {
            this.success(d);
        };
        return AjaxCallback;
    })();
    PwE.AjaxCallback = AjaxCallback;
})(PwE || (PwE = {}));
/*!
* pwe.ts (PwE)
*
* http://github.com/jsinglet/PwE
*
* Copyright 2013, John L. Singleton <jsinglet@gmail.com>
* Licensed under the MIT license
* http://www.opensource.org/licenses/mit-license.php
*
*/
///<reference path='requestHeaders.ts'/>
///<reference path='httpVerbs.ts'/>
///<reference path='arrays.ts'/>
///<reference path='ajaxCallback.ts'/>
var PwE;
(function (PwE) {
    var AjaxMode;
    (function (AjaxMode) {
        AjaxMode[AjaxMode["ASYNC"] = 0] = "ASYNC";

        AjaxMode[AjaxMode["SYNC"] = 1] = "SYNC";
    })(AjaxMode || (AjaxMode = {}));

    var Ajax = (function () {
        /**
        * Initializes a Ajax request object.
        *
        * @param callback - optional callback when this ajax function completes. If you set this value the Ajax request will be async.
        */
        function Ajax(callback) {
            this.xhr = new XMLHttpRequest();
            if (callback != undefined) {
                this.callback = callback;
                this.mode = AjaxMode.ASYNC;
            } else {
                this.callback = undefined;
                this.mode = AjaxMode.SYNC;
            }
        }
        Ajax.prototype.executeMethod = function (clazz, method, argNames, argValues) {
            return this.executeMethodSpec("/" + clazz + "/" + method, argNames, argValues);
        };

        Ajax.prototype.toPOSTString = function (argNames, argValues) {
            // escape all the argument values
            var transformedArguments = argValues.map(function (arg) {
                return encodeURIComponent(arg);
            });

            // convert it to name value pairs
            var zippedList = PwE.Arrays.zipWith(argNames, transformedArguments, "=");

            var postString = zippedList.join("&");

            return postString;
        };

        Ajax.prototype.getCallback = function () {
            return this.callback;
        };

        Ajax.prototype.executeMethodSpec = function (methodSpec, argNames, argValues) {
            if (this.mode == AjaxMode.ASYNC) {
                var thisObject = this;

                this.xhr.onreadystatechange = function () {
                    if (this.readyState == 4) {
                        if (this.status == 200) {
                            thisObject.getCallback().success(this);
                        } else {
                            thisObject.getCallback().failure(this);
                        }
                    }
                };

                this.xhr.open(PwE.HTTPVerbs.POST, methodSpec, true);
            } else {
                this.xhr.open(PwE.HTTPVerbs.POST, methodSpec, false);
            }
            this.xhr.setRequestHeader("Content-type", PwE.RequestHeader.AJAX_FORM_POST_CONTENT_TYPE);
            this.xhr.send(this.toPOSTString(argNames, argValues));
            return this.xhr;
        };
        return Ajax;
    })();
    PwE.Ajax = Ajax;
})(PwE || (PwE = {}));
//@ sourceMappingURL=_pwe.js.map
