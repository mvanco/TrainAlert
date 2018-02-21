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
    public static final int ECODE_GENERIC_ERROR = -1;

    @Expose
    private @ErrorCode
    int errorCode;

    @Expose
    private T data;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ECODE_OK, ECODE_GENERIC_ERROR})
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
