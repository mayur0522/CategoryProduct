package com.Rest.CategoryProduct.Java8.Functional_Interface;

import java.util.function.Function;
import java.util.function.Predicate;

// This class contains basics of Functional Interface
public class Basics {

    public static void main(String args[]){
        // Default Interfaces

        /* Predicate
         Returns: true/false
         true if the input argument matches the predicate, otherwise false */
        Predicate<Integer> isEven = x -> x % 2 == 0;
        boolean result1 = isEven.test(20);
        boolean result2 = isEven.test(21);
        System.out.println(result1);
        System.out.println(result2);


        // Function
        Function<Integer, Integer> square = x -> x*x;
        Integer resultF = square.apply(4);
        System.out.println(resultF);
    }
}
