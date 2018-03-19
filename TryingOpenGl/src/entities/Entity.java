package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import entities.collisionDetection.HitBox;
import textures.ModelTexture;

public class Entity {
	
	private TexturedModel model;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	
	private float textureIndex = 0;

	private HitBox box;
	private boolean hasHitBox = false;
	private boolean colides = false;
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super();
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}
	
	public Entity(TexturedModel model,int index, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super();
		this.textureIndex = index;
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}

	public HitBox getBox() {
		return box;
	}

	public void setBox(HitBox box) {
		hasHitBox = true;
		this.box = box;
		box.setPosition(new Vector3f(position.x, position.y, position.z));
		box.setRotation(new Vector3f(rotX, rotY, rotZ));

	}


	
	public float getTextureXOffset(){
		int column = (int) (textureIndex%model.getTexture().getNumberOfRows());
		return (float)column/(float)model.getTexture().getNumberOfRows();
	}
	
	public float getTextureYOffset(){
		int row = (int) (textureIndex/model.getTexture().getNumberOfRows());
		return (float)row/(float)model.getTexture().getNumberOfRows();
	}
	
	public void increasePosition(float dx, float dy, float dz){
		this.position.x+=dx;
		this.position.y+=dy;
		this.position.z+=dz;
		if (hasHitBox) {
			box.setPosition(new Vector3f(position.x, 0, position.y));
		}
	}
	
	public void increaseRotation(float dx, float dy, float dz){
		this.rotX+=dx;
		this.rotY+=dy;
		this.rotZ+=dz;
		if (hasHitBox) {
			box.setRotation(new Vector3f(rotX, rotY, rotZ));
		}
	}
	
	public TexturedModel getModel() {
		return model;
	}
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}
	public float getRotY() {
		return rotY;
	}
	public float getRotZ() {
		return rotZ;
	}
	public float getScale() {
		return scale;
	}
	
	
	
}
