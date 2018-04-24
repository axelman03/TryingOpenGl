package entities.collisionDetection;

import org.lwjgl.util.vector.Matrix;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import toolBox.Maths;

public class RawHitBoxMesh {
    private HitBoxMeshVAO vao;
    private HitBoxMeshVAO transformedVao;
    private HitBoxMeshVAO transformedVaoNoRot;
    private int vertexCount;

    private Vector3f position;
    private Vector3f rotation;
    private float scale;

    public RawHitBoxMesh(HitBoxMeshVAO vao, int vertexCount, Vector3f position, Vector3f rotation, float scale){
        this.vao = vao;
        this.vertexCount = vertexCount;

        float[] emptyTransform = new float[vao.getVertexPositionsSize()];
        float[] emptyTransformNoRot = new float[vao.getVertexPositionsSize()];
        transformedVao = new HitBoxMeshVAO(emptyTransform);
        transformedVaoNoRot = new HitBoxMeshVAO(emptyTransformNoRot);
        createVaos(vao);
        setScale(scale);
        setPosition(position);
        setRotation(rotation);
        /*
        for(int x = 0; x < vao.getVertexPositionsSize()-2; x = x + 3){
            System.out.println(transformedVao.getVertexPositions(x) + " " + transformedVao.getVertexPositions(x + 1) + " " + transformedVao.getVertexPositions(x + 2));
        }
        */
    }

    private void createVaos(HitBoxMeshVAO vao){
        for(int x = 0; x < vao.getVertexPositionsSize(); x++){
            transformedVao.setVertexPositions(x,vao.getVertexPositions(x));
            transformedVaoNoRot.setVertexPositions(x,vao.getVertexPositions(x));
        }
    }

    public HitBoxMeshVAO getVao() {
        return vao;
    }

    public HitBoxMeshVAO getTransformedVao() {
        return transformedVao;
    }


    private void transformVaoSetScale(float scale) {
        for(int c = 0; c < vao.getVertexPositionsSize(); c++){
            vao.setVertexPositions(c, vao.getVertexPositions(c) * scale);
            transformedVaoNoRot.setVertexPositions(c, transformedVaoNoRot.getVertexPositions(c) * scale);
            transformedVao.setVertexPositions(c, transformedVao.getVertexPositions(c) * scale);
        }


    }

    private void transformVaoIncreasePosition(Vector3f position) {
        for(int x = 0; x < vao.getVertexPositionsSize()-2; x = x + 3){
            transformedVao.setVertexPositions(x,transformedVao.getVertexPositions(x) + position.x);
            transformedVaoNoRot.setVertexPositions(x,transformedVaoNoRot.getVertexPositions(x) + position.x);
        }
        for(int y = 1; y < vao.getVertexPositionsSize()-1; y = y + 3){
            transformedVao.setVertexPositions(y,transformedVao.getVertexPositions(y) + position.y);
            transformedVaoNoRot.setVertexPositions(y,transformedVaoNoRot.getVertexPositions(y) + position.y);
        }
        for(int z = 2; z < vao.getVertexPositionsSize(); z = z + 3){
            transformedVao.setVertexPositions(z,transformedVao.getVertexPositions(z) + position.z);
            transformedVaoNoRot.setVertexPositions(z,transformedVaoNoRot.getVertexPositions(z) + position.z);
        }
        transformedVao.setPosition(position);
        transformedVaoNoRot.setPosition(position);
    }

