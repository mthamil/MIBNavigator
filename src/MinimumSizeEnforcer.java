/**
 * MIB Navigator
 *
 * Copyright (C) 2005, Matt Hamilton <matthew.hamilton@washburn.edu>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * This class is a way of enforcing a minimum size for a Component.  Currently, setting a 
 * minimum size does not actually prevent a user from resizing a component such as a frame 
 * below its minimum.  In situations where resizing a component to be larger than its 
 * defaults but not smaller is a desirable feature, this class can be used. The listener 
 * reacts to resize events and checks to see if the component has been resized below the 
 * minimum. If this is the case, the component will be returned to its default size.
 */
public class MinimumSizeEnforcer extends ComponentAdapter 
{
    // minimum width and height values
    private long width;
    private long height;
    
    /**
     * Creates a new MinimumSizeEnforcer with a minimum width and height.
     * 
     * @param width the minimum width for a component.  This must be greater than 0.
     * @param height the minimum height for a component.  This must be greater than 0.
     */
    public MinimumSizeEnforcer(final long width, final long height)
    {
        if (width <= 0)
            throw new IllegalArgumentException("Width must be greater than zero.");
        
        this.width = width;
        
        if (height <= 0)
            throw new IllegalArgumentException("Height must be greater than zero.");
        
        this.height = height;
    }
    
    /**
     * Checks whether a Component has been resized below its minimum dimensions and
     * resets its size accordingly.
     * 
     * @param e the event generated by a resize action
     */
    public void componentResized(ComponentEvent e)
    {
        Component comp = e.getComponent();
           
        double curWidth;
        double curHeight;
        
        Dimension compSize = comp.getSize();
        curWidth = compSize.getWidth();
        curHeight = compSize.getHeight();
        
        if ((curWidth < this.width) && (curHeight < this.height))
        {
            compSize.setSize(this.width, this.height);
            comp.setSize(compSize);
        }
        else if (curWidth < this.width)
        {
            compSize.setSize(this.width, curHeight);
            comp.setSize(compSize);
        }
        else if (curHeight < this.height)
        {
            compSize.setSize(curWidth, this.height);
            comp.setSize(compSize);
        }
    }

}
