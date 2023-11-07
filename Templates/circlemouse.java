package Templates;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.swing.*;

/**
 *
 * @author Mohamed
 */
public class circlemouse extends JFrame {

    static GLCanvas glcanvas = null;

    public static void main(String[] args) {
        final circlemouse app = new circlemouse();
// show what we've done
        SwingUtilities.invokeLater(
                new Runnable() {
            public void run() {
                app.setVisible(true);
                glcanvas.requestFocusInWindow();
            }
        }
        );
    }

    public circlemouse() {
//set the JFrame title
        super("circle and the mouse :)");
//kill the process when the JFrame is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//create our KeyDisplay which serves two purposes
// 1) it is our GLEventListener, and
// 2) it is our KeyListener
        MouseMotionDisplay mmd = new MouseMotionDisplay();
//only three JOGL lines of code ... and here they are
        GLCapabilities glcaps = new GLCapabilities();
        glcanvas = new GLCanvas();
        glcanvas.addGLEventListener(mmd);
        glcanvas.addMouseMotionListener(mmd);
//we'll want this for our repaint requests
        mmd.setGLCanvas(glcanvas);
//add the GLCanvas just like we would any Component
        getContentPane().add(glcanvas, BorderLayout.CENTER);
        setSize(500, 500);
//center the JFrame on the screen
        centerWindow(this);
    }

    public void centerWindow(Component frame) {
        Dimension screenSize
                = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        frame.setLocation(
                (screenSize.width - frameSize.width) >> 1,
                (screenSize.height - frameSize.height) >> 1
        );
    }
}

/**
 * For our purposes only two of the GLEventListeners matter. Those would be
 * init() and display().
 */
class MouseMotionDisplay
        implements GLEventListener, MouseMotionListener {

    int xPosition = 50;
    int yPosition = 50;
    float red = 1.0f;
    float green = 1.0f;
    float blue = 1.0f;
    GLCanvas glc;

    public void setGLCanvas(GLCanvas glc) {
        this.glc = glc;
    }

    /**
     * Take care of initialization here.
     */
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //to change the color of background
        red = 1.0f;
        green = 1.0f;
        blue = 1.0f;
        gl.glClearColor(red, green, blue, 0.0f);
        gl.glViewport(0, 0, 100, 100);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0.0, 100, 0, 100, -1, 1);
    }

    /**
     * Take care of drawing here.
     */
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        gl.glBegin(GL.GL_POINTS);
        //to change the color of the bigger circle
        gl.glColor3f(0.0f,0.0f,0.0f);
        for (double a = 0; a < Math.toRadians(360); a += Math.toRadians(1)) {
            double x = 50 + 20 * (Math.cos(a));
            double y = 50 + 20 * (Math.sin(a));
            gl.glVertex2d(x, y);
        }
        gl.glVertex2i(xPosition, yPosition);
        gl.glEnd();
        gl.glPointSize(5.0f);
        //here to change color
        switch (intersects(xPosition, yPosition)) {
            case 1:
                red = 1.0f;
                green = 0.0f;
                blue = 0.0f;
                break;
    // Circles intersect with colore red
            case 2:
                 // Circles are completely separate with the colore black
                red = 0.0f;
                green = 0.0f;
                blue = 0.0f;
                 break;
            case 3:
                red = 0.0f;
                green = 1.0f;
                blue = 0.0f;
                 break;
                   // the two centers are on each other with the color blue
            case 4:
                red = 0.0f;
                green = 0.0f;
                blue = 1.0f;
                 break;
        }
//this to darw the circle center on the mouse coordinats
        gl.glColor3f(red, green, blue);
        gl.glBegin(GL.GL_POINTS);
        for (double a = 0; a < Math.toRadians(360); a += Math.toRadians(1)) {
            double x = xPosition + 10 * (Math.cos(a));
            double y = yPosition + 10 * (Math.sin(a));
            gl.glVertex2d(x, y);
        }
        gl.glVertex2i(xPosition, yPosition);
        System.out.println("("+xPosition +","+yPosition+")");
        gl.glEnd();
    }
//a method to check if the circle inside or intersect or seprate or have the same center
    public int intersects(double x, double y) {
        double distance = Math.sqrt(Math.pow((50 - x), 2) + Math.pow((50 - y), 2));
        double combinedRadius = 30;

        if (distance ==0) {

            return 3;
        }else if (distance +10 <= 20) {
            // the two centers are on each other or one inside the other

            return 4;
        }
        else if(distance < combinedRadius){
            return 1;
        }
        else if (distance > combinedRadius) {
            // Circles are completely separate

            return 2;
        }  else {
            return 4;
        }
    }

    /**
     * Called when the GLDrawable (GLCanvas or GLJPanel) has changed in size. We
     * won't need this, but you may eventually need it -- just not yet.
     */
    public void reshape(
            GLAutoDrawable drawable,
            int x,
            int y,
            int width,
            int height
    ) {
    }

    /**
     * If the display depth is changed while the program is running this method
     * is called. Nowadays this doesn't happen much, unless a programmer has his
     * program do it.
     */
    public void displayChanged(
            GLAutoDrawable drawable,
            boolean modeChanged,
            boolean deviceChanged
    ) {
    }
////////////////////////////////////////////
// MouseMotionListener implementation below

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        Component c = e.getComponent();
        double width = c.getWidth();
        double height = c.getHeight();
//get percent of GLCanvas instead of
//points and then converting it to our
//'100' based coordinate system.
        xPosition = (int) ((x / width) * 100);
        yPosition = ((int) ((y / height) * 100));
//reversing direction of y axis
        yPosition = 100 - yPosition;
        glc.repaint();
    }
}
