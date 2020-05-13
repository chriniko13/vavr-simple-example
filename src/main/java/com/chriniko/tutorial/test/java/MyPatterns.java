package com.chriniko.tutorial.test.java;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.match.annotation.Patterns;
import io.vavr.match.annotation.Unapply;

@Patterns
class MyPatterns {

    @Unapply
    static Tuple2<String, Tuple2<String, Tuple2<String, Double>>> Event(Event e) {
        return Tuple.of(e.name, Tuple.of(e.game.name, Tuple.of(e.game.outcome.detail, e.game.outcome.odd)));
    }

}
