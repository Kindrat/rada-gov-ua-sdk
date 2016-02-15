package com.github.kindrat.radagovuasdk.utils;

import lombok.experimental.UtilityClass;
import org.slf4j.helpers.MessageFormatter;

@UtilityClass
public class StringUtils {
    public static String format(String template, Object... args) {
        return MessageFormatter.arrayFormat(template, args).getMessage();
    }
}
