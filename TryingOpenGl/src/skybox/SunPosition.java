package skybox;

import java.time.LocalTime;

import org.lwjgl.util.vector.Vector3f;

import entities.Light;
import renderEngine.DisplayManager;

public class SunPosition {

    static float time;
	static LocalTime realTime;
	static double angle;
	static float daylightTime;
	static double daylightAngle;
	
	public static void setRealTime(Light light) {
    	//Clock Initializing
        realTime = LocalTime.now();
    	time = realTime.toSecondOfDay();
    	angle = (double)(time / 240);  //angle of line from the center of map to the point where the sun is
    	angle = (angle*Math.PI) / 180;  //degrees to radians
    	
        light = new Light(new Vector3f(0.0f, (float)(-(1000 * Math.cos(angle))), (float)((1000 * Math.sin(angle)))),new Vector3f(0.2f,0.2f, 0.3f)); //The light of the sun 
	}
	
	public static void realLifeSun(Light light) {
	    //Clock to make the sun go around based on the real time	
	   	time += DisplayManager.getFrameTimeSeconds();
		time %= 86400;
		angle = (double)(time / 240);
    	angle = (angle*Math.PI) / 180;
		light.setPosition(new Vector3f(0.0f, (float)(-(1000 * Math.cos(angle))), (float)((1000 * Math.sin(angle)))));
		daylightTime = time;
		//one hour is 3600 seconds
		//Used to change the color of the daylight over time
		if(time > 14400 && time <= 21600) {  //sunrise
			daylightTime %= 7200;
			daylightAngle = (double)(daylightTime / 20);
	    	daylightAngle = (daylightAngle*Math.PI) / 180;	
	    	light.setColor(new Vector3f( (float)(0.9 * Math.cos(daylightAngle) + 0.2), (float)(0.5 * Math.cos(daylightAngle) + 0.2), (float)(0.4 * Math.cos(daylightAngle) + 0.3)));
		}
		else if(time > 21600 && time <= 25200) {  //sunrise - day transition
			daylightTime %= 3600;
			daylightAngle = (double)(daylightTime / 10);
	    	daylightAngle = (daylightAngle*Math.PI) / 180;	
	    	light.setColor(new Vector3f((float)(-0.4 * Math.cos(daylightAngle) + 1.1), 0.7f, 0.7f));
		}
		else if(time > 25200 && time <= 61200) {  //day
			daylightTime %= 36000;
			daylightAngle = (double)(daylightTime / 100);
	    	daylightAngle = (daylightAngle*Math.PI) / 180;	
	    	light.setColor(new Vector3f(0.7f, 0.7f, 0.7f));
		}
		else if(time > 61200 && time <= 64800) {  //day - sunset transition
			daylightTime %= 3600;
			daylightAngle = (double)(daylightTime / 10);
	    	daylightAngle = (daylightAngle*Math.PI) / 180;	
	    	light.setColor(new Vector3f((float)(-0.5 * Math.cos(daylightAngle) + 0.7), 0.7f, 0.7f));
		}
		else if(time > 64800 && time <= 72000) {  //sunset
			daylightTime %= 7200;
			daylightAngle = (double)(daylightTime / 20);
	    	daylightAngle = (daylightAngle*Math.PI) / 180;	
	    	light.setColor(new Vector3f( (float)(1 * Math.cos(daylightAngle) + 0.2), (float)(0.5 * Math.cos(daylightAngle) + 0.2), (float)(0.4 * Math.cos(daylightAngle) + 0.3)));
		}
		else if(time > 72000 && time <= 14400) {  //night
			daylightTime %= 28800;
			daylightAngle = (double)(daylightTime / 80);
	    	daylightAngle = (daylightAngle*Math.PI) / 180;	
	    	light.setColor(new Vector3f( (float)(0.2 * Math.cos(daylightAngle)), (float)(0.2 * Math.cos(daylightAngle)), (float)(0.2 * Math.cos(daylightAngle) + 0.1)));
		}
    	
    	
	}
	
	
}

