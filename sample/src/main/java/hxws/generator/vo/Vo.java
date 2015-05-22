package hxws.generator.vo;

import java.io.Serializable;

/**
 * Created by hxws on 15-5-18.
 */
public class Vo implements Serializable{

    private String err_msg;
    private int error;

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public int getError() {
        return error;
    }
}
