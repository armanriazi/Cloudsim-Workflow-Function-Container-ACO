/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wfc.scheduler;

import java.util.Map;
import org.cloudbus.cloudsim.Log;
import org.workflowsim.WorkflowSimTags;
import org.cloudbus.cloudsim.container.core.*;
import org.wfc.allocation.WFCContainerAlgoACO;
import org.wfc.core.WFCConstants;

/**
 * MCT algorithm
 *
 * @author Arman Riazi
 * @since WorkflowSim Toolkit 1.0
 * @date March 29, 2020
 */
public class WFCMCTSchedulingAlgo extends WFCBaseSchedulingAlgo {

    public WFCMCTSchedulingAlgo() {
        super();
    }

    @Override
    public void run() {
  int size = getContainerList().size();

        for (int i = 0; i < size; i++) {
            Container cloudlet = (Container) getContainerList().get(i);
            int vmSize = getContainerVmList().size();
            ContainerVm firstIdleVm = null;

            for (int j = 0; j < vmSize; j++) {
                ContainerVm vm = (ContainerVm) getContainerVmList().get(j);
                if (vm.getState() == WorkflowSimTags.VM_STATUS_IDLE) {
                    firstIdleVm = vm;
                    break;
                }
            }
            if (firstIdleVm == null) {
                break;
            }

            for (int j = 0; j < vmSize; j++) {
                ContainerVm vm = (ContainerVm) getContainerVmList().get(j);
                if ((vm.getState() == WorkflowSimTags.VM_STATUS_IDLE)
                        && (vm.getCurrentRequestedTotalMips() > firstIdleVm.getCurrentRequestedTotalMips())) {
                    firstIdleVm = vm;
                }
            }
            firstIdleVm.setState(WorkflowSimTags.VM_STATUS_BUSY);
            cloudlet.setVm(firstIdleVm);
            getScheduledList().put(cloudlet.getUid(),firstIdleVm);
            Log.printLine("Schedules " + cloudlet.getId() + " to VM " + firstIdleVm.getId()
                    + " with " + firstIdleVm.getCurrentRequestedTotalMips());
        }
    }
}
