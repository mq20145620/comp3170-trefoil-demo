package comp3170.demos.trefoil;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import comp3170.GLException;
import comp3170.Shader;
import comp3170.demos.trefoil.sceneobjects.Axes;
import comp3170.demos.trefoil.sceneobjects.Trefoil;

public class TrefoilDemo extends JFrame implements GLEventListener {

	public static final float TAU = (float) (2 * Math.PI);		// https://tauday.com/tau-manifesto
	
	private int width = 800;
	private int height = 800;

	private GLCanvas canvas;
	

	private Animator animator;
	private long oldTime;
	private InputManager input;

	private Axes axes;
	private Trefoil trefoil;

	private Matrix4f viewMatrix;
	private Matrix4f projectionMatrix;


	public TrefoilDemo() {
		super("Trefoil demo");

		// set up a GL canvas
		GLProfile profile = GLProfile.get(GLProfile.GL4);		 
		GLCapabilities capabilities = new GLCapabilities(profile);
		canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		add(canvas);
		
		// set up Animator		

		animator = new Animator(canvas);
		animator.start();
		oldTime = System.currentTimeMillis();		

		// input
		
		input = new InputManager(canvas);
		
		// set up the JFrame
		
		setSize(width,height);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		// set the background colour to black
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_BACK);
		
		axes = new Axes();
		trefoil = new Trefoil();
		
		viewMatrix = new Matrix4f();
		projectionMatrix = new Matrix4f();
	}

	
	private static final float ROTATION_SPEED = TAU / 4;
	
	private void update() {
		long time = System.currentTimeMillis();
		float deltaTime = (time-oldTime) / 1000f;
		oldTime = time;
		
		Vector3f angle = new Vector3f();
		trefoil.getAngle(angle);

		if (input.isKeyDown(KeyEvent.VK_LEFT)) {
			angle.y += ROTATION_SPEED * deltaTime;			
		}
		if (input.isKeyDown(KeyEvent.VK_RIGHT)) {
			angle.y -= ROTATION_SPEED * deltaTime;			
		}
		if (input.isKeyDown(KeyEvent.VK_UP)) {
			angle.x += ROTATION_SPEED * deltaTime;			
		}
		if (input.isKeyDown(KeyEvent.VK_DOWN)) {
			angle.x -= ROTATION_SPEED * deltaTime;			
		}
			
		if (input.isKeyDown(KeyEvent.VK_PAGE_DOWN)) {
			cameraDistance -= CAMERA_MOVE * deltaTime;
		}
		if (input.isKeyDown(KeyEvent.VK_PAGE_UP)) {
			cameraDistance += CAMERA_MOVE * deltaTime;
		}
		
		trefoil.setAngle(angle);
				
		input.clear();
	}
	
	private float cameraDistance = 3;
	private static final float CAMERA_MOVE = 1;
	private static final float CAMERA_WIDTH = 8;
	private static final float CAMERA_HEIGHT = 8;
	private static final float CAMERA_NEAR = 1;
	private static final float CAMERA_FAR = 10;
	private static final float CAMERA_FOVY = TAU / 6; 
	
	@Override	
	public void display(GLAutoDrawable arg0) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		update();
		
        // clear the colour buffer
		gl.glClear(GL_COLOR_BUFFER_BIT);		
		gl.glClear(GL_DEPTH_BUFFER_BIT);		
		
		//  Y up W--X
		//       |
		//       Z (out of screen)
		//
		//    
		//    (0,0,3)
		//  Y up C--X
		//       |
		//       Z
		
		viewMatrix.identity();
		viewMatrix.translate(0,0,cameraDistance);
		viewMatrix.invert();

		projectionMatrix.setOrtho(
				-CAMERA_WIDTH/2, CAMERA_WIDTH/2, 
				-CAMERA_HEIGHT/2, CAMERA_HEIGHT/2, 
				CAMERA_NEAR, CAMERA_FAR);

//		projectionMatrix.setPerspective(CAMERA_FOVY, 
//				CAMERA_WIDTH / CAMERA_HEIGHT,
//				CAMERA_NEAR, CAMERA_FAR);

		// draw the scene
		//axes.draw(viewMatrix, projectionMatrix);
		trefoil.draw(viewMatrix, projectionMatrix);
		
	}

	@Override
	public void reshape(GLAutoDrawable d, int x, int y, int width, int height) {
		this.width = width;
		this.height = height;		
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) { 
		new TrefoilDemo();
	}


}
