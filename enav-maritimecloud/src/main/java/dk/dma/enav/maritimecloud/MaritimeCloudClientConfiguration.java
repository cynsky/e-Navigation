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
package dk.dma.enav.maritimecloud;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dk.dma.enav.maritimecloud.broadcast.BroadcastOptions;
import dk.dma.enav.model.MaritimeId;
import dk.dma.enav.model.geometry.PositionTime;
import dk.dma.enav.util.function.Consumer;
import dk.dma.enav.util.function.Supplier;

/**
 * 
 * @author Kasper Nielsen
 */
public class MaritimeCloudClientConfiguration {

    static final String FACTORY = "dk.dma.navnet.client.DefaultMaritimeCloudClient";

    boolean autoConnect = true;

    BroadcastOptions broadcastDefaults = new BroadcastOptions();

    private MaritimeId id;

    long keepAliveNanos = TimeUnit.SECONDS.toNanos(2);

    final List<MaritimeCloudConnection.Listener> listeners = new ArrayList<>();

    private String nodes = "localhost:43234";

    private Supplier<PositionTime> positionSupplier = new Supplier<PositionTime>() {
        public PositionTime get() {
            return PositionTime.create(0, 0, 0);
        }
    };

    MaritimeCloudClientConfiguration(MaritimeId id) {
        this.id = id;
    }

    /**
     * Adds a state listener that will be invoked whenever the state of the connection changes.
     * 
     * @param stateListener
     *            the state listener
     * @throws NullPointerException
     *             if the specified listener is null
     * @see #removeStateListener(Consumer)
     */
    public MaritimeCloudClientConfiguration addListener(MaritimeCloudConnection.Listener listener) {
        listeners.add(requireNonNull(listener, "listener is null"));
        return this;
    }

    @SuppressWarnings("unchecked")
    public MaritimeCloudClient build() {
        Class<?> c;
        try {
            c = Class.forName(FACTORY);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find factory class " + FACTORY + " make sure it is on the classpath");
        }
        Constructor<MaritimeCloudClient> con;
        try {
            con = (Constructor<MaritimeCloudClient>) c.getConstructor(MaritimeCloudClientConfiguration.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find a valid constructor", e);
        }

        MaritimeCloudClient client;
        try {
            client = con.newInstance(this);
        } catch (ReflectiveOperationException e) {
            if (e instanceof InvocationTargetException) {
                InvocationTargetException ite = (InvocationTargetException) e;
                if (ite.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) ite.getCause();
                } else if (ite.getCause() instanceof Error) {
                    throw (Error) ite.getCause();
                }
                throw new RuntimeException("Could not create a valid connection", ite.getCause());
            }
            throw new RuntimeException("Could not create a valid connection", e);
        }
        if (autoConnect) {
            client.connection().connect();
        }
        return client;
    }

    public MaritimeCloudClient build(MaritimeId id) {
        this.id = id;
        return build();
    }

    /**
     * @return the broadcastDefaults
     */
    public BroadcastOptions getDefaultBroadcastOptions() {
        return broadcastDefaults;
    }

    public String getHost() {
        return nodes;
    }

    /**
     * @return the id
     */
    public MaritimeId getId() {
        return id;
    }

    /**
     * @return the keepAliveNanos
     */
    public long getKeepAlive(TimeUnit unit) {
        return unit.convert(keepAliveNanos, TimeUnit.NANOSECONDS);
    }


    /**
     * @return the listeners
     */
    public List<MaritimeCloudConnection.Listener> getListeners() {
        return listeners;
    }

    /**
     * @return the positionSupplier
     */
    public Supplier<PositionTime> getPositionSupplier() {
        return positionSupplier;
    }

    /**
     * @return the autoConnect
     */
    public boolean isAutoConnect() {
        return autoConnect;
    }

    /**
     * @param autoConnect
     *            the autoConnect to set
     */
    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    /**
     * @param broadcastDefaults
     *            the broadcastDefaults to set
     */
    public MaritimeCloudClientConfiguration setDefaultBroadcastOptions(BroadcastOptions defaults) {
        this.broadcastDefaults = requireNonNull(defaults);
        return this;
    }

    public MaritimeCloudClientConfiguration setHost(String host) {
        this.nodes = requireNonNull(host);
        return this;
    }

    public MaritimeCloudClientConfiguration setId(MaritimeId id) {
        this.id = id;
        return this;
    }

    public MaritimeCloudClientConfiguration setKeepAlive(long time, TimeUnit unit) {
        keepAliveNanos = unit.toNanos(time);
        return this;
    }

    public MaritimeCloudClientConfiguration setPositionSupplier(Supplier<PositionTime> positionSupplier) {
        this.positionSupplier = requireNonNull(positionSupplier);
        return this;
    }

    public static MaritimeCloudClientConfiguration create() {
        return new MaritimeCloudClientConfiguration(null);
    }

    public static MaritimeCloudClientConfiguration create(MaritimeId id) {
        return new MaritimeCloudClientConfiguration(id);
    }

    public static MaritimeCloudClientConfiguration create(String id) {
        return new MaritimeCloudClientConfiguration(MaritimeId.create(id));
    }
}
