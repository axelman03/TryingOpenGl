package scenes;

public interface SceneSetup {

	public void load();
	public void Create();
	public void CreateTerrain();
	public void CreateObjects();
	public void CreateLighting();
	public void CreateNormalMappedObjects();
	public void CreatePlayer();
	public void CreateGui();
	public void CreateMousePicker();
	public void CreateWater();
	public void Run();
	public void Destroy();
}
