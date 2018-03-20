package entities.collisionDetection;

public class HitBoxCircle extends HitBox{

    private float distance;

    private float yMax;
    private float yMin;

    public HitBoxCircle(float distance, float yMax, float yMin) {
        this.distance = distance;
        this.yMax = yMax;
        this.yMin = yMin;
        HitBoxManager.addHitBox(this);
    }

    public float getDistance() {
        return distance;
    }

    public float getyMax() {
        return yMax;
    }

    public float getyMin() {
        return yMin;
    }

}

