package engineTester;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.ModelData;
import renderEngine.OBJFileLoader;
import renderEngine.OBJLoader;
import terrain.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolBox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	 public static void main(String[] args) {
		 
	        DisplayManager.createDisplay();
	        Loader loader = new Loader();
	        
	       
	        //Loading Terrain
	        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
	        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
	        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
	        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
	        
	        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
	        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
	        
	        List<Terrain> terrains = new ArrayList<Terrain>();
		    terrains.add(new Terrain(0,-1,loader,texturePack,blendMap, "heightMap"));
		    terrains.add(new Terrain(-1,-1,loader,texturePack,blendMap, "heightMap"));
		    /*Terrain terrain1 = new Terrain(0,-1,loader,texturePack,blendMap, "heightMap");
		    Terrain terrain2 = new Terrain(-1,-1,loader,texturePack,blendMap, "heightMap");
		    Terrain terrain3 = new Terrain(-1,0,loader,texturePack,blendMap, "heightMap");
		    Terrain terrain4 = new Terrain(0,0,loader,texturePack,blendMap, "heightMap");
		    Terrain[][] terrain;
		    terrain = new Terrain[2][2];
		    terrain[0][0] = terrain1;
		    terrain[1][0] = terrain2;
		    terrain[0][1] = terrain3;
		    terrain[1][1] = terrain4;*/
		    
		    //Creating Models and Stuff
		    ModelData data = OBJFileLoader.loadOBJ("tree");
	        RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
	        
	        TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("tree")));
	        TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),new ModelTexture(loader.loadTexture("grassTexture"))); 
	        grass.getTexture().setHasTransparency(true);
	        grass.getTexture().setUseFakeLighting(true);
	        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
	        fernTextureAtlas.setNumberOfRows(2);
	        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),fernTextureAtlas);
	        fern.getTexture().setHasTransparency(true);
	        TexturedModel flowers = new TexturedModel(OBJLoader.loadObjModel("fern", loader),new ModelTexture(loader.loadTexture("flower")));
	        flowers.getTexture().setHasTransparency(true);
	        TexturedModel tree = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader),new ModelTexture(loader.loadTexture("lowPolyTree"))); 
	        
	        //Loading Models and Stuff - make separate class to do this
	        List<Entity> entities = new ArrayList<Entity>();
	        Random random = new Random(676452);
	       
	        for (Terrain terrain:terrains){
	        	for(int i=0;i<500;i++){
		        	if(i%20 == 0){
		        		float x = random.nextInt((int)terrain.getSize())+terrain.getX();
		        		float z = random.nextInt((int)terrain.getSize())+terrain.getZ();
		        		float y = terrain.getHeightOfTerrain(x,z);
		        		entities.add(new Entity(staticModel, new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, 6f));
		        		x = random.nextInt((int)terrain.getSize())+terrain.getX();
		        		z = random.nextInt((int)terrain.getSize())+terrain.getZ();
		        		y = terrain.getHeightOfTerrain(x,z);
		        		entities.add(new Entity(grass, new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, 2f));
		        		x = random.nextInt((int)terrain.getSize())+terrain.getX();
		        		z = random.nextInt((int)terrain.getSize())+terrain.getZ();
		        		y = terrain.getHeightOfTerrain(x,z);
		        		entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, 0.9f));
		        		x = random.nextInt((int)terrain.getSize())+terrain.getX();
		        		z = random.nextInt((int)terrain.getSize())+terrain.getZ();
		        		y = terrain.getHeightOfTerrain(x,z);
		        		entities.add(new Entity(tree, new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, random.nextFloat()*0.1f + 0.8f));
		        		x = random.nextInt((int)terrain.getSize())+terrain.getX();
		        		z = random.nextInt((int)terrain.getSize())+terrain.getZ();
		        		y = terrain.getHeightOfTerrain(x,z);
		        		entities.add(new Entity(flowers, new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, 0.9f));
		        	}
	        	}
	        }
	        
	       
	        MasterRenderer renderer = new MasterRenderer(loader);
	        
	        //Lighting - make seperate class to do this - including adding the lamps
	        Light light = new Light(new Vector3f(0,10000,-7000),new Vector3f(0.7f,0.7f,0.7f)); //The light of the sun
	        List<Light>lights = new ArrayList<Light>();
	        lights.add(light);
	        //lights.add(new Light(new Vector3f(-200,10,-200), new Vector3f(10,0,0))); //example added non-attenuating light
	        lights.add(new Light(new Vector3f(185,10,-293),new Vector3f(0,2,3), new Vector3f(1,0.01f,0.002f))); //example added attenuating point light
	        TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),new ModelTexture(loader.loadTexture("lamp"))); 
	        Entity lampEntity = (new Entity(lamp, new Vector3f(185,-4.7f,-293),0,0,0,1));
	        entities.add(lampEntity);
	        //Loading the Player
	        RawModel personModel = OBJLoader.loadObjModel("person", loader);
	        TexturedModel person = new TexturedModel(personModel, new ModelTexture(loader.loadTexture("playerTexture")));
	        Player player = new Player(person, new Vector3f(100, 0 ,-50), 0 ,0,0,1);
	        Camera camera = new Camera(player);  
	        
	        //Loading the Gui
	        List<GuiTexture>guis = new ArrayList<GuiTexture>();
	        GuiTexture gui = new GuiTexture(loader.loadTexture("health"), new Vector2f(-0.75f, 0.90f),0, new Vector2f(0.20f, 0.30f));
	        guis.add(gui);
	        
	        GuiRenderer guiRenderer = new GuiRenderer(loader);
	        
	        //Mouse Picker
	        MousePicker picker =null;
	        
	        //Water
	        WaterShader waterShader = new WaterShader();
	        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix());
	        List<WaterTile> waters = new ArrayList<WaterTile>();
	        WaterTile water = new WaterTile(275, -275, 0);
	        waters.add(water);
	        
	        
	        WaterFrameBuffers fbos = new WaterFrameBuffers();
	        GuiTexture refraction = new GuiTexture(fbos.getRefractionTexture(), new Vector2f(0.5f,0.5f), 0, new Vector2f(0.25f,0.25f));
	        GuiTexture reflection = new GuiTexture(fbos.getReflectionTexture(), new Vector2f(-0.5f,0.5f), 0, new Vector2f(0.25f,0.25f));
	        guis.add(refraction);
	        guis.add(reflection);
	        
	        //The GameLoop
	        while(!Display.isCloseRequested()){
	        	
	        	 for(Terrain terrain : terrains) {
	        		if(terrain.getX() <= player.getPosition().x) { 
	        		    if(terrain.getX() + terrain.getSize() > player.getPosition().x) {
	        		        if(terrain.getZ() <= player.getPosition().z) {
	        		            if(terrain.getZ() + terrain.getSize() > player.getPosition().z) {
	        		                player.move(terrain);
	        		                picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain); //NEED TO CHANGE BUT THIS IS PROGRESS
	        		            }
	        		        }
	        		   }
	        	    }
	        	}
	            camera.move();
	            picker.update();
	            
	            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
	            
	            //Frame Buffers for reflections
	            fbos.bindReflectionFrameBuffer();
	            float distance = 2*(camera.getPosition().y - water.getHeight());
	            camera.getPosition().y -= distance;
	            camera.invertPitch();
	            renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()));
	            camera.getPosition().y += distance;
	            camera.invertPitch();
	            
	            fbos.bindRefractionFrameBuffer();
	            renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));
	            
	            
	            
	            //for mouse picker, to move lamp around
	            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
	            if(terrainPoint!=null){
	            	 lampEntity.setPosition(terrainPoint);
	            	light.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y+15, terrainPoint.z));
	            }
	            
	            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
	            fbos.unbindCurrentFrameBuffer();
	            renderer.processEntity(player);
	            renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, 15));
	            waterRenderer.render(waters, camera);
	            guiRenderer.render(guis);
	            DisplayManager.updateDisplay();
	        }
	        fbos.cleanUp();
	        waterShader.cleanUp();
	        guiRenderer.cleanUp();
	        renderer.cleanUp();
	        loader.cleanUp();
	        DisplayManager.closeDisplay();
	 
	    }
	}
