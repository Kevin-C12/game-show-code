// William Harrison
//APCSA
//Game show project
// 10-28-2026

package gameshow.src.main;

                                                    // Sorts an integer array by inserting each value into its correct position.
public class InsertionSort {
  
                                                    // Sorts the array in place using the insertion sort .
    void sort(int a[])
    {
        int n = a.length;                           // Number of values in the array.

                                                    // The first value is already a sorted section of length one.
        for (int i = 1; i < n; ++i) {
            int k = a[i];                           // Value to insert into the sorted section.
            int j = i - 1;                          // Start at the end of the sorted section.

                                                    // Shift larger values one position right to make room for k.
            while (j >= 0 && a[j] > k) {
                a[j + 1] = a[j];
                j = j - 1;                          // Continue checking values to the left.
            }

                                                    // Place k in the opening created by the shifts.
            a[j + 1] = k;

                                                    // Display the array after this insertion step.
            printArray(a);
        }


    }

                                                    // Demonstrates insertion sort with a sample array.
    public static void main(String args[])
    {
        int a[] = { 12, 11, 13, 5, 6 };

        InsertionSort ob = new InsertionSort();
        printArray(a);                              // Show the array before sorting.
        System.out.println("Sorting array");
        ob.sort(a);
        System.out.println("Sorted array");
        printArray(a);                              // Show the completed sorted array.
    }

                                                    // Prints every array value on one line.
    static void printArray(int a[])
    {
        int n = a.length;
        for (int i = 0; i < n; ++i)
            System.out.print(a[i] + " ");
        System.out.println();                       // Start the next output on a new line.
    }


}
