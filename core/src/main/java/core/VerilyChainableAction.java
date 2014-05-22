package core;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public enum VerilyChainableAction {
    OK,         // OK means we did some processing and it worked.
    STOP,       // STOP means we should stop filter processing
    ERROR,      // ERROR means we tried to process a request, and something failed.
    CONTINUE;   // CONTINUE means we either a) won't process a request or don't need to or
                // b) we started to process the request but the request is expected to be processed by a
                // filter further down the chain.

    private Object reason;
    private int statusCode;

    VerilyChainableAction(){}
    VerilyChainableAction(Object reason){this.reason = reason;}

    public Object getReason(){return this.reason;}
    public void   setReason(Object reason){this.reason=reason;}
    public int getStatusCode(){return this.statusCode;}
    public void setStatusCode(int statusCode){this.statusCode=statusCode;}


}

