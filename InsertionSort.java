// William Harrison
//APCSA
//Game show project
// 10-28-2026

package gameshow.src.main;


public class InsertionSort {
  
    // Function to sort array using insertion sort
    void sort(int a[])
    {
        int n = a.length;
        for (int i = 1; i < n; ++i) {
            int k = a[i];
            int j = i - 1;

            // Move elements of arr[0..i-1], that are
            // greater than key, to one position ahead
            // of their current position
            while (j >= 0 && a[j] > k) {
                a[j + 1] = a[j];
                
                j = j - 1;
            }
            
            a[j + 1] = k;
            
            printArray(a);
        }
        
    }

    public static void main(String args[])
    {
        int a[] = { 12, 11, 13, 5, 6 };

        InsertionSort ob = new InsertionSort();
        printArray(a);
        System.out.println("Sorting array");
        ob.sort(a);
        System.out.println("Sorted array");
        printArray(a);
        
    }

    static void printArray(int a[])
    {
        int n = a.length;
        for (int i = 0; i < n; ++i)
            System.out.print(a[i] + " ");
        System.out.println();
    }
}
