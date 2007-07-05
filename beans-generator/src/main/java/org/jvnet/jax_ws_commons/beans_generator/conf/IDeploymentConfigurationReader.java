/**
 * Copyright (c) 2006-2007, Magnetosoft, LLC
 * All rights reserved.
 * 
 * Licensed under the Magnetosoft License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.magnetosoft.ru/LICENSE
 *
 * file: IDeploymentConfigurationReader.java
 */

package org.jvnet.jax_ws_commons.beans_generator.conf;

/**
 * Interface of configuration
 * 
 * Created: 04.06.2007
 * @author Malyshkin Fedor (fedor.malyshkin@magnetosoft.ru)
 * @version $Revision: 240 $
 */
public interface IDeploymentConfigurationReader {
    IEndpointData readConfiguration()
	    throws DeploymentConfigurationReadingException;
}
