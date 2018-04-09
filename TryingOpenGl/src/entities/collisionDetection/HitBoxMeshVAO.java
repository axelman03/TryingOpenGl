package entities.collisionDetection;

public class HitBoxMeshVAO {
    private float[] vertexPositions;
    private float[] vertexNormals;
    private int[] vertexIndices;
    public HitBoxMeshVAO(float[] positions, float[] normals, int[] indices){
        vertexPositions = positions;
        vertexNormals = normals;
        vertexIndices = indices;
    }

    public float[] getVertexPositions() {
        return vertexPositions;
    }

    public void setVertexPositions(float[] vertexPositions) {
        this.vertexPositions = vertexPositions;
    }

    public float getVertexPositions(int index) {
        return vertexPositions[index];
    }

    public void setVertexPositions(int index, float vertexPosition) {
        vertexPositions[index] = vertexPosition;
    }

    public int getVertexPositionsSize() {
        return vertexPositions.length;
    }



    public float[] getVertexNormals() {
        return vertexNormals;
    }

    public void setVertexNormals(float[] vertexNormals) {
        this.vertexNormals = vertexNormals;
    }

    public float getVertexNormals(int index) {
        return vertexNormals[index];
    }

    public void setVertexNormals(int index, float vertexNormal) {
        vertexNormals[index] = vertexNormal;
    }

    public int getVertexNormalsSize() {
        return vertexNormals.length;
    }



    public int[] getVertexIndices() {
        return vertexIndices;
    }

    public void setVertexIndices(int[] vertexIndices) {
        this.vertexIndices = vertexIndices;
    }

    public int getVertexIndices(int index) {
        return vertexIndices[index];
    }

    public void setVertexIndices(int index, int vertexIndice) {
        vertexIndices[index] = vertexIndice;
    }

    public int getVertexIndicesSize() {
        return vertexIndices.length;
    }
}
