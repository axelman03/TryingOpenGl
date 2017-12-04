package engineTester;

import org.lwjgl.opengl.Display;

import renderEngine.DisplayManager;
import scenes.TestScene;


public class MainGameLoop {
	

	
	
	
	 public static void main(String[] args) {
		 
	        DisplayManager.createDisplay();
	        TestScene testScene = new TestScene();
	        testScene.Create();
	        
	 
	        
	        //The GameLoop
	        while(!Display.isCloseRequested()){
	        	testScene.Run();
	        }
	        
	        DisplayManager.closeDisplay();
	        testScene.Disable();
	    }
	}
