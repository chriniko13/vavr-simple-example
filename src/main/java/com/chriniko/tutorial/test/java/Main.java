/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chriniko.tutorial.test.java;

import io.vavr.Lazy;
import io.vavr.Tuple;
import io.vavr.*;
import io.vavr.collection.CharSeq;
import io.vavr.collection.List;
import io.vavr.collection.Queue;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.collection.*;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import io.vavr.test.Arbitrary;
import io.vavr.test.Property;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.chriniko.tutorial.test.java.MyPatternsPatterns.$Event;
import static io.vavr.API.*;
import static io.vavr.Patterns.*;

/**
 * Contents:
 * list example
 * queue example
 * sortedSet example
 * stream
 * tuple
 * functions (composition, lifting, memoization, currying, partial application)
 * option
 * try
 * lazy
 * either
 * future
 * validation
 * try pattern matching
 * property checking
 * user-defined pattern for pattern matching
 *
 * @author chriniko
 */
public class Main {

    public static void main(String[] args) {

        // ------------- list example ----------------------------
        System.out.println("\n");

        List<Integer> l = List.of(1, 2, 3);

        List<Integer> l2 = l.append(0);

        System.out.println(l.hashCode() + " " + l);
        System.out.println(l2.hashCode() + " " + l2);


        // ------------- queue example ----------------------------
        System.out.println("\n");
        Queue<Integer> queue = Queue.of(1, 2, 3);

        Option<Tuple2<Integer, Queue<Integer>>> dequeued = queue.dequeueOption();


        Match(dequeued).of(
                Case($Some($Tuple2($(), $())), h -> run(() -> {
                    System.out.println("elem: " + h._1 + " --- remaining elems: " + h._2);
                })),
                Case($None(), v -> run(() -> System.out.println("no result for deque operation")))

        );


        // ------------- sortedSet example ----------------------------
        System.out.println("\n");

        TreeSet<Person> treeSet = TreeSet.of(Comparator.comparing(Person::getName), new Person("name1"), new Person("name2"));

        System.out.println(treeSet);


        // ------------- stream example ----------------------------
        System.out.println("\n");
        Stream<String> stream = Stream.of(1, 2, 3).map(Object::toString); // stream is a lazy linked list.
        System.out.println(stream);

        String s = Stream.of("one", "two", "three")
                .intersperse(", ")
                .foldLeft(new StringBuilder(), StringBuilder::append)
                .toString();
        System.out.println(s);


        // ------------- tuple example ----------------------------
        System.out.println("\n");
        Tuple2<String, Integer> java8 = Tuple.of("Java", 8);
        System.out.println(java8);

        Tuple2<String, Integer> that = java8.map(
                (_s, i) -> Tuple.of(_s.substring(2) + "vr", i / 8)
        );
        System.out.println(that);

        String applyResult = java8.apply(
                (_s, _i) -> _s.substring(2) + "vr " + _i / 8
        );
        System.out.println(applyResult);


        // --------------functions example -------------------------
        System.out.println("\n");

        /*
            In fact Vavr functional interfaces are Java 8 functional interfaces on steroids. They also provide features like:

                Composition

                Lifting

                Currying

                Memoization
         */
        class SomeService {
            public int doCalc(int x, int y) {
                System.out.println("will doCalc...");
                return x + y;
            }
        }


        Function2<Integer, Integer, Integer> fun2 = Function2.<Integer, Integer, Integer>of((x, y) -> new SomeService().doCalc(x, y));
        System.out.println("fun result is: " + fun2.apply(1, 1));

        System.out.println("composition is simple like: " + fun2.andThen(res -> res * 2).apply(2, 2));

        System.out.println("lifting is simple like: " + Function2.lift(fun2).apply(1, 1));
        System.out.println("lifting is simple like: " + Function2.liftTry(fun2).apply(1, 1));


        Function3<Integer, Integer, Integer, Integer> fun3 = Function3.of((x, y, z) -> x + y + z);
        System.out.println("partial application fun3 arity: " + fun3.apply(1).arity());
        System.out.println("currying fun3 arity: " + fun3.curried().apply(1).arity());

        Function2<Integer, Integer, Integer> memoizedFun2 = fun2.memoized();
        System.out.println("memoizedFun2 result is: " + memoizedFun2.apply(1, 1));
        System.out.println("memoizedFun2 result is: " + memoizedFun2.apply(1, 1));


        // ------------- option example -----------------------------------------
        System.out.println("\n");

        Option<String> maybeFoo = Option.of("foo");

        Option<String> maybeFooBar = maybeFoo
                .flatMap(_s -> Option.of((String) null))
                .map(_s -> _s.toUpperCase() + "bar");
        println("option maybeFooBar: " + maybeFooBar);


        // ------------- try example -----------------------------------------
        System.out.println("\n");

        Try.ofSupplier(() -> {
            throw new IllegalThreadStateException("thread illegal state");
        }).onFailure(error -> {
            println("try failure: " + error.getMessage());
        });


        // ------------- lazy example -----------------------------------------
        System.out.println("\n");

        /*
            Lazy is a monadic container type which represents a lazy evaluated value. Compared to a Supplier, Lazy is memoizing, i.e. it evaluates only once and therefore is referentially transparent.
         */
        Lazy<Double> randomLazyMemo = Lazy.of(() -> {
            System.out.println("calculating lazy things...");
            return Math.random();
        });
        System.out.println("randomLazyMemo: " + randomLazyMemo.get());
        System.out.println("randomLazyMemo: " + randomLazyMemo.get());


        // ------------- either example -----------------------------------------
        System.out.println("\n");

        class ErrorState {
            protected String reason;

            ErrorState(String reason) {
                this.reason = reason;
            }

            public String getReason() {
                return reason;
            }
        }
        class ChemicalErrorState extends ErrorState {
            ChemicalErrorState(String reason) {
                super(reason);
            }
        }

        class SuccessState {
            protected double output;

            SuccessState(double output) {
                this.output = output;
            }

            public double getOutput() {
                return output;
            }
        }

        Function1<Integer, Either<ErrorState, SuccessState>> performTask = i -> {
            if (i == 1) {
                return Either.left(new ChemicalErrorState("oops"));
            }
            return Either.right(new SuccessState(2.3));
        };

        System.out.println("either: " + performTask.apply(1));
        System.out.println("either: " + performTask.apply(2));

        Match(performTask.apply(1)).of(
                Case($Left($()), errorState -> run(() -> System.out.println("errorState: " + errorState.reason))),
                Case($Right($()), successState -> run(() -> System.out.println("successState: " + successState.output)))
        );


        // ------------- future example -----------------------------------------
        System.out.println("\n");

        var latch = new CountDownLatch(1);
        Future<Integer> f = Future.of(() -> {
            Thread.sleep(2000);
            return 42;
        });


        f.onComplete(t -> {
            var result = Match(t).of(
                    Case($Success($()), _result -> _result),
                    Case($Failure($()), _error -> -42)
            );
            println("future finished, result: " + result);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        // ------------- validation example -----------------------------------------
        System.out.println("\n");

        class Person {

            public final String name;
            public final int age;

            public Person(String name, int age) {
                this.name = name;
                this.age = age;
            }

            @Override
            public String toString() {
                return "Person(" + name + ", " + age + ")";
            }

        }

        class PersonValidator {

            private static final String VALID_NAME_CHARS = "[a-zA-Z ]";
            private static final int MIN_AGE = 0;

            public Validation<Seq<String>, Person> validatePerson(String name, int age) {
                return Validation.combine(validateName(name), validateAge(age)).ap(Person::new);
            }

            private Validation<String, String> validateName(String name) {
                return CharSeq.of(name).replaceAll(VALID_NAME_CHARS, "").transform(seq -> seq.isEmpty()
                        ? Validation.valid(name)
                        : Validation.invalid("Name contains invalid characters: '"
                        + seq.distinct().sorted() + "'"));
            }

            private Validation<String, Integer> validateAge(int age) {
                return age < MIN_AGE
                        ? Validation.invalid("Age must be at least " + MIN_AGE)
                        : Validation.valid(age);
            }

        }

        PersonValidator personValidator = new PersonValidator();

        Validation<Seq<String>, Person> valid = personValidator.validatePerson("John Doe", 30);
        println("validation valid: " + valid);

        Validation<Seq<String>, Person> invalid = personValidator.validatePerson("John? Doe!4", -1);
        println("validation invalid: " + invalid);


        // ------------- try pattern matching example ----------------------------
        System.out.println("\n");

        var x = 14;
        System.out.println(x);

        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                throw new CompletionException(ex);
            }
            return 1;
        }).orTimeout(2, TimeUnit.SECONDS);

