package scenes;



public abstract class SceneSetup {
	
	public abstract void load();
	public void Create() {
	       CreateTerrain();
	       CreateObjects();
	       CreateNormalMappedObjects();
	       CreateLighting();
	       CreatePlayer();
	       CreateGui();
	       CreateMousePicker();
	       CreateWater();
	}
	public abstract void CreateTerrain();
	public abstract void CreateObjects();
	public abstract void CreateNormalMappedObjects();
	public abstract void CreateLighting();
	public abstract void CreatePlayer();
	public abstract void CreateGui();
	public abstract void CreateMousePicker();
	public abstract void CreateWater();
	public abstract void Run();
	public abstract void Destroy();
}
