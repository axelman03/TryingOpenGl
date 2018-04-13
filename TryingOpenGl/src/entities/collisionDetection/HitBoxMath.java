package entities.collisionDetection;

import org.lwjgl.util.vector.Vector3f;
import org.lwjglx.debug.org.eclipse.jetty.servlet.Source;
import toolBox.Maths;

import java.util.ArrayList;

public class HitBoxMath {

    private static HitBoxSquare tempCollidedHitBox;
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
    public static boolean isBroadPlaneColliding(HitBoxSquare collider, ArrayList<HitBoxSquare> hitBoxes){
        boolean isHit = false;
        for(HitBoxSquare hitBox : hitBoxes){
            if(collider.getXMax() >= hitBox.getXMin() && collider.getXMin() <= hitBox.getXMax()){
                if(collider.getZMax() >= hitBox.getZMin() && collider.getZMin() <= hitBox.getZMax()){
                    if(collider.getYMax() >= hitBox.getYMin() && collider.getYMin() <= hitBox.getYMax()){
                        setCollidedHitbox(hitBox);
                        isHit = true;
                    }
                }
            }
        }
        return isHit;
    }

    private static void setCollidedHitbox(HitBoxSquare hitBox){
        tempCollidedHitBox = hitBox;
    }

    public static HitBoxSquare getCollidedHitbox(){
        return tempCollidedHitBox;
    }

    //Read comments to do for third dimension
    public boolean narrowPlaneCollision(HitBoxMeshVAO collider, HitBoxMeshVAO collided){
        boolean collideLoop = true;
        int loop = 0;
        ArrayList<Vector3f> simplex = new ArrayList<Vector3f>();
        Vector3f vecDirection = null;
        Vector3f.sub(collided.getPosition(), collider.getPosition(), vecDirection);
        Vector3f p1 = getSupport(collider, collided, vecDirection);
        simplex.add(p1);
        vecDirection.negate(vecDirection);

        while(true){
            Vector3f p2 = getSupport(collider, collided, vecDirection);
            simplex.add(p2);
            // make sure that the last point we added actually passed the origin
            if (Vector3f.dot(simplex.get(simplex.size() - 1), vecDirection) <= 0){ //This works on a 2D plane, but what about a 3D plane? Do I multiply that with the Dot of another Vector to get it in a triangle or what?
                // if the point added last was not past the origin in the direction of vecDirection then the Minkowski Sum cannot possibly contain the origin since the last point added is on the edge of the Minkowski Difference
                collideLoop = false;
            }
            else{
                // otherwise we need to determine if the origin is in the current simplex
                if(simplex.contains(Origin)){
                    //If it does, there is a collision
                    collideLoop = true;
                }
                else{
                    // otherwise we cannot be certain so find the edge who is closest to the origin and use its normal (in the direction of the origin) as the new vecDirection and continue the loop
                    //Make loop iterate twice before testing different sides, since its 3D
                    if(loop <= 2){
                        loop++;
                    }
                    else{
                        simplex.remove(simplex.size() - 2);
                    }
                    vecDirection = getDirection(p1, p2);

                }
            }
        }
        return collideLoop;
    }

    //gets the direction of the new vector for each iteration
    private Vector3f getDirection(Vector3f a, Vector3f b){
        //the vectors subtracting a from b
        Vector3f AB = new Vector3f(b.x - a.x, b.y - a.y, b.z - a.z);
        Vector3f AO = new Vector3f(0 - a.x, 0 - a.y, 0 - a.z);
        float ADot = Vector3f.dot(AB, AB);
        float BDot = Vector3f.dot(AO, AB);
        Vector3f vecDirectionA = new Vector3f(AO.x * ADot, AO.y * ADot, AO.z * ADot);
        Vector3f vecDirectionB = new Vector3f(AB.x * BDot, AB.y * BDot, AB.z * BDot);
        Vector3f vecDirection = new Vector3f((vecDirectionA.x - vecDirectionB.x)/ BDot, (vecDirectionA.y - vecDirectionB.y) / BDot, (vecDirectionA.z - vecDirectionB.z) / BDot);
        return vecDirection;
    }

