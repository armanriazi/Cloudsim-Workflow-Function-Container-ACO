/**
 * Copyright 2019-2020 University Of Southern California
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.wfc.scheduler;

import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.container.core.ContainerVm;

/**
 * The Scheduler interface
 *
 * @author Arman Riazi
 * @since WorkflowSim Toolkit 1.0
 * @date March 29, 2020
 */
public interface WFCSchedulingAlgoInterface {

    /**
     * Sets the job list.
     * @param list
     */
    public void setContainerList(List list);

    /**
     * Sets the vm list.
     * @param list
     */
    public void setContainerVmList(List list);

    /**
     * Gets the job list.
     * @return 
     */
    public List getContainerList();

    /**
     * Gets the vm list.
     * @return 
     */
    public List getContainerVmList();

    /**
     * the main function.
     * @throws java.lang.Exception
     */
    public void run() throws Exception;

    /**
     * Gets the scheduled jobs.
     * @return 
     */
    public  Map<String, ContainerVm> getScheduledList();
}
