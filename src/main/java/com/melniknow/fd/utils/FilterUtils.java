package com.melniknow.fd.utils;

import com.melniknow.fd.Context;
import com.melniknow.fd.advanced.Exception;
import com.melniknow.fd.core.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FilterUtils {
    public static ArrayList<Parser.Fork> filter(List<Parser.Fork> forks_) {
        // Применяем наши фильтры к вилкам
        return new ArrayList<>(
            forks_.stream()
                .filter(Filters.eventCountFilter)
                .filter(Filters.betTypesFilter)
                .filter(Filters.exceptionFilter)
                .filter(Filters.repeatFilter)
                .filter(Filters.cfFilter)
                .toList()
        );
    }

    private static class Filters {
        // Проверка количества вилок в ивенте
        public static final Predicate<Parser.Fork> eventCountFilter = fork -> {
            var count = Context.eventIdToCountSuccessForks.get(fork.eventId().longValue());
            return count == null || count < Context.parserParams.countFork().longValue();
        };

        // Проверка допустимых BetTypes
        public static final Predicate<Parser.Fork> betTypesFilter = fork -> {
            var betTypes = Context.sportToBetTypes.get(fork.sport());
            if (betTypes == null) return false;

            return betTypes.contains(fork.betInfo1().BK_bet_type()) && betTypes.contains(fork.betInfo2().BK_bet_type());
        };

        // Проверка исключений
        public static final Predicate<Parser.Fork> exceptionFilter = fork -> {
            var ex1 = Context.exceptionForBookmaker.get(BetUtils.getBookmakerByNameInApi(fork.betInfo1().BK_name()));
            var ex2 = Context.exceptionForBookmaker.get(BetUtils.getBookmakerByNameInApi(fork.betInfo2().BK_name()));

            return Exception.checkException(ex1, fork.sport(), fork.betInfo1(), true, fork.isMiddles()) &&
                Exception.checkException(ex2, fork.sport(), fork.betInfo2(), false, fork.isMiddles());
        };

        // Проверка на повтор вилок
        public static final Predicate<Parser.Fork> repeatFilter = fork -> {
            if (!Context.parserParams.isRepeatFork()) {
                return !Context.forksCache.containsKey(new MathUtils.ForkKey(fork.betInfo1().BK_name(), fork.betInfo1().BK_event_id(), fork.betInfo1().BK_bet()))
                    && !Context.forksCache.containsKey(new MathUtils.ForkKey(fork.betInfo2().BK_name(), fork.betInfo2().BK_event_id(), fork.betInfo2().BK_bet()));
            }
            return true;
        };

        // Проверка допустимости коэффициентов
        public static final Predicate<Parser.Fork> cfFilter = fork -> {
            var params1 = Context.betsParams.get(BetUtils.getBookmakerByNameInApi(fork.betInfo1().BK_name()));
            var params2 = Context.betsParams.get(BetUtils.getBookmakerByNameInApi(fork.betInfo2().BK_name()));

            return params1.maxCf().compareTo(fork.betInfo1().BK_cf()) >= 0 &&
                params1.minCf().compareTo(fork.betInfo1().BK_cf()) <= 0 &&
                params2.maxCf().compareTo(fork.betInfo2().BK_cf()) >= 0 &&
                params2.minCf().compareTo(fork.betInfo2().BK_cf()) <= 0;
        };
    }
}
