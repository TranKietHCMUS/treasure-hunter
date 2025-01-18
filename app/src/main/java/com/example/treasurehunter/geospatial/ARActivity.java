/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.treasurehunter.geospatial;

import static com.example.treasurehunter.data.viewModel.GameViewModel.getCoordinatesAsList;
import static com.example.treasurehunter.data.viewModel.GameViewModel.markTreasureAsFound;
import static com.example.treasurehunter.data.viewModel.GameViewModel.changeScreenMode;

import com.example.treasurehunter.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.treasurehunter.data.model.ScreenMode;
import com.example.treasurehunter.data.model.Treasure;
import com.example.treasurehunter.data.viewModel.TreasureViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Earth;
import com.google.ar.core.Frame;
import com.google.ar.core.GeospatialPose;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.StreetscapeGeometry;
import com.google.ar.core.TrackingState;
import com.google.ar.core.VpsAvailability;
import com.example.treasurehunter.common.helpers.CameraPermissionHelper;
import com.example.treasurehunter.common.helpers.DisplayRotationHelper;
import com.example.treasurehunter.common.helpers.FullScreenHelper;
import com.example.treasurehunter.common.helpers.LocationPermissionHelper;
import com.example.treasurehunter.common.helpers.SnackbarHelper;
import com.example.treasurehunter.common.helpers.TrackingStateHelper;
import com.example.treasurehunter.common.samplerender.Framebuffer;
import com.example.treasurehunter.common.samplerender.IndexBuffer;
import com.example.treasurehunter.common.samplerender.Mesh;
import com.example.treasurehunter.common.samplerender.SampleRender;
import com.example.treasurehunter.common.samplerender.Shader;
import com.example.treasurehunter.common.samplerender.Shader.BlendFactor;
import com.example.treasurehunter.common.samplerender.Texture;
import com.example.treasurehunter.common.samplerender.VertexBuffer;
import com.example.treasurehunter.common.samplerender.arcore.BackgroundRenderer;
import com.example.treasurehunter.common.samplerender.arcore.PlaneRenderer;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.FineLocationPermissionNotGrantedException;
import com.google.ar.core.exceptions.GooglePlayServicesLocationLibraryNotLinkedException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.ar.core.exceptions.UnsupportedConfigurationException;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Main activity for the Geospatial API example.
 *
 * <p>This example shows how to use the Geospatial APIs. Once the device is localized, anchors can
 * be created at the device's geospatial location. Anchor locations are persisted across sessions
 * and will be recreated once localized.
 */
