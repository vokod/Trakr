package com.awolity.trakr.view;

import com.google.android.gms.maps.model.CameraPosition;

class MainActivityStatus {

    private boolean isRecording = false;
    private boolean isContinueRecording = false;
    private CameraPosition cameraPosition;

    boolean isRecording() {
        return isRecording;
    }

    void setRecording(boolean recording) {
        isRecording = recording;
    }

    boolean isContinueRecording() {
        return isContinueRecording;
    }

    void setContinueRecording() {
        isContinueRecording = true;
    }

    boolean isThereACameraPosition() {
        return cameraPosition != null;
    }

    CameraPosition getCameraPosition() {
        return cameraPosition;
    }

    void setCameraPosition(CameraPosition cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

}
