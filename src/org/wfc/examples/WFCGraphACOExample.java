/**
 * Copyright 2019-2020 ArmanRiazi
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
package org.wfc.examples;

import com.utils.*;
import java.io.File;
import java.lang.management.*;
import java.text.DecimalFormat;
import java.util.*;
import org.cloudbus.cloudsim.container.core.*;
import org.cloudbus.cloudsim.container.hostSelectionPolicies.*;
import org.cloudbus.cloudsim.container.resourceAllocatorMigrationEnabled.*;
import org.cloudbus.cloudsim.container.schedulers.*;
import org.cloudbus.cloudsim.container.utils.IDs;
import org.cloudbus.cloudsim.container.vmSelectionPolicies.*;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.HarddriveStorage;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.container.core.*;
import org.cloudbus.cloudsim.container.resourceAllocators.*;
import org.cloudbus.cloudsim.container.containerProvisioners.*;
import org.cloudbus.cloudsim.container.containerVmProvisioners.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.*;
import org.workflowsim.CondorVM;
import org.workflowsim.Task;
import org.workflowsim.WorkflowDatacenter;
import org.workflowsim.Job;
import org.workflowsim.WFCEngine;
import org.workflowsim.WFCPlanner;
import org.workflowsim.utils.*;
import org.cloudbus.cloudsim.util.Conversion;
import org.workflowsim.failure.*;
import org.workflowsim.utils.DistributionGenerator;
import org.wfc.core.WFCDatacenter;
import org.wfc.core.WFCConstants;
import org.workflowsim.utils.Parameters.ClassType;
import org.wfc.allocation.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RefineryUtilities;
import sun.java2d.Disposer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.IntervalCategoryDataset;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import javax.swing.JFrame;
import org.jfree.data.gantt.*;
import org.jfree.data.time.Month;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;

/*
 * @author Arman Riazi
 * @since WFC Toolkit 1.0
 * @date March 29, 2020
 */
/*
ConstantsExamples.WFC_DC_SCHEDULING_INTERVAL+ 0.1D

On Vm (
    Allocation= PowerContainerVmAllocationPolicyMigrationAbstractHostSelection
    Scheduler = ContainerVmSchedulerTimeSharedOverSubscription    
    SelectionPolicy = PowerContainerVmSelectionPolicyMaximumUsage
    Pe = CotainerPeProvisionerSimple
    Overhead = 0
    ClusteringMethod.NONE
    SchedulingAlgorithm.MINMIN
    PlanningAlgorithm.INVALID
    FileSystem.LOCAL
)

On Host (
    Scheduler = ContainerVmSchedulerTimeSharedOverSubscription    
    SelectionPolicy = HostSelectionPolicyFirstFit
    Pe = PeProvisionerSimple 
)

On Container (
    Allocation = PowerContainerAllocationPolicySimple
    Scheduler = ContainerCloudletSchedulerDynamicWorkload 
    UtilizationModelFull
)
*/

public class WFCGraphACOExample {
    
    private static String experimentName="";
    public static  int num_user = 0;
    private static boolean trace_flag = true;  // mean trace events
    private static boolean failure_flag = false;   
    private static List<Container> containerList;           
    private static List<ContainerHost> hostList;        
    
