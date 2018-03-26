package entities.collisionDetection;

import org.lwjgl.util.vector.Vector3f;

public class HitBoxSquare extends HitBox{
    //public Vector3f[] corners = new Vector3f[8];

    private float xMin;
    private float xMax;

    private float yMin;
    private float yMax;

    private float zMin;
    private float zMax;

    private Vector3f pointMin;  //c1
    private Vector3f pointMax;  //c8
    private Vector3f rotation;
    private Vector3f c2;

    private Vector3f c3;
    private Vector3f c4;

    private Vector3f c5;
    private Vector3f c6;

    private Vector3f c7;



    public HitBoxSquare(float xMin, float xMax, float yMin, float yMax, float zMin, float zMax, float scale, Vector3f rotation) {
        pointMin = new Vector3f(xMin, yMin, zMin);
        pointMax = new Vector3f(xMax, yMax, zMax);
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.zMin = zMin;
        this.zMax = zMax;
        this.rotation = rotation;
        setScale(scale);
        HitBoxManager.addHitBox(this);
    }

    private void generateCorners(float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
        pointMin = new Vector3f(xMin, yMin, zMin);
        c2 = new Vector3f(xMin, yMax, zMin);

        c3 = new Vector3f(xMax, yMin, zMin);
        c4 = new Vector3f(xMax, yMax, zMin);

        c5 = new Vector3f(xMin, yMin, zMax);
        c6 = new Vector3f(xMin, yMax, zMax);

        c7 = new Vector3f(xMax, yMin, zMax);
        pointMax = new Vector3f(xMax, yMax, zMax);



        /*
        corners[0] = c1;
        corners[1] = c2;
        corners[2] = c3;
        corners[3] = c4;
        corners[4] = c5;
        corners[5] = c6;
        corners[6] = c7;
        corners[7] = c8;
        */
    }

    public void setScale(float scale) {
        super.setScale(scale);
        xMin = xMin * scale;
        xMax = xMax * scale;
        yMin = yMin * scale;
        yMax = yMax * scale;
        zMin = zMin * scale;
        zMax = zMax * scale;

        generateCorners(xMin, xMax, yMin, yMax, zMin, zMax);
    }

    public void setPosition(Vector3f position){
        super.setPosition(position);
        xMin = xMin + position.x;
        xMax = xMax + position.x;
        yMin = yMin + position.y;
        yMax = yMax + position.y;
        zMin = zMin + position.z;
        zMax = zMax + position.z;

        generateCorners(xMin, xMax, yMin, yMax, zMin, zMax);
    }

    public void setRotation(Vector3f rotation, Vector3f position){
        super.setRotation(rotation);
        if(this.rotation.x == rotation.x && this.rotation.z == rotation.z && this.rotation.y == rotation.y){
                    generateCorners(xMin, xMax, yMin, yMax, zMin, zMax);
        }
        else{
            pointMin = rotate(rotation, position, pointMin);
            pointMax = rotate(rotation,position, pointMax);

            xMin = pointMin.x;
            yMin = pointMin.y;
            zMin = pointMin.z;

            xMax = pointMax.x;
            yMax = pointMax.y;
            zMax = pointMax.z;

            System.out.println();
            //System.out.println(xMax + " " + yMax + " " + zMax);
            //System.out.println(getXMin() + " " + getYMin() + " " + getZMin());

            System.out.println();
            generateCorners(xMin, xMax, yMin, yMax, zMin, zMax);
            this.rotation = rotation;
        }

    }


    public Vector3f rotate(Vector3f rotation, Vector3f position, Vector3f point) {
        //This Math is not working it seems, it just keeps adding
        //https://stackoverflow.com/questions/6721544/circular-rotation-around-an-arbitrary-axis
        float rotXAroundX;
        float rotYAroundX;
        float rotZAroundX;

        float rotXAroundY;
        float rotYAroundY;
        float rotZAroundY;

        float rotXAroundZ;
        float rotYAroundZ;
        float rotZAroundZ;

        float originX = point.x - position.x;
        float originY = point.y - position.y;
        float originZ = point.z - position.z;
        //Rotate around Z axis (not X like stated)
        rotXAroundZ = (float) (originX * Math.cos(Math.toRadians(rotation.z)) - (originY * Math.sin(Math.toRadians(rotation.z))));
        rotYAroundZ = (float)((originX *  Math.sin(Math.toRadians(rotation.z))) + (originY * Math.cos(Math.toRadians(rotation.z))));
        rotZAroundZ = originZ;
        //rotate around Y axis
        rotZAroundY = (float)((rotZAroundZ* Math.cos(Math.toRadians(rotation.y))) - (rotXAroundZ * Math.sin(Math.toRadians(rotation.y))));
        rotXAroundY = (float)((rotZAroundZ * Math.sin(Math.toRadians(rotation.y))) + (rotXAroundZ * Math.cos(Math.toRadians(rotation.y))));
        rotYAroundY = rotYAroundZ;
        //rotate around X axis
        rotYAroundX = (float)((rotYAroundY * Math.cos(Math.toRadians(rotation.x))) - (rotZAroundY * Math.sin(Math.toRadians(rotation.x))));
        rotZAroundX = (float)((rotYAroundY * Math.sin(Math.toRadians(rotation.x))) + (rotZAroundY * Math.cos(Math.toRadians(rotation.x))));
        rotXAroundX = rotXAroundY;
        System.out.println(rotXAroundX + " " +  rotYAroundX + " " + rotZAroundX);
        //System.out.println(point.x + rotXAroundX + " " + point.y + rotYAroundX + " " + point.z + rotZAroundX);
        return new Vector3f(point.x + rotXAroundX, point.y + rotYAroundX, point.z + rotZAroundX);
    }

    public float getXMin() {
        return xMin;
    }

    public float getXMax() {
        return xMax;
    }

    public float getYMin() {
        return yMin;
    }

    public float getYMax() {
        return yMax;
    }

    public float getZMin() {
        return zMin;
    }

    public float getZMax() {
        return zMax;
    }
}
