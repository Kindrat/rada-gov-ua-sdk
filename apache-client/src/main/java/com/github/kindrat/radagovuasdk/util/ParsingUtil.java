package com.github.kindrat.radagovuasdk.util;

import com.github.kindrat.radagovuasdk.exceptions.RestClientException;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Stream;

@UtilityClass
public class ParsingUtil {
    public static Document wrapHttpEntity(HttpEntity entity) {
        try {
            return Jsoup.parse(IOUtils.toString(entity.getContent(), "windows-1251")); //fucking idiots
        } catch (IOException e) {
            throw new RestClientException(e);
        }
    }

    public static Stream<String> streamFromMatcher(Matcher matcher) {
        List<String> results = new ArrayList<>();
        while (matcher.find()) {
            results.add(matcher.group(2));
        }
        return results.stream();
    }
}
