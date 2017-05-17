package server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XMLscanner {
  private final int queueSize=1000;
  private final int numbrOfThreads=1000;

  private BlockingQueue<String> columnsToProcess = new ArrayBlockingQueue<>(queueSize);
  private BlockingQueue<String[]> columnDatatypes = new ArrayBlockingQueue<>(queueSize);

  Map<String, String> startScan(XMLhandler xml) throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(numbrOfThreads);
    xml.getColumns().forEach(col -> columnsToProcess.add(col));
    for (int i = 0; i < xml.getColumns().size(); i++) {
      Runnable worker = new WorkerThread(columnsToProcess, columnDatatypes, xml);
      executor.execute(worker);
    }
    executor.shutdown();

    Map<String, String>  result = new HashMap<>();

    for (int i = 0; i < xml.getColumns().size(); i++) {
      String[] data = columnDatatypes.take();
      result.put(data[0], data[1]);
    }

    return result;
  }
}
