package com.example;

import java.util.Random;

public class BubbleSort {
    public static int[] arrayGen() {
        int[] arr = new int[6];

        Random rand = new Random();

        for (int i = 0; i < arr.length; i++) {
            arr[i] = rand.nextInt(0,10);
        }

        return arr;
    }

    public static int[][] bubbleSort(int[] arr) {
        
        int[][] steps = new int[16][6];
        int count = 0;
        steps[count] = arr.clone();
        count ++;

        for (int i = 0; i < arr.length - 1; i++) {
            for (int n = 0; n < arr.length - 1 - i; n++) {
                if (arr[n] > arr[n + 1]) {
                    int temp = arr[n];

                    arr[n] = arr[n + 1];
                    arr[n + 1] = temp;

                    steps[count] = arr.clone();
                    count ++;
                }
            }
        }

        return steps;
    }
}
