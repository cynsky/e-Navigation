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
package dk.dma.enav.maritimecloud.broadcast;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import dk.dma.enav.model.MaritimeId;
import dk.dma.enav.model.geometry.PositionTime;

/**
 * Abstract class for a message that can be broadcast.
 * <p>
 * Any implementation of this message must contain a <tt>public static final String CHANNEL = "name"</tt> field.
 * Initialized to a unique channel that the broadcast is send via.
 * 
 * @author Kasper Nielsen
 */
public abstract class BroadcastMessage {

    static final ConcurrentHashMap<String, Class<? extends BroadcastMessage>> CACHE = new ConcurrentHashMap<>();

    /**
     * Returns the broadcast channel on which the broadcast is sent. Defaults (for now) to the name of the class.
     * 
     * @return the broadcast channel on which the message should be sent
     */
    public final String channel() {
        return getClass().getCanonicalName();
    }

    static ServiceLoader<BroadcastMessage> sl = ServiceLoader.load(BroadcastMessage.class);

    /**
     * Finds the broadcast message for the specified channel.
     * 
     * @param channel
     *            the name of the channel
     * @return the corresponding to the specified channel
     */
    public static Class<? extends BroadcastMessage> findClass(String channel) {
        requireNonNull("channel is null");
        for (BroadcastMessage m : sl) {
            if (getChannelField(m.getClass()).equals("channel")) {
                return m.getClass();
            }
        }
        return null;
    }

    public static String findChannelForMessageType(Class<? extends BroadcastMessage> type) {
        String channel = getChannelField(type);

        return channel;
    }

    static String getChannelField(Class<? extends BroadcastMessage> type) {
        Field f;
        try {
            f = type.getField("CHANNEL");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("No field named CHANNEL found on " + type.getCanonicalName(), e);
        }
        if (!Modifier.isFinal(f.getModifiers())) {
            throw new RuntimeException("Field named CHANNEL on " + type.getCanonicalName() + " must be final");
        } else if (!Modifier.isStatic(f.getModifiers())) {
            throw new RuntimeException("Field named CHANNEL on " + type.getCanonicalName() + " must be static");
        } else if (!Modifier.isPublic(f.getModifiers())) {
            throw new RuntimeException("Field named CHANNEL on " + type.getCanonicalName() + " must be public");
        } else if (f.getType() != String.class) {
            throw new RuntimeException("Field named CHANNEL on " + type.getCanonicalName()
                    + " must be of a String type");
        }
        try {
            return (String) f.get(null);
        } catch (IllegalArgumentException e) {
            throw new Error("oops, should never happen since the field is a string", e);
        } catch (IllegalAccessException e) {
            throw new Error("oops, should never happen since the field is public", e);
        }
    }


    /** An acknowledgment that can be send every time a broadcast is received by an actor. */
    public interface Ack {

        /**
         * Returns the id of the actor that received the broadcast.
         * 
         * @return the id of the actor that received the broadcast
         */
        MaritimeId getId();

        /**
         * Returns the position and time when the actor received the message.
         * 
         * @return the position and time when the actor received the message
         */
        PositionTime getPosition();
    }
}
