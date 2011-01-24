package model.algorithms.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DCTWorkerpool extends Observable implements Observer {
	public static final int BLOCK_SIZE = 16;

	private ConcurrentLinkedQueue<Integer> blockNumbers;
	private List<Block> dct_blocks;
	private HashMap<DCTWorker, Thread> workerMap = new HashMap<DCTWorker, Thread>();
	private boolean aborted;

	private int width;
	private int[] image;
	private int height;
	private float quality;
	private int parallelTasks;

	public DCTWorkerpool(int[] input, int width, int height, float quality,
			int paralellTasks, Observer observer) {
		this.addObserver(observer);
		this.aborted = false;

		this.width = width;
		this.image = input;
		this.height = height;
		this.quality = quality;
		this.parallelTasks = paralellTasks;

		dct_blocks = new ArrayList<Block>((width - BLOCK_SIZE + 1)
				* (height - BLOCK_SIZE + 1));

		/*
		 * Create the queue of blocks that should be processed.
		 */
		blockNumbers = new ConcurrentLinkedQueue<Integer>();
		for (int i = 0; i < (width - BLOCK_SIZE + 1)
				* (height - BLOCK_SIZE + 1); i++) {
			blockNumbers.add(i);
		}
	}

	public void start() {
		/*
		 * Create the threads...
		 */
		for (int i = 0; i < this.parallelTasks; i++) {
			DCTWorker worker = new DCTWorker(blockNumbers, image, width,
					quality);
			worker.addObserver(this);

			/*
			 * Create the thread
			 */
			Thread wThread = new Thread(worker);
			wThread.start();

			/*
			 * Add to the map, so we can later on stop and join to the threads.
			 */
			workerMap.put(worker, wThread);
		}

		for (Thread t : workerMap.values()) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void abort() {
		for (Entry<DCTWorker, Thread> entry : workerMap.entrySet()) {
			try {
				entry.getKey().abort();
				entry.getValue().join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		aborted = true;
	}

	public boolean getAborted() {
		return aborted;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg != null && arg instanceof Block) {
			synchronized (dct_blocks) {
				dct_blocks.add((Block) arg);
			}
			if (dct_blocks.size() % width == 0) {
				setChanged();
				notifyObservers(new Float((float) dct_blocks.size()
						/ (dct_blocks.size() + blockNumbers.size())));
			}
		}
	}

	public List<Block> getResult() {
		return dct_blocks;
	}
}
