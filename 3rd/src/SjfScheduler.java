import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * CloudSim experiment using Shortest Job First (SJF), a policy not supplied by
 * the stock broker. Cloudlets are sorted by increasing length before they are
 * submitted to a single space-shared VM, ensuring shortest jobs run first.
 */
public final class SjfScheduler {
    private SjfScheduler() { }

    public static void main(String[] args) {
        Log.printLine("Starting CloudSim SJF scheduling experiment...");
        CloudSim.init(1, Calendar.getInstance(), false);

        createDatacenter("Datacenter_0");
        DatacenterBroker broker;
        try {
            broker = new DatacenterBroker("SJF_Broker");
        } catch (Exception exception) {
            throw new IllegalStateException("Could not create the broker", exception);
        }

        int brokerId = broker.getId();
        List<Vm> vms = new ArrayList<Vm>();
        vms.add(new Vm(0, brokerId, 1000, 1, 512, 1000, 10000, "Xen",
                new CloudletSchedulerSpaceShared()));
        broker.submitVmList(vms);

        List<Cloudlet> jobs = createJobs(brokerId);
        jobs.sort(Comparator.comparingLong(Cloudlet::getCloudletLength));
        printSubmissionOrder(jobs);
        broker.submitCloudletList(jobs);

        CloudSim.startSimulation();
        List<Cloudlet> completedJobs = broker.getCloudletReceivedList();
        CloudSim.stopSimulation();
        printResults(completedJobs);
        Log.printLine("SJF scheduling experiment finished.");
    }

    private static List<Cloudlet> createJobs(int brokerId) {
        long[] lengths = {4000, 1000, 3000, 2000, 5000};
        List<Cloudlet> jobs = new ArrayList<Cloudlet>();
        UtilizationModel fullUtilization = new UtilizationModelFull();
        for (int id = 0; id < lengths.length; id++) {
            Cloudlet job = new Cloudlet(id, lengths[id], 1, 300, 300,
                    fullUtilization, fullUtilization, fullUtilization);
            job.setUserId(brokerId);
            jobs.add(job);
        }
        return jobs;
    }

    private static Datacenter createDatacenter(String name) {
        List<Pe> processingElements = new ArrayList<Pe>();
        processingElements.add(new Pe(0, new PeProvisionerSimple(1000)));

        List<Host> hosts = new ArrayList<Host>();
        hosts.add(new Host(0, new RamProvisionerSimple(2048),
                new BwProvisionerSimple(10000), 1_000_000, processingElements,
                new org.cloudbus.cloudsim.VmSchedulerTimeShared(processingElements)));

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                "x86", "Linux", "Xen", hosts, 10.0, 3.0, 0.05, 0.001, 0.0);
        try {
            return new Datacenter(name, characteristics,
                    new VmAllocationPolicySimple(hosts), new LinkedList<Storage>(), 0);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not create the datacenter", exception);
        }
    }

    private static void printSubmissionOrder(List<Cloudlet> jobs) {
        Log.printLine("SJF submission order (job ID : length):");
        for (Cloudlet job : jobs) {
            Log.printLine("  " + job.getCloudletId() + " : " + job.getCloudletLength());
        }
    }

    private static void printResults(List<Cloudlet> jobs) {
        DecimalFormat format = new DecimalFormat("#.##");
        Log.printLine("\nCompleted jobs:");
        Log.printLine("ID\tSTATUS\tDATACENTER\tVM\tSTART\tFINISH");
        for (Cloudlet job : jobs) {
            Log.print(job.getCloudletId() + "\t");
            if (job.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.printLine("SUCCESS\t" + job.getResourceId() + "\t\t" + job.getVmId()
                        + "\t" + format.format(job.getExecStartTime())
                        + "\t" + format.format(job.getFinishTime()));
            }
        }
    }
}
