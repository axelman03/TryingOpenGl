package entities.collisionDetection;

import org.lwjgl.util.vector.Vector3f;

public class RawHitBoxMesh {
    private HitBoxMeshVAO vao;
    private HitBoxMeshVAO transformedVao;
    private int vertexCount;

    private Vector3f position;
    private Vector3f rotation;

    public RawHitBoxMesh(HitBoxMeshVAO vao, int vertexCount){
        this.vao = vao;
        this.vertexCount = vertexCount;
    }

    public HitBoxMeshVAO getVao() {
        return vao;
    }

    public HitBoxMeshVAO getTransformedVao() {
        return transformedVao;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }
}
