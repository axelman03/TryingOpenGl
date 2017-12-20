package renderEngine;


import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.openvr.OpenVR;
import org.lwjgl.ovr.OVR;
import org.lwjgl.system.MemoryStack;

public class DisplayManager {
	
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int FPS_CAP = 120;
	public static final float ASPECT_RATIO = 1;
	
	private static long lastFrameTime;
	private static float delta;
	
	static boolean vrTrue = false;
	
	public static void createDisplay(){
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);
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
				Display.create(new PixelFormat(), attribs);
				Display.setTitle("My Cool Game!");
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
	
	public static void runVR() {
		
		// https://github.com/LWJGL/lwjgl3/tree/master/modules/core/src/test/java/org/lwjgl
		//Should have some classes to help me learn how to install open vr and also updeated openGL
		
		 System.err.println("VR_IsRuntimeInstalled() = " + VR_IsRuntimeInstalled());
	        System.err.println("VR_RuntimePath() = " + VR_RuntimePath());
	        System.err.println("VR_IsHmdPresent() = " + VR_IsHmdPresent());

	        try (MemoryStack stack = stackPush()) {
	            IntBuffer peError = stack.mallocInt(1);

	            int token = VR_InitInternal(peError, 0);
	            if (peError.get(0) == 0) {
	                try {
	                    OpenVR.create(token);

	                    System.err.println("Model Number : " + VRSystem_GetStringTrackedDeviceProperty(
	                        k_unTrackedDeviceIndex_Hmd,
	                        ETrackedDeviceProperty_Prop_ModelNumber_String,
	                        peError
	                    ));
	                    System.err.println("Serial Number: " + VRSystem_GetStringTrackedDeviceProperty(
	                        k_unTrackedDeviceIndex_Hmd,
	                        ETrackedDeviceProperty_Prop_SerialNumber_String,
	                        peError
	                    ));

	                    IntBuffer w = stack.mallocInt(1);
	                    IntBuffer h = stack.mallocInt(1);
	                    VRSystem_GetRecommendedRenderTargetSize(w, h);
	                    System.err.println("Recommended width : " + w.get(0));
	                    System.err.println("Recommended height: " + h.get(0));
	                } finally {
	                    VR_ShutdownInternal();
	                }
	            } else {
	                System.out.println("INIT ERROR SYMBOL: " + VR_GetVRInitErrorAsSymbol(peError.get(0)));
	                System.out.println("INIT ERROR  DESCR: " + VR_GetVRInitErrorAsEnglishDescription(peError.get(0)));
	            }
	        
	    }
	}
	
	public static void updateVRCamera() {
		
	}
	
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
