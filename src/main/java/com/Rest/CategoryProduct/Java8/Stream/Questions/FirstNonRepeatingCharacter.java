package com.Rest.CategoryProduct.Java8.Stream.Questions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FirstNonRepeatingCharacter {

    public static char getChar(String str){
        Character ans = str.toLowerCase().chars()
                .mapToObj(ch -> (char) ch)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .filter(e -> e.getValue() == 1)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        return ans;
    }

    public static void main(String args[]){
        String str = "programming";
        System.out.println(getChar(str));
    }
}
