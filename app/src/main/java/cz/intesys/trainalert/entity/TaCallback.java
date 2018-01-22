package cz.intesys.trainalert.entity;

public interface TaCallback<T> {
    void onResponse(T response);

    void onFailure(Throwable t);

}
