package ggappsdev.Models;

public class GetRefOwner {

    private String codeOwner;

    public GetRefOwner(){}

    public GetRefOwner(String codeOwner) {
        this.codeOwner = codeOwner;
    }

    public String getCodeOwner() {
        return codeOwner;
    }

    public void setCodeOwner(String codeOwner) {
        this.codeOwner = codeOwner;
    }
}
