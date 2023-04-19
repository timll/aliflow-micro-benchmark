package com.alipay.infoflow.util;

import java.io.IOException;
import java.net.URISyntaxException;

public class TestConfig {
    public static FlowDroidTestWrapper getDefault() {
        try {
            return new FlowDroidTestWrapper();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
