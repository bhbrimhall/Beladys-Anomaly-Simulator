import java.util.LinkedList;
import java.util.Queue;

/**
 * TaskFIFO class implements the FIFO page replacement algorithm.
 */
public class TaskFIFO implements Runnable{
    private final int[] sequence;
    private final int maxMemoryFrames;
    private final int maxPageReference;
    private final int[] pageFaults;
    private final Queue<Integer> pagesInFrames = new LinkedList<>();


    public TaskFIFO(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults){
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.maxPageReference = maxPageReference;
        this.pageFaults = pageFaults;
    }

    /**
     * Runs the FIFO page replacement algorithm.
     */
    @Override
    public void run() {
        for (int i = 0; i < sequence.length; i++) {

            if (pagesInFrames.contains(sequence[i])) {
                
            }else if (pagesInFrames.size() == maxMemoryFrames) { 
                pagesInFrames.poll(); 
                pagesInFrames.add(sequence[i]); 
                pageFaults[maxMemoryFrames]++;
            } else {
                pagesInFrames.add(sequence[i]); 
                pageFaults[maxMemoryFrames]++;
            }
        }
    }
    
}