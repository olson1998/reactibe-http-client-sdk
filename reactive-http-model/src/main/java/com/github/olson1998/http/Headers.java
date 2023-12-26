package com.github.olson1998.http;

import java.util.ArrayList;
import java.util.List;

public class Headers extends AbstractHttpHeaders {

    public Headers() {
        super(new ArrayList<>());
    }

    public Headers(List<HttpHeader> httpHeaders) {
        super(writeHttpHeaders(httpHeaders));
    }

    @Override
    public HttpHeaders readOnly() {
        var readOnly = getHttpHeaderList().stream()
                .map(httpHeader -> new ReadOnlyHttpHeader(httpHeader.getKey(), httpHeader.getValue()))
                .toList();
        return new ReadOnlyHttpHeaders(readOnly);
    }

}
