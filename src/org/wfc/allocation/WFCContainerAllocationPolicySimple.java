/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wfc.allocation;

import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.container.core.Container;
import org.cloudbus.cloudsim.container.resourceAllocators.PowerContainerAllocationPolicy;

/**
 *
 * @author w1903ten
 */
public class WFCContainerAllocationPolicySimple extends WFCContainerAllocationPolicy {


    public WFCContainerAllocationPolicySimple() {
        super();
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Container> containerList) {
        return null;
    }
}
