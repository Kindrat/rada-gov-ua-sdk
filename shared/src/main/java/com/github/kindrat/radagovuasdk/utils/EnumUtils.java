package com.github.kindrat.radagovuasdk.utils;

import com.github.kindrat.radagovuasdk.exceptions.ValidationException;
import lombok.experimental.UtilityClass;

import static com.github.kindrat.radagovuasdk.utils.StringUtils.format;
import static java.util.Arrays.stream;

@UtilityClass
public class EnumUtils {
    public static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String value) {
        return stream(enumClass.getEnumConstants()).filter(eEnum -> eEnum.toString().equals(value)).findAny()
                .orElseThrow(() -> new ValidationException(format("Unknown value {} for enum {}", value, enumClass)));
    }
}
