package textures;
 
import java.nio.ByteBuffer;
 
public class TextureData {
     
    private int width;
    private int height;
    private ByteBuffer buffer;
     
    public TextureData(ByteBuffer buffer2, int width, int height){
        this.buffer = buffer2;
        this.width = width;
        this.height = height;
    }
     
    public int getWidth(){
        return width;
    }
     
    public int getHeight(){
        return height;
    }
     
    public ByteBuffer getBuffer(){
        return buffer;
    }
 
}