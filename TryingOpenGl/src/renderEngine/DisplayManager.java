package renderEngine;


import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;
/*
import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.ovr.OVR;
import org.lwjgl.ovr.OVRDetectResult;
import org.lwjgl.ovr.OVRGraphicsLuid;
import org.lwjgl.ovr.OVRHmdDesc;
import org.lwjgl.ovr.OVRInitParams;
import org.lwjgl.ovr.OVRLogCallback;
import org.lwjgl.ovr.OVRUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
*/
public class DisplayManager {
	
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int FPS_CAP = 120;
	public static final float ASPECT_RATIO = 1;
	
	private static long lastFrameTime;
	private static float delta;
	
	static boolean vrTrue = false;
	
	public static void createDisplay(){
		ContextAttribs attribs = new ContextAttribs(3,3)
		.withForwardCompatible(true)
		.withProfileCore(true);
		//runOculusVR();
		if(vrTrue) {
			try {
				Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT));
				//Display.setFullscreen(true);
				Display.create(new PixelFormat(), attribs);
				Display.setTitle("My Cool Game!");
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
			
			GL11.glViewport(0, 0, WIDTH, HEIGHT);
			lastFrameTime= getCurrentTime();
		}
		else {
			try {
				Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT));
				//Display.setFullscreen(true);
				Display.create(new PixelFormat().withSamples(8).withDepthBits(24), attribs);
				Display.setTitle("My Cool Game!");
				GL11.glEnable(GL13.GL_MULTISAMPLE);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
			
			GL11.glViewport(0, 0, WIDTH, HEIGHT);
			lastFrameTime= getCurrentTime();
		}
	}
	
	public static void updateDisplay(){
		
		Display.sync(FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta =(currentFrameTime -lastFrameTime)/1000f;
		lastFrameTime = currentFrameTime;
	}
	/*
	public static void runVR() {
		
		// https://github.com/LWJGL/lwjgl3/tree/master/modules/core/src/test/java/org/lwjgl
		//https://github.com/WhiteHexagon/example-lwjgl3-rift/blob/master/src/main/java/com/sunshineapps/rift/experimental/RiftWindow0800.java
		//Should have some classes to help me learn how to install open vr and also updeated openGL
		
		 System.err.println("VR_IsRuntimeInstalled() = " + VR.VR_IsRuntimeInstalled());
	     System.err.println("VR_RuntimePath() = " + VR.VR_RuntimePath());
	     System.err.println("VR_IsHmdPresent() = " + VR.VR_IsHmdPresent());

	        try (MemoryStack stack = MemoryStack.stackPush()) {
	            IntBuffer peError = stack.mallocInt(1);

	            int token = VR.VR_InitInternal(peError, 0);
	            if (peError.get(0) == 0) {
	                try {
	                    OpenVR.create(token);

	                    System.err.println("Model Number : " + VRSystem.VRSystem_GetStringTrackedDeviceProperty(
	                        VR.k_unTrackedDeviceIndex_Hmd,
	                        VR.ETrackedDeviceProperty_Prop_ModelNumber_String,
	                        peError
	                    ));
	                    System.err.println("Serial Number: " + VRSystem.VRSystem_GetStringTrackedDeviceProperty(
	                        VR.k_unTrackedDeviceIndex_Hmd,
	                        VR.ETrackedDeviceProperty_Prop_SerialNumber_String,
	                        peError
	                    ));

	                    IntBuffer w = stack.mallocInt(1);
	                    IntBuffer h = stack.mallocInt(1);
	                    VRSystem.VRSystem_GetRecommendedRenderTargetSize(w, h);
	                    System.err.println("Recommended width : " + w.get(0));
	                    System.err.println("Recommended height: " + h.get(0));
	                } finally {
	                    VR.VR_ShutdownInternal();
	                }
	            } else {
	                System.out.println("INIT ERROR SYMBOL: " + VR.VR_GetVRInitErrorAsSymbol(peError.get(0)));
	                System.out.println("INIT ERROR  DESCR: " + VR.VR_GetVRInitErrorAsEnglishDescription(peError.get(0)));
	            }
	        
	    }
	}
	
	
	public static void runOculusVR() {
		try (OVRDetectResult detect = OVRDetectResult.calloc()) {
            OVRUtil.ovr_Detect(0, detect);

            System.out.println("OVRDetectResult.IsOculusHMDConnected = " + detect.IsOculusHMDConnected());
            System.out.println("OVRDetectResult.IsOculusServiceRunning = " + detect.IsOculusServiceRunning());
            if(detect.IsOculusHMDConnected() == true) {
            	vrTrue = true;
            }
            else {
            	return;
            }
        }

        OVRLogCallback callback;
        try (
            OVRInitParams initParams = OVRInitParams.calloc()
            	.LogCallback((userData, level, message) -> System.out.println("LibOVR [" + level + "] " + MemoryUtil.memASCII(message)))
            	.Flags(OVR.ovrInit_Debug)) {
            		callback = initParams.LogCallback();
            		System.out.println("ovr_Initialize = " + OVR.ovr_Initialize(initParams));
        }

        System.out.println("ovr_GetVersionString = " + OVR.ovr_GetVersionString());

        try (
            OVRGraphicsLuid luid = OVRGraphicsLuid.calloc();
            OVRHmdDesc desc = OVRHmdDesc.malloc();
        ) {
            PointerBuffer pSession = MemoryUtil.memAllocPointer(1);
            System.out.println("ovr_Create = " + OVR.ovr_Create(pSession, luid));

            Long session = pSession.get(0);
            MemoryUtil.memFree(pSession);

            OVR.ovr_GetHmdDesc(session, desc);
            System.out.println("ovr_GetHmdDesc = " + desc.ManufacturerString() + " " + desc.ProductNameString() + " " + desc.SerialNumberString());

            if (session != null) {
                OVR.ovr_Destroy(session);
            }
        }
        OVR.ovr_Shutdown();
        callback.free();
    }
	*/
	
	public static float getFrameTimeSeconds(){
		return delta;
	}
	
	public static void closeDisplay(){
		
		Display.destroy();
		
		
	}
	
	private static long getCurrentTime(){
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}
	
}
