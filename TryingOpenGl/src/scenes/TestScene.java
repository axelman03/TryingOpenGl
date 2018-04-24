package scenes;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entities.collisionDetection.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import audio.AudioMaster;
import audio.Source;
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
import skybox.SunPosition;
import terrain.HeightsGenerator;
import terrain.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
//ToDo: Make collision Detection Work
/*
	add narrow sweep
	experiment with different shapes and with simple object meshes
	https://github.com/kmammou/v-hacd  - program to make the simple meshes
	https://www.toptal.com/game/video-game-physics-part-ii-collision-detection-for-solid-objects
*/
public class TestScene extends SceneSetup{
	public final static int GRIDX = 2;
	public final static int GRIDY = 2;
	Loader loader;
	MasterRenderer renderer;
	
	Terrain[][] terrain;
	ArrayList<Entity> entities;
	List<Entity> normalMapEntities;
	List<Light>lights;
	List<GuiTexture>guis;
	List<WaterTile>waters;
	List<Integer>sounds;
	ArrayList<HitBoxSquare>hitBoxes;
	
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
    
    Source soundSource;
    
	ParticleSystem particleSystem;

	TexturedModel car;
	
	//need to make water reflections work for each object in the lights array
	
    public TestScene() {
    	load();
    }
    
    public void load() {
    	

    	
    	//Basic Scene Loading
    	loader = new Loader();
    	TextMaster.init(loader);
    	
    	//Player Loading
    	personModel = OBJLoader.loadObjModel("person", loader);
    	person = new TexturedModel(personModel, new ModelTexture(loader.loadTexture("playerTexture")), OBJLoader.getMaxVertices(), OBJLoader.getMinVertices());
    	player = new Player(person, new Vector3f(100, 0 ,-50), 0 ,0,0,1, person.getMaxVertices(), person.getMinVertices());
    	camera = new Camera(player);
    	
    	//More Basic Scene Loading
    	renderer = new MasterRenderer(loader, camera);
    	
    	//Entities and other Array Lists Loading
    	entities = new ArrayList<Entity>();
    	normalMapEntities = new ArrayList<Entity>();
    	lights = new ArrayList<Light>();
    	guis = new ArrayList<GuiTexture>();
    	sounds = new ArrayList<Integer>();
    	hitBoxes = new ArrayList<HitBoxSquare>();
    	
    	//Water Loading
    	fbos = new WaterFrameBuffers();
    	waters = new ArrayList<WaterTile>();
    	waterShader = new WaterShader();
    	waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
    	
    	//GUI Loading
    	guiRenderer = new GuiRenderer(loader);
    	font = new FontType(loader.loadTexture("candara"), new File("TryingOpenGl/res/candara.fnt"));
    	
    	//Particle System Loading
    	ParticleMaster.init(loader,  renderer.getProjectionMatrix());
    	ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4);
    	ParticleMaster.init(loader, renderer.getProjectionMatrix());
    	particleSystem = new ParticleSystem(particleTexture, 50, 25, 0.3f, 4, 1);
    	particleSystem.setDirection(new Vector3f(0, 1, 0), 0.1f);
    	particleSystem.setLifeError(0.1f);  //The higher the values, the more random they are
    	particleSystem.setSpeedError(0.4f);
    	particleSystem.setScaleError(0.8f);
    	particleSystem.randomizeRotation();


