public class InsertionSort {

    // Function to sort array using insertion sort
    static void sort(int[] a) {
        if (a == null || a.length <= 1) {
            return;
        }

        int n = a.length;
        for (int i = 1; i < n; ++i) {
            int key = a[i];
            int j = i - 1;

            // Move elements of arr[0..i-1], that are
            // greater than key, to one position ahead
            // of their current position
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }

            a[j + 1] = key;
            printArray(a);
        }
    }

    public static void main(String[] args) {
        int[] a = { 12, 11, 13, 5, 6 };

        System.out.println("Original array");
        printArray(a);
        System.out.println("Sorting array");
        sort(a);
        System.out.println("Sorted array");
        printArray(a);
    }

    static void printArray(int[] a) {
        if (a == null) {
            System.out.println("[]");
            return;
        }

        for (int value : a) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}
