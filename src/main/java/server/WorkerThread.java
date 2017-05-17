package server;

import java.util.concurrent.BlockingQueue;

public class WorkerThread implements Runnable{
  private BlockingQueue<String> columnsToProcess;
  private BlockingQueue<String[]> dataTypes;
  private final XMLhandler xml;

  WorkerThread(BlockingQueue<String> columnsToProcess, BlockingQueue<String[]> dataTypes, XMLhandler xml) {
    this.columnsToProcess = columnsToProcess;
    this.dataTypes = dataTypes;
    this.xml = xml;
  }

  @Override
  public void run() {
    String currentColumn = columnsToProcess.poll();

    String dataType = xml.dataTypeofColumn(currentColumn);
    try {
      dataTypes.put(new String[]{currentColumn, dataType});
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
