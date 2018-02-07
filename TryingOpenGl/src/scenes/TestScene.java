package scenes;

import java.io.File;
import java.time.LocalTime;
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
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
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

public class TestScene extends SceneSetup{
	public final static int GRIDX = 2;
	public final static int GRIDY = 2;
	Loader loader;
	MasterRenderer renderer;
	
	Terrain[][] terrain;
	List<Entity> entities;
	List<Entity> normalMapEntities;
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
    FontType font;
    GUIText text;
    
    float time;
	LocalTime realTime;
	double angle;
	float daylightTime;
	double daylightAngle;
	
	ParticleSystem particleSystem;
	
	//need to make water reflections work for each object in the lights array
	
    public TestScene() {
    	load();
    }
    
    public void load() {
    	
    	//Basic Scene Loading
    	loader = new Loader();
    	TextMaster.init(loader);
    	renderer = new MasterRenderer(loader);
    	ParticleMaster.init(loader,  renderer.getProjectionMatrix());
    	
    	//Entities and other Array Lists Loading
    	entities = new ArrayList<Entity>();
    	normalMapEntities = new ArrayList<Entity>();
    	lights = new ArrayList<Light>();
    	guis = new ArrayList<GuiTexture>();
    	
    	//Player Loading
    	personModel = OBJLoader.loadObjModel("person", loader);
    	person = new TexturedModel(personModel, new ModelTexture(loader.loadTexture("playerTexture")));
    	player = new Player(person, new Vector3f(100, 0 ,-50), 0 ,0,0,1);
    	camera = new Camera(player);
    	
    	//Water Loading
    	fbos = new WaterFrameBuffers();
    	waters = new ArrayList<WaterTile>();
    	waterShader = new WaterShader();
    	waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
    	
    	//GUI Loading
    	guiRenderer = new GuiRenderer(loader);
    	font = new FontType(loader.loadTexture("candara"), new File("res/candara.fnt"));
    	
    	//Particle System Loading
    	ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4);
    	ParticleMaster.init(loader, renderer.getProjectionMatrix());
    	particleSystem = new ParticleSystem(particleTexture, 50, 25, 0.3f, 4, 1);
    	particleSystem.setDirection(new Vector3f(0, 1, 0), 0.1f);
    	particleSystem.setLifeError(0.1f);  //The higher the values, the more random they are
    	particleSystem.setSpeedError(0.4f);
    	particleSystem.setScaleError(0.8f);
    	particleSystem.randomizeRotation();

    	
    	//Clock Initializing
        realTime = LocalTime.now();
    	time = realTime.toSecondOfDay();
    	angle = (double)(time / 240);  //angle of line from the center of map to the point where the sun is
    	angle = (angle*Math.PI) / 180;  //degrees to radians
    }
   	
	@Override
	public void CreateTerrain() {
		 //Loading Terrain
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

	    Terrain terrain1 = new Terrain(0,-1,loader,texturePack,blendMap, "heightmap");
	    Terrain terrain2 = new Terrain(-1,-1,loader,texturePack,blendMap, "heightmap");
	    Terrain terrain3 = new Terrain(-1,0,loader,texturePack,blendMap, "heightmap");
	    Terrain terrain4 = new Terrain(0,0,loader,texturePack,blendMap, "heightmap");

	    terrain = new Terrain[GRIDY][GRIDX];
	    terrain[0][0] = terrain1;
	    terrain[1][0] = terrain2;
	    terrain[0][1] = terrain3;
	    terrain[1][1] = terrain4;
		
	}

	
	@Override
	public void CreateObjects() {
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
		
	}
	
	
	@Override
	public void CreateNormalMappedObjects() {
		//Loading up normal Mapped Entities
        TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader), new ModelTexture(loader.loadTexture("barrel")));
        barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
        barrelModel.getTexture().setShineDamper(10);
        barrelModel.getTexture().setReflectivity(0.1f);
        normalMapEntities.add(new Entity(barrelModel, new Vector3f(75, 10, -75), 0, 0, 0, 1f));
        
        TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader), new ModelTexture(loader.loadTexture("crate")));
        crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
        crateModel.getTexture().setShineDamper(10);
        crateModel.getTexture().setReflectivity(0.1f);
        normalMapEntities.add(new Entity(crateModel, new Vector3f(65, 10, -75), 0, 0, 0, 0.05f));
        
        TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader), new ModelTexture(loader.loadTexture("boulder")));
        boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
        boulderModel.getTexture().setShineDamper(10);
        boulderModel.getTexture().setReflectivity(0.1f);
        normalMapEntities.add(new Entity(boulderModel, new Vector3f(55, 10, -75), 0, 0, 0, 1f));      
	}

	
	@Override
	public void CreateLighting() {
		 //Lighting - make seperate class to do this - including adding the lamps

        Light sun = new Light(new Vector3f(0.0f, (float)(-(1000 * Math.cos(angle))), (float)((1000 * Math.sin(angle)))),new Vector3f(0.2f,0.2f, 0.3f)); //The light of the sun 
        

        lights.add(sun);
        //lights.add(new Light(new Vector3f(-200,10,-200), new Vector3f(10,0,0))); //example added non-attenuating light
        lights.add(new Light(new Vector3f(185,10,-293),new Vector3f(0,2,3), new Vector3f(1,0.01f,0.002f))); //example added attenuating point light
        TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),new ModelTexture(loader.loadTexture("lamp"))); 
        Entity lampEntity = (new Entity(lamp, new Vector3f(185,-4.7f,-293),0,0,0,1));
        entities.add(lampEntity);
        
 
		
	}
	

	@Override
	public void CreatePlayer() {
        //Loading the Player
	}
	
	
	@Override
	public void CreateGui() {
		 //Loading the Gui
        GuiTexture gui = new GuiTexture(loader.loadFontTextureAltlas("health"), new Vector2f(-0.75f, 0.90f),0, new Vector2f(0.20f, 0.30f));
        guis.add(gui);
        
        //Adding the text on the screen
        		//To get font in hiero you pick font, set java, add characters, reset cashe, make padding 8 on each side
        		//then size it to as big as possible on one page, then add distance field on side, remove just color
        		//make the spread 10 and the scale 15
        text = new GUIText("This is a test text!", 3, font, new Vector2f(0.0f,0.4f), 0.5f, true);
        text.setColor(0, 0, 0);
		
	}

	
	@Override
	public void CreateMousePicker() {
        //Mouse Picker
        //MousePicker picker =null;
		
	}

	
	@Override
	public void CreateWater() {
        //Water
        WaterTile water = new WaterTile(275, -275, 0);
        waters.add(water);
		
	}
	
	
	
	//In the while loop
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
	   	 
	    //Clock to make the sun go around based on the real time	
	   	time += DisplayManager.getFrameTimeSeconds();
		time %= 86400;
		angle = (double)(time / 240);
    	angle = (angle*Math.PI) / 180;
		lights.get(0).setPosition(new Vector3f(0.0f, (float)(-(1000 * Math.cos(angle))), (float)((1000 * Math.sin(angle)))));
		daylightTime = time;
		//one hour is 3600 seconds
		//Used to change the color of the daylight over time
		if(time > 14400 && time <= 21600) {  //sunrise
			daylightTime %= 7200;
			daylightAngle = (double)(daylightTime / 20);
	    	daylightAngle = (daylightAngle*Math.PI) / 180;	
	    	lights.get(0).setColor(new Vector3f( (float)(0.9 * Math.cos(daylightAngle) + 0.2), (float)(0.5 * Math.cos(daylightAngle) + 0.2), (float)(0.4 * Math.cos(daylightAngle) + 0.3)));
		}
		else if(time > 21600 && time <= 25200) {  //sunrise - day transition
			daylightTime %= 3600;
			daylightAngle = (double)(daylightTime / 10);
	    	daylightAngle = (daylightAngle*Math.PI) / 180;	
	    	lights.get(0).setColor(new Vector3f((float)(-0.4 * Math.cos(daylightAngle) + 1.1), 0.7f, 0.7f));
		}
		else if(time > 25200 && time <= 61200) {  //day
			daylightTime %= 36000;
			daylightAngle = (double)(daylightTime / 100);
	    	daylightAngle = (daylightAngle*Math.PI) / 180;	
	    	lights.get(0).setColor(new Vector3f(0.7f, 0.7f, 0.7f));
		}
		else if(time > 61200 && time <= 64800) {  //day - sunset transition
			daylightTime %= 3600;
			daylightAngle = (double)(daylightTime / 10);
	    	daylightAngle = (daylightAngle*Math.PI) / 180;	
	    	lights.get(0).setColor(new Vector3f((float)(-0.5 * Math.cos(daylightAngle) + 0.7), 0.7f, 0.7f));
		}
		else if(time > 64800 && time <= 72000) {  //sunset
			daylightTime %= 7200;
			daylightAngle = (double)(daylightTime / 20);
	    	daylightAngle = (daylightAngle*Math.PI) / 180;	
	    	lights.get(0).setColor(new Vector3f( (float)(1 * Math.cos(daylightAngle) + 0.2), (float)(0.5 * Math.cos(daylightAngle) + 0.2), (float)(0.4 * Math.cos(daylightAngle) + 0.3)));
		}
		else if(time > 72000 && time <= 14400) {  //night
			daylightTime %= 28800;
			daylightAngle = (double)(daylightTime / 80);
	    	daylightAngle = (daylightAngle*Math.PI) / 180;	
	    	lights.get(0).setColor(new Vector3f( (float)(0.2 * Math.cos(daylightAngle)), (float)(0.2 * Math.cos(daylightAngle)), (float)(0.2 * Math.cos(daylightAngle) + 0.1)));
		}
	   	 
	   	 
	    //picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
	            

	   	 camera.move();
	   	 
	     //picker.update();
	       
	   	 
	   	 //For the Particle System	   	 
	   	 particleSystem.generateParticles(player.getPosition());
	     ParticleMaster.renderParticles(camera);
	     ParticleMaster.update(camera);

	     
	   	 
	     
	     GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
	       
	     //Frame Buffers for reflections
	     fbos.bindReflectionFrameBuffer();
	     float distance = 2*(camera.getPosition().y - waters.get(0).getHeight());
	     camera.getPosition().y -= distance;
	     camera.invertPitch();
	     renderer.renderScene(entities, normalMapEntities, terrain, lights, camera, new Vector4f(0, 1, 0, -waters.get(0).getHeight() + 1f), GRIDX, GRIDY);
	     camera.getPosition().y += distance;
	     camera.invertPitch();
	       
	     fbos.bindRefractionFrameBuffer();
	     renderer.renderScene(entities, normalMapEntities, terrain, lights, camera, new Vector4f(0, -1, 0, waters.get(0).getHeight() + 1f), GRIDX, GRIDY);
	       
	       
	     //for mouse picker, to move lamp around
//	     Vector3f terrainPoint = picker.getCurrentTerrainPoint();
//	     if(terrainPoint!=null){
//	     	lampEntity.setPosition(terrainPoint);
//	     light.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y+15, terrainPoint.z));
//	     }
	       
	     GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
	     fbos.unbindCurrentFrameBuffer();
	     renderer.processEntity(player);
	     renderer.renderScene(entities, normalMapEntities, terrain, lights, camera, new Vector4f(0, -1, 0, 15), GRIDX, GRIDY);
	     waterRenderer.render(waters, camera, lights.get(0));
	     guiRenderer.render(guis);
	     
	     //For the text
	     float textWidth = 0.5f;
	     float textEdge = 0.1f;
	     float textBorderWidth = 0.7f;
	     float textBorderEdge = 0.1f;
	     Vector2f textOffset = new Vector2f(0.003f, 0.003f);
	     Vector3f textOutlineColor = new Vector3f(1, 0, 0);
	     
	     TextMaster.render(textWidth, textEdge, textBorderWidth, textBorderEdge, textOffset, textOutlineColor);

	}
	
	
	
	
	public void Destroy() {
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		fbos.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
	}

	
	
}
