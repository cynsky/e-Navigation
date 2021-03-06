/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dma.enav.model.geometry.grid;

/**
 * 
 * @author Kasper Nielsen
 */
public class Cell implements Comparable<Cell> {
    long id;

    Cell(long id) {
        this.id = id;
    }

    public int getCellId() {
        return (int) id;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Cell o) {
        return o.getCellId() - getCellId();
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return id == ((Cell) obj).id;
    }

}
