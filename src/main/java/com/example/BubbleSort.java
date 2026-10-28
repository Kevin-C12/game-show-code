package com.example;

import java.util.Random;

public class BubbleSort {
    //Generates array of 6 random integers
    //Returns the randomly generated array
    public static int[] arrayGen() {
        //Creates Array
        int[] arr = new int[6];

        //Creates a random number generator
        Random rand = new Random();

        //Fills array with random numbers
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rand.nextInt(10);
        }

        //Returns array
        return arr;
    }

    //Sorts array using bubble sort
    //Saves each step in a 2D array
    //returns the 2D array
    public static int[][] bubbleSort(int[] arr) {
        
        //Creates 2D array
        int[][] steps = new int[16][6];

        //Tracks stored steps
        int count = 0;
        
        //Stores original array
        steps[count] = arr.clone();
        count ++;

        //Sorting process
        for (int i = 0; i < arr.length - 1; i++) {
            //Compares adjacent elements
            for (int n = 0; n < arr.length - 1 - i; n++) {
                //Check for if swap is needed
                if (arr[n] > arr[n + 1]) {
                    //Stores current element temporarily
                    int temp = arr[n];

                    //Swaps elements
                    arr[n] = arr[n + 1];
                    arr[n + 1] = temp;

                    //Stores the current sorting step
                    steps[count] = arr.clone();
                    count ++;
                }
            }
        }

        //Return sorted array and steps
        return steps;
    }
}
