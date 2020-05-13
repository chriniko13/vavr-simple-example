package com.chriniko.tutorial.test.java;

class Person {

        private String name;

        public Person(String n) {
                this.name = n;
        }

        public String getName() {
                return name;
        }

        @Override
        public String toString() {
                return "Person{" +
                        "name='" + name + '\'' +
                        '}';
        }
}
