package com.github.olson1998.http;

import java.util.Collections;
import java.util.List;

public class ReadOnlyHttpHeaders extends AbstractHttpHeaders {

    public ReadOnlyHttpHeaders() {
        super(Collections.emptyList());
    }

    public ReadOnlyHttpHeaders(List<ReadOnlyHttpHeader> readOnlyHttpHeadersList) {
        super(writeHttpHeaders(readOnlyHttpHeadersList));
    }

    @Override
    public HttpHeaders readOnly() {
        return this;
    }
}
