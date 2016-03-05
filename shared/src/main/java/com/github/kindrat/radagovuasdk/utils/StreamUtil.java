package com.github.kindrat.radagovuasdk.utils;

import lombok.experimental.UtilityClass;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@UtilityClass
public class StreamUtil {
    public static <T> Stream<T> takeWhile(Stream<T> stream, Predicate<T> predicate) {
        return StreamSupport.stream(new PredicateSpliterator<>(stream, predicate), false);
    }

    private static class PredicateSpliterator<T> extends Spliterators.AbstractSpliterator<T> {
        private final Iterator<T> iterator;
        private final Predicate<T> predicate;

        PredicateSpliterator(Stream<T> stream, Predicate<T> predicate) {
            super(Long.MAX_VALUE, IMMUTABLE);
            this.iterator = stream.iterator();
            this.predicate = predicate;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            if (iterator.hasNext()) {
                T value = iterator.next();
                if (predicate.test(value)) {
                    action.accept(value);
                    return true;
                }
            }
            return false;
        }
    }
}
