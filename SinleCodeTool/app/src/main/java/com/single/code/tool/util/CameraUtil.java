package com.single.code.tool.util;

import android.hardware.Camera;

/**
 * 摄像机
 * Created by admin on 2015/9/21.
 */
public class CameraUtil {
    /**
     * @return 前置摄像头的ID
     */
    public static int getFrontCameraId() {
        return getCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    /**
     * @return 后置摄像头的ID
     */
    public static int getBackCameraId() {
        return getCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    private static int getCameraId(int tagInfo) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        // 开始遍历摄像头，得到camera info
        int cameraId, cameraCount;
        for (cameraId = 0, cameraCount = Camera.getNumberOfCameras(); cameraId < cameraCount; cameraId++) {
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == tagInfo) {
                break;
            }
        }
        return cameraId;
    }
}
