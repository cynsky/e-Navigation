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
package dk.dma.enav.maritimecloud.service.spi;

import static java.util.Objects.requireNonNull;

import java.util.ServiceConfigurationError;

/**
 * 
 * @author Kasper Nielsen
 */
public abstract class ServiceMessage<T> {

    private final transient Class<? extends Service> serviceType;

    @SuppressWarnings("unchecked")
    public ServiceMessage() {
        this.serviceType = (Class<? extends Service>) requireNonNull(getClass().getDeclaringClass());
    }

    public String messageName() {
        String name = getClass().getSimpleName();
        return name;
    }

    public String serviceName() {
        try {
            return (String) serviceType().getField("NAME").get(null);
        } catch (ReflectiveOperationException e) {
            throw new ServiceConfigurationError("oops", e);
        }
    }

    /**
     * @return the serviceType
     */
    public Class<? extends Service> serviceType() {
        return serviceType;
    }
}
