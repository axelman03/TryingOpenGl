package entities.collisionDetection;

import entities.Entity;
import org.lwjgl.util.vector.Vector3f;
import org.lwjglx.debug.org.eclipse.jetty.servlet.Source;
import toolBox.Maths;

import java.util.ArrayList;

public class HitBoxMath {

    private static int tempEntityIndex;

    private static ArrayList<Vector3f> simplex = new ArrayList<Vector3f>();
    private static Vector3f vecDirection = null;
/*
    public static boolean isColliding(HitBox box1, HitBox box2) {
        boolean isBox = false;
        if (box1 instanceof HitBoxSquare && box2 instanceof HitBoxSquare) {
            isBox = isCollidingSquare((HitBoxSquare) box1, (HitBoxSquare) box2);
        }
        if ((box1 instanceof HitBoxSquare && box2 instanceof HitBoxCircle)) {
            isBox = isCollidingCSHybrid(box1, box2);
        }
        if ((box2 instanceof HitBoxSquare && box1 instanceof HitBoxCircle)) {
            isBox = isCollidingCSHybrid(box1, box2);
        }
        if (box1 instanceof HitBoxCircle && box2 instanceof HitBoxCircle) {
            isBox = isCollidingCircle((HitBoxCircle) box1, (HitBoxCircle) box2);
        }
        return isBox;
    }
*/
    //Collider is object that is hittting other objects, so player and such
    public static boolean isBroadPlaneColliding(Entity colliderEntity, ArrayList<Entity> entities){
        boolean isHit = false;
        HitBoxSquare hitBox = null;
        for(int x = 0; x < entities.size(); x++){
            if(colliderEntity != entities.get(x)){
                if(entities.get(x).getBox() != null) {
                    HitBoxSquare collider = colliderEntity.getBox();
                    hitBox = entities.get(x).getBox();
                    if (collider.getXMax() >= hitBox.getXMin() && collider.getXMin() <= hitBox.getXMax()) {
                        if (collider.getZMax() >= hitBox.getZMin() && collider.getZMin() <= hitBox.getZMax()) {
                            if (collider.getYMax() >= hitBox.getYMin() && collider.getYMin() <= hitBox.getYMax()) {
                                setCollidedEntityIndex(x);
                                isHit = true;
                            }
                        }
                    }
                }
            }
        }
        return isHit;
    }
    private static void setCollidedEntityIndex(int index){
        tempEntityIndex = index;
    }
    public static int getCollidedEntityIndex(){
        return tempEntityIndex;
    }




    public static boolean narrowPlaneCollision(HitBoxMeshVAO collider, HitBoxMeshVAO collided){
        vecDirection = new Vector3f(collided.getPosition().x - collider.getPosition().x, collided.getPosition().y - collider.getPosition().y, collided.getPosition().z - collider.getPosition().z);
        Vector3f p1 = getSupport(collider, collided, vecDirection);
        simplex.add(p1);
        vecDirection = new Vector3f(-vecDirection.x, -vecDirection.y, -vecDirection.z);
        while(true){
            Vector3f p2 = getSupport(collider, collided, vecDirection);
            simplex.add(p2);
            //make sure that the last point we added actually passed the origin
            if (Vector3f.dot(simplex.get(simplex.size() - 1), vecDirection) <= 0){ //This works on a 2D plane, but what about a 3D plane? Do I multiply that with the Dot of another Vector to get it in a triangle or what?
                System.out.println(Vector3f.dot(simplex.get(simplex.size() - 1), vecDirection));
                //System.out.println(simplex);
                //System.out.println(vecDirection);
                //if the point added last was not past the origin in the direction of vecDirection then the Minkowski Sum cannot possibly contain the origin since the last point added is on the edge of the Minkowski Difference
                System.out.println(simplex);
                System.out.println(minkowskiSum(collider, collided));
                System.out.println();
                vecDirection = null;
                simplex.clear();
                return false;
            }
            else{
                //otherwise we need to determine if the origin is in the current simplex
                if(containsOrigin(simplex)){
                    //If it does, there is a collision
                    vecDirection = null;
                    simplex.clear();
                    return true;
                }

            }
        }
    }

