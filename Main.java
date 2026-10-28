//Pierce Mennen
//APCSA
//Game Show Project
//10-27-2026

package familyfeud;

import java.util.Random; //Provides random numbers for shuffling the array


//Selection Sort Integer Demo

//Creates a random arrangement of 1 through 20
//Then shows selection sort finding and moving the smallest remaining value during each pass
public class Main {
    public static void main(String[] args) {
        int[] numbers = new int[20];                                            //Stores the 20 integers being sorted
        Random random = new Random();                                           //Generates random positions for the shuffle

        //Fills the array with each integer from 1 through 20
        for (int i = 0; i < numbers.length; i++) {                              //Visits each array position
            numbers[i] = i + 1;                                                 //Store 1 in index 0, 2 in index 1, etc etc
        }

        //Shuffles the values so the starting array is in random order
        for (int i = numbers.length - 1; i > 0; i--) {                          //Works backward through the array
            int randomIndex = random.nextInt(i + 1);                            //Chooses an index from 0 through i
            int temp = numbers[i];                                              //Saves this value before we replace it
            numbers[i] = numbers[randomIndex];                                  //Moves the randomly chosen value to i
            numbers[randomIndex] = temp;                                        //Puts the saved value in the chosen index
        }

        //Prints a heading and show the unsorted array in a grid
        System.out.println("--------------------------------------------");                           //Header for the sort
        System.out.println("       SELECTION SORT: 1 THROUGH 20");
        System.out.println("--------------------------------------------");
        System.out.println("   Original numbers in random positions:");                               //Introduces the starting array
        printNumbers(numbers, 0);                                                           //Shows zero values as sorted because sorting has not started

        int comparisons = 0;                                                                            //Counts how many pairs of values are compared
        int swaps = 0;                                                                                  //Then counts how many times two values exchange positions

        //Each pass finds the smallest value in the unsorted part of the array
        for (int start = 0; start < numbers.length - 1; start++) {                                      //Chooses the next sorted position
            int minIndex = start;                                                                       //Assumes the first unsorted value is the smallest so far
            System.out.println();                                                                       //Leaves a blank line between passes for readability (below line is also a header)
            System.out.println("--------------- PASS " + (start + 1) + " ---------------");

            //Compares the current smallest value with every later unsorted value
            for (int check = start + 1; check < numbers.length; check++) {                              //Checks each remaining position
                System.out.printf("Step %d: [MIN %2d] compared with [CHECK %2d]",
                        comparisons + 1, numbers[minIndex], numbers[check]);                            //Shows the comparison

                if (numbers[check] < numbers[minIndex]) {                                               //Tests whether the checked value is smaller
                    minIndex = check;                                                                   //Remembers the index of the new smallest value
                    System.out.printf(" -> new minimum is %d%n", numbers[minIndex]);            //Announce the new minimum.
                } else { // The checked value is not smaller than the current minimum.
                    System.out.println(" -> minimum stays the same");                                //Explains that the minimum did not change
                }

                comparisons++;                                                                         //Counts the comparison's for the final summary
            }

            //Moves the smallest value found to the beginning of the unsorted section
            if (minIndex != start) {                                                                   //Swaps only if the minimum is not already in place
                int temp = numbers[start];                                                             //Save the value currently at the sorted position
                numbers[start] = numbers[minIndex];                                                    //Places the smallest value at the sorted position
                numbers[minIndex] = temp;                                                              //Moves the saved value to the minimum's old position
                swaps++;                                                                               //Counts this exchange of two array values
                System.out.println("Swap: moved " + numbers[start]
                        + " into position " + (start + 1) + ".");                                      //Reports the completed swap
            } else {                                                                                   //The first unsorted value was already the smallest
                System.out.println(numbers[start] + " is already in position "
                        + (start + 1) + ".");                                                          //Reports that no swap was needed
            }

            System.out.println("Array after pass " + (start + 1)
                    + " ( * = sorted position):");                                                     //Explains the asterisk in the grid
            printNumbers(numbers, start + 1);                                                          //Displays the array with this pass's value marked sorted
        }

        //Prints the final sorted array and the algorithm's totals
        System.out.println();                                                                          //Separates the final result from the last pass
        System.out.println("--------------- SORT COMPLETE ---------------");                        //Marks the end of sorting
        System.out.println("Final numbers:");                                                       //Introduces the completely sorted array
        printNumbers(numbers, numbers.length);                                                        //Marks all array values as sorted
        System.out.println("Comparisons: " + comparisons);                                            //Shows the total number of comparisons
        System.out.println("Swaps: " + swaps);                                                        //Shows the total number of swaps
    }


    //Prints the array as a four-column grid


    //A value is marked with an asterisk when its index is less than sortedCount

    public static void printNumbers(int[] numbers, int sortedCount) {
        for (int i = 0; i < numbers.length; i++) {                                          //Prints every value in the array
            if (i < sortedCount) {                                                          //Checks whether this position has already been sorted
                System.out.printf("| *%2d ", numbers[i]);                                   //Asterisk marks a value in the sorted section
            } else {                                                                        //This position is still part of the unsorted section
                System.out.printf("|  %2d ", numbers[i]);                                   //Prints the value without a sorted marker
            }

            if ((i + 1) % 4 == 0) {                                                         //Checks whether four values have been printed on this row
                System.out.println("|");                                                    //Closes the row and start a new one
            }
        }
        System.out.println();                                                               //Just adds spacing after the complete grid
    }
}
