//- Declare the target module namespace
var TestBasic = {};

//- the synchronous interface
TestBasic.simpleFunction = function (arg1) {

    var request = new PwE.Ajax();

    return request.executeMethod("TestBasic", "simpleFunction", ["arg1"], [arg1])
}

//- the asynchronous interface
TestBasic.Async = {};

TestBasic.Async.simpleFunction = function (arg1, success, failure) {

    var callbacks = new PwE.AjaxCallback(success, failure);

    var request = new PwE.Ajax(callbacks);

    request.executeMethod("TestBasic", "simpleFunction", ["arg1"], [arg1])
}
