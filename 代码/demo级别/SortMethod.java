package sort;

import java.util.Arrays;

/**
 * @author 11102342 suchang 2019/7/22
 * <p>
 * 包含:插排 / 快排 / 归并排序 / 堆排 / 对数器 / 排序算法检查方法
 */
public class SortMethod {

    /**
     * 插排
     */
    public static void insertSort(int[] arr) {
        if (null == arr || arr.length < 2) {
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            for (int j = i; j > 0; j--) {
                if (arr[j] < arr[j - 1]) {
                    swap(arr, j, j - 1);
                } else {
                    break;
                }
            }
        }
    }

    /**
     * 堆排序
     */
    public static void heapSort(int[] arr) {
        if (null == arr || arr.length < 2) {
            return;
        }
        // 建大根堆(上浮)
        createBigHeap(arr);
        int heapSize = arr.length - 1;
        while (heapSize > 0) {
            // 将最大值调到末尾(排好一个位置)
            swap(arr, 0, heapSize--);
            // 重新调整堆为大根堆(下沉)
            heapify(arr, 0, heapSize);
        }
    }

    private static void createBigHeap(int[] arr) {
        if (null == arr || arr.length < 2) {
            return;
        }
        int index = 0;
        while (index < arr.length) {
            int cur = index;
            int dad = (cur - 1) >> 1;
            while (dad >= 0 && arr[dad] < arr[cur]) {
                swap(arr, cur, dad);
                cur = dad;
                dad = (cur - 1) >> 1;
            }
            index++;
        }
    }

    private static void heapify(int[] arr, int cur, int heapSize) {
        if (null == arr || arr.length < 2 || cur >= heapSize) {
            return;
        }
        int leftChild = cur * 2 + 1;
        while (leftChild <= heapSize) {
            int rightChild = leftChild + 1;
            int biggerChildValue = rightChild > heapSize ? arr[leftChild] : Math.max(arr[leftChild], arr[rightChild]);
            if (biggerChildValue > arr[cur]) {
                int biggerChild = biggerChildValue == arr[leftChild] ? leftChild : rightChild;
                swap(arr, cur, biggerChild);
                cur = biggerChild;
                leftChild = cur * 2 + 1;
            } else {
                break;
            }
        }
    }


    /**
     * 随机快排
     */
    public static void randomQuickSort(int[] arr) {
        if (null == arr || arr.length < 2) {
            return;
        }
        randomQuickSort(arr, 0, arr.length - 1);
    }

    private static void randomQuickSort(int[] arr, int left, int right) {
        if (null == arr || arr.length < 2 || left >= right) {
            return;
        }
        int randomIndex = left + (int) (Math.random() * (right - left + 1));
        int[] equalZone = partition(arr, left, right, arr[randomIndex]);
        randomQuickSort(arr, left, equalZone[0] - 1);
        randomQuickSort(arr, equalZone[1] + 1, right);
    }

    private static int[] partition(int[] arr, int left, int right, int target) {
        if (null == arr || arr.length < 2 || left >= right) {
            return null;
        }
        int small = left - 1;
        int big = right + 1;
        int cur = left;
        while (cur < big) {
            if (arr[cur] < target) {
                swap(arr, ++small, cur++);
            } else if (arr[cur] == target) {
                cur++;
            } else {
                swap(arr, --big, cur);
            }
        }
        return new int[]{small + 1, big - 1};
    }


    /**
     * 归并排序
     */
    public static void mergeSort(int[] arr) {
        if (null == arr || arr.length < 2) {
            return;
        }
        mergeSort(arr, 0, arr.length - 1);
    }

    private static void mergeSort(int[] arr, int left, int right) {
        if (null == arr || arr.length < 2 || left >= right) {
            return;
        }
        int mid = (left + right) >> 1;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    private static void merge(int[] arr, int left, int mid, int right) {
        if (null == arr || arr.length < 2 || left >= right) {
            return;
        }
        int p1 = left;
        int p2 = mid + 1;
        int[] temp = new int[right - left + 1];
        int index = 0;
        while (p1 <= mid && p2 <= right) {
            temp[index++] = arr[p1] <= arr[p2] ? arr[p1++] : arr[p2++];
        }
        while (p1 <= mid) {
            temp[index++] = arr[p1++];
        }
        while (p2 <= right) {
            temp[index++] = arr[p2++];
        }
        for (int i : temp) {
            arr[left++] = i;
        }
    }

    /**
     * 检查排序方法
     * times:比较次数 size:数组的最大长度 range:数组中值可取的最大可能值
     */
    public static void checkSortMethod(int times, int size, int range) {
        int[] arr1 = new int[size];
        int[] arr2 = new int[size];
        boolean success = true;
        while (times-- > 0 && success == true) {
            arr1 = randomIntArray(size, range);
            arr2 = copyArray(arr1);
            // 一定正确的排序方法
            Arrays.sort(arr1);

            // 自己写的排序方法
            insertSort(arr2);
            for (int i = 0; i < arr1.length; i++) {
                if (arr1[i] != arr2[i]) {
                    success = false;
                    break;
                }

            }
        }
        if (times > 0) { // 两者不一致
            System.out.println("Method is wrong!");
            System.out.println("right: " + Arrays.toString(arr1));
            System.out.println("wrong: " + Arrays.toString(arr2));
        } else {
            System.out.println("Success!");
        }
    }

    /**
     * 产生随机数组
     */
    public static int[] randomIntArray(int maxSize, int maxValue) {
        int[] arr = new int[(int) (Math.random() * maxSize + 1)];
        int l = arr.length;
        for (int i = 0; i < l; i++) {
            // [0,range]
            arr[i] = (int) (Math.random() * (maxValue + 1));
        }
        return arr;
    }

    /**
     * 复制数组
     */
    public static int[] copyArray(int[] arr) {

        int[] copy = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            copy[i] = arr[i];
        }
        return copy;
    }

    /**
     * 交换数组元素
     */
    public static void swap(int[] arr, int p1, int p2) {
        if (p1 == p2) {
            return;
        }
        arr[p1] = arr[p1] ^ arr[p2];
        arr[p2] = arr[p1] ^ arr[p2];
        arr[p1] = arr[p1] ^ arr[p2];
    }

    public static void main(String[] args) {
        checkSortMethod(1000, 20, 100);
    }

}
