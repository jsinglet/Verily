package verily.lang;

public class VerilyParserModes {

    public enum VerilyModeType {
        TYPE_ROUTER("Router"), TYPE_METHOD("Method");

        private String type;

        VerilyModeType(String type){
            this.type = type;
        }

        public String toString(){return this.type;}
    };
}
