/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wfc.scheduler;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.container.core.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.Vm;

/**
 * The base scheduler has implemented the basic features. Every other scheduling method
 * should extend from BaseSchedulingAlgorithm but should not directly use it. 
 *
 * @author Arman Riazi
 * @since WorkflowSim Toolkit 1.0
 * @date March 29, 2020
 */
public abstract class WFCBaseSchedulingAlgo implements WFCSchedulingAlgoInterface {

     private final Map<String, ContainerVm> containerTable = new HashMap<>();
    /**
     * the job list.
     */
    private List<? extends Container> containerList;
    /**
     * the vm list.
     */
    private List<? extends ContainerVm> vmList;    
  
    public WFCBaseSchedulingAlgo() {
        
    }

    /**
     * Sets the job list.
     *
     * @param list
     */
    @Override
    public void setContainerList(List list) {
        this.containerList = list;
    }

    /**
     * Sets the vm list
     *
     * @param list
     */
    @Override
    public void setContainerVmList(List list) {
        this.vmList = new ArrayList(list);
    }

    /**
     * Gets the job list.
     *
     * @return the job list
     */
    @Override
    public List getContainerList() {
        return this.containerList;
    }

    /**
     * Gets the vm list
     *
     * @return the vm list
     */
    @Override
    public List getContainerVmList() {
        return this.vmList;
    }

    /**
     * The main function
     * @throws java.lang.Exception
     */
    @Override
    public abstract void run() throws Exception;

    /**
     * Gets the scheduled job list
     *
     * @return job list
     */
    @Override
    public Map<String,ContainerVm> getScheduledList() {
        return containerTable;
    }
}

