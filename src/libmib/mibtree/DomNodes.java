/**
 * libmib - Java SNMP Management Information Base Library
 *
 * Copyright (C) 2010, Matt Hamilton <matthamilton@live.com>
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

package libmib.mibtree;

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utilities.iteration.ImmutableIterator;

/**
 * Contains utility methods for W3C DOM Node objects.
 */
public final class DomNodes
{
	private DomNodes() { }
	
	public static Node first(NodeList nodes)
	{
		return nodes.item(0);
	}
	
	public static Element firstElement(NodeList nodes)
	{
		return (Element)first(nodes);	
	}
	
	public static Iterable<Node> asIterable(NodeList nodes)
	{
		return new NodeIterable(nodes);
	}
	
	private static final class NodeIterable implements Iterable<Node>
	{
		private final NodeList nodes;
		
		public NodeIterable(NodeList nodes)
		{
			this.nodes = nodes;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		public Iterator<Node> iterator()
		{
			return new NodeIterator(nodes);
		}
		
		private static final class NodeIterator extends ImmutableIterator<Node>
		{
			private final NodeList nodes;
			private int currentIndex = 0;
			
			public NodeIterator(NodeList nodes)
			{
				this.nodes = nodes;
			}

			/* (non-Javadoc)
			 * @see java.util.Iterator#hasNext()
			 */
			public boolean hasNext()
			{
				return currentIndex < nodes.getLength();
			}

			/* (non-Javadoc)
			 * @see java.util.Iterator#next()
			 */
			public Node next()
			{
				Node node = nodes.item(currentIndex++);
				return node;
			}
		}
	}
}
