var MyClass = (function () {
    function MyClass(what) {
        this.test = what;
    }
    MyClass.prototype.doSomething = function () {
        console.log(this.test);
    };
    return MyClass;
})();
exports.MyClass = MyClass;

//@ sourceMappingURL=pwe.js.map
