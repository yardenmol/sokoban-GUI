package model.db;

import java.sql.Time;

import commons.Level;


public class test {

	public static void main(String[] args) {
		
		Manager m=Manager.getInstance();
		User u=new User("Tomer124");
		m.add(u);
		
		Level l=new Level();
		l.setLevelID("level2");
		m.add(l);
		Record r=new Record(l.getLevelID(), u.getName(), 60, new Time (System.currentTimeMillis()));
		//Record rec = new Record(l.getLevelID(),u.getId(),50, new Time(0) );
		m.add(r);
		
		m.getAllRecords();
		
		/*Record r=new Record("level1", "yardi", 5, new Time(0));
		RecordDBManager rm=new RecordDBManager();
		rm.add(r);*/
	}

}