package org.example.azoi.dto;

/**
 * code : -1 error
 *        1  success
 * @param <T>
 */
public class Result<T> {
    public static final int SUCCESS = 1;
    public static final int FAIL = -1;

    T obj;
    int  code;
    String msg;

    public Result() {
    }

    public Result(T obj, int code, String msg) {
        this.obj = obj;
        this.code = code;
        this.msg = msg;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
