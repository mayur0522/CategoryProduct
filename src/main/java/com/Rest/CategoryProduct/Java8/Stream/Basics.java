package com.Rest.CategoryProduct.Java8.Stream;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class Basics {

    public static void main(String args[]){
        List<Integer> num =  Arrays.asList(1,2,3,4,5,6,7,8,9,10,20,40,45);
        List<String> names = Arrays.asList("yogesh", "java", "stream");

        // Sum
        getSum(num);

        // Even
        even(num);

        // Odd
        odd(num);

        // Even and Odd
        // Using partitionedBy from collectors class
        evenOdd(num);

        // Convert String to Uppercase
        setStringToUppercase(names);

        // Convert First Letter of Word to uppercase From the list of strings.
        setFirstLetterOfStringToUppercase(names);

        //Number greater than n
        int n = 20;
        getCunt(num, n);
    }

    public static void getSum(List<Integer> list){
        int sum = list.stream().reduce(Integer::sum).get();
        System.out.println("Sum of all numbers : "+sum);
    }

    public static void even(List<Integer> list){

        List<Integer> even = list.stream()
                .filter(num -> num % 2 == 0)
                .collect(Collectors.toList());

        System.out.println("Even Numbers : "+even);
    }

    public static void odd(List<Integer> list){

        List<Integer> odd = list.stream()
                .filter(num -> num % 2 == 1)
                .collect(Collectors.toList());

        System.out.println("Odd Numbers : "+odd);
    }

    public static void evenOdd(List<Integer> list){

        Map<Boolean, List<Integer>> partitionedNum = list.stream()
                .collect(Collectors.partitioningBy(num -> num%2 == 0));

        System.out.println("Even Numbers of Group : "+partitionedNum.get(true));
        System.out.println("Odd Numbers of Group : "+partitionedNum.get(false));
    }

    // Convert list of strings to uppercase
    public static void setStringToUppercase(List<String> names){

        List<String> uppercaseNames = names.stream()
                .map(str -> str.toUpperCase())
                .collect(Collectors.toList());

        System.out.println("Uppercase String : "+uppercaseNames);
    }

    // Convert First Letter of Word to uppercase From the list of strings.
    public static void setFirstLetterOfStringToUppercase(List<String> names){

        List<String> uppercaseNames = names.stream()
                .map(word -> word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase())
                        .collect(Collectors.toList());

        System.out.println("Updated String : "+uppercaseNames);
    }

    // Find the count of elements greater than n
    public static void getCunt(List<Integer> list, int n){

        List<Integer> count = list.stream()
                .filter(num -> num > n)
                .collect(Collectors.toList());
        System.out.println("Numbers greater than "+n+": "+count);
    }
}
