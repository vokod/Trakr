package com.awolity.trakr.trackrecorder;

class LowPassFilter {

    private boolean isFirstTime = true;
    private final double[] alphas;
    private final int size;
    private final double invertedSize;

    LowPassFilter(int parameter) {
        size = parameter;
        alphas = new double[size];
        invertedSize = (double) 1 / (double) size;
    }

    // Deemphasize transient forces
    private double lowPass() {

        double result = 0;
        for (int i = 0; i < size; i++) {
            result += alphas[i] * invertedSize;
        }
        return result;
    }

    double filterNext(double current) {
        if (isFirstTime) {
            for (int i = 0; i < size; i++) {
                alphas[i] = current;
            }
            isFirstTime = false;
        } else {
            System.arraycopy(alphas, 1, alphas, 0, size - 1);
            alphas[size - 1] = current;
        }
        return lowPass();
    }

}
