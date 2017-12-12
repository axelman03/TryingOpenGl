package scenes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class TestScene {
	public final static int GRIDX = 2;
	public final static int GRIDY = 2;
	Loader loader;
	MasterRenderer renderer;
	
	Terrain[][] terrain;
	List<Entity> entities;
	List<Light>lights;
	List<GuiTexture>guis;
	List<WaterTile>waters;
	
	WaterFrameBuffers fbos;
    WaterRenderer waterRenderer;
    WaterShader waterShader;
    
	RawModel personModel;
	TexturedModel person;
	Player player;
    Camera camera; 
    
    GuiRenderer guiRenderer;
	
    public TestScene() {
    	loader = new Loader();
    	renderer = new MasterRenderer(loader);
    	
    	entities = new ArrayList<Entity>();
    	lights = new ArrayList<Light>();
    	guis = new ArrayList<GuiTexture>();
    	
    	personModel = OBJLoader.loadObjModel("person", loader);
    	person = new TexturedModel(personModel, new ModelTexture(loader.loadTexture("playerTexture")));
    	player = new Player(person, new Vector3f(100, 0 ,-50), 0 ,0,0,1);
    	camera = new Camera(player);
    	
    	fbos = new WaterFrameBuffers();
    	waters = new ArrayList<WaterTile>();
    	waterShader = new WaterShader();
    	waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
    	
    	guiRenderer = new GuiRenderer(loader);
    }
	public void Create() {
		
        
	       
        //Loading Terrain
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        
        //List<Terrain> terrains = new ArrayList<Terrain>();
	    //terrains.add(new Terrain(0,-1,loader,texturePack,blendMap, "heightMap"));
	    //terrains.add(new Terrain(-1,-1,loader,texturePack,blendMap, "heightMap"));
	    Terrain terrain1 = new Terrain(0,-1,loader,texturePack,blendMap, "heightMap");
	    Terrain terrain2 = new Terrain(-1,-1,loader,texturePack,blendMap, "heightMap");
	    Terrain terrain3 = new Terrain(-1,0,loader,texturePack,blendMap, "heightMap");
	    Terrain terrain4 = new Terrain(0,0,loader,texturePack,blendMap, "heightMap");

	    
	    terrain = new Terrain[GRIDY][GRIDX];
	    terrain[0][0] = terrain1;
	    terrain[1][0] = terrain2;
	    terrain[0][1] = terrain3;
	    terrain[1][1] = terrain4;
	    
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
        
        Random random = new Random(676452);
       
        for (int q = 0; q < GRIDY; q++){
        	for (int c = 0; c < GRIDX; c++){
        		for(int i=0;i<500;i++){
        			if(i%20 == 0){
	        			float x = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getX();
	        			float z = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getZ();
	        			float y = terrain[q][c].getHeightOfTerrain(x,z);
	        			entities.add(new Entity(staticModel, new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, 6f));
	        			x = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getX();
	        			z = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getZ();
	        			y = terrain[q][c].getHeightOfTerrain(x,z);
	        			entities.add(new Entity(grass, new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, 2f));
	        			x = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getX();
	        			z = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getZ();
	        			y = terrain[q][c].getHeightOfTerrain(x,z);
	        			entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, 0.9f));
	        			x = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getX();
	        			z = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getZ();
	        			y = terrain[q][q].getHeightOfTerrain(x,z);
	        			entities.add(new Entity(tree, new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, random.nextFloat()*0.1f + 0.8f));
	        			x = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getX();
	        			z = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getZ();
	        			y = terrain[q][c].getHeightOfTerrain(x,z);
	        			entities.add(new Entity(flowers, new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, 0.9f));
	        		}
        		}
        	}
        }
        
       
       
        
        //Lighting - make seperate class to do this - including adding the lamps
        Light sun = new Light(new Vector3f(0,10000,-7000),new Vector3f(0.7f,0.7f,0.7f)); //The light of the sun
        lights.add(sun);
        //lights.add(new Light(new Vector3f(-200,10,-200), new Vector3f(10,0,0))); //example added non-attenuating light
        lights.add(new Light(new Vector3f(185,10,-293),new Vector3f(0,2,3), new Vector3f(1,0.01f,0.002f))); //example added attenuating point light
        TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),new ModelTexture(loader.loadTexture("lamp"))); 
        Entity lampEntity = (new Entity(lamp, new Vector3f(185,-4.7f,-293),0,0,0,1));
        entities.add(lampEntity);
        //Loading the Player
 
        //Loading the Gui
        GuiTexture gui = new GuiTexture(loader.loadTexture("health"), new Vector2f(-0.75f, 0.90f),0, new Vector2f(0.20f, 0.30f));
        guis.add(gui);
  
        //Mouse Picker
        //MousePicker picker =null;
        
        //Water
        WaterTile water = new WaterTile(275, -275, 0);
        waters.add(water);
	}
	
	public void Run() {
		
   	 for(int q = 0; q < GRIDY; q++) {
   		 for (int c = 0; c < GRIDX; c++){
   			 if(terrain[q][c].getX() <= player.getPosition().x) { 
   		    	if(terrain[q][c].getX() + terrain[q][c].getSize() > player.getPosition().x) {
   		    		if(terrain[q][c].getZ() <= player.getPosition().z) {
   		            	if(terrain[q][c].getZ() + terrain[q][c].getSize() > player.getPosition().z) {
   		                	player.move(terrain[q][c]);
   		                

   		            	}
   		        	}
   		    	}
   	    	}
   		 }
   	 }
   	 //picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
            

       camera.move();
       //picker.update();
       
       GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
       
       //Frame Buffers for reflections
       fbos.bindReflectionFrameBuffer();
       float distance = 2*(camera.getPosition().y - waters.get(0).getHeight());
       camera.getPosition().y -= distance;
       camera.invertPitch();
       renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, 1, 0, -waters.get(0).getHeight() + 1f), GRIDX, GRIDY);
       camera.getPosition().y += distance;
       camera.invertPitch();
       
       fbos.bindRefractionFrameBuffer();
       renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, -1, 0, waters.get(0).getHeight() + 1f), GRIDX, GRIDY);
       
       
       //for mouse picker, to move lamp around
//       Vector3f terrainPoint = picker.getCurrentTerrainPoint();
//       if(terrainPoint!=null){
//       	 lampEntity.setPosition(terrainPoint);
//       	light.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y+15, terrainPoint.z));
//       }
       
       GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
       fbos.unbindCurrentFrameBuffer();
       renderer.processEntity(player);
       renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, -1, 0, 15), GRIDX, GRIDY);
       waterRenderer.render(waters, camera, lights.get(0)); //Need to change the light part into a sun, that means adding a sun
       guiRenderer.render(guis);
       DisplayManager.updateDisplay();
	}
	
	public void Disable() {
		fbos.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
	}
}
