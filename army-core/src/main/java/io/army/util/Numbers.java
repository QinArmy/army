package io.army.util;


public abstract class Numbers {

    protected Numbers() {
        throw new UnsupportedOperationException();
    }


    public static int intArraySum(int[] intArray) {
        int sum = 0;
        for (int i : intArray) {
            sum += i;
        }
        return sum;
    }


}