public class ARActivity extends AppCompatActivity
        implements SampleRender.Renderer,
        VpsAvailabilityNoticeDialogFragment.NoticeDialogListener,
        PrivacyNoticeDialogFragment.NoticeDialogListener {

    private static final String TAG = ARActivity.class.getSimpleName();

    private static final String ALLOW_GEOSPATIAL_ACCESS_KEY = "ALLOW_GEOSPATIAL_ACCESS";

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 1000f;

    // The thresholds that are required for horizontal and orientation accuracies before entering into
    // the LOCALIZED state. Once the accuracies are equal or less than these values, the app will
    // allow the user to place anchors.
    private static final double LOCALIZING_HORIZONTAL_ACCURACY_THRESHOLD_METERS = 10;
    private static final double LOCALIZING_ORIENTATION_YAW_ACCURACY_THRESHOLD_DEGREES = 15;

    // Once in the LOCALIZED state, if either accuracies degrade beyond these amounts, the app will
    // revert back to the LOCALIZING state.
    private static final double LOCALIZED_HORIZONTAL_ACCURACY_HYSTERESIS_METERS = 10;
    private static final double LOCALIZED_ORIENTATION_YAW_ACCURACY_HYSTERESIS_DEGREES = 10;

    private static final int LOCALIZING_TIMEOUT_SECONDS = 180;

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView surfaceView;

    private boolean installRequested;

    /** Timer to keep track of how much time has passed since localizing has started. */
    private long localizingStartTimestamp;

    enum State {
        /** The Geospatial API has not yet been initialized. */
        UNINITIALIZED,
        /** The Geospatial API is not supported. */
        UNSUPPORTED,
        /** The Geospatial API has encountered an unrecoverable error. */
        EARTH_STATE_ERROR,
        /** The Session has started, but {@link \Earth} isn't {@link \TrackingState.TRACKING} yet. */
        PRETRACKING,
        /**
         * {@link \Earth} is {@link \TrackingState.TRACKING}, but the desired positioning confidence
         * hasn't been reached yet.
         */
        LOCALIZING,
        /** The desired positioning confidence wasn't reached in time. */
        LOCALIZING_FAILED,
        /**
         * {@link \Earth} is {@link \TrackingState.TRACKING} and the desired positioning confidence has
         * been reached.
         */
        LOCALIZED
    }

    private State state = State.UNINITIALIZED;

    private Session session;
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
    private DisplayRotationHelper displayRotationHelper;
    private final TrackingStateHelper trackingStateHelper = new TrackingStateHelper(this);
    private SampleRender render;
    private SharedPreferences sharedPreferences;

    private String lastStatusText;
    private TextView geospatialPoseTextView;
    private TextView statusTextView;
    private TextView tapScreenTextView;

    private Button mapButton;
    private Button puzzleButton;

    private PlaneRenderer planeRenderer;
    private BackgroundRenderer backgroundRenderer;
    private Framebuffer virtualSceneFramebuffer;
    private boolean hasSetTextureNames = false;
    // Set rendering Streetscape Geometry.
    private boolean isRenderStreetscapeGeometry = false;
    private boolean hasInitializedAnchors = false;
    // Virtual object (ARCore geospatial)
    private Mesh virtualObjectMesh;
    private Shader geospatialAnchorVirtualObjectShader;

    private final Object anchorsLock = new Object();

    @GuardedBy("anchorsLock")
    private final List<Anchor> anchors = new ArrayList<>();

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16]; // view x model
    private final float[] modelViewProjectionMatrix = new float[16]; // projection x view x model

    // Locks needed for synchronization
    private final Object singleTapLock = new Object();

    @GuardedBy("singleTapLock")
    private MotionEvent queuedSingleTap;
    // Tap handling and UI.
    private GestureDetector gestureDetector;

    // Point Cloud
    private VertexBuffer pointCloudVertexBuffer;
    private Mesh pointCloudMesh;
    private Shader pointCloudShader;
    // Keep track of the last point cloud rendered to avoid updating the VBO if point cloud
    // was not changed.  Do this using the timestamp since we can't compare PointCloud objects.
    private long lastPointCloudTimestamp = 0;

    // Provides device location.
    private FusedLocationProviderClient fusedLocationClient;

    // Streetscape geometry.
    private final ArrayList<float[]> wallsColor = new ArrayList<>();
    private final List<Treasure> coordinates = getCoordinatesAsList();

    private Shader streetscapeGeometryTerrainShader;
    private Shader streetscapeGeometryBuildingShader;
    // A set of planes representing building outlines and floors.
    private final Map<StreetscapeGeometry, Mesh> streetscapeGeometryToMeshes = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceview);
        geospatialPoseTextView = findViewById(R.id.geospatial_pose_view);
        statusTextView = findViewById(R.id.status_text_view);
        tapScreenTextView = findViewById(R.id.tap_screen_text_view);

        mapButton = findViewById(R.id.map_button);
        puzzleButton = findViewById(R.id.puzzle_button);

        mapButton.setOnClickListener(v -> {
            finish(); // Clean up AR session
            changeScreenMode(ScreenMode.MAP);
        });

        puzzleButton.setOnClickListener(v -> {
            finish(); // Clean up AR session
            changeScreenMode(ScreenMode.PUZZLE);
        });


        displayRotationHelper = new DisplayRotationHelper(/* activity= */ this);

        // Set up renderer.
        render = new SampleRender(surfaceView, this, getAssets());

        installRequested = false;

        // Set up touch listener.
        gestureDetector =
                new GestureDetector(
                        this,
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                                synchronized (singleTapLock) {
                                    queuedSingleTap = e;
                                }
                                return true;
                            }

                            @Override
                            public boolean onDown(MotionEvent e) {
                                return true;
                            }
                        });
        surfaceView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(/* context= */ this);
    }

    @Override
    protected void onDestroy() {
        if (session != null) {
            // Explicitly close ARCore Session to release native resources.
            // Review the API reference for important considerations before calling close() in apps with
            // more complicated lifecycle requirements:
            // https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/Session#close()
            session.close();
            session = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean(ALLOW_GEOSPATIAL_ACCESS_KEY, /* defValue= */ false)) {
            createSession();
        } else {
            showPrivacyNoticeDialog();
        }

        surfaceView.onResume();
        displayRotationHelper.onResume();
    }

    private void showPrivacyNoticeDialog() {
        DialogFragment dialog = PrivacyNoticeDialogFragment.createDialog();
        dialog.show(getSupportFragmentManager(), PrivacyNoticeDialogFragment.class.getName());
    }

    private void createSession() {
        Exception exception = null;
        String message = null;
        if (session == null) {

            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this);
                    return;
                }
                if (!LocationPermissionHelper.hasFineLocationPermission(this)) {
                    LocationPermissionHelper.requestFineLocationPermission(this);
                    return;
                }

                // Create the session.
                // Plane finding mode is default on, which will help the dynamic alignment of terrain
                // anchors on ground.
                session = new Session(/* context= */ this);
            } catch (UnavailableArcoreNotInstalledException
                     | UnavailableUserDeclinedInstallationException e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update this app";
                exception = e;
            } catch (UnavailableDeviceNotCompatibleException e) {
                message = "This device does not support AR";
                exception = e;
            } catch (Exception e) {
                message = "Failed to create AR session";
                exception = e;
            }

            if (message != null) {
                messageSnackbarHelper.showError(this, message);
                Log.e(TAG, "Exception creating session", exception);
                return;
            }
        }
        // Check VPS availability before configure and resume session.
        getLastLocation();

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            configureSession();
            // To record a live camera session for later playback, call
            // `session.startRecording(recordingConfig)` at anytime. To playback a previously recorded AR
            // session instead of using the live camera feed, call
            // `session.setPlaybackDatasetUri(Uri)` before calling `session.resume()`. To
            // learn more about recording and playback, see:
            // https://developers.google.com/ar/develop/java/recording-and-playback
            session.resume();
        } catch (CameraNotAvailableException e) {
            message = "Camera not available. Try restarting the app.";
            exception = e;
        } catch (GooglePlayServicesLocationLibraryNotLinkedException e) {
            message = "Google Play Services location library not linked or obfuscated with Proguard.";
            exception = e;
        } catch (FineLocationPermissionNotGrantedException e) {
            message = "The Android permission ACCESS_FINE_LOCATION was not granted.";
            exception = e;
        } catch (UnsupportedConfigurationException e) {
            message = "This device does not support GeospatialMode.ENABLED.";
            exception = e;
        } catch (SecurityException e) {
            message = "Camera failure or the internet permission has not been granted.";
            exception = e;
        }

        if (message != null) {
            session = null;
            messageSnackbarHelper.showError(this, message);
            Log.e(TAG, "Exception configuring and resuming the session", exception);
        }
    }

    private void getLastLocation() {
        try {
            fusedLocationClient
                    .getLastLocation()
                    .addOnSuccessListener(
                            location -> {
                                double latitude = 0;
                                double longitude = 0;
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                } else {
                                    Log.e(TAG, "Error location is null");
                                }
                                checkVpsAvailability(latitude, longitude);
                            });
        } catch (SecurityException e) {
            Log.e(TAG, "No location permissions granted by User!");
        }
    }

    private void checkVpsAvailability(double latitude, double longitude) {
        session.checkVpsAvailabilityAsync(
                latitude,
                longitude,
                availability -> {
                    if (availability != VpsAvailability.AVAILABLE) {
                        showVpsNotAvailabilityNoticeDialog();
                    }
                });
    }

    private void showVpsNotAvailabilityNoticeDialog() {
        DialogFragment dialog = VpsAvailabilityNoticeDialogFragment.createDialog();
        dialog.show(getSupportFragmentManager(), VpsAvailabilityNoticeDialogFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            displayRotationHelper.onPause();
            surfaceView.onPause();
            session.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            // Use toast instead of snackbar here since the activity will exit.
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
        // Check if this result pertains to the location permission.
        if (LocationPermissionHelper.hasFineLocationPermissionsResponseInResult(permissions)
                && !LocationPermissionHelper.hasFineLocationPermission(this)) {
            // Use toast instead of snackbar here since the activity will exit.
            Toast.makeText(
                            this,
                            "Precise location permission is needed to run this application",
                            Toast.LENGTH_LONG)
                    .show();
            if (!LocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                LocationPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
    }

    @Override
    public void onSurfaceCreated(SampleRender render) {
        // Prepare the rendering objects. This involves reading shaders and 3D model files, so may throw
        // an IOException.
        try {
            planeRenderer = new PlaneRenderer(render);
            backgroundRenderer = new BackgroundRenderer(render);
            virtualSceneFramebuffer = new Framebuffer(render, /* width= */ 1, /* height= */ 1);

            // Virtual object to render (ARCore geospatial)
            Texture virtualObjectTexture =
                    Texture.createFromAsset(
                            render,
                            "models/TreasureChest_UV_03.png",  // Update this path if needed
                            Texture.WrapMode.CLAMP_TO_EDGE,
                            Texture.ColorFormat.SRGB);

            virtualObjectMesh = Mesh.createFromAsset(render, "models/TreasureChest.obj");
            geospatialAnchorVirtualObjectShader =
                    Shader.createFromAssets(
                                    render,
                                    "shaders/ar_unlit_object.vert",
                                    "shaders/ar_unlit_object.frag",
                                    /* defines= */ null)
                            .setTexture("u_Texture", virtualObjectTexture);

            backgroundRenderer.setUseDepthVisualization(render, false);
            backgroundRenderer.setUseOcclusion(render, false);

            // Point cloud
            pointCloudShader =
                    Shader.createFromAssets(
                                    render,
                                    "shaders/point_cloud.vert",
                                    "shaders/point_cloud.frag",
                                    /* defines= */ null)
                            .setVec4(
                                    "u_Color", new float[] {31.0f / 255.0f, 188.0f / 255.0f, 210.0f / 255.0f, 1.0f})
                            .setFloat("u_PointSize", 5.0f);
            // four entries per vertex: X, Y, Z, confidence
            pointCloudVertexBuffer =
                    new VertexBuffer(render, /* numberOfEntriesPerVertex= */ 4, /* entries= */ null);
            final VertexBuffer[] pointCloudVertexBuffers = {pointCloudVertexBuffer};
            pointCloudMesh =
                    new Mesh(
                            render, Mesh.PrimitiveMode.POINTS, /* indexBuffer= */ null, pointCloudVertexBuffers);

            streetscapeGeometryBuildingShader =
                    Shader.createFromAssets(
                                    render,
                                    "shaders/streetscape_geometry.vert",
                                    "shaders/streetscape_geometry.frag",
                                    /* defines= */ null)
                            .setBlend(
                                    BlendFactor.DST_ALPHA, // RGB (src)
                                    BlendFactor.ONE); // ALPHA (dest)

            streetscapeGeometryTerrainShader =
                    Shader.createFromAssets(
                                    render,
                                    "shaders/streetscape_geometry.vert",
                                    "shaders/streetscape_geometry.frag",
                                    /* defines= */ null)
                            .setBlend(
                                    BlendFactor.DST_ALPHA, // RGB (src)
                                    BlendFactor.ONE); // ALPHA (dest)
            wallsColor.add(new float[] {0.5f, 0.0f, 0.5f, 0.3f});
            wallsColor.add(new float[] {0.5f, 0.5f, 0.0f, 0.3f});
            wallsColor.add(new float[] {0.0f, 0.5f, 0.5f, 0.3f});
        } catch (IOException e) {
            Log.e(TAG, "Failed to read a required asset file", e);
            messageSnackbarHelper.showError(this, "Failed to read a required asset file: " + e);
        }
    }

    @Override
    public void onSurfaceChanged(SampleRender render, int width, int height) {
        displayRotationHelper.onSurfaceChanged(width, height);
        virtualSceneFramebuffer.resize(width, height);
    }

    @Override
    public void onDrawFrame(SampleRender render) {
        if (session == null) {
            return;
        }

        // Initialize anchors from coordinates list if not already done
        if (!hasInitializedAnchors) {
            Earth earth = session.getEarth();
            if (earth != null && earth.getTrackingState() == TrackingState.TRACKING) {
                for (Treasure coord : coordinates) {
                    if (!coord.getFound()) {
                        double latitude = coord.getLocation().latitude;
                        double longitude = coord.getLocation().longitude;
                        double altitude = 0; // Default altitude
                        float[] quaternion = {0, 0.7071f, 0, 0.7071f}; // Default orientation

                        // Create an anchor at the specified coordinates
                        createAnchor(earth, latitude, longitude, altitude, quaternion);
                    }
                }
                hasInitializedAnchors = true;
            } else {
                Log.e(TAG, "Earth is not tracking. Cannot initialize anchors.");
            }
        }


        // Texture names should only be set once on a GL thread unless they change. This is done during
        // onDrawFrame rather than onSurfaceCreated since the session is not guaranteed to have been
        // initialized during the execution of onSurfaceCreated.
        if (!hasSetTextureNames) {
            session.setCameraTextureNames(
                    new int[] {backgroundRenderer.getCameraColorTexture().getTextureId()});
            hasSetTextureNames = true;
        }

        // -- Update per-frame state

        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session);
        updateStreetscapeGeometries(session.getAllTrackables(StreetscapeGeometry.class));

        // Obtain the current frame from ARSession. When the configuration is set to
        // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
        // camera framerate.
        Frame frame;
        try {
            frame = session.update();
        } catch (CameraNotAvailableException e) {
            Log.e(TAG, "Camera not available during onDrawFrame", e);
            messageSnackbarHelper.showError(this, "Camera not available. Try restarting the app.");
            return;
        }
        Camera camera = frame.getCamera();

        // BackgroundRenderer.updateDisplayGeometry must be called every frame to update the coordinates
        // used to draw the background camera image.
        backgroundRenderer.updateDisplayGeometry(frame);

        // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
        trackingStateHelper.updateKeepScreenOnFlag(camera.getTrackingState());

        Earth earth = session.getEarth();
        if (earth != null) {
            updateGeospatialState(earth);
        }

        // Show a message based on whether tracking has failed, if planes are detected, and if the user
        // has placed any objects.
        String message = null;
        switch (state) {
            case UNINITIALIZED:
                break;
            case UNSUPPORTED:
                message = getResources().getString(R.string.status_unsupported);
                break;
            case PRETRACKING:
                message = getResources().getString(R.string.status_pretracking);
                break;
            case EARTH_STATE_ERROR:
                message = getResources().getString(R.string.status_earth_state_error);
                break;
            case LOCALIZING:
                message = getResources().getString(R.string.status_localize_hint);
                break;
            case LOCALIZING_FAILED:
                message = getResources().getString(R.string.status_localize_timeout);
                break;
            case LOCALIZED:
                if (lastStatusText.equals(getResources().getString(R.string.status_localize_hint))) {
                    message = getResources().getString(R.string.status_localize_complete);
                }
                break;
        }

        if (message != null && !Objects.equals(lastStatusText, message)) {
            lastStatusText = message;
            runOnUiThread(
                    () -> {
                        statusTextView.setVisibility(View.VISIBLE);
                        statusTextView.setText(lastStatusText);
                    });
        }

        // Handle user input.
        //handleTap(frame);

        // -- Draw background

        if (frame.getTimestamp() != 0) {
            // Suppress rendering if the camera did not produce the first frame yet. This is to avoid
            // drawing possible leftover data from previous sessions if the texture is reused.
            backgroundRenderer.drawBackground(render);
        }

        // If not tracking, don't draw 3D objects.
        if (camera.getTrackingState() != TrackingState.TRACKING || state != State.LOCALIZED) {
            return;
        }

        // -- Draw virtual objects

        // Get projection matrix.
        camera.getProjectionMatrix(projectionMatrix, 0, Z_NEAR, Z_FAR);

        // Get camera matrix and draw.
        camera.getViewMatrix(viewMatrix, 0);

        // Visualize tracked points.
        // Use try-with-resources to automatically release the point cloud.
        try (PointCloud pointCloud = frame.acquirePointCloud()) {
            if (pointCloud.getTimestamp() > lastPointCloudTimestamp) {
                pointCloudVertexBuffer.set(pointCloud.getPoints());
                lastPointCloudTimestamp = pointCloud.getTimestamp();
            }
            Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
            pointCloudShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix);
            render.draw(pointCloudMesh, pointCloudShader);
        }

        // Visualize planes.
        planeRenderer.drawPlanes(
                render,
                session.getAllTrackables(Plane.class),
                camera.getDisplayOrientedPose(),
                projectionMatrix);

        // Visualize anchors created by touch.
        render.clear(virtualSceneFramebuffer, 0f, 0f, 0f, 0f);

        // -- Draw Streetscape Geometries.
        if (isRenderStreetscapeGeometry) {
            int index = 0;
            for (Map.Entry<StreetscapeGeometry, Mesh> set : streetscapeGeometryToMeshes.entrySet()) {
                StreetscapeGeometry streetscapeGeometry = set.getKey();
                if (streetscapeGeometry.getTrackingState() != TrackingState.TRACKING) {
                    continue;
                }
                Mesh mesh = set.getValue();
                Pose pose = streetscapeGeometry.getMeshPose();
                pose.toMatrix(modelMatrix, 0);

                // Calculate model/view/projection matrices
                Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
                Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

                if (streetscapeGeometry.getType() == StreetscapeGeometry.Type.BUILDING) {
                    float[] color = wallsColor.get(index % wallsColor.size());
                    index += 1;
                    streetscapeGeometryBuildingShader
                            .setVec4(
                                    "u_Color",
                                    new float[] {/* r= */ color[0], /* g= */ color[1], /* b= */ color[2], color[3]})
                            .setMat4("u_ModelViewProjection", modelViewProjectionMatrix);
                    render.draw(mesh, streetscapeGeometryBuildingShader);
                } else if (streetscapeGeometry.getType() == StreetscapeGeometry.Type.TERRAIN) {
                    streetscapeGeometryTerrainShader
                            .setVec4("u_Color", new float[] {/* r= */ 0f, /* g= */ .5f, /* b= */ 0f, 0.3f})
                            .setMat4("u_ModelViewProjection", modelViewProjectionMatrix);
                    render.draw(mesh, streetscapeGeometryTerrainShader);
                }
            }
        }
        render.clear(virtualSceneFramebuffer, 0f, 0f, 0f, 0f);
        synchronized (anchorsLock) {
            for (int i = 0; i < anchors.size(); i++) {
                Anchor anchor = anchors.get(i);
                if (anchor.getTrackingState() != TrackingState.TRACKING) {
                    continue;
                }

                Pose anchorPose = anchor.getPose();
                Pose cameraPose = frame.getCamera().getDisplayOrientedPose();
                double distance = calculateDistance(anchorPose, cameraPose);

                // Check if camera is focused on anchor (within certain angle and distance)


                // If camera is looking at anchor (within 20 degrees) and close enough (within 2 meters)
                if (distance < 4) {
                    // Make the anchor "tappable"
                    runOnUiThread(() -> {
                        tapScreenTextView.setVisibility(View.VISIBLE);
                        tapScreenTextView.setText(R.string.tap_on_treasure);
                    });

                    // Handle tap on anchor
                    synchronized (singleTapLock) {
                        if (queuedSingleTap != null) {
                            finish();
                            markTreasureAsFound(coordinates.get(i).getLocation());
                            changeScreenMode(ScreenMode.PUZZLE);
                            TreasureViewModel.openChest();
                            queuedSingleTap = null;
                        }
                    }
                } else {
                    runOnUiThread(() -> tapScreenTextView.setVisibility(View.INVISIBLE));
                }

                if (distance < 20) {
                    renderAnchorObject(anchor, camera);
                }
            }

            if (!anchors.isEmpty()) {
                String anchorMessage =
                        getResources()
                                .getQuantityString(
                                        R.plurals.status_anchors_set, anchors.size(), anchors.size());
                runOnUiThread(
                        () -> {
                            statusTextView.setVisibility(View.VISIBLE);
                            statusTextView.setText(anchorMessage);
                        });
            }
        }
        // Compose the virtual scene withthe background.
        backgroundRenderer.drawVirtualScene(render, virtualSceneFramebuffer, Z_NEAR, Z_FAR);
    }

    private void updateStreetscapeGeometries(Collection<StreetscapeGeometry> streetscapeGeometries) {
        for (StreetscapeGeometry streetscapeGeometry : streetscapeGeometries) {
            // If the Streetscape Geometry node is already added to the scene, then we'll simply update
            // the pose.
            if (streetscapeGeometryToMeshes.containsKey(streetscapeGeometry)) {
            } else {
                // Otherwise, we create a StreetscapeGeometry mesh and add it to the scene.
                Mesh mesh = getSampleRenderMesh(streetscapeGeometry);
                streetscapeGeometryToMeshes.put(streetscapeGeometry, mesh);
            }
        }
    }

    private Mesh getSampleRenderMesh(StreetscapeGeometry streetscapeGeometry) {
        FloatBuffer streetscapeGeometryBuffer = streetscapeGeometry.getMesh().getVertexList();
        streetscapeGeometryBuffer.rewind();
        VertexBuffer meshVertexBuffer =
                new VertexBuffer(
                        render, /* numberOfEntriesPerVertex= */ 3, /* entries= */ streetscapeGeometryBuffer);
        IndexBuffer meshIndexBuffer =
                new IndexBuffer(render, streetscapeGeometry.getMesh().getIndexList());
        final VertexBuffer[] meshVertexBuffers = {meshVertexBuffer};
        return new Mesh(
                render,
                Mesh.PrimitiveMode.TRIANGLES,
                /* indexBuffer= */ meshIndexBuffer,
                meshVertexBuffers);
    }

    /** Configures the session with feature settings. */
    private void configureSession() {
        // Earth mode may not be supported on this device due to insufficient sensor quality.
        if (!session.isGeospatialModeSupported(Config.GeospatialMode.ENABLED)) {
            state = State.UNSUPPORTED;
            return;
        }

        Config config = session.getConfig();
        config =
                config
                        .setGeospatialMode(Config.GeospatialMode.ENABLED)
                        .setStreetscapeGeometryMode(Config.StreetscapeGeometryMode.ENABLED);
        session.configure(config);
        state = State.PRETRACKING;
        localizingStartTimestamp = System.currentTimeMillis();
    }

    /** Change behavior depending on the current {@link State} of the application. */
    private void updateGeospatialState(Earth earth) {
        if (earth.getEarthState() != Earth.EarthState.ENABLED) {
            state =State.EARTH_STATE_ERROR;
            return;
        }
        if (earth.getTrackingState() != TrackingState.TRACKING) {
            state = State.PRETRACKING;
            return;
        }
        if (state == State.PRETRACKING) {
            updatePretrackingState(earth);
        } else if (state == State.LOCALIZING) {
            updateLocalizingState(earth);
        } else if (state == State.LOCALIZED) {
            updateLocalizedState(earth);
        }
    }

    /**
     * Handles the updating for {@link \State.PRETRACKING}. In this state, wait for {@link Earth} to
     * have {@link \TrackingState.TRACKING}. If it hasn't been enabled by now, then we've encountered
     * an unrecoverable {@link \State.EARTH_STATE_ERROR}.
     */
    private void updatePretrackingState(Earth earth) {
        if (earth.getTrackingState() == TrackingState.TRACKING) {
            state = State.LOCALIZING;
            return;
        }

        runOnUiThread(() -> geospatialPoseTextView.setText(R.string.geospatial_pose_not_tracking));
    }

    /**
     * Handles the updating for {@link \State.LOCALIZING}. In this state, wait for the horizontal and
     * orientation threshold to improve until it reaches your threshold.
     *
     * <p>If it takes too long for the threshold to be reached, this could mean that GPS data isn't
     * accurate enough, or that the user is in an area that can't be localized with StreetView.
     */
    private void updateLocalizingState(Earth earth) {
        GeospatialPose geospatialPose = earth.getCameraGeospatialPose();
        if (geospatialPose.getHorizontalAccuracy() <= LOCALIZING_HORIZONTAL_ACCURACY_THRESHOLD_METERS
                && geospatialPose.getOrientationYawAccuracy()
                <= LOCALIZING_ORIENTATION_YAW_ACCURACY_THRESHOLD_DEGREES) {
            state = State.LOCALIZED;
            return;
        }

        if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - localizingStartTimestamp)
                > LOCALIZING_TIMEOUT_SECONDS) {
            state = State.LOCALIZING_FAILED;
            return;
        }

        updateGeospatialPoseText(geospatialPose);
    }

    /**
     * Handles the updating for {@link \State.LOCALIZED}. In this state, check the accuracy for
     * degradation and return to {@link \State.LOCALIZING} if the position accuracies have dropped too
     * low.
     */
    private void updateLocalizedState(Earth earth) {
        GeospatialPose geospatialPose = earth.getCameraGeospatialPose();
        // Check if either accuracy has degraded to the point we should enter back into the LOCALIZING
        // state.
        if (geospatialPose.getHorizontalAccuracy()
                > LOCALIZING_HORIZONTAL_ACCURACY_THRESHOLD_METERS
                + LOCALIZED_HORIZONTAL_ACCURACY_HYSTERESIS_METERS
                || geospatialPose.getOrientationYawAccuracy()
                > LOCALIZING_ORIENTATION_YAW_ACCURACY_THRESHOLD_DEGREES
                + LOCALIZED_ORIENTATION_YAW_ACCURACY_HYSTERESIS_DEGREES) {
            // Accuracies have degenerated, return to the localizing state.
            state = State.LOCALIZING;
            localizingStartTimestamp = System.currentTimeMillis();
            runOnUiThread(
                    () -> tapScreenTextView.setVisibility(View.INVISIBLE));
            return;
        }

        updateGeospatialPoseText(geospatialPose);
    }

    private void updateGeospatialPoseText(GeospatialPose geospatialPose) {
        float[] quaternion = geospatialPose.getEastUpSouthQuaternion();
        String poseText =
                getResources()
                        .getString(
                                R.string.geospatial_pose,
                                geospatialPose.getLatitude(),
                                geospatialPose.getLongitude(),
                                geospatialPose.getHorizontalAccuracy(),
                                geospatialPose.getAltitude(),
                                geospatialPose.getVerticalAccuracy(),
                                quaternion[0],
                                quaternion[1],
                                quaternion[2],
                                quaternion[3],
                                nearestDistance(geospatialPose));
        runOnUiThread(
                () -> geospatialPoseTextView.setText(poseText));
    }

    private float nearestDistance (GeospatialPose geospatialPose) {
        float nearestDistance = Float.MAX_VALUE;
        for (Treasure anchors : coordinates) {
            float disc = (float)coordinatesDistance(
                    geospatialPose.getLatitude(),
                    anchors.getLocation().latitude,
                    geospatialPose.getLongitude(),
                    anchors.getLocation().longitude,
                    geospatialPose.getAltitude(),
                    0
            );

            if (disc < nearestDistance) {
                nearestDistance = disc;
            }
        }
        return nearestDistance;
    }
    // Return the scale in range [1, 2] after mapping a distance between camera and anchor to [2, 20].
    private float getScale(Pose anchorPose, Pose cameraPose) {
        double distance =
                Math.sqrt(
                        Math.pow(anchorPose.tx() - cameraPose.tx(), 2.0)
                                + Math.pow(anchorPose.ty() - cameraPose.ty(), 2.0)
                                + Math.pow(anchorPose.tz() - cameraPose.tz(), 2.0));
        // Adjust these values based on your chest model's size
        double mapDistance = Math.min(Math.max(1, distance), 15);
        return (float) (mapDistance - 1) / (15 - 1) + 0.5f; // Smaller scale range
    }

    /** Create an anchor at a specific geodetic location using a EUS quaternion. */
    private void createAnchor(
            Earth earth, double latitude, double longitude, double altitude, float[] quaternion) {
        Anchor anchor =
                earth.createAnchor(
                        latitude,
                        longitude,
                        altitude,
                        quaternion[0],
                        quaternion[1],
                        quaternion[2],
                        quaternion[3]);
        synchronized (anchorsLock) {
            anchors.add(anchor);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (!sharedPreferences.edit().putBoolean(ALLOW_GEOSPATIAL_ACCESS_KEY, true).commit()) {
            throw new AssertionError("Could not save the user preference to SharedPreferences!");
        }
        createSession();
    }

    @Override
    public void onDialogContinueClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    private void onRenderStreetscapeGeometryChanged(CompoundButton button, boolean isChecked) {
        if (session == null) {
            return;
        }
        isRenderStreetscapeGeometry = isChecked;
    }

    /**
     * Handles the most recent user tap.
     *
     * <p>We only ever handle one tap at a time, since this app only allows for a single anchor.
     * \@param \frame the current AR frame
     * \@param \cameraTrackingState the current camera tracking state
     */

    private double calculateDistance(Pose anchorPose, Pose cameraPose) {
        return Math.sqrt(Math.pow(anchorPose.tx() - cameraPose.tx(), 2.0) + Math.pow(anchorPose.ty() - cameraPose.ty(), 2.0) + Math.pow(anchorPose.tz() - cameraPose.tz(), 2.0));
    }

    public static double coordinatesDistance(double lat1, double lat2, double lon1,
                                             double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    private void renderAnchorObject(Anchor anchor, Camera camera) {
        anchor.getPose().toMatrix(modelMatrix, 0);
        float[] scaleMatrix = new float[16];
        Matrix.setIdentityM(scaleMatrix, 0);
        float scale = getScale(anchor.getPose(), camera.getDisplayOrientedPose());
        scaleMatrix[0] = scale;
        scaleMatrix[5] = scale;
        scaleMatrix[10] = scale;
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scaleMatrix, 0);

        // Update rotation for treasure chest model
        float[] rotationMatrix = new float[16];
        Matrix.setRotateM(rotationMatrix, 0, 180, 0.0f, 1.0f, 0.0f);
        float[] rotationModelMatrix = new float[16];
        Matrix.multiplyMM(rotationModelMatrix, 0, modelMatrix, 0, rotationMatrix, 0);

        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, rotationModelMatrix, 0);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

        geospatialAnchorVirtualObjectShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix);
        render.draw(virtualObjectMesh, geospatialAnchorVirtualObjectShader, virtualSceneFramebuffer);
    }
}