    	//Sound Loading
    	AudioMaster.init();
    	AudioMaster.setListenerData(camera.getPosition().x, camera.getPosition().y,camera.getPosition().z);
		soundSource = new Source();
		AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE_CLAMPED);  //The farther from sound, the less you hear the noise; look at the last sound tutorial as reference for models




    }
   	
	@Override
	public void createTerrain() {
		 //Loading Terrain
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        //Desert Race Track
		TerrainTexture desertBackgroundTexture = new TerrainTexture(loader.loadTexture("sandy"));
		TerrainTexture desertRTexture = new TerrainTexture(loader.loadTexture("grassAroundTrack"));
		TerrainTexture desertGTexture = new TerrainTexture(loader.loadTexture("hardSand"));
		TerrainTexture desertBTexture = new TerrainTexture(loader.loadTexture("racingDirt"));

		TerrainTexturePack desertTexturePack = new TerrainTexturePack(desertBackgroundTexture, desertRTexture, desertGTexture, desertBTexture);
		TerrainTexture desertBlendMap = new TerrainTexture(loader.loadTexture("blendMapDesertRaceTrack"));

	    Terrain terrain1 = new Terrain(0,-1,loader,desertTexturePack,desertBlendMap, "heightmap", false);
	    Terrain terrain2 = new Terrain(-1,-1,loader,texturePack,blendMap, "heightmap", false);
	    Terrain terrain3 = new Terrain(-1,0,loader,texturePack,blendMap, "heightmap", false);
	    Terrain terrain4 = new Terrain(0,0,loader,texturePack,blendMap, "heightmap", false);

	    terrain = new Terrain[GRIDY][GRIDX];
	    terrain[0][0] = terrain1;
	    terrain[1][0] = terrain2;
	    terrain[0][1] = terrain3;
	    terrain[1][1] = terrain4;
	    
	    HeightsGenerator.smoothBetweenTerrains(terrain1, terrain2);
	    HeightsGenerator.smoothBetweenTerrains(terrain2, terrain3);
	    HeightsGenerator.smoothBetweenTerrains(terrain3, terrain4);
	    HeightsGenerator.smoothBetweenTerrains(terrain4, terrain1);


	}

	//To make Object textures work, select all in blender at bottom, and hit ctrl-J or join
	
	@Override
	public void createObjects() {
		   //Creating Models and Stuff
	    ModelData data = OBJFileLoader.loadOBJ("tree");
        RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        
        TexturedModel highPolyTree = new TexturedModel(model,new ModelTexture(loader.loadTexture("tree")), OBJLoader.getMaxVertices(), OBJLoader.getMinVertices());
        TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),new ModelTexture(loader.loadTexture("grassTexture")), OBJLoader.getMaxVertices(), OBJLoader.getMinVertices());
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),fernTextureAtlas, OBJLoader.getMaxVertices(), OBJLoader.getMinVertices());
        fern.getTexture().setHasTransparency(true);
        TexturedModel flowers = new TexturedModel(OBJLoader.loadObjModel("fern", loader),new ModelTexture(loader.loadTexture("flower")), OBJLoader.getMaxVertices(), OBJLoader.getMinVertices());
        flowers.getTexture().setHasTransparency(true);
        TexturedModel tree = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader),new ModelTexture(loader.loadTexture("lowPolyTree")), OBJLoader.getMaxVertices(), OBJLoader.getMinVertices());

        car = new TexturedModel(OBJLoader.loadObjModel("bullet350_3", loader), new ModelTexture(loader.loadTexture("bullet350Skin2_Texture")), OBJLoader.getMaxVertices(), OBJLoader.getMinVertices());
        entities.add(new Entity(car, new Vector3f(35, 5, -75), 0, 0, 0, 6f, car.getMaxVertices(), car.getMinVertices()));
        entities.get(1).setBox("bullet350HitBoxMesh");
        /*
        for(int x = 0; x < entities.get(1).getHitBoxMesh().getTransformedVao().getVertexPositions().length - 2; x = x + 3) {
			System.out.println(entities.get(1).getHitBoxMesh().getTransformedVao().getVertexPositions(x));
			System.out.println(entities.get(1).getHitBoxMesh().getTransformedVao().getVertexPositions(x + 1));
			System.out.println(entities.get(1).getHitBoxMesh().getTransformedVao().getVertexPositions(x + 2));
			System.out.println();
		}
		*/
        hitBoxes.add(entities.get(1).getBox());
		//System.out.println(entities.get(1).getBox().getPosition());
		//System.out.println(entities.get(1).getBox().getXMax() + " " + entities.get(1).getBox().getYMax() + " " + entities.get(1).getBox().getZMax());
		//System.out.println(entities.get(1).getBox().getXMin() + " " + entities.get(1).getBox().getYMin() + " " + entities.get(1).getBox().getZMin());
        //Loading Models and Stuff - make separate class to do this
        Random random = new Random(676452);
       
        for (int q = 0; q < GRIDY; q++){
        	for (int c = 0; c < GRIDX; c++){
        		for(int i=0;i<500;i++){
        			for(int w = 0; w < waters.size(); w++) {
        				if(i%20 == 0){
    	        			float x = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getX();
    	        			float z = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getZ();
    	        			float y = terrain[q][c].getHeightOfTerrain(x,z);
    	        			do {
    	        				x = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getX();
    	    	        		z = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getZ();
    	    	        		y = terrain[q][c].getHeightOfTerrain(x,z);
    	        			}while(y <= waters.get(w).getHeight());
    	        			entities.add(new Entity(highPolyTree, new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, 6f, highPolyTree.getMaxVertices(), highPolyTree.getMinVertices()));
    	        			do {
    	        				x = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getX();
    	    	        		z = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getZ();
    	    	        		y = terrain[q][c].getHeightOfTerrain(x,z);
    	        			}while(y <= waters.get(w).getHeight());
    	        			entities.add(new Entity(grass, new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, 2f, grass.getMaxVertices(), grass.getMinVertices()));
    	        			do {
    	        				x = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getX();
    	    	        		z = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getZ();
    	    	        		y = terrain[q][c].getHeightOfTerrain(x,z);
    	        			}while(y <= waters.get(w).getHeight());
    	        			entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, 0.9f, fern.getMaxVertices(), fern.getMinVertices()));
    	        			do {
    	        				x = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getX();
    	    	        		z = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getZ();
    	    	        		y = terrain[q][c].getHeightOfTerrain(x,z);
    	        			}while(y <= waters.get(w).getHeight());
    	        			entities.add(new Entity(tree, new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, random.nextFloat()*0.1f + 0.8f, tree.getMaxVertices(), tree.getMinVertices()));
    	        			do {
    	        				x = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getX();
    	    	        		z = random.nextInt((int)terrain[q][c].getSize())+terrain[q][c].getZ();
    	    	        		y = terrain[q][c].getHeightOfTerrain(x,z);
    	        			}while(y <= waters.get(w).getHeight());
    	        			entities.add(new Entity(flowers, new Vector3f(x,y,z),0,random.nextFloat() * 360, 0, 0.9f, flowers.getMaxVertices(), flowers.getMinVertices()));

    	        		}
        			}
        			
        		}
        	}
        }
		
	}

	@Override
	public void createNormalMappedObjects() {
		//Loading up normal Mapped Entities
        TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader), new ModelTexture(loader.loadTexture("barrel")), OBJLoader.getMaxVertices(), OBJLoader.getMinVertices());
        barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
        barrelModel.getTexture().setShineDamper(10);
        barrelModel.getTexture().setReflectivity(0.1f);
        normalMapEntities.add(new Entity(barrelModel, new Vector3f(75, 10, -75), 0, 0, 0, 1f, barrelModel.getMaxVertices(), barrelModel.getMinVertices()));
        
        TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader), new ModelTexture(loader.loadTexture("crate")), OBJLoader.getMaxVertices(), OBJLoader.getMinVertices());
        crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
        crateModel.getTexture().setShineDamper(10);
        crateModel.getTexture().setReflectivity(0.1f);
        normalMapEntities.add(new Entity(crateModel, new Vector3f(65, 10, -75), 0, 0, 0, 0.05f, crateModel.getMaxVertices(), crateModel.getMinVertices()));
        
        TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader), new ModelTexture(loader.loadTexture("boulder")), OBJLoader.getMaxVertices(), OBJLoader.getMinVertices());
        boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
        boulderModel.getTexture().setShineDamper(10);
        boulderModel.getTexture().setReflectivity(0.1f);
        normalMapEntities.add(new Entity(boulderModel, new Vector3f(55, 10, -75), 0, 0, 0, 1f, boulderModel.getMaxVertices(), boulderModel.getMinVertices()));

        TexturedModel carModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("bullet350_3", loader), new ModelTexture(loader.loadTexture("bullet350Skin2_Texture2_COLOR")), OBJLoader.getMaxVertices(), OBJLoader.getMinVertices());
        carModel.getTexture().setNormalMap(loader.loadTexture("bullet350Skin2_Texture2_NRM"));
        carModel.getTexture().setShineDamper(10);
        carModel.getTexture().setReflectivity(0.1f);
        normalMapEntities.add(new Entity(carModel, new Vector3f(15, 10, -75), 0, 0, 0, 10f, carModel.getMaxVertices(), carModel.getMinVertices()));
	}

	
	@Override
	public void createLighting() {
		 //Lighting - make seperate class to do this - including adding the lamps

        Light sun = new Light(new Vector3f(0.0f, 0.0f, 0.0f),new Vector3f(0.0f,0.0f, 0.0f)); //The light of the sun 
        

        lights.add(sun);
        //lights.add(new Light(new Vector3f(-200,10,-200), new Vector3f(10,0,0))); //example added non-attenuating light
        lights.add(new Light(new Vector3f(185,10,-293),new Vector3f(0,2,3), new Vector3f(1,0.01f,0.002f))); //example added attenuating point light
        TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),new ModelTexture(loader.loadTexture("lamp")), OBJLoader.getMaxVertices(), OBJLoader.getMinVertices());
        Entity lampEntity = (new Entity(lamp, new Vector3f(185,-4.7f,-293),0,0,0,1, lamp.getMaxVertices(), lamp.getMinVertices()));
        entities.add(lampEntity);
        
    	SunPosition.setRealTime(lights.get(0));
        
 
		
	}
	

	@Override
	public void createPlayer() {
    	player.setBox("PersonHitBoxMesh");
        //Loading the Player
	}
	
	
	@Override
	public void createGui() {
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
	public void createMousePicker() {
        //Mouse Picker
        //MousePicker picker =null;
		
	}

	
	@Override
	public void createWater() {
        //Water
        WaterTile water = new WaterTile(275, -275, 0);
        waters.add(water);
		
	}
	
	@Override
	public void createSound() {
		//Sound Effects
		int buffer = AudioMaster.loadSound("audio/bounce.wav");
		sounds.add(buffer);
		soundSource.setLooping(true);
		soundSource.setVolume(5);
		soundSource.setPitch(1);
		soundSource.setPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
	   	soundSource.play(sounds.get(0));
	}
	
	
	
	//In the while loop
	public void run() {
		
	   	 for(int q = 0; q < GRIDY; q++) {
	   		 for (int c = 0; c < GRIDX; c++){
	   			 if(terrain[q][c].getX() <= player.getPosition().x) { 
	   		    	if(terrain[q][c].getX() + terrain[q][c].getSize() > player.getPosition().x) {
	   		    		if(terrain[q][c].getZ() <= player.getPosition().z) {
	   		            	if(terrain[q][c].getZ() + terrain[q][c].getSize() > player.getPosition().z) {
	   		                	player.move(terrain[q][c], entities);
	   		            	}
	   		        	}
	   		    	}
	   	    	}
	   		 }
	   	 }

		if (Keyboard.isKeyDown(Keyboard.KEY_R)){
	   	 	car.setTexture(new ModelTexture(loader.loadTexture("bullet350Skin1_Texture")));
			entities.add(1, new Entity(car, new Vector3f(35, 10, -75), 0, 0, 0, 10f, car.getMaxVertices(), car.getMinVertices()));
			//entities.get(1).setBox();
		}

		player.wallCollision(entities);






		SunPosition.realLifeSun(lights.get(0));
	     //picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
	            
	  
	   	 camera.move();
	   	 
	     //picker.update();
	       
	   	 //For Sound System

	   	 
	   	 //For the Particle System	   	 
	   	 particleSystem.generateParticles(new Vector3f(275, 0, -275));
	     ParticleMaster.update(camera);
   	 
	     renderer.renderShadowMap(entities, lights.get(0));
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
	     
	     
	     //Rendering to screen
	     GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
	     fbos.unbindCurrentFrameBuffer();
	     renderer.processEntity(player);
	     renderer.renderScene(entities, normalMapEntities, terrain, lights, camera, new Vector4f(0, -1, 0, 15), GRIDX, GRIDY);
	     waterRenderer.render(waters, camera, lights.get(0));
	     ParticleMaster.renderParticles(camera);
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
	
	
	
	
	public void destroy() {
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		AudioMaster.cleanUp();
		fbos.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
	}

	
	
}