    public static void main(String[] args) {
        
        try {                                             
            WFCConstants.WFCBrokerTypes="ACO";
            DecimalFormat dft = new DecimalFormat("####.#####");       
            List<DefaultCategoryDataset> datasets = new ArrayList<>();                                             
            DefaultCategoryDataset datasetLineTotalTimeToCost = new DefaultCategoryDataset();   
            DefaultCategoryDataset datasetLineThroughputTotalPerCloudlet = new DefaultCategoryDataset();      
            DefaultCategoryDataset datasetLineAverageTurnAroundTimeTotalPerCloudlet = new DefaultCategoryDataset();  
            DefaultCategoryDataset datasetLineAverageWaitingTimeTotalPerCloudlet = new DefaultCategoryDataset();              
            DefaultCategoryDataset datasetLineAverageTransferTimeForContainer = new DefaultCategoryDataset();              
            DefaultCategoryDataset datasetLineMakeSpanBaseOnDC = new DefaultCategoryDataset();   
            DefaultCategoryDataset datasetLineMakeSpanBaseOnTotalActualCPUTime = new DefaultCategoryDataset();   
            DefaultCategoryDataset datasetLineRateReqPerSec = new DefaultCategoryDataset();   
            DefaultCategoryDataset datasetLineResponseTimeBaseOneMaxMinTimesMillisecond = new DefaultCategoryDataset();               
            DefaultCategoryDataset datasetLineAverageResponseTime = new DefaultCategoryDataset();   
            DefaultPieDataset datasetPieResources = new DefaultPieDataset();               
            DefaultPieDataset datasetPie = new DefaultPieDataset();                                    
            DefaultCategoryDataset datasetBar = new DefaultCategoryDataset();           
            DefaultCategoryDataset datasetLine = new DefaultCategoryDataset();       
            DefaultIntervalCategoryDataset datasetGanttChart ;//= new DefaultIntervalCategoryDataset();    
            double totalSimulationTime=0.0;
            WFCConstants.CAN_PRINT_SEQ_LOG = true;
            WFCConstants.CAN_PRINT_SEQ_LOG_Just_Step = true;
            WFCConstants.ENABLE_OUTPUT = true;
            WFCConstants.FAILURE_FLAG = false;            
            WFCConstants.RUN_AS_STATIC_RESOURCE = false;                 
            WFCConstants.WFC_ACO_M = 37;
            WFCConstants.WFC_ACO_Q = 1;
            WFCConstants.WFC_ACO_APLPHA = 2;
            WFCConstants.WFC_ACO_BETA = 1;
            WFCConstants.WFC_ACO_GAMMA = 2;
            WFCConstants.WFC_ACO_RHO = 0.05;
            int[] montage={0,26,51,101,1001};
            
            
        for (int k=1;k<montage.length;k++){
            List<Job> outputList0=new ArrayList<>();
            containerList=new ArrayList<>();
            hostList=new ArrayList<>();
            hostList = createHostList(WFCConstants.WFC_NUMBER_HOSTS);            
            WFCContainerAllocationPolicySimple containerAllocationPolicyACO=null;
            PowerContainerAllocationPolicySimple powercontainerAllocationPolicy=null;
            IDs.polldReset();
            experimentName="WFCGraphACOExample_"+WFCConstants.WFCNameOfWorkflow+"_" + String.valueOf(montage[k]) ;
            FailureParameters.FTCMonitor ftc_monitor = null;
            FailureParameters.FTCFailure ftc_failure = null;
            FailureParameters.FTCluteringAlgorithm ftc_method = null;
            DistributionGenerator[][] failureGenerators = null;
             
            
            Log.printLine("Starting " + experimentName + " ... ");
            WFCConstants.WFC_NUMBER_CLOUDLETS=montage[k];
            WFCConstants.WFC_NUMBER_CONTAINER=montage[k];
            String daxPath = "./config/dax/"+WFCConstants.WFCNameOfWorkflow+"_" + (WFCConstants.WFC_NUMBER_CLOUDLETS - 1) + ".xml";
            
            File daxFile = new File(daxPath);
            if (!daxFile.exists()) {
                Log.printLine("Warning: Please replace daxPath with the physical path in your working environment!");
                return;
            }
     
            if(failure_flag){
                /*
                *  Fault Tolerant Parameters
                */
               /**
                * MONITOR_JOB classifies failures based on the level of jobs;
                * MONITOR_VM classifies failures based on the vm id; MOINTOR_ALL
                * does not do any classification; MONITOR_NONE does not record any
                * failiure.
                */
                ftc_monitor = FailureParameters.FTCMonitor.MONITOR_ALL;
               /**
                * Similar to FTCMonitor, FTCFailure controls the way how we
                * generate failures.
                */
                ftc_failure = FailureParameters.FTCFailure.FAILURE_ALL;
               /**
                * In this example, we have no clustering and thus it is no need to
                * do Fault Tolerant Clustering. By default, WorkflowSim will just
                * rety all the failed task.
                */
                ftc_method = FailureParameters.FTCluteringAlgorithm.FTCLUSTERING_NOOP;
               /**
                * Task failure rate for each level
                *
                */
               failureGenerators = new DistributionGenerator[1][1];
               failureGenerators[0][0] = new DistributionGenerator(DistributionGenerator.DistributionFamily.WEIBULL,
                       100, 1.0, 30, 300, 0.78);
            }
            
            Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.MINMIN;//local
            Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.INVALID;//global-stage
            WFCReplicaCatalog.FileSystem file_system = WFCReplicaCatalog.FileSystem.LOCAL;

            OverheadParameters op = new OverheadParameters(0, null, null, null, null, 0);
   
            ClusteringParameters.ClusteringMethod method = ClusteringParameters.ClusteringMethod.NONE;
            ClusteringParameters cp = new ClusteringParameters(0, 0, method, null);

            if(failure_flag){
                FailureParameters.init(ftc_method, ftc_monitor, ftc_failure, failureGenerators);
            }
          
           Parameters.init(WFCConstants.WFC_NUMBER_VMS, daxPath, null,
                    null, op, cp, sch_method, pln_method,
                    null, 0);
            WFCReplicaCatalog.init(file_system);

            
            if (failure_flag) {
              FailureMonitor.init();
              FailureGenerator.init();
            }
            
            WFCReplicaCatalog.init(file_system);
            
            Calendar calendar = Calendar.getInstance();            

            CloudSim.init(num_user++, calendar, trace_flag);


            
            //powercontainerAllocationPolicy = new PowerContainerAllocationPolicySimple();
            containerAllocationPolicyACO = new WFCContainerAllocationPolicySimple();
            PowerContainerVmSelectionPolicy vmSelectionPolicy = new PowerContainerVmSelectionPolicyMaximumUsage();
            HostSelectionPolicy hostSelectionPolicy = new HostSelectionPolicyFirstFit();

            String logAddress = "~/Results";                                                               
            
            ContainerVmAllocationPolicy vmAllocationPolicyACO = new
                    PowerContainerVmAllocationPolicyMigrationAbstractHostSelection(hostList, vmSelectionPolicy,
                    hostSelectionPolicy, WFCConstants.WFC_CONTAINER_OVER_UTILIZATION_THRESHOLD, WFCConstants.WFC_CONTAINER_UNDER_UTILIZATION_THRESHOLD);        
                      
           /* ContainerVmAllocationPolicy vmAllocationPolicy = new
                    PowerContainerVmAllocationPolicyMigrationAbstractHostSelection(hostList, vmSelectionPolicy,
                    hostSelectionPolicy, WFCConstants.WFC_CONTAINER_OVER_UTILIZATION_THRESHOLD, WFCConstants.WFC_CONTAINER_UNDER_UTILIZATION_THRESHOLD);        
            */
             //BROKER_TYPES = 0 Default 1 ACO
            WFCDatacenter datacenterACO = (WFCDatacenter) createDatacenter(1,"datacenter_1_"+ Integer.toString(num_user),
                        PowerContainerDatacenterCM.class, hostList, vmAllocationPolicyACO,containerList,containerAllocationPolicyACO,
                        getExperimentName(experimentName, String.valueOf(WFCConstants.OVERBOOKING_FACTOR)),
                        WFCConstants.WFC_DC_SCHEDULING_INTERVAL, logAddress,
                        WFCConstants.WFC_VM_STARTTUP_DELAY,
                        WFCConstants.WFC_CONTAINER_STARTTUP_DELAY);
            
            /*
            WFCDatacenter datacenter = (WFCDatacenter) createDatacenter(0,"datacenter_0"+ Integer.toString(num_user),
                        PowerContainerDatacenterCM.class, hostList, vmAllocationPolicy,containerList,powercontainerAllocationPolicy,
                        getExperimentName(experimentName, String.valueOf(WFCConstants.OVERBOOKING_FACTOR)),
                        WFCConstants.WFC_DC_SCHEDULING_INTERVAL, logAddress,
                        WFCConstants.WFC_VM_STARTTUP_DELAY,
                        WFCConstants.WFC_CONTAINER_STARTTUP_DELAY);
            */
            
            WFCPlanner wfPlannerACO = new WFCPlanner("planner_1_"+Integer.toString(num_user), 1);
            //WFCPlanner wfPlanner = new WFCPlanner("planner_0_"+Integer.toString(num_user), 1);

            WFCEngine wfEngineACO = wfPlannerACO.getWorkflowEngine();
            //WFCEngine wfEngine = wfPlanner.getWorkflowEngine();
            
            wfEngineACO.bindSchedulerDatacenter(datacenterACO.getId(), 0);
            //wfEngine.bindSchedulerDatacenter(datacenter.getId(), 0);
            
            totalSimulationTime=CloudSim.startSimulation();       
            //CloudSim.terminateSimulation(WFCConstants.SIMULATION_LIMIT);                        
            outputList0 = wfEngineACO.getJobsReceivedList();
            
           
                                               
            CloudSim.stopSimulation();
                        
            printJobList(outputList0,datacenterACO,totalSimulationTime,datacenterACO.getContainerList());
            //printJobList(outputList0,datacenter,totalSimulationTime,datacenter.getContainerList());
                        
            showChartLineTotalTimeToCost(datasetLineTotalTimeToCost,outputList0,montage[k],dft);
            showChartLineThroughputTotalPerCloudlet(datasetLineThroughputTotalPerCloudlet,outputList0,montage[k],dft);
            showChartLineAverageTurnAroundTimeTotalPerCloudlet(datasetLineAverageTurnAroundTimeTotalPerCloudlet,outputList0,montage[k],dft);            
            showChartLineAverageTransferTimeForContainer(datasetLineAverageTransferTimeForContainer,outputList0,datacenterACO.getContainerList(),montage[k],dft);      
            //showChartLineAverageTransferTimeForContainer(datasetLineAverageTransferTimeForContainer,outputList0,datacenter.getContainerList(),montage[k],dft);      
            showChartLineMakeSpanBaseOnOneDC(datasetLineMakeSpanBaseOnDC,outputList0,montage[k],dft);      
            showChartLineMakeSpanBaseOnTotalActualCPUTime(datasetLineMakeSpanBaseOnTotalActualCPUTime,outputList0,montage[k],dft);       
            showChartLineAverageResponseTime(datasetLineAverageResponseTime,outputList0,montage[k],dft);       
            showChartLineRateReqPerSec(datasetLineRateReqPerSec,outputList0,montage[k],dft);       
            showChartLineResponseTimeBaseOneMaxMinTimes(datasetLineResponseTimeBaseOneMaxMinTimesMillisecond,outputList0,montage[k],dft);                        
            showChartPieResources(datasetPieResources,k);
            //showChartLineAverageWaitingTimeTotalPerCloudlet(datasetLineAverageWaitingTimeTotalPerCloudlet,outputList0,montage[k],dft);           
            
            /*ok
            showChartPieTimes(datasetPie,outputList0,montage[k],dft);                        
            chartPieImplement("Total Times For "+String.valueOf(montage[k])+" Cloudlets",false,true,false,datasetPie);                
            datasetPie =new DefaultPieDataset();
            */            
            
            showBarChartTimes(datasetBar,outputList0,montage[k],dft);                        
            chartBarImplement("Total Times For "+String.valueOf(montage[k])+" Cloudlets","Total Time"," ",false,true,false,datasetBar);                
            datasetBar =new DefaultCategoryDataset();
            
            showChartPieCosts(datasetPie,outputList0,montage[k],dft);                                    
            chartPieImplement("Total Costs Of "+String.valueOf(montage[k])+" Cloudlets",false,true,false,datasetPie);    
            datasetPie =new DefaultPieDataset();
                      
            /*
                TimeSeriesChart_AWT demo = new TimeSeriesChart_AWT(
                "Time Series Chart Demo 1");
                demo.pack();
                RefineryUtilities.centerFrameOnScreen(demo);
                demo.setVisible(true);
            */
            }                                      
                                
            chartLineImplement("ResponseTime Base On Max&Min Times (Millisecond)","CPU Times","Cloudlets",datasetLineResponseTimeBaseOneMaxMinTimesMillisecond);            
            chartLineImplement("AverageResponseTime","ResponseTime","Cloudlets",datasetLineAverageResponseTime);                       
            chartLineImplement("Rate Req/Sec","Rate","Cloudlets",datasetLineRateReqPerSec);                      
            chartLineImplement("MakeSpanBaseOnTotalActualCPUTime","CPU Times","Cloudlets",datasetLineMakeSpanBaseOnTotalActualCPUTime);                       
            chartLineImplement("MakeSpanBaseOnOneDC","CPU Times","Cloudlets",datasetLineMakeSpanBaseOnDC);                       
            chartLineImplement("Total Time/Cost","CPU Times","Cloudlets",datasetLineTotalTimeToCost);                       
            chartLineImplement("AverageTransferTimeForContainer","TransfetTime","Cloudlets",datasetLineAverageTransferTimeForContainer);                      
            chartLineImplement("AverageTurnAroundTimeTotalPerCloudlet","AverageTurnAroundTime","Cloudlets",datasetLineAverageTurnAroundTimeTotalPerCloudlet);          
            chartLineImplement("ThroughputTotalPerCloudlet","Throughput","Cloudlets",datasetLineThroughputTotalPerCloudlet);                                            
            chartPieImplement("Number of Resources", false,true,false,datasetPieResources);            
            //chartLineImplement("AverageWaitingTimeTotalPerCloudlet","AverageWaitingTime","Cloudlets",datasetLineAverageWaitingTimeTotalPerCloudlet);          
            
            ManagementFactory.getGarbageCollectorMXBeans().clear();
            ManagementFactory.getMemoryPoolMXBeans().clear();
            ManagementFactory.getMemoryManagerMXBeans().clear();
            ManagementFactory.getThreadMXBean().resetPeakThreadCount();            
            ManagementFactory.getMemoryMXBean().gc();
            Log.printLine(experimentName + " Finished!");
            //outputByRunnerAbs();
            
        } catch (Exception e) {                        
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
            Log.printLine(e.getMessage());
            System.exit(0);
        }
    }

        
     public static WFCDatacenter createDatacenter(int broker_type,String name, Class<? extends WFCDatacenter> datacenterClass,
                                                       List<ContainerHost> hostList,
                                                       ContainerVmAllocationPolicy vmAllocationPolicy,
                                                       List<Container> containerList,
                                                       ContainerAllocationPolicy containerAllocationPolicy,                                                       
                                                       String experimentName, double schedulingInterval, String logAddress, double VMStartupDelay,
                                                       double ContainerStartupDelay) throws Exception {
       
        // 4. Create a DatacenterCharacteristics object that stores the
        //    properties of a data center: architecture, OS, list of
        //    Machines, allocation policy: time- or space-shared, time zone
        //    and its price (G$/Pe time unit).
        
        LinkedList<Storage> storageList =new LinkedList<Storage>();
        WFCDatacenter datacenter = null;

        // 5. Finally, we need to create a storage object.
        /**
         * The bandwidth within a data center in MB/s.
         */
        //int maxTransferRate = 15;// the number comes from the futuregrid site, you can specify your bw

        try {
            // Here we set the bandwidth to be 15MB/s
            HarddriveStorage s1 = new HarddriveStorage(name, 1e12);
            s1.setMaxTransferRate(WFCConstants.WFC_DC_MAX_TRANSFER_RATE);
            storageList.add(s1);           

            ContainerDatacenterCharacteristics characteristics = new              
                ContainerDatacenterCharacteristics(WFCConstants.WFC_DC_ARCH, WFCConstants.WFC_DC_OS, WFCConstants.WFC_DC_VMM,
                                                     hostList, WFCConstants.WFC_DC_TIME_ZONE, WFCConstants.WFC_DC_COST , WFCConstants.WFC_DC_COST_PER_MEM, 
                                                     WFCConstants.WFC_DC_COST_PER_STORAGE,WFCConstants.WFC_DC_COST_PER_BW);
            
            datacenter = new WFCDatacenter(broker_type,name, 
                    characteristics, 
                    vmAllocationPolicy,
                    containerAllocationPolicy, 
                    storageList, 
                    schedulingInterval, 
                    experimentName, 
                    logAddress
                    );
                
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
            Log.printLine(e.getMessage());
            System.exit(0);
        }
        return datacenter;
    }
    
       
    public static List<ContainerHost> createHostList(int hostsNumber) {
        
            ArrayList<ContainerHost> hostList = new ArrayList<ContainerHost>();
        // 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
        //    create a list to store these PEs before creating
        //    a Machine.
        
         try {
            for (int i = 1; i <= WFCConstants.WFC_NUMBER_HOSTS; i++) {
                ArrayList<ContainerVmPe> peList = new ArrayList<ContainerVmPe>();            
                // 3. Create PEs and add these into the list.
                //for a quad-core machine, a list of 4 PEs is required:
                for (int p = 0; p < WFCConstants.WFC_NUMBER_HOST_PES; p++) {
                  peList.add(new ContainerVmPe(p, new ContainerVmPeProvisionerSimple(WFCConstants.WFC_HOST_MIPS))); // need to store Pe id and MIPS Rating            
                }
                
                 hostList.add(new PowerContainerHostUtilizationHistory(i,
                        new ContainerVmRamProvisionerSimple(WFCConstants.WFC_HOST_RAM),
                        new ContainerVmBwProvisionerSimple(WFCConstants.WFC_HOST_BW), WFCConstants.WFC_HOST_STORAGE , peList,
                        //new WFCContainerVmSchedulerSpaceShared(peList),
                        //new WFCContainerVmSchedulerSpaceSharedOverSubscription(peList),
                        new ContainerVmSchedulerTimeSharedOverSubscription(peList),
                        //new ContainerVmSchedulerTimeShared(peList),
                        WFCConstants.HOST_POWER[2]));
            }
          } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
            Log.printLine(e.getMessage());
            System.exit(0);
        }
        return hostList;
    }
          
        private static String getExperimentName(String... args) {
        StringBuilder experimentName = new StringBuilder();

        for (int i = 0; i < args.length; ++i) {
            if (!args[i].isEmpty()) {
                if (i != 0) {
                    experimentName.append("_");
                }

                experimentName.append(args[i]);
            }
        }

        return experimentName.toString();
    }

    /**
     * Gets the maximum number of GB ever used by the application's heap.
     * @return the max heap utilization in GB
     * @see <a href="https://www.oracle.com/webfolder/technetwork/tutorials/obe/java/gc01/index.html">Java Garbage Collection Basics (for information about heap space)</a>
     */
    private static double getMaxHeapUtilizationGB() {
        final double memoryBytes =
            ManagementFactory.getMemoryPoolMXBeans()
                             .stream()
                             .filter(bean -> bean.getType() == MemoryType.HEAP)
                             .filter(bean -> bean.getName().contains("Eden Space") || bean.getName().contains("Survivor Space"))
                             .map(MemoryPoolMXBean::getPeakUsage)
                             .mapToDouble(MemoryUsage::getUsed)
                             .sum();
        return Conversion.bytesToGigaBytes(memoryBytes);
    }

                   
    public static List<Container> createContainerList(int brokerId, int containersNumber) {
        LinkedList<Container> list = new LinkedList<>();        
        //peList.add(new ContainerPe(0, new CotainerPeProvisionerSimple((double)mips * ratio)));         
        //create VMs
        try{
            Container[] containers = new Container[containersNumber];
            for (int i = 0; i < containersNumber; i++) {

                containers[i] = new PowerContainer(i+1, brokerId, (double) WFCConstants.WFC_CONTAINER_MIPS ,
                        WFCConstants.WFC_CONTAINER_PES_NUMBER , WFCConstants.WFC_CONTAINER_RAM,
                        WFCConstants.WFC_CONTAINER_BW, WFCConstants.WFC_CONTAINER_SIZE, WFCConstants.WFC_CONTAINER_VMM,
                        //new ContainerCloudletSchedulerTimeShared(),WFCConstants.WFC_DC_SCHEDULING_INTERVAL);                    
                        new ContainerCloudletSchedulerDynamicWorkload(WFCConstants.WFC_CONTAINER_MIPS, WFCConstants.WFC_CONTAINER_PES_NUMBER),
                        WFCConstants.WFC_DC_SCHEDULING_INTERVAL);                    
                list.add(containers[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
            Log.printLine(e.getMessage());
            System.exit(0);
        }
        return list;
       }
    
    
    /*
    private Vm createVm(DatacenterBroker broker) {
        final long   storage = 10000; // vm image size (Megabyte)
        final int    ram = 512; // vm memory (Megabyte)
        final long   bw = 1000; // vm bandwidth

        return new VmSimple(numberOfCreatedVms++, VM_MIPS, VM_PES)
            .setRam(ram).setBw(bw).setSize(storage)
            .setCloudletScheduler(new CloudletSchedulerCompletelyFair());
    }
    */
      public static List<ContainerVm> createVmList(int brokerId, int containerVmsNumber) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<ContainerVm> list = new LinkedList<>();
        ArrayList peList = new ArrayList();
       
        try{
            for (int p = 0; p < WFCConstants.WFC_NUMBER_VM_PES ; p++) {
              peList.add(new ContainerPe(p, new CotainerPeProvisionerSimple((double)WFCConstants.WFC_VM_MIPS * WFCConstants.WFC_VM_RATIO)));         
            }
           //create VMs
           ContainerVm[] vm = new ContainerVm[containerVmsNumber];

           for (int i = 0; i < containerVmsNumber; i++) {           
               vm[i] = new PowerContainerVm(i+1, brokerId, WFCConstants.WFC_VM_MIPS, (float)WFCConstants.WFC_VM_RAM,
                        WFCConstants.WFC_VM_BW, WFCConstants.WFC_VM_SIZE,  WFCConstants.WFC_VM_VMM,                  
                        new ContainerSchedulerTimeShared(peList),
                       //new ContainerSchedulerTimeSharedOverSubscription(peList),
                       new ContainerRamProvisionerSimple(WFCConstants.WFC_VM_RAM),
                       new ContainerBwProvisionerSimple(WFCConstants.WFC_VM_BW), peList,
                       WFCConstants.WFC_DC_SCHEDULING_INTERVAL);

                       /*new ContainerVm(IDs.pollId(ContainerVm.class), brokerId, (double) mips, (float) ram,
                       bw, size, "Xen", new ContainerSchedulerTimeShared(peList),
                       new ContainerRamProvisionerSimple(ram),
                       new ContainerBwProvisionerSimple(bw), peList);*/

                       //new ContainerVm(i, userId, mips * ratio, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
               list.add(vm[i]);
           }
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
            Log.printLine(e.getMessage());
            System.exit(0);
        }
        return list;
    }
    /**
     * Prints the job objects
     *
     * @param list list of jobs
     */
    protected static void printJobList(List<Job> list, WFCDatacenter datacenter,double totalSimulationTime,List<? extends Container> listOfContainers) {
        double maxHeapUtilizationGB = getMaxHeapUtilizationGB();
        String indent = "    ";        
        double cost = 0.0;
        double makespanActualCpuTime = 0.0;
        double length= 0.0;
        int counter = 1;
        int success_counter = 0;
        int failed_counter = 0;

        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet Column=Task=>Length,WFType,Impact # Times of Task=>Actual,Exec,Finish.");//,CloudletOutputSize
        Log.printLine();
        Log.printLine(indent+"Row"+indent + "JOB ID"  +  indent + indent + "CLOUDLET" + indent + indent 
                + "STATUS" + indent
                + "Data CENTER ID" 
                //+ indent + indent + "HOST ID" 
                + indent + "VM ID" + indent + indent+ "CONTAINER ID" + indent + indent
                + "TIME" + indent +  indent +"START TIME" + indent + indent + "FINISH TIME" + indent + "DEPTH" + indent + indent + "Cost");
        
        DecimalFormat dft0 = new DecimalFormat("###.####");
        DecimalFormat dft = new DecimalFormat("####.#####");
        
        for (Job job : list) {
            Log.print(String.format("%6d |",counter++)+indent + job.getCloudletId() + indent + indent);
            if (job.getClassType() == ClassType.STAGE_IN.value) {
                Log.print("STAGE-IN");
            }
            for (Task task : job.getTaskList()) {                
               
              Log.print(task.getCloudletId()+ " ,");
              Log.print(task.getCloudletLength()+ " ,");               
              Log.print(task.getType());                                            
              //Log.print(dft0.format(task.getImpact()));
              
              Log.print("\n"+"\t\t\t ("+dft0.format(task.getActualCPUTime())+ " ,");      
              Log.print("\n"+"\t\t\t"+dft0.format(task.getExecStartTime())+ " ,");      
              Log.print("\n"+"\t\t\t"+dft0.format(task.getTaskFinishTime())+ " )");      
             
            }
            Log.print(indent);
                
            cost += job.getProcessingCost();
            makespanActualCpuTime += job.getActualCPUTime();
            length +=job.getCloudletLength();
            
            if (job.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("     SUCCESS");         
                success_counter++;
                //datacenter.getContainerAllocationPolicy().getContainerVm(job.getContainerId(), job.getUserId()).getHost().getId()
                Log.printLine(indent + indent +indent + job.getResourceId() 
                        //+ indent + indent  + indent + indent + datacenter.getVmAllocationPolicy().getHost(job.getVmId(), job.getUserId()).getId()
                        + indent + indent + indent + job.getVmId()
                        + indent + indent + indent + job.getContainerId()
                        + indent + indent + indent + dft.format(job.getActualCPUTime())
                        + indent + indent + indent + dft.format(job.getExecStartTime()) + indent + indent + indent
                        + dft.format(job.getFinishTime()) + indent + indent + indent + job.getDepth()
                        + indent + indent + indent 
                        + dft.format(job.getProcessingCost()                       
                       
                        ));
                  //Log.printLine();                              
                  /*
                   Log.printLine(datacenter.getContainerAllocationPolicy().getContainerVm(job.getContainerId(), job.getUserId()).getAllocatedMipsForContainer(datacenter.getContainerList().get(job.getContainerId()-1)));
                  Log.printLine(datacenter.getVmAllocationPolicy().getHost(job.getVmId(), job.getUserId()).getBw());
                  Log.printLine(datacenter.getVmAllocationPolicy().getHost(job.getVmId(), job.getUserId()).getMaxAvailableMips());
                  Log.printLine(datacenter.getVmAllocationPolicy().getHost(job.getVmId(), job.getUserId()).getContainerVmBwProvisioner().getUsedBw());
                  Log.printLine(datacenter.getVmAllocationPolicy().getHost(job.getVmId(), job.getUserId()).getContainerVmRamProvisioner().getUsedVmRam());
                  Log.printLine(datacenter.getVmAllocationPolicy().getHost(job.getVmId(), job.getUserId()).getContainerVmScheduler().getTotalAllocatedMipsForContainerVm(datacenter.getVmAllocationPolicy().get(job.getVmId(), job.getUserId()).);
                  */
       
            } else if (job.getCloudletStatus() == Cloudlet.FAILED) {
                Log.print("      FAILED");                
                failed_counter++;
                Log.printLine(indent + indent + job.getResourceId() 
                        + indent + indent  + indent + indent + datacenter.getVmAllocationPolicy().getHost(job.getVmId(), job.getUserId()).getId()
                        + indent + indent + indent + job.getVmId()
                        + indent + indent + indent + job.getContainerId()
                        + indent + indent + indent + dft.format(job.getActualCPUTime())
                        + indent + indent + indent + dft.format(job.getExecStartTime()) + indent + indent + indent
                        + dft.format(job.getFinishTime()) + indent + indent + indent + job.getDepth()
                        + indent + indent + indent + dft.format(job.getProcessingCost()
                        
                        ));
            }
        }    
        Log.printLine();
        Log.printLine("MinTimeBetweenEvents is " + dft.format(CloudSim.getMinTimeBetweenEvents()));
        Log.printLine("Used MaxHeapUtilization/GB is " + dft.format(maxHeapUtilizationGB));
        Log.printLine("----------------------------------------");
        Log.printLine("The length cloudlets is " + dft.format(length));    
        Log.printLine("The total cost is " + dft.format(cost));
        Log.printLine("The total time/cost is " + dft.format(calculateTotalTimeToCost(list)));        
        Log.printLine("----------------------------------------");
        Log.printLine("The makespanActualCpuTime total actual cput time is " + dft.format(calculateMakeSpanBaseOnActualCpuTime(list)));        
        Log.printLine("The makespanActualCpuTime base on datacenter is " + dft.format(calculateMakeSpanBaseOnDC(list)));        
        Log.printLine("The throughput for each cloudlet is " + dft.format(calculateThroughputTotalPerCloudlet(list)));      
        Log.printLine("The average turn around time for each cloudlet is " + dft.format(calculateAverageTurnAroundTimeTotalPerCloudlet(list)));                      
        Log.printLine("The average transfer time for each container is " + dft.format(calculateAverageTransferTimeForContainers(list,listOfContainers)));                    
        Log.printLine("The average response time is " + dft.format(calculateAverageResponseTime(list)));                    
        Log.printLine("The response time base on max&min times to millisecond is " + dft.format(calculateResponseTimeBaseOneMaxMinTimesMillisecond(list)));                    
        Log.printLine("The rate Req/Sec is " + dft.format(calculateRateReqPerSec(list)));                    
        
        //Log.printLine("The average waiting time for each cloudlet is " + dft.format(calculateAverageWaitingTimeTotalPerCloudlet(list)));              
        Log.printLine("----------------------------------------");
        Log.printLine("The total failed counter is " + dft.format(failed_counter));
        Log.printLine("The total success counter is " + dft.format(success_counter));
        Log.printLine("The total Total complete simulation time is " + dft.format(totalSimulationTime));
        
    }   
              
    private  static void chartLineImplement(String chartTitle,String xAxisLabel,String yAxisLabel,DefaultCategoryDataset dataset) {
	     
        LineChart_AWT chart = new LineChart_AWT(
	         chartTitle,
	         xAxisLabel,
                 yAxisLabel
                ,dataset);
	      chart.pack( );              
	      RefineryUtilities.centerFrameOnScreen( chart );              
	      chart.setVisible( true );	               
     } 
    
     private  static void chartPieImplement(String chartTitle,boolean legend,boolean tooltips,boolean noUrl,DefaultPieDataset dataset ) {	     
        PieChart_AWT chart = new PieChart_AWT(
	         chartTitle,                 
	         legend,
                 tooltips,
                 noUrl
                ,dataset);
	      chart.pack( );              
	      RefineryUtilities.centerFrameOnScreen( chart );              
	      chart.setVisible( true );	               
     } 
    
     private  static void chartBarImplement(String chartTitle,String xAxisLabel,String yAxisLabel,boolean legend,boolean tooltips,boolean noUrl,DefaultCategoryDataset dataset) {	           
        BarChart_AWT chart = new BarChart_AWT(
	         chartTitle,         
                 xAxisLabel,
                 yAxisLabel,
	         legend,
                 tooltips,
                 noUrl
                ,dataset);
	      chart.pack( );              
	      RefineryUtilities.centerFrameOnScreen( chart );              
	      chart.setVisible( true );	               
     } 
   
         
    private  static DefaultCategoryDataset  showChartLineResponseTimeBaseOneMaxMinTimes(DefaultCategoryDataset datasetResponseTimeBaseOneMaxMinTimes,List<Job> cloudlets,int wfSize,DecimalFormat dft) {                  
       datasetResponseTimeBaseOneMaxMinTimes.addValue(wfSize,"WFC - (ACO) BrokerType",Double.valueOf(dft.format(calculateResponseTimeBaseOneMaxMinTimesMillisecond(cloudlets))));               
       return datasetResponseTimeBaseOneMaxMinTimes;            
    }
        
    private  static DefaultCategoryDataset  showChartLineTotalTimeToCost(DefaultCategoryDataset datasetTotalTimeToCost,List<Job> cloudlets,int wfSize,DecimalFormat dft) {                                
      datasetTotalTimeToCost.addValue(wfSize,"WFC - (ACO) BrokerType",Double.valueOf(dft.format(calculateTotalTimeToCost(cloudlets))));               
     return datasetTotalTimeToCost;                
    }    
     
    private  static DefaultCategoryDataset  showChartLineThroughputTotalPerCloudlet(DefaultCategoryDataset datasetThroughputTotalPerCloudlet,List<Job> cloudlets,int wfSize,DecimalFormat dft) {                                                                 
     datasetThroughputTotalPerCloudlet.addValue(wfSize,"WFC - (ACO) BrokerType",Double.valueOf(dft.format(calculateThroughputTotalPerCloudlet(cloudlets))));               
     return datasetThroughputTotalPerCloudlet;     
    }
    private  static DefaultCategoryDataset  showChartLineAverageTurnAroundTimeTotalPerCloudlet(DefaultCategoryDataset datasetAverageTurnAroundTimeTotalPerCloudlet,List<Job> cloudlets,int wfSize,DecimalFormat dft) {                                                                 
     datasetAverageTurnAroundTimeTotalPerCloudlet.addValue(wfSize,"WFC - (ACO) BrokerType",Double.valueOf(dft.format(calculateAverageTurnAroundTimeTotalPerCloudlet(cloudlets))));               
     return datasetAverageTurnAroundTimeTotalPerCloudlet;     
    }
    private  static DefaultCategoryDataset  showChartLineAverageWaitingTimeTotalPerCloudlet(DefaultCategoryDataset datasetAverageWaitingTimeTotalPerCloudlet,List<Job> cloudlets,int wfSize,DecimalFormat dft) {                                                                 
     datasetAverageWaitingTimeTotalPerCloudlet.addValue(wfSize,"WFC - (ACO) BrokerType",Double.valueOf(dft.format(calculateAverageWaitingTimeTotalPerCloudlet(cloudlets))));               
     return datasetAverageWaitingTimeTotalPerCloudlet;     
    }
    private  static DefaultCategoryDataset  showChartLineAverageTransferTimeForContainer(DefaultCategoryDataset datasetAverageTransferTimeForContainer,List<Job> cloudlets,List<Container> containers,int wfSize,DecimalFormat dft) {                                                                 
     datasetAverageTransferTimeForContainer.addValue(wfSize,"WFC - (ACO) BrokerType",Double.valueOf(dft.format(calculateAverageTransferTimeForContainers(cloudlets,containers))));               
     return datasetAverageTransferTimeForContainer;     
    }
     private  static DefaultCategoryDataset  showChartLineMakeSpanBaseOnOneDC(DefaultCategoryDataset datasetLineMakeSpanBaseOnDC,List<Job> cloudlets,int wfSize,DecimalFormat dft) {                                                                 
     datasetLineMakeSpanBaseOnDC.addValue(wfSize,"WFC - (ACO) BrokerType",Double.valueOf(dft.format(calculateMakeSpanBaseOnDC(cloudlets))));               
     return datasetLineMakeSpanBaseOnDC;     
    }
    private  static DefaultCategoryDataset  showChartLineMakeSpanBaseOnTotalActualCPUTime(DefaultCategoryDataset datasetMakeSpanBaseOnTotalActualCPUTime,List<Job> cloudlets,int wfSize,DecimalFormat dft) {                                                                 
     datasetMakeSpanBaseOnTotalActualCPUTime.addValue(wfSize,"WFC - (ACO) BrokerType",Double.valueOf(dft.format(calculateMakeSpanBaseOnActualCpuTime(cloudlets))));               
     return datasetMakeSpanBaseOnTotalActualCPUTime;     
    }
    private  static DefaultCategoryDataset  showChartLineAverageResponseTime(DefaultCategoryDataset datasetAverageResponseTime,List<Job> cloudlets,int wfSize,DecimalFormat dft) {                                                                 
     datasetAverageResponseTime.addValue(wfSize,"WFC - (ACO) BrokerType",Double.valueOf(dft.format(calculateAverageResponseTime(cloudlets))));               
     return datasetAverageResponseTime;     
    }
    private  static DefaultCategoryDataset  showChartLineRateReqPerSec(DefaultCategoryDataset datasetRateReqPerSec,List<Job> cloudlets,int wfSize,DecimalFormat dft) {                                                                 
     datasetRateReqPerSec.addValue(wfSize,"WFC - (ACO) BrokerType",Double.valueOf(dft.format(calculateRateReqPerSec(cloudlets))));               
     return datasetRateReqPerSec;     
    }
     
                    
    private  static DefaultCategoryDataset  showBarChartTimes(DefaultCategoryDataset datasetTimes,List<Job> cloudlets,int wfSize,DecimalFormat dft) {                                
     double finishTime = 0;
     double execStartTime = 0;
     double actualCPUTime=0;
     double submissionTime=0;
     double waitingTime=0;
     double wallClockTime=0;
     for (int j=0;j<wfSize;j++){                                
         finishTime += cloudlets.get(j).getFinishTime();
         execStartTime += cloudlets.get(j).getExecStartTime();
         actualCPUTime += cloudlets.get(j).getActualCPUTime();
         submissionTime += cloudlets.get(j).getSubmissionTime();
         waitingTime += cloudlets.get(j).getWaitingTime();
         wallClockTime += cloudlets.get(j).getWallClockTime();
     }          
     datasetTimes.addValue(Double.valueOf(dft.format(finishTime)),"FinishTime("+String.valueOf(dft.format(finishTime))+")",Double.valueOf(dft.format(finishTime)));               
     datasetTimes.addValue(Double.valueOf(dft.format(execStartTime)),"ExecStartTime("+String.valueOf(dft.format(execStartTime))+")",Double.valueOf(dft.format(execStartTime)));               
     datasetTimes.addValue(Double.valueOf(dft.format(actualCPUTime)),"ActualCPUTime("+String.valueOf(dft.format(actualCPUTime))+")",Double.valueOf(dft.format(actualCPUTime)));               
     //datasetTimes.addValue(Double.valueOf(dft.format(submissionTime)),"SubmissionTime("+String.valueOf(dft.format(submissionTime))+")",Double.valueOf(dft.format(submissionTime)));               
     datasetTimes.addValue(Double.valueOf(dft.format(waitingTime)),"AverageWaitingTime("+String.valueOf(dft.format(waitingTime))+")",Double.valueOf(dft.format(waitingTime)));              
     //datasetTimes.addValue(Double.valueOf(dft.format(wallClockTime)),"WallClockTime("+String.valueOf(dft.format(wallClockTime))+")",Double.valueOf(dft.format(wallClockTime)));              
     return datasetTimes;    
    }
       
   private  static DefaultPieDataset  showChartPieTimes(DefaultPieDataset datasetTimes,List<Job> cloudlets,int wfSize,DecimalFormat dft) {                      
     double finishTime = 0;
     double execStartTime = 0;
     double actualCPUTime=0;
     double submissionTime=0;
     double waitingTime=0;
     double wallClockTime=0;
     for (int j=0;j<wfSize;j++){                                
         finishTime += cloudlets.get(j).getFinishTime();
         execStartTime += cloudlets.get(j).getExecStartTime();
         actualCPUTime += cloudlets.get(j).getActualCPUTime();
         submissionTime += cloudlets.get(j).getSubmissionTime();
         waitingTime += cloudlets.get(j).getWaitingTime();
         wallClockTime += cloudlets.get(j).getWallClockTime();
     }          
     datasetTimes.setValue("FinishTime("+String.valueOf(dft.format(finishTime))+")",Double.valueOf(dft.format(finishTime)));               
     datasetTimes.setValue("ExecStartTime("+String.valueOf(dft.format(execStartTime))+")",Double.valueOf(dft.format(execStartTime)));               
     datasetTimes.setValue("Makespan("+String.valueOf(dft.format(actualCPUTime))+")",Double.valueOf(dft.format(actualCPUTime)));               
     datasetTimes.setValue("SubmissionTime("+String.valueOf(dft.format(submissionTime))+")",Double.valueOf(dft.format(submissionTime)));               
     datasetTimes.setValue("AverageWaitingTime("+String.valueOf(dft.format(waitingTime))+")",Double.valueOf(dft.format(waitingTime)));              
     datasetTimes.setValue("WallClockTime("+String.valueOf(dft.format(wallClockTime))+")",Double.valueOf(dft.format(wallClockTime)));              
     return datasetTimes;     
    }
        
     private  static DefaultPieDataset  showChartPieCosts(DefaultPieDataset datasetResponseTime,List<Job> cloudlets,int wfSize,DecimalFormat dft) {                           
        double costTotalPerSec=0;
        double processingCost=0;
        for (int j=0;j<wfSize;j++){                                         
            costTotalPerSec = cloudlets.get(j).getCostPerSec();
            processingCost = cloudlets.get(j).getProcessingCost();
        }          
        datasetResponseTime.setValue("CostTotalPerSec("+String.valueOf(dft.format(costTotalPerSec))+")",Double.valueOf(dft.format(costTotalPerSec)));                    
        datasetResponseTime.setValue("processingCost("+String.valueOf(dft.format(processingCost))+")",Double.valueOf(dft.format(processingCost)));               
        return datasetResponseTime;     
    }
     private  static DefaultPieDataset  showChartPieResources(DefaultPieDataset datasetResources,int k) {                                   
        datasetResources.setValue("Hosts("+String.valueOf(WFCConstants.WFC_NUMBER_HOSTS)+")",(Number)WFCConstants.WFC_NUMBER_HOSTS);               
        datasetResources.setValue("VMs("+String.valueOf(WFCConstants.WFC_NUMBER_VMS)+")",(Number)WFCConstants.WFC_NUMBER_VMS);                    
        datasetResources.setValue("Containers("+String.valueOf(WFCConstants.WFC_NUMBER_CONTAINER)+") On Execution("+String.valueOf(k)+")",(Number)WFCConstants.WFC_NUMBER_CONTAINER);                       
        return datasetResources;     
    }
    
   
    private static Date getTimeForChart(int year, int month, int day, int hour,
            int minute, int second) {
        final java.util.Calendar calendar = java.util.Calendar
                .getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(year, month - 1, day, hour, minute, second);
        final Date result = calendar.getTime();
        return result;
    }
   
    private static double calculateTotalTimeToCost (List <? extends Cloudlet> cloudletList){
           double cost = 0;
            double time = 0;            
             int NoCloudlets = cloudletList.size();
            for (int j=0;j<NoCloudlets-1;j++){                        
                   cost += cloudletList.get(j).getProcessingCost();                        
                   time += cloudletList.get(j).getActualCPUTime();                                                                   
            }                                                  
            return time/cost;     
    }
    private static double calculateThroughputTotalPerCloudlet (List <? extends Cloudlet> cloudletList){
            double maxFT=0.0;
            double throughput=0.0;
            int NoCloudlets = cloudletList.size();
            for(int i=0;i<NoCloudlets-1;i++){
                double currentFT = cloudletList.get(i).getFinishTime();
                if (currentFT > maxFT)
                    maxFT = currentFT;                         
                throughput += NoCloudlets / maxFT;            
             }
            return throughput;
    }
    private static double    calculateAverageTurnAroundTimeTotalPerCloudlet (List <? extends Cloudlet> cloudletList){          
            double totalTime = 0.0;
            double avgTAT=0.0;
            int NoCloudlets = cloudletList.size();
            for(int i=0;i<NoCloudlets-1;i++)            
                totalTime += cloudletList.get(i).getFinishTime();            
             avgTAT = totalTime / NoCloudlets;
            return avgTAT;
    }
     private static double  calculateAverageWaitingTimeTotalPerCloudlet(List <? extends Cloudlet> cloudletList){        
         int NoCloudlets = cloudletList.size();
         double watingTime=0.0;
        for (int i=0; i< NoCloudlets - 1;i++)
            watingTime  += cloudletList.get(i).getWaitingTime();//cloudletList.get(i).getExecStartTime() - cloudletList.get(i).getSubmissionTime();
        return watingTime/NoCloudlets;
     }
     private static double  calculateAverageTransferTimeForContainers(List <? extends Cloudlet> cloudletList,List<? extends Container> containers){        
         int NoCloudlets = cloudletList.size();
         double transferTime=0.0;
         for (int i=0; i< NoCloudlets - 1;i++)
            transferTime += cloudletList.get(i).getCloudletLength()/ containers.get(i).getBw();
         return transferTime/NoCloudlets;
     }
      private static double calculateMakeSpanBaseOnDC(List <? extends Cloudlet> cloudletList) {
        double makespan = 0;
        double[] dcWorkingTime = new double[WFCConstants.WFC_DC_NUM];
        int NoCloudlets = cloudletList.size();
        for (int i=0; i< NoCloudlets - 1;i++){
                int dcId = cloudletList.get(i).getVmId() % WFCConstants.WFC_DC_NUM;
                if (dcWorkingTime[dcId] != 0)
                    --dcWorkingTime[dcId];
                dcWorkingTime[dcId] += cloudletList.get(i).getCostPerSec();
                makespan = Math.max(makespan, dcWorkingTime[dcId]);
             }        
        return makespan;        
    }
     private static double calculateMakeSpanBaseOnActualCpuTime(List <? extends Cloudlet> cloudletList) {
        double makespan = 0;        
        int NoCloudlets = cloudletList.size();
        for (int i=0; i< NoCloudlets - 1;i++){              
                makespan += cloudletList.get(i).getActualCPUTime();
             }        
        return makespan;        
    }
          
    private static double calculateAverageResponseTime(List <? extends Cloudlet> cloudletList) {
        double serveTime = 0;        
        int NoCloudlets = cloudletList.size();
        for (int i=0; i< NoCloudlets - 1;i++){              
                serveTime += cloudletList.get(i).getFinishTime() - cloudletList.get(i).getExecStartTime();
             }        
        return serveTime/NoCloudlets;        
    }
    private static double calculateRateReqPerSec(List <? extends Cloudlet> cloudletList) {
        double serveTime = 0;        
        int NoCloudlets = cloudletList.size();
        for (int i=0; i< NoCloudlets - 1;i++){              
                serveTime += cloudletList.get(i).getFinishTime() - cloudletList.get(i).getExecStartTime();
             }        
        return NoCloudlets/serveTime;        
    }         
     private static double calculateResponseTimeBaseOneMaxMinTimesMillisecond(List <? extends Cloudlet> cloudletList) {                       
         int NoCloudlets = cloudletList.size();
         double max = 0;
         double min = 1000000;

         for (int j=0;j<NoCloudlets-1;j++){
                if(max < cloudletList.get(j).getFinishTime())
                        max = cloudletList.get(j).getFinishTime();
         }

         for (int j=0;j<NoCloudlets-1;j++){
                if(min > cloudletList.get(j).getExecStartTime())
                        min = cloudletList.get(j).getExecStartTime();
         }     
         return (max-min)*60;     
    }     
   
}
