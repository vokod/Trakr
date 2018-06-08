package com.awolity.trakr.trackrecorder;

public class AltitudeFilter {


    private boolean isFirstTime = true;
    private double[] alphas;
    private int size;
    private double invertedSize;

    public AltitudeFilter(int parameter) {
        size = parameter;
        alphas = new double[size];
        invertedSize = (double)1/(double) size;
    }

    // Deemphasize transient forces
    private double lowPass() {

        double result=0;
        for (int i = 0; i< size; i++) {
            result += alphas[i]* invertedSize;
        }
        return result;
    }

    public double filterNext(double current) {
        if (isFirstTime) {
            for (int i = 0; i< size; i++){
                alphas[i]=current;
            }
            isFirstTime = false;
        } else {
            for (int i = 0; i< size -1; i++){
                alphas[i]=alphas[i+1];
            }
            alphas[size -1]=current;
        }
        return lowPass();
    }

}
