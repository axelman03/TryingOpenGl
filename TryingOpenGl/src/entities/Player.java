package entities;

import java.util.ArrayList;
import java.util.Timer;

import entities.collisionDetection.HitBoxMath;
import entities.collisionDetection.RawHitBoxMesh;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrain.Terrain;

public class Player extends Entity {
	
	public static final float RUN_SPEED = 20;
	public static final float TURN_SPEED = 160;
	public static final float JUMP_POWER = 30;
	
	public float currentSpeed = 0;
	public float currentTurnSpeed = 0;
	//private float upwardsInitialVelocity = 0;
	public float upwardsVelocity = 0;
	//private float gravity = 0;
	//private float jumpStartTime = 0;
	//private float jumpCurrentTime = 0;
	
	private boolean isInAir = false;
	private boolean collision = false;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, Vector3f maxVertices, Vector3f minVertices) {
		super(model, position, rotX, rotY, rotZ, scale, maxVertices, minVertices);
		
	}
	public void move(Terrain terrain, ArrayList<Entity> entities,  ArrayList<Entity> normalMappedEntities){
		checkInputs(entities, normalMappedEntities);


		for(Entity entity : entities){
			if(super.getPosition() == entity.getPosition()){
				collision = true;
			}
		}

		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);


		//jumpCurrentTime = System.currentTimeMillis() / 1000;
		//upwardsVelocity = toolBox.Maths.getVelocity(upwardsInitialVelocity, gravity, DisplayManager.getFrameTimeSeconds());
		//System.out.println(upwardsVelocity);
		upwardsVelocity += toolBox.Maths.GRAVITY2 * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0,upwardsVelocity * DisplayManager.getFrameTimeSeconds(),0);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(super.getPosition().y<=terrainHeight){
			//upwardsInitialVelocity = 0;
			upwardsVelocity = 0;
			//gravity = 0;
			//jumpStartTime = 0; 
			super.getPosition().y = terrainHeight;
			super.getBox().setPosition(terrainHeight, 'y');
			for(RawHitBoxMesh playerMesh : this.getHitBoxMesh()) {
				playerMesh.setPosition(terrainHeight, 'y');
			}
			isInAir = false;
		}


	}
	private void jump(){
		if(!isInAir){	
			this.upwardsVelocity = JUMP_POWER;
			isInAir = true;
		}
		
	}
	
	private void checkInputs(ArrayList<Entity> entities,  ArrayList<Entity> normalMappedEntities){
		if(HitBoxMath.isBroadPlaneColliding(this, entities)) {
			for(RawHitBoxMesh playerMesh : this.getHitBoxMesh()) {
				for(RawHitBoxMesh entityMesh : entities.get(HitBoxMath.getCollidedEntityIndex()).getHitBoxMesh()) {
					if (HitBoxMath.narrowPlaneCollision(playerMesh.getTransformedVao(), entityMesh.getTransformedVao())) {
						System.out.println("Collision");
					}
				}
			}
		}

		if(HitBoxMath.isBroadPlaneColliding(this, normalMappedEntities)) {
			for(RawHitBoxMesh playerMesh : this.getHitBoxMesh()) {
				for(RawHitBoxMesh entityMesh : normalMappedEntities.get(HitBoxMath.getCollidedEntityIndex()).getHitBoxMesh()) {
					if (HitBoxMath.narrowPlaneCollision(playerMesh.getTransformedVao(), entityMesh.getTransformedVao())) {
						System.out.println("Collision2");
					}
				}
			}
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_W)){
			this.currentSpeed = RUN_SPEED;
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_S)){
			this.currentSpeed = -RUN_SPEED;
		}
		else{
			this.currentSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D)){
			this.currentTurnSpeed = -TURN_SPEED;
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_A)){
			this.currentTurnSpeed = TURN_SPEED;
		}
		else{
			this.currentTurnSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			jump();
		}
	}
/*
	public void wallCollision(ArrayList<Entity> entities){
		if(HitBoxMath.isBroadPlaneColliding(this, entities)){
			if(HitBoxMath.narrowPlaneCollision(this.getHitBoxMesh().getTransformedVao(), entities.get(HitBoxMath.getCollidedEntityIndex()).getHitBoxMesh().getTransformedVao())){
				System.out.println("Collision2");
				if (Keyboard.isKeyDown(Keyboard.KEY_W)){
					currentSpeed = 0;
					if (Keyboard.isKeyDown(Keyboard.KEY_S)){
						currentSpeed = -RUN_SPEED;
					}
				}else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
					currentSpeed = 0;
					if (Keyboard.isKeyDown(Keyboard.KEY_W)){
						currentSpeed = RUN_SPEED;
					}
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_D)){
					currentTurnSpeed = 0;
					if (Keyboard.isKeyDown(Keyboard.KEY_A)){
						currentSpeed = TURN_SPEED;
					}
				}else if (Keyboard.isKeyDown(Keyboard.KEY_A)){
					currentTurnSpeed = 0;
					if (Keyboard.isKeyDown(Keyboard.KEY_D)){
						currentSpeed = -TURN_SPEED;
					}
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
					//jump();
				}
			}
		}
	}
	*/
}
