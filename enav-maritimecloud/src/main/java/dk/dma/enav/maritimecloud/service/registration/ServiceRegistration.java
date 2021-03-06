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
package dk.dma.enav.maritimecloud.service.registration;

import java.util.concurrent.TimeUnit;

import dk.dma.enav.maritimecloud.service.invocation.ServiceUnavailableException;

/**
 * 
 * @author Kasper Nielsen
 */
public interface ServiceRegistration {

    /**
     * Returns the current state of this registration
     * 
     * @return
     */
    State getState();

    /**
     * Cancels the service registration. All services will automatically be shutdown when the user explicit disconnects.
     * Remote clients that tries to access the service will receive a {@link ServiceUnavailableException}.
     */
    void cancel();

    boolean awaitRegistered(long timeout, TimeUnit unit) throws InterruptedException;

    /** The current state of the registration. */
    enum State {

        /**
         * The initial state of this registration. When created at the client but before the server has registered the
         * service registration.
         */
        INITIATED,

        /** The service has been registered with server. And remote clients may now invoke the service */
        REGISTERED,

        /**
         * The client no longer offers the service. Remote clients attempting to invoke the service will fail with a
         * {@link ServiceUnavailableException}.
         */
        CANCELLED;
    }
}
