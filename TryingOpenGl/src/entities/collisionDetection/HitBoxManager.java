package entities.collisionDetection;

import java.util.Collection;
import java.util.HashMap;

public class HitBoxManager {

    public static HashMap<Integer, HitBox> hitboxes = new HashMap<Integer, HitBox>();

    public static void addHitBox(HitBox box) {
        int id = hitboxes.size();
        box.setID(id);
        hitboxes.put(id, box);
    }

    public static Collection<HitBox> getNearHitBoxes(HitBox hitbox) {
        return hitboxes.values();
    }
}
