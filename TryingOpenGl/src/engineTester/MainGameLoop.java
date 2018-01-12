package engineTester;

import org.lwjgl.opengl.Display;

import renderEngine.DisplayManager;
import scenes.TestScene;


public class MainGameLoop {
	
	
	
	
	
	 public static void main(String[] args) {
		 
	        DisplayManager.createDisplay();
	        TestScene testScene = new TestScene();
	        
	        //Scenes get created here
	        testScene.Create();
	        
	 
	        
	        //The GameLoop
	        while(!Display.isCloseRequested()){
	        	
	        	//Scenes get ran here
	        	testScene.Run();
	        	
	   	     	DisplayManager.updateDisplay();
	        }
	        
	        DisplayManager.closeDisplay();
	        
	        //Scenes get destroyed here
	        testScene.Destroy();
	    }
	}
