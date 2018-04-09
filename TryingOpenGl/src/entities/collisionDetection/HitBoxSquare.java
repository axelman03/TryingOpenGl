package entities.collisionDetection;

import org.lwjgl.util.vector.Vector3f;

public class HitBoxSquare extends HitBox{
    //public Vector3f[] corners = new Vector3f[8];

    //-Min/Max is actual square coordinate
    //-Min/MaxNoRot is coordinate without rotation, so that when object is rotated the math is on original so that its not rotating with new cube and adding
    //-Min/MaxOrig is coordinate without rotation or position, used when teleporting the object to set its position with the coordinate offsets
    private float xMin, xMinNoRot, xMinOrig;
    private float xMax, xMaxNoRot, xMaxOrig;

    private float yMin, yMinNoRot, yMinOrig;
    private float yMax, yMaxNoRot, yMaxOrig;

    private float zMin, zMinNoRot, zMinOrig;
    private float zMax, zMaxNoRot, zMaxOrig;

    private Vector3f pointMin;
    private Vector3f pointMax;
    private Vector3f rotation;


    public HitBoxSquare(float xMin, float xMax, float yMin, float yMax, float zMin, float zMax, float scale, Vector3f rotation) {
        pointMin = new Vector3f(xMin, yMin, zMin);
        pointMax = new Vector3f(xMax, yMax, zMax);
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.zMin = zMin;
        this.zMax = zMax;


        xMinNoRot = xMin;
        xMaxNoRot = xMax;
        yMinNoRot = yMin;
        yMaxNoRot = yMax;
        zMinNoRot = zMin;
        zMaxNoRot = zMax;

        xMinOrig = xMin;
        xMaxOrig = xMax;
        yMinOrig = yMin;
        yMaxOrig = yMax;
        zMinOrig = zMin;
        zMaxOrig = zMax;

        this.rotation = rotation;
        setScale(scale);
        HitBoxManager.addHitBox(this);
    }

    public void setScale(float scale) {
        super.setScale(scale);
        xMin = xMin * scale;
        xMax = xMax * scale;
        yMin = yMin * scale;
        yMax = yMax * scale;
        zMin = zMin * scale;
        zMax = zMax * scale;

        xMinNoRot = xMinNoRot * scale;
        xMaxNoRot = xMaxNoRot * scale;
        yMinNoRot = yMinNoRot * scale;
        yMaxNoRot = yMaxNoRot * scale;
        zMinNoRot = zMinNoRot * scale;
        zMaxNoRot = zMaxNoRot * scale;

        xMinOrig = xMinOrig * scale;
        xMaxOrig = xMaxOrig * scale;
        yMinOrig = yMinOrig * scale;
        yMaxOrig = yMaxOrig * scale;
        zMinOrig = zMinOrig * scale;
        zMaxOrig = zMaxOrig * scale;

    }

    public void increasePosition(Vector3f position){
        super.increasePosition(position);
        xMin = xMin + position.x;
        xMax = xMax + position.x;
        yMin = yMin + position.y;
        yMax = yMax + position.y;
        zMin = zMin + position.z;
        zMax = zMax + position.z;

        xMinNoRot = xMinNoRot + position.x;
        xMaxNoRot = xMaxNoRot + position.x;
        yMinNoRot = yMinNoRot + position.y;
        yMaxNoRot = yMaxNoRot + position.y;
        zMinNoRot = zMinNoRot + position.z;
        zMaxNoRot = zMaxNoRot + position.z;

    }

    public void setPosition(float position, char axis){
        switch (axis){
            case 'x':
                xMin = xMinOrig + position;
                xMax = xMaxOrig + position;
                xMinNoRot = xMinOrig + position;
                xMaxNoRot = xMaxOrig + position;
                this.position.x = position;
                break;
            case 'y':
                yMin = yMinOrig + position;
                yMax = yMaxOrig + position;
                yMinNoRot = yMinOrig + position;
                yMaxNoRot = yMaxOrig + position;
                this.position.y = position;
                break;
            case 'z':
                zMin = zMinOrig + position;
                zMax = zMaxOrig + position;
                zMinNoRot = zMinOrig + position;
                zMaxNoRot = zMaxOrig + position;
                this.position.z = position;
                break;
        }
    }

    public void setPosition(Vector3f position){

        xMin = xMinOrig + position.x;
        xMax = xMaxOrig + position.x;
        yMin = yMinOrig + position.y;
        yMax = yMaxOrig + position.y;
        zMin = zMinOrig + position.z;
        zMax = zMaxOrig + position.z;

        xMinNoRot = xMinOrig + position.x;
        xMaxNoRot = xMaxOrig + position.x;
        yMinNoRot = yMinOrig + position.y;
        yMaxNoRot = yMaxOrig + position.y;
        zMinNoRot = zMinOrig + position.z;
        zMaxNoRot = zMaxOrig + position.z;
    }

    public void setRotation(Vector3f rotation, Vector3f position){
        pointMin = new Vector3f(xMinNoRot, yMinNoRot, zMinNoRot);
        pointMax = new Vector3f(xMaxNoRot, yMaxNoRot, zMaxNoRot);
        super.setRotation(rotation);
        if(this.rotation.x == rotation.x && this.rotation.z == rotation.z && this.rotation.y == rotation.y){

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
            this.rotation = rotation;
        }

    }


    private Vector3f rotate(Vector3f rotation, Vector3f position, Vector3f point) {
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
        //Rotate around Z axis
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
        //System.out.println(rotXAroundX + " " +  rotYAroundX + " " + rotZAroundX);

        if(rotation.x == 0 && rotation.z == 0){
            return new Vector3f(point.x + rotXAroundX, point.y, point.z + rotZAroundX);
        }
        else if(rotation.x == 0 && rotation.y == 0){
            return new Vector3f(point.x + rotXAroundX, point.y + rotYAroundX, point.z);
        }
        else if(rotation.y == 0 && rotation.z == 0){
            return new Vector3f(point.x, point.y + rotYAroundX, point.z + rotZAroundX);
        }
        else{
            return new Vector3f(point.x + rotXAroundX, point.y + rotYAroundX, point.z + rotZAroundX);
        }
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