    private void transformVaoSetPosition(Vector3f position) {
        for(int x = 0; x < vao.getVertexPositionsSize()-2; x = x + 3){
            transformedVao.setVertexPositions(x,vao.getVertexPositions(x) + position.x);
            transformedVaoNoRot.setVertexPositions(x,vao.getVertexPositions(x) + position.x);
        }
        for(int y = 1; y < vao.getVertexPositionsSize()-1; y = y + 3){
            transformedVao.setVertexPositions(y,vao.getVertexPositions(y) + position.y);
            transformedVaoNoRot.setVertexPositions(y,vao.getVertexPositions(y) + position.y);
        }
        for(int z = 2; z < vao.getVertexPositionsSize(); z = z + 3){
            transformedVao.setVertexPositions(z,vao.getVertexPositions(z) + position.z);
            transformedVaoNoRot.setVertexPositions(z,vao.getVertexPositions(z) + position.z);
        }
        transformedVao.setPosition(position);
        transformedVaoNoRot.setPosition(position);
    }

    private void transformVaoSetPosition(float position, char axis) {
        switch (axis){
            case 'x':
                for(int x = 0; x < vao.getVertexPositionsSize()-2; x = x + 3){
                    transformedVao.setVertexPositions(x,vao.getVertexPositions(x) + position);
                    transformedVaoNoRot.setVertexPositions(x,vao.getVertexPositions(x) + position);
                    this.position.x = position;
                }
                break;
            case 'y':
                for(int y = 1; y < vao.getVertexPositionsSize()-1; y = y + 3){
                    transformedVao.setVertexPositions(y,vao.getVertexPositions(y) + position);
                    transformedVaoNoRot.setVertexPositions(y,vao.getVertexPositions(y) + position);
                    this.position.y = position;
                }
                break;
            case 'z':
                for(int z = 2; z < vao.getVertexPositionsSize(); z = z + 3){
                    transformedVao.setVertexPositions(z,vao.getVertexPositions(z) + position);
                    transformedVaoNoRot.setVertexPositions(z,vao.getVertexPositions(z) + position);
                    this.position.z = position;
                }
                break;
        }
        transformedVao.setPosition(this.position);
        transformedVaoNoRot.setPosition(this.position);

    }


    private void transformVaoSetRotation(Vector3f rotation, Vector3f position){
        if(this.rotation.x == rotation.x && this.rotation.z == rotation.z && this.rotation.y == rotation.y){

        }
        else{
            for(int c = 0; c < transformedVaoNoRot.getVertexPositionsSize() - 2; c = c + 3) {
                rotate(rotation, position, new Vector3f(transformedVaoNoRot.getVertexPositions(c), transformedVaoNoRot.getVertexPositions(c + 1), transformedVaoNoRot.getVertexPositions(c + 2)), c);
                this.rotation = rotation;
            }
        }

    }

    private void rotate(Vector3f rotation, Vector3f position, Vector3f point, int index) {
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
            transformedVao.setVertexPositions(index, point.x + rotXAroundX);
            transformedVao.setVertexPositions(index + 1,  point.y);
            transformedVao.setVertexPositions(index + 2,  point.z + rotZAroundX);
        }
        else if(rotation.x == 0 && rotation.y == 0){
            transformedVao.setVertexPositions(index, point.x + rotXAroundX);
            transformedVao.setVertexPositions(index + 1,  point.y + rotYAroundX);
            transformedVao.setVertexPositions(index + 2,  point.z);
        }
        else if(rotation.y == 0 && rotation.z == 0){
            transformedVao.setVertexPositions(index, point.x);
            transformedVao.setVertexPositions(index + 1,  point.y + rotYAroundX);
            transformedVao.setVertexPositions(index + 2,  point.z + rotZAroundX);
        }
        else{
            transformedVao.setVertexPositions(index, point.x + rotXAroundX);
            transformedVao.setVertexPositions(index + 1,  point.y + rotYAroundX);
            transformedVao.setVertexPositions(index + 2,  point.z + rotZAroundX);
        }
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void increasePosition(Vector3f position) {
        this.position = position;
        transformVaoIncreasePosition(position);
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        transformVaoSetPosition(position);
    }

    public void setPosition(float position, char axis) {
        transformVaoSetPosition(position, axis);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
        transformVaoSetRotation(rotation, position);
    }

    public void setScale(float scale) {
        this.scale = scale;
        transformVaoSetScale(scale);
    }
}
