package cz.intesys.trainalert.api;

import android.support.annotation.IntDef;

import com.google.gson.annotations.Expose;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Exposed field names must correspond to server response JSON
 */
public class ResponseApi<T> {

    public static final int ECODE_OK = 0;
    public static final int ECODE_GENERIC_ERROR = 1;
    public static final int ECODE_RESOURCE_ALREADY_EXIST = 2;
    public static final int ECODE_RESOURCE_DOES_NOT_EXIST = 3;
    public static final int ECODE_TRIP_REGISTATION = 4;
    public static final int ECODE_NO_TRIP_REGISTERED = 5;

    @Expose
    private @ErrorCode
    int errorCode;

    @Expose
    private T data;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ECODE_OK, ECODE_GENERIC_ERROR, ECODE_RESOURCE_ALREADY_EXIST, ECODE_RESOURCE_DOES_NOT_EXIST, ECODE_TRIP_REGISTATION, ECODE_NO_TRIP_REGISTERED})
    public @interface ErrorCode {
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}