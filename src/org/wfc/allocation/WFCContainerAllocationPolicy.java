package org.wfc.allocation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.container.core.Container;
import org.cloudbus.cloudsim.container.core.ContainerVm;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.container.core.Container;
import org.cloudbus.cloudsim.container.core.ContainerVm;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.wfc.allocation.*;
import org.wfc.core.WFCConstants;
import org.wfc.scheduler.WFCMCTSchedulingAlgo;
import org.wfc.scheduler.WFCBaseSchedulingAlgo;
import org.workflowsim.scheduling.BaseSchedulingAlgorithm;
import org.workflowsim.utils.Parameters;
/**
 * Created by sareh on 16/07/15.
 */
public abstract class WFCContainerAllocationPolicy extends ContainerAllocationPolicy{

        /** The container table. */
        private final Map<String, ContainerVm> containerTable = new HashMap<>();

        /**
         * Instantiates a new power vm allocation policy abstract.
         *
         */
        public WFCContainerAllocationPolicy() {
            
            super();            
        }

        /*
         * (non-Javadoc)
         * @see org.cloudbus.cloudsim.VmAllocationPolicy#allocateHostForVm(org.cloudbus.cloudsim.Vm)
         */
        @Override
        public boolean allocateVmForContainer(Container container, List<ContainerVm> containerVmList) {
            setContainerVmList(containerVmList);            
            return allocateVmForContainer(container, findVmForContainer(container));
        }
         
      

        @Override
        public void allocateVmsForContainersMCT( List<Container> containerList, List<ContainerVm> containerVmList) {            
            setContainerVmList(containerVmList);
            setContainerList(containerList);          
 
           WFCBaseSchedulingAlgo scheduler = getScheduler("MCT");
             scheduler.setContainerList(containerList);
             scheduler.setContainerVmList(getContainerVmList());                
             try {
                    scheduler.run();
                    Map<String, ContainerVm> containerTbl=  scheduler.getScheduledList();
                       getContainerTable().putAll(containerTbl);  
                   
            } catch (Exception e) {
                    Log.printLine("Error in configuring scheduler_method");
                    Log.printLine(e.getMessage());            
            }
        }
             
         @Override
        public void allocateVmsForContainersACO( List<Container> containerList, List<ContainerVm> containerVmList,int m, double Q, double alpha, double beta, double gamma, double rho) {
            setContainerVmList(containerVmList);
            setContainerList(containerList);
            try{
            WFCContainerAlgoACO lbaco1 = new WFCContainerAlgoACO(m,Q,alpha,beta,gamma,rho);       
            Map<String, ContainerVm> containerTbl = lbaco1.getScheduledList(containerList, getContainerVmList(),WFCConstants.WFC_NUMBER_CONTAINER);
             getContainerTable().putAll(containerTbl);                  
            } catch (Exception e) {
            Log.printLine("Error in configuring scheduler_method");
            Log.printLine(e.getMessage());
            //by arman I commented log  e.printStackTrace();
        }
        
        }
        /*
         * (non-Javadoc)
         * @see org.cloudbus.cloudsim.VmAllocationPolicy#allocateHostForVm(org.cloudbus.cloudsim.Vm,
         * org.cloudbus.cloudsim.Host)
         */
        @Override
        public boolean allocateVmForContainer(Container container, ContainerVm containerVm) {
            if (containerVm == null) {
                Log.formatLine("%.2f: No suitable VM found for Container#" + container.getId() + "\n", CloudSim.clock());
                return false;
            }
            if (containerVm.containerCreate(container)) { // if vm has been succesfully created in the host
                //int a=getContainerTable().size();//.put(container.getUid(), containerVm);
//                container.setVm(containerVm);
                Log.formatLine(
                        "%.2f: Container #" + container.getId() + " has been allocated to the VM #" + containerVm.getId(),
                        CloudSim.clock());
                return true;
            }
            Log.formatLine(
                    "%.2f: Creation of Container #" + container.getId() + " on the Vm #" + containerVm.getId() + " failed\n",
                    CloudSim.clock());
            return false;
        }

        /**
         * Find host for vm.
         *
         * @param container the vm
         * @return the power host
         */
        public ContainerVm findVmForContainer(Container container) {
            for (ContainerVm containerVm : getContainerVmList()) {
//                Log.printConcatLine("Trying vm #",containerVm.getId(),"For container #", container.getId());
                if (containerVm.isSuitableForContainer(container)) {
                    return containerVm;
                }
            }
            return null;
        }

        /*
         * (non-Javadoc)
         * @see org.cloudbus.cloudsim.VmAllocationPolicy#deallocateHostForVm(org.cloudbus.cloudsim.Vm)
         */
        @Override
        public void deallocateVmForContainer(Container container) {
            ContainerVm containerVm = getContainerTable().remove(container.getUid());
            if (containerVm != null) {
                containerVm.containerDestroy(container);
            }
        }

        /*
         * (non-Javadoc)
         * @see org.cloudbus.cloudsim.VmAllocationPolicy#getHost(org.cloudbus.cloudsim.Vm)
         */
        @Override
        public ContainerVm getContainerVm(Container container) {
            return getContainerTable().get(container.getUid());
        }

        /*
         * (non-Javadoc)
         * @see org.cloudbus.cloudsim.VmAllocationPolicy#getHost(int, int)
         */
        @Override
        public ContainerVm getContainerVm(int containerId, int userId) {
            return getContainerTable().get(containerId);
        }

        /**
         * Gets the vm table.
         *
         * @return the vm table
         */
        public Map<String, ContainerVm> getContainerTable() {
            return containerTable;
        }

        /**
     * Switch between multiple schedulers. Based on algorithm.method
     *
     * @param name the SchedulingAlgorithm name
     * @return the algorithm that extends BaseSchedulingAlgorithm
     */
    private WFCBaseSchedulingAlgo getScheduler(String name) {
        WFCBaseSchedulingAlgo algorithm=null;

        // choose which algorithm to use. Make sure you have add related enum in
        //Parameters.java
        switch (name) {
            case "MCT":
                algorithm = new WFCMCTSchedulingAlgo();
                break;           

        }
        return algorithm;
    }

    
    }

/*
 //by default it is Static
            case FCFS:
                algorithm = new FCFSSchedulingAlgorithm();
                break;
            case MINMIN:
                algorithm = new MinMinSchedulingAlgorithm();
                break;
            case MAXMIN:
                algorithm = new MaxMinSchedulingAlgorithm();
                break;            
            case DATA:
                algorithm = new DataAwareSchedulingAlgorithm();
                break;
            case STATIC:
                algorithm = new StaticSchedulingAlgorithm();
                break;
            case ROUNDROBIN:
                algorithm = new RoundRobinSchedulingAlgorithm();
                break;
            default:
                algorithm = new StaticSchedulingAlgorithm();
                break;
*/