    private static boolean containsOrigin(ArrayList<Vector3f> simplex) {
        // get the last point added to the simplex
        Vector3f a = simplex.get(simplex.size() - 1);
        // compute AO (same thing as -A)
        Vector3f ao = new Vector3f(-a.x, -a.y, -a.z);
        if (simplex.size() == 4) {
            // then its the tetrahedron case
            // get b and c
            Vector3f b = simplex.get(simplex.size() - 2);
            Vector3f c = simplex.get(simplex.size() - 3);
            Vector3f d = simplex.get(simplex.size() - 4);
            // compute the edges
            Vector3f ab = getEdges(a, b);
            Vector3f ac = getEdges(a, c);
            //Vector3f ad = getEdges(a, d);
            Vector3f bc = getEdges(b, c);
            Vector3f bd = getEdges(b, d);
            Vector3f cd = getEdges(c, d);
            // compute the faces
            //Vector3f abPerp = getDirection(ac, ab, ab);
            //Vector3f acPerp = getDirection(ab, ac, ac);
            Vector3f abcFacePerp = getFaceDirection(ab, bc);
            Vector3f abdFacePerp = getFaceDirection(ab, bd);
            Vector3f acdFacePerp = getFaceDirection(ac, cd);
            Vector3f bcdFacePerp = getFaceDirection(bc, cd);
            // Origin Checking
            if (Vector3f.dot(abcFacePerp, ao) > 0) {
                // remove point d
                simplex.remove(simplex.size() - 4);
                // set the new direction to abcFacePerp
                vecDirection = abcFacePerp;
                //System.out.println(vecDirection);
            }
            else if (Vector3f.dot(abdFacePerp, ao) > 0) {
                // remove point c
                simplex.remove(simplex.size() - 3);
                // set the new direction to abdFacePerp
                vecDirection = abdFacePerp;
                //System.out.println(vecDirection);
            }
            else if (Vector3f.dot(acdFacePerp, ao) > 0) {
                // remove point b
                simplex.remove(simplex.size() - 2);
                // set the new direction to acdFacePerp
                vecDirection = acdFacePerp;
                //System.out.println(vecDirection);
            }
            else if (Vector3f.dot(bcdFacePerp, ao) > 0) {
                // remove point a
                simplex.remove(simplex.size() - 1);
                // set the new direction to bcdFacePerp
                vecDirection = bcdFacePerp;
                //System.out.println(vecDirection);
            }
            else{
                // otherwise we know its in R5 so we can return true
                return true;
            }
        }
        else if (simplex.size() == 3) {
            // then its the triangle case
            // get b and c
            Vector3f b = simplex.get(simplex.size() - 2);
            Vector3f c = simplex.get(simplex.size() - 3);
            // compute the edges
            Vector3f ab = getEdges(a, b);
            //Vector3f ac = getEdges(a, c);
            Vector3f bc = getEdges(b, c);
            // compute the normals
            //Vector3f abPerp = getDirection(ac, ab, ab);
            //Vector3f acPerp = getDirection(ab, ac, ac);
            //Vector3f bcPerp = getDirection(ac, bc, bc);
            Vector3f facePerp = getFaceDirection(ab, bc);

            vecDirection = facePerp;
            //System.out.println(vecDirection);
        }
        else {
            // then its the line segment case
            Vector3f b = simplex.get(simplex.size() - 2);
            // compute AB
            Vector3f ab = getEdges(a, b);
            // get the perp to AB in the direction of the origin
            Vector3f abPerp = getDirection(ab, ao, ab);
            // set the direction to abPerp
            vecDirection = abPerp;
        }
        return false;
    }

    private static Vector3f getFaceDirection(Vector3f a, Vector3f b){
        Vector3f normal = new Vector3f(0,0,0);
        Vector3f.cross(a, b, normal);
        return normal;

    }

    //gets the direction of the new vector for each iteration, and for Origin Testing, Its the vector perpendicular to the line
    /*
    (A x B) x C = B(C.dot(A)) – A(C.dot(B))
    The Perpendicular math to get the different sides of the triangle
    */
    private static Vector3f getDirection(Vector3f a, Vector3f b, Vector3f c){
        //the vectors subtracting a from b

        float aDot = Vector3f.dot(c, a);
        float bDot = Vector3f.dot(c, b);
        Vector3f vecDirectionA = new Vector3f(b.x * aDot, b.y * aDot, b.z * aDot);
        Vector3f  vecDirectionB = new Vector3f(a.x * bDot, a.y * bDot, a.z * bDot);
        float x =  (vecDirectionA.x - vecDirectionB.x);
        float y =  (vecDirectionA.y - vecDirectionB.y);
        float z =  (vecDirectionA.z - vecDirectionB.z);
        Vector3f vecDirection = new Vector3f(x/Math.abs(x), y/Math.abs(x), z/Math.abs(x));
        return vecDirection;
    }

    private static Vector3f getEdges(Vector3f a, Vector3f b){
        Vector3f ab = new Vector3f(b.x - a.x, b.y - a.y, b.z - a.z);

        return ab;
    }


    //Returns the support function of the shape of the Minkowski sum, so a vertex on the edge of the shape
    private static Vector3f getSupport(HitBoxMeshVAO collider, HitBoxMeshVAO collided, Vector3f vectorDirection){
        Vector3f p1 = getFarthestPoint(collider, vectorDirection);
        Vector3f p2 = getFarthestPoint(collided, new Vector3f(-vectorDirection.x, -vectorDirection.y, -vectorDirection.z));
        Vector3f p3 = new Vector3f(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
        return p3;
    }

    //Returns the farthest vertex on the object given from the vector direction given
    private static Vector3f getFarthestPoint(HitBoxMeshVAO collider, Vector3f vectorDirection){
        Vector3f largest;
        float largestTemp = 0;
        float testTemp;
        int indexTemp = 0;
        for(int x = 0; x < collider.getVertexPositionsSize() - 2; x = x + 3){
            testTemp = /*Math.abs(*/Vector3f.dot(vectorDirection, new Vector3f(collider.getVertexPositions(x), collider.getVertexPositions(x + 1), collider.getVertexPositions(x + 2)))/*)*/;
            //System.out.println(testTemp);
            if (testTemp > largestTemp){
                largestTemp = testTemp;
                indexTemp = x;
            }
        }
        largest = new Vector3f(collider.getVertexPositions(indexTemp), collider.getVertexPositions(indexTemp + 1), collider.getVertexPositions(indexTemp + 2));
        return largest;
    }


    private static ArrayList<Vector3f> minkowskiSum(HitBoxMeshVAO collider, HitBoxMeshVAO collided){
        ArrayList<Vector3f> sum = new ArrayList<>();
        for(int x = 0; x < collider.getVertexPositions().length -2; x = x + 3){
            for(int y = 0; y < collided.getVertexPositions().length -2; y = y + 3){
                sum.add(new Vector3f(collider.getVertexPositions(x) - collided.getVertexPositions(y), collider.getVertexPositions(x + 1) - collided.getVertexPositions(y + 1), collider.getVertexPositions(x + 1) - collided.getVertexPositions(y + 1)));
            }
        }
        return sum;
    }

}
