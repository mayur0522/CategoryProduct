package com.Rest.CategoryProduct.Java8.Stream.Questions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupWordsByTheirLength {
    public static void getWordsByTheirLength1(List<String> words){

        Map<String, Integer> ans = words.stream()
                .collect(Collectors.toMap(
                        word -> word,
                        word -> word.length()
                ));
        System.out.println(ans);
    }

    public static void getWordsByTheirLength2(List<String> words){

        Map<Integer, List<String>> ans = words.stream()
                        .collect(Collectors.groupingBy(String::length));
        System.out.println(ans);
    }

    public static void main(String args[]){
        List<String> list = Arrays.asList("Yogesh", "Vitthal", "Jathar");
        getWordsByTheirLength1(list);
        getWordsByTheirLength2(list);
    }
}
