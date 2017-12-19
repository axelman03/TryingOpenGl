package skybox;


import java.time.LocalTime;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import models.RawModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;

public class SkyboxRenderer {
	
	private static final float SIZE = 500f;
	
	private static final float[] VERTICES = {        
	    -SIZE,  SIZE, -SIZE,
	    -SIZE, -SIZE, -SIZE,
	    SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	    -SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE
	};
	
	private static String[] DAY_TEXTURE_FILES = {"right","left","top","bottom","back","front"};
	private static String[] NIGHT_TEXTURE_FILES = {"nightRight","nightLeft","nightTop","nightBottom","nightBack","nightFront"};
	private RawModel cube;
	private int dayTexture;
	private int nightTexture;
	private SkyboxShader shader;
	private float time = 0;
	
	private LocalTime realTime;
	
	public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix){
		cube = loader.loadToVAO(VERTICES, 3);
		dayTexture = loader.loadCubeMap(DAY_TEXTURE_FILES);
		nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES);
		shader = new SkyboxShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Camera camera, float r, float g, float b){
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadFogColor(r, g, b);
		GL30.glBindVertexArray(cube.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		bindTextures();
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	
	private void bindTextures(){
		//for day/night cycle
		realTime = LocalTime.now();
		time = realTime.toSecondOfDay();
		time += DisplayManager.getFrameTimeSeconds();
		time %= 86400;
		int texture1;
		int texture2;
		float blendFactor;		
		if(time >= 0 && time < 21600){
			texture1 = nightTexture;
			texture2 = nightTexture;
			blendFactor = (time - 0)/(21600 - 0);
		}else if(time >= 21600 && time < 43200){
			texture1 = nightTexture;
			texture2 = dayTexture;
			blendFactor = (time - 21600)/(43200 - 21600);
		}else if(time >= 43200 && time < 64800){
			texture1 = dayTexture;
			texture2 = dayTexture;
			blendFactor = (time - 43200)/(64800 - 43200);
		}else{
			texture1 = dayTexture;
			texture2 = nightTexture;
			blendFactor = (time - 64800)/(86400 - 64800);
		}
		//System.out.println(realTime.toSecondOfDay());

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
		shader.loadBlendFactor(blendFactor);  
		
		
		// for total skybox Blending, no day/night cycle
//		GL13.glActiveTexture(GL13.GL_TEXTURE0);
//		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, dayTexture);
//		GL13.glActiveTexture(GL13.GL_TEXTURE1);
//		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, nightTexture);
//		shader.loadBlendFactor(0.7f);
	}
	
}
