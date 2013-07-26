

module PwE {


    export class AjaxCallback {

        private onSuccess : Function;
        private onFailure : Function;

        constructor(onSuccess:Function, onFailure:Function){
            this.onSuccess = onSuccess;
            this.onFailure = onFailure;
        }

        public failure(d:any){
            this.failure(d);
        }

        public success(d:any){
            this.success(d);
        }



    }
}