package api.reqres.registration;

public class UnSuccesReg {
    private String error;

    public UnSuccesReg() {
    }

    public UnSuccesReg(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
