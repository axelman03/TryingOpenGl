package entities.collisionDetection;

import entities.Entity;
import org.lwjgl.util.vector.Vector3f;
import org.lwjglx.debug.org.eclipse.jetty.servlet.Source;
import toolBox.Maths;

import java.util.ArrayList;

public class HitBoxMath {

    private static int tempEntityIndex;

    private static ArrayList<Vector3f> simplexCollision = new ArrayList<Vector3f>();
    private static ArrayList<Vector3f> simplexDistance = new ArrayList<Vector3f>();
    private static ArrayList<Vector3f> simplexPenatration = new ArrayList<Vector3f>();
    private static Vector3f vecDirectionCollision = null;
    private static Vector3f vecDirectionDistance = null;

    private static final float TOLERANCE = 0.00001f;

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
        int iterationCount = 0;
        vecDirectionCollision = new Vector3f(collided.getPosition().x - collider.getPosition().x, collided.getPosition().y - collider.getPosition().y, collided.getPosition().z - collider.getPosition().z);
        Vector3f p1 = getSupport(collider, collided, vecDirectionCollision);
        simplexCollision.add(p1);
        vecDirectionCollision = new Vector3f(-vecDirectionCollision.x, -vecDirectionCollision.y, -vecDirectionCollision.z);
        while(true){
            Vector3f p2 = getSupport(collider, collided, vecDirectionCollision);
            simplexCollision.add(p2);
            //make sure that the last point we added actually passed the origin
            if ((Vector3f.dot(simplexCollision.get(simplexCollision.size() - 1), vecDirectionCollision) <= -10) && simplexCollision.size() > 2 || simplexCollision.get(simplexCollision.size() - 1) == simplexCollision.get(simplexCollision.size() - 2)){
                //System.out.println( Vector3f.dot(vecDirection, new Vector3f(1,1,1)));
/*
                System.out.println(Vector3f.dot(simplexCollision.get(simplexCollision.size() - 1), vecDirectionCollision));
                System.out.println(simplexCollision);
                System.out.println(vecDirectionCollision);
*/
                //if the point added last was not past the origin in the direction of vecDirection then the Minkowski Sum cannot possibly contain the origin since the last point added is on the edge of the Minkowski Difference
                //System.out.println(simplex);
                //minkowskiSum(collider, collided);
                //System.out.println();
                //System.out.println();

                //System.out.println(Vector3f.dot(simplex.get(simplex.size() - 1), vecDirection));
                //System.out.println(Math.sqrt(Math.pow(vecDirection.x, 2) + Math.pow(vecDirection.y, 2) + Math.pow(vecDirection.z, 2)));
                simplexPenatration = new ArrayList<>();
                for(Vector3f points : simplexPenatration){
                    simplexPenatration.add(points);
                }
                vecDirectionCollision = null;
                simplexCollision.clear();
                return false;
            }
            else{
                //otherwise we need to determine if the origin is in the current simplex
                if((containsOrigin(simplexCollision) || (Vector3f.dot(vecDirectionCollision, new Vector3f(1,1,1)) < 2 && Vector3f.dot(vecDirectionCollision, new Vector3f(1,1,1)) > -2)) && simplexCollision.size() == 4  || iterationCount == 100){
                    //If it does, there is a collision
                    /*
                    System.out.println(Vector3f.dot(simplex.get(simplex.size() - 1), vecDirection));
                    System.out.println(simplex);
                    System.out.println(vecDirection);
                    */
                    //System.out.println( Vector3f.dot(vecDirection, new Vector3f(1,1,1)));
                    //System.out.println(simplex);
                    //System.out.println(vecDirection);
                    //System.out.println();
                    vecDirectionCollision = null;
                    simplexCollision.clear();
                    return true;
                }
                iterationCount++;
                //System.out.println(iterationCount);

            }
        }
    }

    private static boolean containsOrigin(ArrayList<Vector3f> simplex) {
        // get the last point added to the simplex
        Vector3f a = simplex.get(simplex.size() - 1);
        // compute AO (same thing as -A)
        Vector3f aO = new Vector3f(-a.x, -a.y, -a.z);
        if (simplex.size() == 4) {
            // then its the tetrahedron case
            // get b and c
            Vector3f b = simplex.get(simplex.size() - 2);
            Vector3f c = simplex.get(simplex.size() - 3);
            Vector3f d = simplex.get(simplex.size() - 4);

            Vector3f bO = new Vector3f(-b.x, -b.y, -b.z);
            Vector3f cO = new Vector3f(-c.x, -c.y, -c.z);
            Vector3f dO = new Vector3f(-d.x, -d.y, -d.z);
            // compute the edges
            Vector3f ab = getEdges(a, b);
            Vector3f ac = getEdges(a, c);
            Vector3f ad = getEdges(a, d);
            Vector3f bc = getEdges(b, c);
            Vector3f bd = getEdges(b, d);
            Vector3f cd = getEdges(c, d);
            // compute the faces
            //Vector3f abPerp = getDirection(ac, ab, ab);
            //Vector3f acPerp = getDirection(ab, ac, ac);
            Vector3f abcFacePerp = getNormalDirection(ab, ac);
            Vector3f abdFacePerp = getNormalDirection(ab, ad);
            Vector3f acdFacePerp = getNormalDirection(ac, ad);
            Vector3f bcdFacePerp = getNormalDirection(bc, bd);

            // Origin Checking
            if (Vector3f.dot(abcFacePerp, dO) > 0) {
                // remove point d
                simplex.remove(simplex.size() - 4);
                // set the new direction to abcFacePerp
                vecDirectionCollision = abcFacePerp;
                //System.out.println(vecDirection);
                //System.out.println("Iterate part 3");
            }
            else if (Vector3f.dot(abdFacePerp, cO) > 0) {
                // remove point c
                simplex.remove(simplex.size() - 3);
                // set the new direction to abdFacePerp
                vecDirectionCollision = abdFacePerp;
                //System.out.println(vecDirection);
                //System.out.println("Iterate part 4");
            }
            else if (Vector3f.dot(acdFacePerp, bO) > 0) {
                // remove point b
                simplex.remove(simplex.size() - 2);
                // set the new direction to acdFacePerp
                vecDirectionCollision = acdFacePerp;
                //System.out.println(vecDirection);
                //System.out.println("Iterate part 5");
            }
            else if (Vector3f.dot(bcdFacePerp, aO) > 0) {
                // remove point a
                simplex.remove(simplex.size() - 1);
                // set the new direction to bcdFacePerp
                vecDirectionCollision = bcdFacePerp;
                //System.out.println(vecDirection);
                //System.out.println("Iterate part 6");
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
            Vector3f ac = getEdges(a, c);
            // compute the normals
            //Vector3f abPerp = getDirection(ac, ab, ab);
            //Vector3f acPerp = getDirection(ab, ac, ac);
            //Vector3f bcPerp = getDirection(ac, bc, bc);
            Vector3f facePerp = getNormalDirection(ab, ac);

            vecDirectionCollision = facePerp;
            //System.out.println(vecDirection);
            //System.out.println("Iterate part 2");
        }
        else {
            // then its the line segment case
            Vector3f b = simplex.get(simplex.size() - 2);
            // compute AB
            Vector3f ab = getEdges(a, b);
            // get the perp to AB in the direction of the origin
            Vector3f abPerp = getDirection(ab, aO, ab);
            // set the direction to abPerp
            vecDirectionCollision = abPerp;
            //System.out.println("Iterate part 1");
        }
        return false;
    }

    public static float getDistance(HitBoxMeshVAO collider, HitBoxMeshVAO collided){
        int iterationCount = 0;
        vecDirectionDistance = new Vector3f(collided.getPosition().x - collider.getPosition().x, collided.getPosition().y - collider.getPosition().y, collided.getPosition().z - collider.getPosition().z);
        Vector3f p1 = getSupport(collider, collided, vecDirectionDistance);
        simplexDistance.add(p1);
        vecDirectionDistance = new Vector3f(-vecDirectionDistance.x, -vecDirectionDistance.y, -vecDirectionDistance.z);
        Vector3f p2 = getSupport(collider, collided, vecDirectionDistance);
        simplexDistance.add(p2);
        Vector3f ab = getEdges(p1, p2);
        Vector3f ao = new Vector3f(-p1.x, -p1.y, -p1.z);
        Vector3f p3 = getDirection(ab, ao, ab);
        simplexDistance.add(p3);
        Vector3f ac = getEdges(p1, p3);
        // obtain the point on the current simplex closest to the origin
        // start the loop
        vecDirectionDistance = getClosestPointFromClosestLine(simplexDistance.get(0), simplexDistance.get(1));  //Could be getClosestPointFromClosestLine, also want to make sure it points toward origin
        //System.out.println();
        //vecDirectionDistance = getFaceDirection(ab, ac);
        while (true) {
            //System.out.println(vecDirectionDistance);
            // obtain a new Minkowski Difference point along the new direction
            Vector3f p4 = getSupport(collider, collided, vecDirectionDistance);
            // the direction we get from the closest point is pointing from the origin to the closest point, we need to reverse it so that it points towards the origin
            vecDirectionDistance = new Vector3f(-vecDirectionDistance.x, -vecDirectionDistance.y, -vecDirectionDistance.z);
            // check if d is the zero vector
            if (vecDirectionDistance == new Vector3f(0,0,0) || p1 == p4 || p2 == p4 || p3 == p4|| iterationCount == 20) {
                // then the origin is on the Minkowski Difference I consider this touching/collision
                System.out.println(simplexDistance);
                System.out.println(vecDirectionDistance);
                vecDirectionDistance = null;
                simplexDistance.clear();
                return 0;
            }

            // is the point we obtained making progress towards the goal (to get the closest points to the origin)
            float dc = Vector3f.dot(p3, vecDirectionDistance);
            // you can use a or b here it doesn't matter since they will be equally distant from the origin
            float da = Vector3f.dot(simplexDistance.get(0), vecDirectionDistance);
            // tolerance is how accurate you want to be
            if (dc - da < /*tolerance*/0) {
                // if we haven't made enough progress, given some tolerance, to the origin, then we can assume that we are done

                // NOTE: to get the correct distance we need to normalize d then dot it with a or c OR since we know that d is the closest point to the origin, we can just get its magnitude
                float distance = getMagnitude(vecDirectionDistance);
                vecDirectionDistance = null;
                simplexDistance.clear();
                return distance;
            }
            // if we are still getting closer then only keep the points in the simplex that are closest to the origin (we already know that c is closer than both a and b so we only need to choose between these two)
            Vector3f ad = getEdges(p1, p4);
            Vector3f bd = getEdges(p2, p4);
            Vector3f p5 = getClosestPointFromClosestLine(simplexDistance.get(0), p4);  //Could be getClosestPointFromClosestLine or getFaceDirection
            Vector3f p6 = getClosestPointFromClosestLine(simplexDistance.get(1), p4);  //Could be getClosestPointFromClosestLine or getFaceDirection
            Vector3f p7 = getClosestPointFromClosestLine(simplexDistance.get(2), p4);  //Could be getClosestPointFromClosestLine or getFaceDirection
            // getting the closest point on the edges AC and CB allows us to compare the distance between the origin and edge and choose the closer one
            if (getMagnitude(p5) < getMagnitude(p6) && getMagnitude(p5) < getMagnitude(p7)) {
                simplexDistance.set(0, p4);
                vecDirectionDistance = p5;
            } else if(getMagnitude(p6) < getMagnitude(p5) && getMagnitude(p6) < getMagnitude(p7)){
                simplexDistance.set(1, p4);
                vecDirectionDistance = p6;
            } else if(getMagnitude(p7) < getMagnitude(p5) && getMagnitude(p7) < getMagnitude(p6)){
                simplexDistance.set(2, p4);
                vecDirectionDistance = p7;
            }
            iterationCount++;
        }
    }

    //Gets the depth of the collision
    public static float penatrationDistance(HitBoxMeshVAO collider, HitBoxMeshVAO collided){

        //Termination simplex from GJK is the simplex for the penatration depth
        // loop to find the collision information
        while (true) {
            // obtain the feature (edge for 2D) closest to the origin on the Minkowski Difference
            float[] e = findClosestEdge(simplexPenatration);
            // obtain a new support point in the direction of the edge normal
            //new Vector3f(e[1], e[2], e[3]) = e.normal
            Vector3f p = getSupport(collider, collided, new Vector3f(e[1], e[2], e[3]));
            // check the distance from the origin to the edge against the distance p is along e.normal
            float d = Vector3f.dot(p, new Vector3f(e[1], e[2], e[3]));
            if (d - e[0] < TOLERANCE) {
                // the tolerance should be something positive close to zero (ex. 0.00001)

                // if the difference is less than the tolerance then we can assume that we cannot expand the simplex any further and we have our solution
                //Vector3f normal = new Vector3f(e[1], e[2], e[3]);
                return d;
            } else {
                // we haven't reached the edge of the Minkowski Difference so continue expanding by adding the new point to the simplex in between the points that made the closest edge
                simplexPenatration.add((int)(e[4]), p);
            }
        }
    }

    //This will give information on the closest edge to the origin, x is distance, y is normal, and z is index
    private static float[] findClosestEdge(ArrayList<Vector3f> simplex){
        float[] closest = new float[5]; //The edge
        // prime the distance of the edge to the max
        closest[0] = Float.MAX_VALUE;
        // s is the passed in simplex
        for (int i = 0; i < simplex.size(); i++) {
            // compute the next points index
            int j = i + 1 == simplex.size() ? 0 : i + 1;
            // get the current point and the next one
            Vector3f a = simplex.get(i);
            Vector3f b = simplex.get(j);
            // create the edge vector
            Vector3f e = getEdges(a, b);
            // get the vector from the origin to a
            Vector3f oa = new Vector3f(-a.x, -a.y, -a.z); // or a - ORIGIN
            // get the vector from the edge towards the origin
            Vector3f n = getDirection(e, oa, e);
            // normalize the vector
            n = normalise(n);
            // calculate the distance from the origin to the edge
            float d = Vector3f.dot(n, a); // could use b or a here
            // check the distance against the other distances
            if (d < closest[0]) {
                // if this edge is closer then use it
                closest[0] = d;
                closest[1] = n.x;
                closest[2] = n.y;
                closest[3] = n.z;
                closest[4] = j;
            }
        }
        // return the closest edge we found
        return closest;
    }

    private static Vector3f normalise(Vector3f a) {
        float len = a.length();
        if (len != 0.0F) {
            float l = 1.0F / len;
            return new Vector3f(a.x * l, a.y + l, a.z * l);
        } else {
            throw new IllegalStateException("Zero length vector");
        }
    }

    private static Vector3f getClosestPointFromClosestLine(Vector3f a, Vector3f b){
        // create the line
        Vector3f ab = getEdges(a, b);
        Vector3f ao = new Vector3f(-a.x, -a.y, -a.z);
        // project AO onto AB
        float abo = Vector3f.dot(ab, ao);
        // get the length squared
        float abSquared = Vector3f.dot(ab, ab);
        // calculate the distance along AB
        float distance = abo/abSquared;
        // calculate the point
        Vector3f closestPoint = new Vector3f((ab.x * distance) + a.x,(ab.y * distance) + a.y, (ab.z * distance) + a.z);
        return closestPoint;
    }

    private static float getMagnitude(Vector3f line){
        float magnitude = (float)Math.sqrt((Math.abs(Math.pow(line.x, 2))) + (Math.abs(Math.pow(line.y, 2))) + (Math.abs(Math.pow(line.z, 2))));
        return magnitude;
    }



    //Gets the normal of the face of the triangle, or just the normal of a point
    private static Vector3f getNormalDirection(Vector3f a, Vector3f b){
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
        Vector3f vecDirection = new Vector3f(x/bDot, y/bDot, z/bDot);
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
            testTemp = Vector3f.dot(vectorDirection, new Vector3f(collider.getVertexPositions(x), collider.getVertexPositions(x + 1), collider.getVertexPositions(x + 2)));
            //System.out.println(testTemp);
            if (testTemp > largestTemp){
                largestTemp = testTemp;
                indexTemp = x;
            }
        }
        largest = new Vector3f(collider.getVertexPositions(indexTemp), collider.getVertexPositions(indexTemp + 1), collider.getVertexPositions(indexTemp + 2));
        return largest;
    }


}
