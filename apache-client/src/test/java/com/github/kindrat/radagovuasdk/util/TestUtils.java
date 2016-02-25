package com.github.kindrat.radagovuasdk.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class TestUtils {
    public static void print(String header, Iterable<?> iterable) {
        log.info("\n\n\n\t {}", header);
        iterable.forEach(o -> log.info(o.toString()));
    }
}
