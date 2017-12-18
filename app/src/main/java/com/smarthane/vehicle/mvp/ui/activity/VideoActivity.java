/*
 *  UVCCamera
 *  library and sample to access to UVC web camera on non-rooted Android device
 *
 * Copyright (c) 2014-2017 saki t_saki@serenegiant.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  All files in the folder are under this Apache License, Version 2.0.
 *  Files in the libjpeg-turbo, libusb, libuvc, rapidjson folder
 *  may have a different license, see the respective files.
 */

package com.smarthane.vehicle.mvp.ui.activity;

import android.Manifest;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.serenegiant.common.BaseActivity;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.USBMonitor.OnDeviceConnectListener;
import com.serenegiant.usb.USBMonitor.UsbControlBlock;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usbcameracommon.UVCCameraHandler;
import com.serenegiant.widget.CameraViewInterface;
import com.serenegiant.widget.UVCCameraTextureView;
import com.smarthane.vehicle.R;

import kr.co.namee.permissiongen.PermissionGen;

/**
 * Show side by side view from two camera.
 * You cane record video images from both camera, but secondarily started recording can not record
 * audio because of limitation of Android AudioRecord(only one instance of AudioRecord is available
 * on the device) now.
 */
public final class VideoActivity extends BaseActivity implements CameraDialog.CameraDialogParent {
    private static final boolean DEBUG = false;	// FIXME set false when production
    private static final String TAG = "VideoActivity";

    private static final float[] BANDWIDTH_FACTORS = { 0.5f, 0.5f, 0.5f, 0.5f };

    // for accessing USB and USB camera
    private USBMonitor mUSBMonitor;

    private UVCCameraHandler mHandlerR;
    private CameraViewInterface mUVCCameraViewR;
    private ImageButton mCaptureButtonR;
    private Surface mRightPreviewSurface;

    private UVCCameraHandler mHandlerL;
    private CameraViewInterface mUVCCameraViewL;
    private ImageButton mCaptureButtonL;
    private Surface mLeftPreviewSurface;


    private int currentCamera = 1;

    //////////////////////////////
    private UVCCameraHandler mHandler3;
    private CameraViewInterface mUVCCameraView3;
    private ImageButton mCaptureButton3;
    private Surface m3PreviewSurface;

    private UVCCameraHandler mHandler4;
    private CameraViewInterface mUVCCameraView4;
    private ImageButton mCaptureButton4;
    private Surface m4PreviewSurface;

//	private CameraView cameraView;
//	private Fotoapparat fotoapparat;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        PermissionGen.with(this).addRequestCode(100).permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).request();

        findViewById(R.id.RelativeLayout1).setOnClickListener(mOnClickListener);
        mUVCCameraViewL = (CameraViewInterface)findViewById(R.id.camera_view_L);
        mUVCCameraViewL.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH / (float)UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        ((UVCCameraTextureView)mUVCCameraViewL).setOnClickListener(mOnClickListener);
        mCaptureButtonL = (ImageButton)findViewById(R.id.capture_button_L);
        mCaptureButtonL.setOnClickListener(mOnClickListener);
        mCaptureButtonL.setVisibility(View.INVISIBLE);
        mHandlerL = UVCCameraHandler.createHandler(this, mUVCCameraViewL, UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, BANDWIDTH_FACTORS[0]);

        mUVCCameraViewR = (CameraViewInterface)findViewById(R.id.camera_view_R);
        mUVCCameraViewR.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH / (float)UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        ((UVCCameraTextureView)mUVCCameraViewR).setOnClickListener(mOnClickListener);
        mCaptureButtonR = (ImageButton)findViewById(R.id.capture_button_R);
        mCaptureButtonR.setOnClickListener(mOnClickListener);
        mCaptureButtonR.setVisibility(View.INVISIBLE);
        mHandlerR = UVCCameraHandler.createHandler(this, mUVCCameraViewR, UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, BANDWIDTH_FACTORS[1]);

        mUVCCameraView3 = (CameraViewInterface)findViewById(R.id.camera_view_3);
        mUVCCameraView3.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH / (float)UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        ((UVCCameraTextureView)mUVCCameraView3).setOnClickListener(mOnClickListener);
        mCaptureButton3 = (ImageButton)findViewById(R.id.capture_button_3);
        mCaptureButton3.setOnClickListener(mOnClickListener);
        mCaptureButton3.setVisibility(View.INVISIBLE);
        mHandler3 = UVCCameraHandler.createHandler(this, mUVCCameraView3, UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, BANDWIDTH_FACTORS[2]);

        mUVCCameraView4 = (CameraViewInterface)findViewById(R.id.camera_view_4);
        mUVCCameraView4.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH / (float)UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        ((UVCCameraTextureView)mUVCCameraView4).setOnClickListener(mOnClickListener);
        mCaptureButton4 = (ImageButton)findViewById(R.id.capture_button_4);
        mCaptureButton4.setOnClickListener(mOnClickListener);
        mCaptureButton4.setVisibility(View.INVISIBLE);
        mHandler4 = UVCCameraHandler.createHandler(this, mUVCCameraView4, UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, BANDWIDTH_FACTORS[3]);

        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);



