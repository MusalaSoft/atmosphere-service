package com.musala.atmosphere.httprequest;

/**
 * Enumeration containing different HTTP request methods.
 * 
 * @author filareta.yordanova
 *
 */
public enum HttpRequestMethod {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT");

    private String requestMethod;

    private HttpRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    @Override
    public String toString() {
        return requestMethod;
    }
}
