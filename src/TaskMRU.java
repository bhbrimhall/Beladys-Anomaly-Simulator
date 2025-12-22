import java.util.ArrayList;

/**
 * TaskMRU class implements the MRU page replacement algorithm.
 */
public class TaskMRU implements Runnable{
    private final int[] sequence;
    private final int maxMemoryFrames;
    private final int maxPageReference;
    private final int[] pageFaults;
    private  final ArrayList<Integer> pagesInFrames = new ArrayList<>();

    public TaskMRU(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults){
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.maxPageReference = maxPageReference;
        this.pageFaults = pageFaults;
    }

    /**
     * Runs the MRU page replacement algorithm.
     */
    @Override
    public void run() {
         for (int i = 0; i < sequence.length; i++) {

            if (pagesInFrames.contains(sequence[i])) {
                pagesInFrames.remove(Integer.valueOf(sequence[i])); 
                pagesInFrames.add(sequence[i]); 
            }else if (pagesInFrames.size() == maxMemoryFrames) { 
                pagesInFrames.remove(maxMemoryFrames - 1); 
                pagesInFrames.add(sequence[i]); 
                pageFaults[maxMemoryFrames]++;
            } else {
                pagesInFrames.add(sequence[i]); 
                pageFaults[maxMemoryFrames]++;
            }
        }
    }
    
}