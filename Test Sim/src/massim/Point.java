/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim;

/**
 *
 * @author TungPT
 */
public class Point {
    private float xx,yy,zz;

    public float getX() {
        return xx;
    }

    public float getY() {
        return yy;
    }

    public float getZ() {
        return zz;
    }

    
    public Point(double x, double y, double z, double shiftX, double shiftY) {
        Double sx,sy;
        sx = 0.0;
        sy = 0.0;
        xx = (float)(sx+x+shiftX);
        yy = (float)(sy+y+shiftY);
        zz = (float)z;
        
    }
    
}
