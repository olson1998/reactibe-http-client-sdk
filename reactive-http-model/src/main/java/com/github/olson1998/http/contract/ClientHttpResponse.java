package com.github.olson1998.http.contract;

import com.github.olson1998.http.HttpHeaders;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class ClientHttpResponse<C> implements WebResponse<C> {

    private final int statusCode;

    private final HttpHeaders httpHeaders;

    private final C body;

    @Override
    public String toString() {
        var webResponse = new StringBuilder();
        webResponse.append("\n").append(statusCode);
        var httpHeadersList = httpHeaders.getHttpHeaderList();
        for (var httpHeader : httpHeadersList) {
            webResponse.append("\n").append(httpHeader.getKey());
            Optional.ofNullable(httpHeader.getValue()).ifPresent(headerValue -> webResponse.append(": ").append(headerValue));
        }
        Optional.ofNullable(body).ifPresent(bodyValue -> webResponse.append("\n").append(bodyValue));
        return webResponse.toString();
    }
}