    //Returns the support function of the shape of the Minkowski sum, so a vertex on the edge of the shape
    private Vector3f getSupport(HitBoxMeshVAO collider, HitBoxMeshVAO collided, Vector3f vectorDirection){
        Vector3f p1 = getFarthestPoint(collider, vectorDirection);
        Vector3f p2 = getFarthestPoint(collided, vectorDirection.negate(vectorDirection));
        Vector3f p3 = new Vector3f(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
        return p3;
    }

    //Returns the farthest vertex on the object given from the vector direction given
    private Vector3f getFarthestPoint(HitBoxMeshVAO collider, Vector3f vectorDirection){
        Vector3f largest;
        float largestTemp = 0;
        float testTemp;
        int indexTemp = 0;
        for(int x = 0; x < collider.getVertexPositionsSize() - 2; x = x + 3){
            testTemp = Math.abs(Vector3f.dot(vectorDirection, new Vector3f(collider.getVertexPositions(x), collider.getVertexPositions(x + 1), collider.getVertexPositions(x + 2))));
            if (testTemp > largestTemp){
                largestTemp = testTemp;
                indexTemp = x;
            }
        }
        largest = new Vector3f(collider.getVertexPositions(indexTemp), collider.getVertexPositions(indexTemp + 1), collider.getVertexPositions(indexTemp + 2));
        return largest;
    }
/*
    private ArrayList<Vector3f> minkowskiDifferenceObject(HitBoxMeshVAO collider, HitBoxMeshVAO collided){
        ArrayList<Vector3f> differenceObject = new ArrayList<Vector3f>();
        for(int x = 0; x < collider.getVertexPositionsSize() - 2; x = x + 3){
            for(int c = 0; c < collided.getVertexPositionsSize(); c = c + 3){
                differenceObject.add(new Vector3f(collider.getVertexPositions(x) + (-collided.getVertexPositions(c)), collider.getVertexPositions(x + 1) + (-collided.getVertexPositions(c + 1)), collider.getVertexPositions(x + 2) + (-collided.getVertexPositions(c + 2))));
            }
        }

        for(Vector3f vertex : differenceObject){
            for(Vector3f vertices : differenceObject){
                if(vertex == vertices){
                    differenceObject.remove(vertices);
                }
            }
        }
        return differenceObject;
    }
    */
    /*
    private static boolean isCollidingCSHybrid(HitBox box1, HitBox box2) {
        HitBox[] boxes = new HitBox[2];
        HitBoxCircle circle = null;
        HitBoxSquare square = null;
        if (box1 instanceof HitBoxCircle) {
            circle = (HitBoxCircle) box1;
        } else {
            square = (HitBoxSquare) box1;
        }
        if (box2 instanceof HitBoxCircle) {
            circle = (HitBoxCircle) box2;
        } else {
            square = (HitBoxSquare) box2;
        }
        boxes[0] = square;
        boxes[1] = circle;
        return isColliding(boxes);
    }

    private static boolean isCollidingCircle(HitBoxCircle box1, HitBoxCircle box2) {
        HitBoxCircle[] boxes = { (HitBoxCircle) box1, (HitBoxCircle) box2 };
        return isColliding(boxes);
    }

    private static boolean isColliding(HitBox[] boxes) {
        HitBoxCircle circle = (HitBoxCircle) boxes[1];
        HitBoxSquare square = (HitBoxSquare) boxes[0];
        boolean isBox = false;

        isBox = tier1Check(circle, square);

        if (!isBox) {
            isBox = tier2Check(circle, square);
        }
        return isBox;
    }

    private static boolean tier2Check(HitBoxCircle circle, HitBoxSquare square) {
        boolean isBox = false;
        float[] dat = rotate(square.getPosition().x, square.getPosition().z, circle.getPosition().x,
                circle.getPosition().z, square.getRotation().y, circle.getRotation().y);
        Vector3f checkedPos = new Vector3f(dat[0], circle.getPosition().y, dat[1]);
        float bX = checkedPos.x;
        float bZ = checkedPos.z;
        float aX = square.getPosition().x;
        float aZ = square.getPosition().z;
        float aXmin = aX + square.getXMin();
        float aXmax = aX + square.getXMax();
        float aZmin = aZ + square.getZMin();
        float aZmax = aZ + square.getZMax();
        if (bX >= aXmin && bX <= aXmax) {
            if (bZ >= aZ) {
                float tot_off = circle.getDistance() + aZmax;
                float abrange = aZ - bZ;
                double norm = Math.sqrt(abrange * abrange);
                if (norm <= tot_off) {
                    isBox = true;
                }
            }
            if (!isBox) {
                if (bZ < aZ) {
                    float tot_off = circle.getDistance() + -aZmin;
                    float abrange = aZ - bZ;
                    double norm = Math.sqrt(abrange * abrange);
                    if (norm <= tot_off) {
                        isBox = true;
                    }
                }
            }
        }

        if (!isBox) {
            if (bZ >= aZmin && bZ <= aZmax) {
                if (bX >= aX) {
                    float tot_off = circle.getDistance() + aXmax;
                    float abrange = aX - bX;
                    double norm = Math.sqrt(abrange * abrange);
                    if (norm <= tot_off) {
                        isBox = true;
                    }
                }
                if (!isBox) {
                    if (bX < aX) {
                        float tot_off = circle.getDistance() + -aXmin;
                        float abrange = aX - bX;
                        double norm = Math.sqrt(abrange * abrange);
                        if (norm <= tot_off) {
                            isBox = true;
                        }
                    }
                }
            }
        }
        return isBox;
    }

    private static boolean tier1Check(HitBoxCircle circle, HitBoxSquare square) {
        boolean isBox = false;
        float[] dat = rotate(square.getPosition().x, square.getPosition().z, circle.getPosition().x,
                circle.getPosition().z, square.getRotation().y, circle.getRotation().y);
        Vector3f checkedPos = new Vector3f(dat[0], circle.getPosition().y, dat[1]);
        Vector3f[] corners = generateCorners(square, 0);
        if (true) {
            for (Vector3f vec : corners) {
                float x = square.getPosition().x + vec.x;
                float z = square.getPosition().z + vec.z;
                float xOf = x - checkedPos.x;
                float zOf = z - checkedPos.z;
                float dis = (float) Math.sqrt(xOf * xOf + zOf * zOf);
                if (dis < circle.getDistance()) {
                    isBox = true;
                }
            }
        }
        return isBox;
    }

    private static boolean isColliding(HitBoxCircle[] boxes) {
        HitBoxCircle checker = boxes[0];
        HitBoxCircle checked = boxes[1];
        float collideRange = checker.getDistance() + checked.getDistance();
        float distance = Maths.distance(checker.getPosition(), checked.getPosition());
        boolean isBox = false;
        if (collideRange >= distance) {
            isBox = true;
        }
        return isBox;
    }

    private static boolean isCollidingSquare(HitBoxSquare box1, HitBoxSquare box2) {
        HitBoxSquare[] boxes = { (HitBoxSquare) box1, (HitBoxSquare) box2 };

        boolean isBox = false;

        if (isColliding(true, boxes)) {
            isBox = true;
        }
        if (isColliding(false, boxes)) {
            isBox = true;
        }
        return isBox;
    }

    private static boolean isColliding(boolean b, HitBoxSquare[] boxes) {
        int i1 = 0;
        int i2 = 0;
        if (b) {
            i1 = 0;
            i2 = 1;
        } else {
            i1 = 1;
            i2 = 0;
        }

        HitBoxSquare checker = boxes[i1];
        HitBoxSquare checked = boxes[i2];

        boolean inBox = false;
        if (true) {
            float[] dat = rotate(checker.getPosition().x, checker.getPosition().z, checked.getPosition().x,
                    checked.getPosition().z, checker.getRotation().y, checked.getRotation().y);
            float yRot = dat[2];
            Vector3f checkedPos = new Vector3f(dat[0], checked.getPosition().y, dat[1]);
            Vector3f[] corners = generateCorners(checked, yRot);
            for (Vector3f vec : corners) {
                float x = checkedPos.x + vec.x;
                float z = checkedPos.z + vec.z;
                float checkerxMax = checker.getPosition().x + checker.getXMax();
                float checkerxMin = checker.getPosition().x + checker.getXMin();
                float checkerzMax = checker.getPosition().z + checker.getZMax();
                float checkerzMin = checker.getPosition().z + checker.getZMin();
                if (x > checkerxMin && x < checkerxMax) {
                    if (z < checkerzMax && z > checkerzMin) {
                        inBox = true;
                    }
                }
            }

        }
        return inBox;
    }

    private static Vector3f[] generateCorners(HitBoxSquare checked, float yRot) {
        Vector3f[] corners = new Vector3f[checked.corners.length];
        if (true) {
            for (int i = 0; i < corners.length; i++) {
                Vector3f corner = checked.corners[i];
                float[] dat = rotate(0, 0, corner.x, corner.z, yRot, 0);
                Vector3f cornerPos = new Vector3f(dat[0] * checked.getScale(), corner.getY(),
                        dat[1] * checked.getScale());
                corners[i] = cornerPos;
            }
        }
        return corners;
    }
*/
    public static float[] rotate(float inx, float inz, float outx, float outz, float inRot, float outRot) {
        float yrot = -inRot;
        float xoff = outx - inx;
        float zoff = outz - inz;
        float xcalc1 = (float) (xoff * Math.sin(Math.toRadians(yrot + 90)));
        float zcalc1 = (float) (xoff * Math.cos(Math.toRadians(yrot + 90)));
        float xcalc2 = (float) (zoff * Math.sin(Math.toRadians(yrot)));
        float zcalc2 = (float) (zoff * Math.cos(Math.toRadians(yrot)));
        float[] returnFlt = new float[3];
        returnFlt[0] = inx - xcalc1 + xcalc2;
        returnFlt[1] = inz - zcalc1 + zcalc2;
        returnFlt[2] = inRot + outRot;
        return returnFlt;
    }
}
