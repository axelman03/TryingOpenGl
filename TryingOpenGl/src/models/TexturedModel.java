package models;

import org.lwjgl.util.vector.Vector3f;

import textures.ModelTexture;

public class TexturedModel {
	
	
	private RawModel rawModel;
	private ModelTexture texture;
	
	public TexturedModel(RawModel model, ModelTexture texture){
		this.rawModel = model;
		this.texture = texture;
		
		
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
}
