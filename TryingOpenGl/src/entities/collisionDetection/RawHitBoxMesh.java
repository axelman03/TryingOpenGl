package entities.collisionDetection;

public class RawHitBoxMesh {
    private int vaoID;
    private int vertexCount;

    public RawHitBoxMesh(int vaoID, int vertexCount){
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;


    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
