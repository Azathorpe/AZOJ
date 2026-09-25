package org.example.azoi.dto;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * code : -1 FAIL
 * 1  SUCCESS
 *
 * @param <T>
 */
public class Result<T> {
    public static final int SUCCESS = 1;
    public static final int FAIL = -1;

    T obj;
    int code;
    String msg;

    public Result() {
    }

    public Result(T obj, int code, String msg) {
        this.obj = obj;
        this.code = code;
        this.msg = msg;
    }

    public static <T> Result<T> ok() {
        return new Result<>(null, SUCCESS, "ok");
    }

    public static <T> Result<T> ok(T obj) {
        return new Result<>(obj, SUCCESS, "ok");
    }

    public static <T> Result<T> ok(String msg) {
        return new Result<>(null, SUCCESS, msg);
    }

    public static <T> Result<T> ok(T obj, String msg) {
        return new Result<>(obj, SUCCESS, msg);
    }

    public static <T> Result<T> fail() {
        return new Result<>(null, FAIL, "fail");
    }

    public static <T> Result<T> fail(T obj) {
        return new Result<>(obj, FAIL, "fail");
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(null, FAIL, msg);
    }

    public static <T> Result<T> fail(T obj, String msg) {
        return new Result<>(obj, FAIL, msg);
    }

    @Override
    public String toString() {
        return "This result is " + (getCode() == SUCCESS ? "SUCCESS" : "FAIL") + "\n Message: " + getMsg() + "\n obj: " + getObj();
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
