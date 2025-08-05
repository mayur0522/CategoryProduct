package com.Rest.CategoryProduct.Java8.Stream;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Intermediate_Operation {
    public static void main(String args[]){
        System.out.println("Intermediate Operations : ");
        Executor cc = new Executor() {
            @Override
            public void execute(Runnable command) {

            }
        };

        Executors.newCachedThreadPool();

    }
}
