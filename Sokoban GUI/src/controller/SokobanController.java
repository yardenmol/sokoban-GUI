package controller;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import controller.commands.AddRecordCommand;
import controller.commands.AddUserCommand;
import controller.commands.DisplayCLICommand;
import controller.commands.DisplayGUICommand;
import controller.commands.ErrorCommand;
import controller.commands.ExitCommand;
import controller.commands.ICommand;
import controller.commands.LoadLevelCommand;
import controller.commands.MoveCommand;
import controller.commands.QueryCommand;
import controller.commands.SaveLevelCommand;
import controller.commands.ShowRecordsCommand;
import controller.server.MyServer;
import controller.server.SokobanClientHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.IModel;
import view.IView;

public class SokobanController implements Observer{
	
	private IView ui;
	private IModel model;
	private Controller controller;
	private SokobanClientHandler clientHandler;
	private MyServer theServer;
	
	HashMap<String, ICommand> commandsCreator;
	private StringProperty countSteps;
	
	public SokobanController(IView ui,IModel model) {
		
		this.ui=ui;
		this.model=model;
		this.controller=new Controller();
		this.countSteps=new SimpleStringProperty();
		
		initcommandsCreator();
		
		this.controller.start();
		this.ui.createBindSteps(this.countSteps);
	}
	
	public SokobanController(IView ui,IModel model,SokobanClientHandler clientHandler,int port) {	
		
		this.ui=ui;
		this.model=model;
		this.controller=new Controller();
		this.countSteps=new SimpleStringProperty();
		this.clientHandler=clientHandler;
		
		this.theServer=new MyServer(port, this.clientHandler);
		this.theServer.start();
		
		initcommandsCreator();
		
		this.controller.start();
		this.ui.createBindSteps(this.countSteps);
	}
	
	public IView getUi() {
		return ui;
	}

	public void setUi(IView ui) {
		this.ui = ui;
	}

	public IModel getModel() {
		return model;
	}

	public void setModel(IModel model) {
		this.model = model;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	private void initcommandsCreator(){
		
		this.commandsCreator= new HashMap<String,ICommand>();
		this.commandsCreator.put("LOAD", new LoadLevelCommand(this.model));
		this.commandsCreator.put("DISPLAY", new DisplayCLICommand(this.model, this.clientHandler));//separate to 2
		this.commandsCreator.put("MOVE", new MoveCommand(this.model,this.countSteps));
		this.commandsCreator.put("SAVE", new SaveLevelCommand(this.model));
		this.commandsCreator.put("EXIT", new ExitCommand(this.controller, this.theServer));
		this.commandsCreator.put("CHANGED", new DisplayGUICommand(this.model,this.ui));
		this.commandsCreator.put("ERROR", new ErrorCommand(this.ui,this.clientHandler));
		this.commandsCreator.put("QUERY", new QueryCommand(this.model));
		this.commandsCreator.put("SHOWQUERYRESULTS", new ShowRecordsCommand(this.ui, this.model) );
		this.commandsCreator.put("ADDUSER", new AddUserCommand(this.model));
		this.commandsCreator.put("ADDRECORD", new AddRecordCommand(this.model));
	}
	
	private String[] objectToStringArray(Object obj){
		
		String [] result;
		String inputCommand=(String)obj;
		
		result=inputCommand.split(" ", 2);
		result[0]=result[0].toUpperCase();
		
		return result;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		
		String[]params=objectToStringArray(arg);
		ICommand command=this.commandsCreator.get(params[0]);
		if(command==null){
			this.ui.displayError("Invalid command");
	      	if(this.clientHandler!=null)
	      		this.clientHandler.insertToQueue("Invalid command");
		}
		else{
			if(params.length>1)
				command.setParams(params[1]);
			else
				command.setParams(null);
			try {
				this.controller.insertCommand(command);
			} catch (InterruptedException e) {
				this.ui.displayError("Couldn't entred command into the queue");
		      	if(this.clientHandler!=null)
		      		this.clientHandler.insertToQueue("Couldn't entred command into the queue");
			}
		}
		
	}

}
