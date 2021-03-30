package dao;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.logging.Level;

import org.junit.Before;
import org.junit.Test;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.Measure;
public class TestReadMongo
{
	private Collection<Measure> tempCollection;
	private IDAO dao;
	
	@Before
	public void setup() {
		try {
			this.dao = DAOFactory.getDAOFactory().getDAO();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
	public void extractMeasures()
	{
		LocalDateTime datetime = LocalDateTime.of(2015, 11, 2, 0, 0);
		//PARTENDO DALLE 15 CON 1 ORA E POI METTENDO DALLE 0 PER 6 ORE, CI METTE UN SESTO DEL TEMPO. PROBABILMENTE
		//DOVUTO A QUALCHE MECCANISMO INTERNO DI MONGO..
		tempCollection = dao.readMeasures("2015_11", "-1", "-1", "-1", "101143", "Unit 1.0", datetime, "hours", 6);
	}
}
