import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This program simulates and compares the performance of three page replacement algorithms:
 * FIFO (First-In-First-Out), LRU (Least Recently Used), and MRU (Most Recently Used).
 * It generates random page reference sequences and measures the number of page faults.
 * It also detects and reports instances of Belady's Anomaly for each algorithm.
 * @author Brayden Brimhall <a02407835@usu.edu>
 */
public class Assign5 {

    static final int MAX_PAGE_REFERENCE = 250;
    static final int NUM_SIMULATIONS = 1000;
    
    public static void main(String[] args) {

        int numFIFOWins = 0;
        int numLRUWins = 0;
        int numMRUWins = 0;

        ArrayList<String> beladysAnomaliesFIFO = new ArrayList<>();
        ArrayList<String> beladysAnomaliesLRU = new ArrayList<>();
        ArrayList<String> beladysAnomaliesMRU = new ArrayList<>();

        int maxDeltaFIFO = 0;
        int maxDeltaLRU = 0;
        int maxDeltaMRU = 0;
        
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < NUM_SIMULATIONS; i++) {

            int[] sequence = new int[1000];
            Random random = new Random();

            for (int j = 0; j < 1000; j++) {
                int value = random.nextInt(MAX_PAGE_REFERENCE) + 1;
                sequence[j] = value;
            }

            int[] pageFaultsFIFO = new int[101];
            int[] pageFaultsLRU = new int[101];
            int[] pageFaultsMRU = new int[101];

            ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for (int j = 1; j <= 100; j++){
                Runnable taskFIFO = new TaskFIFO(sequence, j, MAX_PAGE_REFERENCE, pageFaultsFIFO);
                threadPool.execute(taskFIFO);
                Runnable taskLRU = new TaskLRU(sequence, j, MAX_PAGE_REFERENCE, pageFaultsLRU);
                threadPool.execute(taskLRU);
                Runnable taskMRU = new TaskMRU(sequence, j, MAX_PAGE_REFERENCE, pageFaultsMRU);
                threadPool.execute(taskMRU);
            }

            threadPool.shutdown();

            try {
                threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (Exception ex) {
                System.out.println("Error in waiting for shutdown");
            }

            for (int j = 2; j <= 100; j++) {

                int pageFaults1 = pageFaultsFIFO[j - 1];
                int pageFaults2 = pageFaultsFIFO[j];

                if (pageFaults1 < pageFaults2) {
                    beladysAnomaliesFIFO.add(String.format(
                        "Anomaly detected in simulation #%03d - %d PF's @ %d frames vs. %d PF's @ %d frames (Δ%d)",
                        i, pageFaults1, (j - 1), pageFaults2, j, (pageFaults2 - pageFaults1)
                    ));
                    if (pageFaults2 - pageFaults1 > maxDeltaFIFO) {
                        maxDeltaFIFO = pageFaults2 - pageFaults1;
                    }
                }
            }

            for (int j = 2; j <= 100; j++) {

                int pageFaults1 = pageFaultsLRU[j - 1];
                int pageFaults2 = pageFaultsLRU[j];

                if (pageFaults1 < pageFaults2) {
                    beladysAnomaliesLRU.add(String.format(
                        "Anomaly detected in simulation #%03d - %d PF's @ %d frames vs. %d PF's @ %d frames (Δ%d)",
                        i, pageFaults1, (j - 1), pageFaults2, j, (pageFaults2 - pageFaults1)
                    ));
                    if (pageFaults2 - pageFaults1 > maxDeltaLRU) {
                        maxDeltaLRU = pageFaults2 - pageFaults1;
                    }
                }
            }

            for (int j = 2; j <= 100; j++) {

                int pageFaults1 = pageFaultsMRU[j - 1];
                int pageFaults2 = pageFaultsMRU[j];

                if (pageFaults1 < pageFaults2) {
                    beladysAnomaliesMRU.add(String.format(
                        "Anomaly detected in simulation #%03d - %d PF's @ %d frames vs. %d PF's @ %d frames (Δ%d)",
                        i, pageFaults1, (j - 1), pageFaults2, j, (pageFaults2 - pageFaults1)
                    ));
                    if (pageFaults2 - pageFaults1 > maxDeltaMRU) {
                        maxDeltaMRU = pageFaults2 - pageFaults1;
                    }
                }
            }

            for (int j = 1; j <= 100; j++) {
                if (pageFaultsFIFO[j] < pageFaultsLRU[j] && pageFaultsFIFO[j] < pageFaultsMRU[j]) {
                    numFIFOWins++;
                } else if (pageFaultsLRU[j] < pageFaultsFIFO[j] && pageFaultsLRU[j] < pageFaultsMRU[j]) {
                    numLRUWins++;
                } else if (pageFaultsMRU[j] < pageFaultsFIFO[j] && pageFaultsMRU[j] < pageFaultsLRU[j]) {
                    numMRUWins++;
                } else if (pageFaultsFIFO[j] == pageFaultsLRU[j] && pageFaultsFIFO[j] < pageFaultsMRU[j]) {
                    numFIFOWins++;
                    numLRUWins++;
                } else if (pageFaultsFIFO[j] == pageFaultsMRU[j] && pageFaultsFIFO[j] < pageFaultsLRU[j]) {
                    numFIFOWins++;
                    numMRUWins++;
                } else if (pageFaultsLRU[j] == pageFaultsMRU[j] && pageFaultsLRU[j] < pageFaultsFIFO[j]) {
                    numLRUWins++;
                    numMRUWins++;
                } else {
                    numFIFOWins++;
                    numLRUWins++;
                    numMRUWins++;
                }
            }

        }

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Simulation took " + duration + " ms\n");

        System.out.println("FIFO min PF: " + numFIFOWins);
        System.out.println("LRU min PF: " + numLRUWins);
        System.out.println("MRU min PF: " + numMRUWins);

        System.out.println("\nBelady's Anomaly Report for FIFO:");
        for (String s : beladysAnomaliesFIFO) {
            System.out.println("        " + s);
        }
        System.out.println("    Anomaly detected "+ beladysAnomaliesFIFO.size() + " times in "+ NUM_SIMULATIONS +" simulations with a max delta of " + maxDeltaFIFO);
        
        System.out.println("\nBelady's Anomaly Report for LRU:");
        for (String s : beladysAnomaliesLRU) {
            System.out.println("        " + s);
        }
        System.out.println("    Anomaly detected "+ beladysAnomaliesLRU.size() + " times in "+ NUM_SIMULATIONS +" simulations with a max delta of " + maxDeltaLRU);
       
        System.out.println("\nBelady's Anomaly Report for MRU:");
        for (String s : beladysAnomaliesMRU) {
            System.out.println("        " + s);
        }
        System.out.println("    Anomaly detected "+ beladysAnomaliesMRU.size() + " times in "+ NUM_SIMULATIONS +" simulations with a max delta of " + maxDeltaMRU);
    }
}