        Try<Integer> t = Try
                .ofSupplier(cf::join)
                .onFailure(error -> System.out.println("error: " + error.getMessage()));

        System.out.println("finished: " + t);

        Void nothing = Match(t).of(
                // if tryInt is only success
                Case($Success($()), value -> run(() -> System.out.println("value: " + value))),
                // if tryInt is failure
                Case($Failure($()), value -> run(() -> System.out.println("error: " + value)))
        );


        // ------------- property checking example ----------------------------
        System.out.println("\n");

        Property.def("performTask(i: Int) works as expected")
                .forAll(Arbitrary.integer())
                .suchThat(test -> {

                    Either<ErrorState, SuccessState> result = performTask.apply(test);
                    if (test == 1) {
                        return result.isLeft();
                    }
                    return result.isRight();
                })
                .check()
                .assertIsSatisfied();


        // ------------- user-defined pattern for pattern matching ----------------------------

        //TODO....


        Outcome o1 = new Outcome();
        o1.detail = "Win A";
        o1.odd = 2.34;

        Outcome o2 = new Outcome();
        o2.detail = "Win B";
        o2.odd = 1.34;

        Outcome o3 = new Outcome();
        o3.detail = "Draw";
        o3.odd = 3.34;

        Game g1 = new Game();
        g1.name = "game 1";
        g1.outcome = o1;

        Game g2 = new Game();
        g2.name = "game 2";
        g2.outcome = o2;


        Event e = new Event();
        e.name = "Some League";
        e.game = g1;


        Match(e).of(

                Case($Event($(), $()), (v1, v2) -> run(() -> System.out.println("here, v1: " + v1 + " --- v2: " + v2))),

                Case($(),
                        () -> run(() -> System.out.println("not found"))
                )
        );


    } // main.

}

class Outcome {
    String detail;
    double odd;
}

class Game {
    String name;
    Outcome outcome;
}

class Event {
    String name;
    Game game;
}
