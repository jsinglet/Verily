<#list modules as theModule>

//- The target module namespace
var ${theModule.name} = {};
${theModule.name}.Async = {};


    <#list theModule.functions as theFunction >

//- the synchronous interface
${theModule.name}.${theFunction.name} = function(${theFunction.argList}){

    var request = new PwE.Ajax();

    return request.executeMethod("${theModule.name}", "${theFunction.name}", [${theFunction.quotedArgList}], [${theFunction.argList}]);

}

//- the asynchronous interface
${theModule.name}.Async.${theFunction.name}  = function (${theFunction.argList}, success, failure) {

    var callbacks = new PwE.AjaxCallback(success, failure);

    var request = new PwE.Ajax(callbacks);

    request.executeMethod("${theModule.name}", "${theFunction.name}", [${theFunction.quotedArgList}], [${theFunction.argList}]);
}
    </#list>

</#list>

