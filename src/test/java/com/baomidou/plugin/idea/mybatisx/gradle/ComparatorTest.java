package com.baomidou.plugin.idea.mybatisx.gradle;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ComparatorTest {
    public static void main(String[] args) {


        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparing(String::length,Comparator.naturalOrder()));
        priorityQueue.add("zs");
        priorityQueue.add("lis");
        priorityQueue.add("wangsu");
        System.out.println(priorityQueue.peek());
    }
}
