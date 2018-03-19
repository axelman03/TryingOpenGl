package entities.collisionDetection;

import org.lwjgl.util.vector.Vector3f;

public abstract class HitBox {
    protected Vector3f position = new Vector3f();
    protected Vector3f rotation = new Vector3f();
    protected float scale = 1;

    protected int hitBoxID;

    public void setID(int id) {
        this.hitBoxID = id;
    }

    public int getHitBoxID() {
        return hitBoxID;
    }

    public void setPosition(Vector3f pos) {
        position = pos;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return this.scale;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setRotation(Vector3f rot) {
        rotation = rot;
    }

    public Vector3f getRotation() {
        return this.rotation;
    }
}