//		cameraView = (CameraView) findViewById(R.id.camera_view_4);
//		fotoapparat = Fotoapparat
//				.with(this)
//				.into(cameraView)
//				.lensPosition(new SelectorFunction<Collection<LensPosition>, LensPosition>() {
//					@Override
//					public LensPosition select(Collection<LensPosition> lensPositions) {
//						return LensPosition.FRONT;
//					}
//				})
//				.build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUSBMonitor.register();
        if (mUVCCameraViewR != null)
            mUVCCameraViewR.onResume();
        if (mUVCCameraViewL != null)
            mUVCCameraViewL.onResume();
        if (mUVCCameraView3 != null)
            mUVCCameraView3.onResume();
        if (mUVCCameraView4 != null)
            mUVCCameraView4.onResume();

//		fotoapparat.start();
    }

    @Override
    protected void onStop() {
        mHandlerR.close();
        if (mUVCCameraViewR != null)
            mUVCCameraViewR.onPause();
        mCaptureButtonR.setVisibility(View.INVISIBLE);
        mHandlerL.close();
        if (mUVCCameraViewL != null)
            mUVCCameraViewL.onPause();
        mCaptureButtonL.setVisibility(View.INVISIBLE);
        mHandler3.close();
        if (mUVCCameraView3 != null)
            mUVCCameraView3.onPause();
        mCaptureButton3.setVisibility(View.INVISIBLE);
        mHandler4.close();
        if (mUVCCameraView4 != null)
            mUVCCameraView4.onPause();
        mCaptureButton4.setVisibility(View.INVISIBLE);
        mUSBMonitor.unregister();

        currentCamera = 1;
//		fotoapparat.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mHandlerR != null) {
            mHandlerR = null;
        }
        if (mHandlerL != null) {
            mHandlerL = null;
        }
        if (mHandler3 != null) {
            mHandler3 = null;
        }
        if (mHandler4 != null) {
            mHandler4 = null;
        }
        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
        mUVCCameraViewR = null;
        mCaptureButtonR = null;
        mUVCCameraViewL = null;
        mCaptureButtonL = null;
        mUVCCameraView3 = null;
        mCaptureButton3 = null;
        mUVCCameraView4 = null;
        mCaptureButton4 = null;
        super.onDestroy();
    }

    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            switch (view.getId()) {
                case R.id.camera_view_L:
                    currentCamera = 1;
                    if (mHandlerL != null) {
                        if (!mHandlerL.isOpened()) {
                            CameraDialog.showDialog(VideoActivity.this);
                        } else {
                            mHandlerL.close();
                            setCameraButton();
                        }
                    }
                    break;
                case R.id.capture_button_L:
                    if (mHandlerL != null) {
                        if (mHandlerL.isOpened()) {
                            if (checkPermissionWriteExternalStorage() && checkPermissionAudio()) {
                                if (!mHandlerL.isRecording()) {
                                    mCaptureButtonL.setColorFilter(0xffff0000);	// turn red
                                    mHandlerL.startRecording();
                                } else {
                                    mCaptureButtonL.setColorFilter(0);	// return to default color
                                    mHandlerL.stopRecording();
                                }
                            }
                        }
                    }
                    break;
                case R.id.camera_view_R:
                    currentCamera = 2;
                    if (mHandlerR != null) {
                        if (!mHandlerR.isOpened()) {
                            CameraDialog.showDialog(VideoActivity.this);
                        } else {
                            mHandlerR.close();
                            setCameraButton();
                        }
                    }
                    break;
                case R.id.capture_button_R:
                    if (mHandlerR != null) {
                        if (mHandlerR.isOpened()) {
                            if (checkPermissionWriteExternalStorage() && checkPermissionAudio()) {
                                if (!mHandlerR.isRecording()) {
                                    mCaptureButtonR.setColorFilter(0xffff0000);	// turn red
                                    mHandlerR.startRecording();
                                } else {
                                    mCaptureButtonR.setColorFilter(0);	// return to default color
                                    mHandlerR.stopRecording();
                                }
                            }
                        }
                    }
                    break;

                //////////////////////////////
                case R.id.camera_view_3:
                    currentCamera = 3;
                    if (mHandler3 != null) {
                        if (!mHandler3.isOpened()) {
                            CameraDialog.showDialog(VideoActivity.this);
                        } else {
                            mHandler3.close();
                            setCameraButton();
                        }
                    }
                    break;
                case R.id.capture_button_3:
                    if (mHandler3 != null) {
                        if (mHandler3.isOpened()) {
                            if (checkPermissionWriteExternalStorage() && checkPermissionAudio()) {
                                if (!mHandler3.isRecording()) {
                                    mCaptureButton3.setColorFilter(0xffff0000);	// turn red
                                    mHandler3.startRecording();
                                } else {
                                    mCaptureButton3.setColorFilter(0);	// return to default color
                                    mHandler3.stopRecording();
                                }
                            }
                        }
                    }
                    break;
                case R.id.camera_view_4:
                    currentCamera = 4;
                    if (mHandler4 != null) {
                        if (!mHandler4.isOpened()) {
                            CameraDialog.showDialog(VideoActivity.this);
                        } else {
                            mHandler4.close();
                            setCameraButton();
                        }
                    }
                    break;
                case R.id.capture_button_4:
                    if (mHandler4 != null) {
                        if (mHandler4.isOpened()) {
                            if (checkPermissionWriteExternalStorage() && checkPermissionAudio()) {
                                if (!mHandler4.isRecording()) {
                                    mCaptureButton4.setColorFilter(0xffff0000);	// turn red
                                    mHandler4.startRecording();
                                } else {
                                    mCaptureButton4.setColorFilter(0);	// return to default color
                                    mHandler4.stopRecording();
                                }
                            }
                        }
                    }
                    break;
            }
        }
    };

    private final OnDeviceConnectListener mOnDeviceConnectListener = new OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onAttach:" + device);
            Toast.makeText(VideoActivity.this, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnect(final UsbDevice device, final UsbControlBlock ctrlBlock, final boolean createNew) {
            if (DEBUG) Log.v(TAG, "onConnect:" + device);
            if (currentCamera==1&&!mHandlerL.isOpened()) {
                mHandlerL.open(ctrlBlock);
                final SurfaceTexture st = mUVCCameraViewL.getSurfaceTexture();
                mHandlerL.startPreview(new Surface(st));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCaptureButtonL.setVisibility(View.VISIBLE);
                    }
                });
            } else if (currentCamera==2&&!mHandlerR.isOpened()) {
                mHandlerR.open(ctrlBlock);
                final SurfaceTexture st = mUVCCameraViewR.getSurfaceTexture();
                mHandlerR.startPreview(new Surface(st));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCaptureButtonR.setVisibility(View.VISIBLE);
                    }
                });
            } else if (currentCamera==3&&!mHandler3.isOpened()) {
                mHandler3.open(ctrlBlock);
                final SurfaceTexture st = mUVCCameraView3.getSurfaceTexture();
                mHandler3.startPreview(new Surface(st));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCaptureButton3.setVisibility(View.VISIBLE);
                    }
                });
            } else if (currentCamera==4&&!mHandler4.isOpened()) {
                mHandler4.open(ctrlBlock);
                final SurfaceTexture st = mUVCCameraView4.getSurfaceTexture();
                mHandler4.startPreview(new Surface(st));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCaptureButton4.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        @Override
        public void onDisconnect(final UsbDevice device, final UsbControlBlock ctrlBlock) {
            if (DEBUG) Log.v(TAG, "onDisconnect:" + device);
            if ((mHandlerL != null) && !mHandlerL.isEqual(device)) {
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mHandlerL.close();
                        if (mLeftPreviewSurface != null) {
                            mLeftPreviewSurface.release();
                            mLeftPreviewSurface = null;
                        }
                        setCameraButton();
                    }
                }, 0);
            } else if ((mHandlerR != null) && !mHandlerR.isEqual(device)) {
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mHandlerR.close();
                        if (mRightPreviewSurface != null) {
                            mRightPreviewSurface.release();
                            mRightPreviewSurface = null;
                        }
                        setCameraButton();
                    }
                }, 0);
            } else if ((mHandler3 != null) && !mHandler3.isEqual(device)) {
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mHandler3.close();
                        if (m3PreviewSurface != null) {
                            m3PreviewSurface.release();
                            m3PreviewSurface = null;
                        }
                        setCameraButton();
                    }
                }, 0);
            } else if ((mHandler4 != null) && !mHandler4.isEqual(device)) {
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mHandler4.close();
                        if (m4PreviewSurface != null) {
                            m4PreviewSurface.release();
                            m4PreviewSurface = null;
                        }
                        setCameraButton();
                    }
                }, 0);
            }
        }

        @Override
        public void onDettach(final UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onDettach:" + device);
            Toast.makeText(VideoActivity.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(final UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onCancel:");
        }
    };

    /**
     * to access from CameraDialog
     * @return
     */
    @Override
    public USBMonitor getUSBMonitor() {
        return mUSBMonitor;
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCameraButton();
                }
            }, 0);
        }
    }

    private void setCameraButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ((mHandlerL != null) && !mHandlerL.isOpened() && (mCaptureButtonL != null)) {
                    mCaptureButtonL.setVisibility(View.INVISIBLE);
                }
                if ((mHandlerR != null) && !mHandlerR.isOpened() && (mCaptureButtonR != null)) {
                    mCaptureButtonR.setVisibility(View.INVISIBLE);
                }
                if ((mHandler3 != null) && !mHandler3.isOpened() && (mCaptureButton3 != null)) {
                    mCaptureButton3.setVisibility(View.INVISIBLE);
                }
                if ((mHandler4 != null) && !mHandler4.isOpened() && (mCaptureButton4 != null)) {
                    mCaptureButton4.setVisibility(View.INVISIBLE);
                }
            }
        }, 0);
    }
}
