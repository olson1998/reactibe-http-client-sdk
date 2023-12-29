package com.github.olson1998.http.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonAttributes {

    public static final String WEB_REQUEST_TIMEOUT_DURATION = "web.request.timeout-duration";

    public static final String WEB_REQUEST_BODY_MAX_SIZE = "web.request.body.max-size";

    public static final String WEB_RESPONSE_BODY_MAX_SIZE = "web.response.body.max-size";
}
