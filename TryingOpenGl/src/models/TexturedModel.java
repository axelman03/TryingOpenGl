package models;

import org.lwjgl.util.vector.Vector3f;

import textures.ModelTexture;

public class TexturedModel {
	
	
	private RawModel rawModel;
	private ModelTexture texture;
	private Vector3f maxVertices;
	private Vector3f minVertices;
	
	public TexturedModel(RawModel model, ModelTexture texture, Vector3f maxVertices, Vector3f minVertices){
		this.rawModel = model;
		this.texture = texture;
		this.maxVertices = maxVertices;
		this.minVertices = minVertices;
		
		
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public ModelTexture getTexture() {
		return texture;

	}

	public void setTexture(ModelTexture texture) {
		this.texture = texture;
	}

	public Vector3f getMaxVertices() {
		return maxVertices;
	}

	public Vector3f getMinVertices() {
		return minVertices;
	}
}
