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
module PwE {

    enum AjaxMode {
        ASYNC,
        SYNC
    }

    export class Ajax {


        private xhr:XMLHttpRequest;
        private callback:AjaxCallback;
        private mode:AjaxMode;

        /**
         * Initializes a Ajax request object.
         *
         * @param callback - optional callback when this ajax function completes. If you set this value the Ajax request will be async.
         */
            constructor(callback?) {
            this.xhr = new XMLHttpRequest();
            if (callback != undefined) {
                this.callback = callback;
                this.mode = AjaxMode.ASYNC;
            } else {
                this.callback = undefined;
                this.mode = AjaxMode.SYNC;
            }
        }

        public executeMethod(clazz:string, method:string, argNames:string[], argValues:string[]) {
            return this.executeMethodSpec("/" + clazz + "/" + method, argNames, argValues);
        }

        private toPOSTString(argNames:string[], argValues:string[]):string {

            // escape all the argument values
            var transformedArguments = argValues.map(arg => encodeURIComponent(arg));

            // convert it to name value pairs
            var zippedList = Arrays.zipWith(argNames, transformedArguments, "=");

            var postString = zippedList.join("&");

            return postString;

        }

        public getCallback():AjaxCallback {
            return this.callback;
        }

        public executeMethodSpec(methodSpec:string, argNames:string[], argValues:string[]) {


            if (this.mode == AjaxMode.ASYNC) {

                var thisObject = this;

                this.xhr.onreadystatechange = function () {

                    if (this.readyState == 4) {

                        // success
                        if (this.status == 200) {
                            thisObject.getCallback().success(this);
                        } else {
                            thisObject.getCallback().failure(this);
                        }
                    }

                }

                this.xhr.open(HTTPVerbs.POST, methodSpec, true);
            } else {
                this.xhr.open(HTTPVerbs.POST, methodSpec, false);
            }
            this.xhr.setRequestHeader("Content-type", RequestHeader.AJAX_FORM_POST_CONTENT_TYPE);
            this.xhr.send(this.toPOSTString(argNames, argValues));
            return this.xhr;

        }


    }
}





