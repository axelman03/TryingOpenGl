package toolBox;


import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engineTester.MainGameLoop;
import entities.Camera;
import terrain.Terrain;

public class MousePicker {
//ray casting
	private static final int RECURSION_COUNT = 200;
	private static final float RAY_RANGE = 600;

	private Vector3f currentRay = new Vector3f();

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	
	private Terrain terrain;
	private Terrain[][] terrain2;
	private Vector3f currentTerrainPoint;
	private int gridX;
	private int gridY;

	public MousePicker(Camera cam, Matrix4f projection, Terrain[][] terrain2, int gridX, int gridY) {
		camera = cam;
		projectionMatrix = projection;
		viewMatrix = Maths.createViewMatrix(camera);
		this.gridX = gridX;
		this.gridY = gridY;
		this.terrain2 = terrain2;
	}
	
	public void setTerrain(Terrain[][] terrain, int q, int c) {
		this.terrain = terrain[q][c];
	}
	
	public Vector3f getCurrentTerrainPoint() {
		return currentTerrainPoint;
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	public void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
		if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
		} else {
			currentTerrainPoint = null;
		}
	}

	private Vector3f calculateMouseRay() {
		float mouseX = Mouse.getX();
		float mouseY = Mouse.getY();
		Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}

	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2.0f * mouseX) / Display.getWidth() - 1f;
		float y = (2.0f * mouseY) / Display.getHeight() - 1f;
		return new Vector2f(x, y);
	}
	//clicking on objects
	//**********************************************************
	
	private Vector3f getPointOnRay(Vector3f ray, float distance) {
		Vector3f camPos = camera.getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return Vector3f.add(start, scaledRay, null);
	}
	
	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
		
		float half = start + ((finish - start) / 2f);
		boolean terrainTrue = false;
		if (count >= RECURSION_COUNT) {
			
			for(int q = 0; q < gridY; q++) {
				for(int c = 0; c < gridX; c++) {
					setTerrain(terrain2, q, c);
					
					Vector3f endPoint = getPointOnRay(ray, half);
					Terrain terrain = getTerrain(endPoint.getX(), endPoint.getZ());
					if (terrain != null) {
						terrainTrue = true;
						break;
					} else {
						terrainTrue = false;
					}
				}
				if(terrainTrue == true) {
					break;
				}
			}
			Vector3f endPoint = getPointOnRay(ray, half);
			if (terrain != null) {
				return endPoint;
			} else {
				return null;
			}
			
		}
		if (intersectionInRange(start, half, ray)) {
			return binarySearch(count + 1, start, half, ray);
		} else {
			return binarySearch(count + 1, half, finish, ray);
		}
	}

	private boolean intersectionInRange(float start, float finish, Vector3f ray) {
		boolean tf = false;
		Vector3f startPoint = getPointOnRay(ray, start);
		Vector3f endPoint = getPointOnRay(ray, finish);
		for(int q = 0; q < gridY; q++) {
			for(int c = 0; c < gridX; c++) {
				if (!isUnderGround(startPoint, q, c) && isUnderGround(endPoint, q, c)) {
					tf = true;
					break;
				} else {
					tf = false;
				}
			}
			if(tf == true){
				break;
			}
		}
		return tf;
		
	}

	private boolean isUnderGround(Vector3f testPoint, int q, int c) {
		setTerrain(terrain2, q, c);
		Terrain terrain = getTerrain(testPoint.getX(), testPoint.getZ());
		float height = 0;
		if (terrain != null) {
			height = terrain.getHeightOfTerrain(testPoint.getX(), testPoint.getZ());
		}
		if (testPoint.y < height) {
			return true;
		} else {
			return false;
		}
	}

	private Terrain getTerrain(float worldX, float worldZ) {
			return terrain;
		}
	}
