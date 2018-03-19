package entities.collisionDetection;

import org.lwjgl.util.vector.Vector3f;

public class HitBoxSquare extends HitBox{
    public Vector3f[] corners = new Vector3f[8];

    public float xMin;
    public float xMax;

    public float yMin;
    public float yMax;

    public float zMin;
    public float zMax;

    public Vector3f cornerMax;
    public Vector3f cornerMin;

    public HitBoxSquare(float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
        cornerMin = new Vector3f(xMin, yMin, zMin);
        cornerMax = new Vector3f(xMax, yMax, zMax);
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.zMin = zMin;
        this.zMax = zMax;
        generateCorners(xMin, xMax, yMin, yMax, zMin, zMax);
        HitBoxManager.addHitBox(this);
    }

    private void generateCorners(float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {

        Vector3f c1 = new Vector3f(xMin, yMin, zMin);
        Vector3f c2 = new Vector3f(xMin, yMax, zMin);

        Vector3f c3 = new Vector3f(xMax, yMin, zMin);
        Vector3f c4 = new Vector3f(xMax, yMax, zMin);

        Vector3f c5 = new Vector3f(xMin, yMin, zMax);
        Vector3f c6 = new Vector3f(xMin, yMax, zMax);

        Vector3f c7 = new Vector3f(xMax, yMin, zMax);
        Vector3f c8 = new Vector3f(xMax, yMax, zMax);

        corners[0] = c1;
        corners[1] = c2;
        corners[2] = c3;
        corners[3] = c4;
        corners[4] = c5;
        corners[5] = c6;
        corners[6] = c7;
        corners[7] = c8;
    }

    public float getxMin() {
        return xMin * getScale();
    }

    public float getxMax() {
        return xMax * getScale();
    }

    public float getyMin() {
        return yMin * getScale();
    }

    public float getyMax() {
        return yMax * getScale();
    }

    public float getzMin() {
        return zMin * getScale();
    }

    public float getzMax() {
        return zMax * getScale();
    }
}
