import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;


public class MainWindow {
	private DisplayMode displayMode;
	
	private int mouseX = 0;
	private int mouseY = 0;
	private boolean rightMouseDown = false;
	private boolean leftMouseDown = false;
	private boolean mouseOnWindow = false;

	private int vertex_buffer_id;
	
	private World theWorld;
	
	public static void main(String[] args) {
		new MainWindow();
	}
	
	public MainWindow() {
		try {
			Display.setFullscreen(false);
			Display.sync(60);
			Display.setVSyncEnabled(true);
	        DisplayMode d[] = Display.getAvailableDisplayModes();
	        for (int i = 0; i < d.length; i++) {
	            if (d[i].getWidth() == 640
	                && d[i].getHeight() == 480
	                && d[i].getBitsPerPixel() == 32) {
	                displayMode = d[i];
	                break;
	            }
	        }
	        Display.setDisplayMode(displayMode);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		initGL();
		
		theWorld.load();
		
		getRenderQuads();

    	FPCameraController camera = new FPCameraController();
    	 
        float dx        = 0.0f;
        float dy        = 0.0f;
        float dt        = 0.0f; //length of frame
        long lastTime   = 0L; // when the last frame was
        long time       = 0L;
     
        float mouseSensitivity = 0.05f;
        float movementSpeed = 1.0f; //move 10 units per second
     
        //hide the mouse
        Mouse.setGrabbed(true);
        
    	while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
        	mouseX = Mouse.getX();
        	mouseY = Mouse.getY();
        	leftMouseDown = Mouse.isButtonDown(0);
        	rightMouseDown = Mouse.isButtonDown(1);
        	mouseOnWindow = Mouse.isInsideWindow();
    		
    		time = Sys.getTime();
            dt = (time - lastTime)/1000.0F;
            lastTime = time;
            
    		//distance in mouse movement from the last getDX() call.
            dx = Mouse.getDX();
            //distance in mouse movement from the last getDY() call.
            dy = Mouse.getDY();
     
            //controll camera yaw from x movement fromt the mouse
            camera.yaw(dx * mouseSensitivity);
            //controll camera pitch from y movement fromt the mouse
            camera.pitch(dy * mouseSensitivity);
            
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                camera.walkForward(movementSpeed*dt);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                camera.walkBackwards(movementSpeed*dt);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                camera.strafeLeft(movementSpeed*dt);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                camera.strafeRight(movementSpeed*dt);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                camera.down(movementSpeed*dt);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                camera.up(movementSpeed*dt);
            }
            
            // set the modelview matrix back to the identity
            GL11.glLoadIdentity();
            // look through the camera before you draw anything
            camera.lookThrough();
            
//            dx = 0;
//    		dy = 0;
            
            run();
			render();

			Display.sync(60);
			Display.update();
		}
		
		Display.destroy();
	}
	
	private void run() {
		
	}

	private void initGL() {
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

	    GL11.glShadeModel(GL11.GL_SMOOTH);

	    GL11.glEnable(GL11.GL_COLOR_MATERIAL);
	    GL11.glEnable(GL11.GL_DEPTH_TEST);

	    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
	    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
	    GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);

	    // set up lighting
//	    GL11.glEnable(GL11.GL_LIGHTING);
//	    GL11.glEnable(GL11.GL_LIGHT0);
//
//	    GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, floatBuffer(1.0f, 1.0f, 1.0f, 1.0f));
//	    GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, 25.0f);

//	    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, floatBuffer(-5.0f, 5.0f, 15.0f, 0.0f));
//
//	    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, floatBuffer(1.0f, 1.0f, 1.0f, 1.0f));
//	    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, floatBuffer(1.0f, 1.0f, 1.0f, 1.0f));
//
//	    GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, floatBuffer(0.1f, 0.1f, 0.1f, 1.0f));

	    // set up the camera
	    GL11.glMatrixMode(GL11.GL_PROJECTION);
	    GL11.glLoadIdentity();

	    GLU.gluPerspective(45.0f,((float)displayMode.getWidth()/(float)displayMode.getHeight()),0.1f,100.0f);

	    GL11.glMatrixMode(GL11.GL_MODELVIEW);
	    GL11.glLoadIdentity();
	    
        GL11.glEnable(GL11.GL_TEXTURE_2D);              
        GL11.glClearDepth(1.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
    }
	
	public FloatBuffer floatBuffer(float a, float b, float c, float d) {
	    float[] data = new float[]{a,b,c,d};
	    FloatBuffer fb = BufferUtils.createFloatBuffer(data.length);
	    fb.put(data);
	    fb.flip();
	    return fb;
	}
	
	public void getRenderQuads() {
		if(vertex_buffer_id == 0) {
		    IntBuffer buffer = BufferUtils.createIntBuffer(1);
		    GL15.glGenBuffers(buffer);
	
		    vertex_buffer_id = buffer.get(0);
		}
		
		FloatBuffer vertex_buffer_data = BufferUtils.createFloatBuffer();
	    vertex_buffer_data.put(ArrayHelper.addArrays(vertex_data_array, BlockVBO.topVBO(1.0f, 0.0f, 0.0f, new Color(1.0f, 0.0f, 0.0f, 1.0f), new Random())));
	    vertex_buffer_data.rewind();

	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertex_buffer_id);
	    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertex_buffer_data, GL15.GL_DYNAMIC_DRAW);
	}
	
	private void render() {
		GL11.glTranslatef(0, -2f, 0);
		
	    // clear the display
	    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

	    // perform rotation transformations
	    GL11.glPushMatrix();
	      
	    GL11.glVertexPointer(3, GL11.GL_FLOAT, 40, 0);
	    GL11.glNormalPointer(GL11.GL_FLOAT, 40, 12);
	    GL11.glColorPointer(4, GL11.GL_FLOAT, 40, 24);

	    GL11.glDrawArrays(GL11.GL_QUADS, 0, 0);

	    // restore the matrix to pre-transformation values
	    GL11.glPopMatrix();
	}
}