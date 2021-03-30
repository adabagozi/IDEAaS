package it.unibs.bodai.ideaas.data_acquisition.implementation;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.timing.TimingDataAcquisition;

public class DataAcquisitionConsumer  implements Runnable{
	private IDAO dao;

  private final BlockingQueue<DataAcquisitionPair> sharedQueue;
  public DataAcquisitionConsumer (BlockingQueue<DataAcquisitionPair> sharedQueue) throws IOException {
	  this.dao = DAOFactory.getDAOFactory().getDAO();
	  this.sharedQueue = sharedQueue;
      
  }

  @Override
  public void run() {
      while(true){
          try {
        	  	long startTime = System.currentTimeMillis();
            DataAcquisitionPair dataAcquisitionPair = sharedQueue.take();
            boolean inserted = this.dao.insertMeasuresList(dataAcquisitionPair.getDocumentsToInsert());
            if (!inserted) { System.out.println("Errore inserimento insieme di file");}
            
            this.dao.insertDataAcquisitionTiming(new TimingDataAcquisition(dataAcquisitionPair.getNumFiles(), dataAcquisitionPair.getTransformationTime(), System.currentTimeMillis() - startTime, dataAcquisitionPair.getDescription(), 0));
            
          } catch (InterruptedException ex) {
              Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
  }

}