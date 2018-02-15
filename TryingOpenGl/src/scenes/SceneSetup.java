package scenes;



public abstract class SceneSetup {
	
	public abstract void load();
	public void create() {
	       createTerrain();
	       createLighting();
	       createPlayer();
	       createGui();
	       createMousePicker();
	       createWater();
	       createObjects();
	       createNormalMappedObjects();
	       createSound();
	}
	public abstract void createTerrain();
	public abstract void createObjects();
	public abstract void createNormalMappedObjects();
	public abstract void createLighting();
	public abstract void createPlayer();
	public abstract void createGui();
	public abstract void createMousePicker();
	public abstract void createWater();
	public abstract void createSound();
	public abstract void run();
	public abstract void destroy();
}
