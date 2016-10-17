package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


public class Main extends Application {

	protected Button preGenbtn, fileUpdatebtn, resetbtn, processOrderbtn;
	protected ComboBox<String> selectLog, selectEcon;
	protected TextArea previewtxa;
	protected MainEventHandler mainEvent = new MainEventHandler();
	protected Connection con; //connection variable for sql
	protected File logFiles, econFiles;
	protected File[] logList, econList;

	@Override
	public void start(Stage primaryStage) {
		try {
			Pane root = buildMaster();
			Scene scene = new Scene(root,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	private Pane buildMaster()
	{
		VBox master = new VBox();
		master.setPadding(new Insets(20, 20, 20, 20));
		Pane top = buildTop();
		top.setPadding(new Insets(20, 20, 20, 20));
		Pane bottom = buildBottom();
		bottom.setPadding(new Insets(20, 20, 20, 20));
		master.getChildren().addAll(top, bottom);

		return master;
	}

	private Pane buildTop()
	{
		GridPane top = new GridPane();
		top.setPadding(new Insets(20, 20, 20, 20));
		top.setVgap(30);
		top.setHgap(8);

		selectLog = new ComboBox<String>();
		selectLog.getItems().add("--- Select Logs File ---");
		selectLog.getSelectionModel().selectFirst();
		selectEcon = new ComboBox<String>();
		selectEcon.getItems().add("--- Select Econ File ---");
		selectEcon.getSelectionModel().selectFirst();

		preGenbtn = new Button();
		preGenbtn.setText("Generate Preview");
		preGenbtn.setOnAction(mainEvent);

		fileUpdatebtn = new Button();
		fileUpdatebtn.setText("Update File System");
		fileUpdatebtn.setOnAction(mainEvent);

		resetbtn = new Button();
		resetbtn.setText("Reset Cut");
		resetbtn.setOnAction(mainEvent);

		processOrderbtn = new Button();
		processOrderbtn.setText("Process Order");
		processOrderbtn.setOnAction(mainEvent);

		GridPane.setConstraints(preGenbtn,16 , 1);
		GridPane.setConstraints(fileUpdatebtn, 32, 0);
		GridPane.setConstraints(resetbtn, 16 , 0);
		GridPane.setConstraints(processOrderbtn, 32, 1);
		GridPane.setConstraints(selectLog, 0 , 0);
		GridPane.setConstraints(selectEcon, 0, 1);

		top.getChildren().addAll(preGenbtn, fileUpdatebtn, resetbtn, processOrderbtn, selectLog, selectEcon);

		return top;
	}

	private Pane buildBottom()
	{
		HBox bottom = new HBox();
		bottom.setPadding(new Insets(20, 20, 20, 20));

		previewtxa = new TextArea();
	 	previewtxa.setPrefRowCount(100);
	 	previewtxa.setPrefColumnCount(100);

		bottom.getChildren().add(previewtxa);

		return bottom;
	}

	private  class MainEventHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {

			if(event.getSource().equals(fileUpdatebtn))
			{
				selectLog.getItems().clear();
				selectLog.getItems().add("--- Select Log File ---");
				selectLog.getSelectionModel().selectFirst();
				logFiles = new File("../NaturalLogsOps/src/LogFiles");
				logList = logFiles.listFiles();
				String text = "Log files that were added: \n";
				for(File file : logList)
				{
					if(file.isFile())
					{
						selectLog.getItems().add(file.getName());
						text += file.getName() + "\n";
					}
				}
				selectEcon.getItems().clear();
				selectEcon.getItems().add("--- Select Econ File ---");
				selectEcon.getSelectionModel().selectFirst();
				econFiles = new File("../NaturalLogsOps/src/EconFiles");
				econList = econFiles.listFiles();
				text += "\nEconomic files that were added: \n";
				for(File file : econList)
				{
					if(file.isFile())
					{
						selectEcon.getItems().add(file.getName());
						text += file.getName() + "\n";
					}
				}
				previewtxa.setText(text);
			}
			else if(event.getSource().equals(preGenbtn))
			{
				File log = new File("");
				File econ = new File("");
				for(File file : logList)
				{
					if(file.getName().equals(selectLog.getSelectionModel().getSelectedItem()))
					{
						log = file;
						break;
					}
				}
				for(File file : econList)
				{
					if(file.getName().equals(selectEcon.getSelectionModel().getSelectedItem()))
					{
						econ = file;
						break;
					}
				}

				try
				{
					optimization(log, econ);
				}
				catch(FileNotFoundException e)
				{
					previewtxa.setText("File not found");
					//Nothing implemented if file is not found.
				}
			}
			else if(event.getSource().equals(resetbtn))
			{
				previewtxa.setText("");
			}
			else if(event.getSource().equals(processOrderbtn))
			{
				//Need jdbc connector to be established for MySQL injection.
				try {
					Class.forName("com.mysql.jdbc.Driver");
					con = DriverManager.getConnection("jdbc:mysql://localhost:3306/warehouse?useSSL=false","root", "root");
				} catch(Exception e) {
					previewtxa.setText("JDBC not implemented. ");
					e.printStackTrace();
				}
				//Need to create MySQL injections for adding a non-existent board to the WoodStack table.
				/*Need to create Update statements for values and quantities of boards to be added from generate preview
				  to the WoodStack where applicable*/
				//May need to add a flux capacitor to speed up processing speed of optimization.
			}
		}
	}

	private void optimization(File logs, File econ) throws FileNotFoundException
	{
		List<Log> loglist = new ArrayList<>();
		List<Lumber> lumberlist = new ArrayList<>();

		String text = "";

		double ttlOne = 0.0;
		double ttlTwo = 0.0;
		double ttlThree = 0.0;
		double ttlScrap = 0.0;
		double ttlVal = 0.0;

		Lumber lOne = new Lumber(1,1,1,0);
		Lumber lTwo = new Lumber(1,1,1,0);
		Lumber lThree = new Lumber(1,1,1,0);

		Scanner scan = new Scanner(logs);

		while(scan.hasNext()){
			String height = scan.next();
			String width = scan.next();
			String length = scan.next();


			double h = Double.parseDouble(height);
			double w = Double.parseDouble(width);
			double l = Double.parseDouble(length);


			Log log = new Log(h,w,l);
			loglist.add(log);

		}

		scan.close();

		scan = new Scanner(econ);

		while(scan.hasNext()){
			String height = scan.next();
			String width = scan.next();
			String length = scan.next();
			String value = scan.next();


			double h = Double.parseDouble(height);
			double w = Double.parseDouble(width);
			double l = Double.parseDouble(length);
			double v = Double.parseDouble(value);


			Lumber lumber = new Lumber(h,w,l,v);
			lumberlist.add(lumber);
		}

		scan.close();

		for(Log l: loglist)
		{
			//We need to implement a method for if the file only contains one type of lumber.
			lOne = lumberlist.get(0);
			lTwo = lumberlist.get(1);
			lThree = lumberlist.get(2);
			Lumber lScrap = lumberlist.get(3);
			Lumber[] priceOrder = new Lumber[3];

			double scrap = 0.0;

			double priceOne = lOne.getValue() / lOne.calculateArea() ;
			double priceTwo = lTwo.getValue() / lTwo.calculateArea() ;
			double priceThree = lThree.getValue() / lThree.calculateArea() ;

			if(priceOne >= priceTwo && priceOne >= priceThree)
			{
				priceOrder[0] = lOne;
				if(priceTwo >= priceThree)
				{
					priceOrder[1] = lTwo;
					priceOrder[2] = lThree;
				}
				else
				{
					priceOrder[1] = lThree;
					priceOrder[2] = lTwo;
				}
			}
			else if(priceTwo >= priceOne && priceTwo >= priceThree)
			{
				priceOrder[0] = lTwo;
				if(priceOne >= priceThree)
				{
					priceOrder[1] = lOne;
					priceOrder[2] = lThree;
				}
				else
				{
					priceOrder[1] = lThree;
					priceOrder[2] = lOne;
				}
			}
			else
			{
				priceOrder[0] = lThree;
				if(priceTwo >= priceOne)
				{
					priceOrder[1] = lTwo;
					priceOrder[2] = lOne;
				}
				else
				{
					priceOrder[1] = lOne;
					priceOrder[2] = lTwo;
				}
			}
			lOne = priceOrder[0];
			lTwo = priceOrder[1];
			lThree = priceOrder[2];

			int multi = (int) (l.getLength() / (int) lOne.getLength());

			double scrapInches = l.getHeight() * l.getWidth() * (l.getLength() - lOne.getLength() * multi);
			scrap += scrapInches / 1728;	// 12" x 12" x 12" = 1728

			double maxHeight = l.getHeight() / lOne.getHeight();
			double maxWidth = l.getWidth() / lOne.getWidth();
			int lOneCount = (int) maxHeight * (int) maxWidth * multi;
			double[] boards = new double[3];

			double heightLeft = l.getHeight() - lOne.getHeight() * maxHeight;
			double widthLeft = l.getWidth() - lOne.getWidth() * maxWidth;

			if(heightLeft > widthLeft)
			{
				Log h = new Log(heightLeft, l.getWidth(), 0.0);
				Log w = new Log(l.getHeight() - heightLeft, widthLeft, 0.0 );
				boards = Optimization(h, w, lTwo, lThree, boards);
			}
			else
			{
				Log h = new Log(heightLeft, l.getWidth() - widthLeft, 0.0);
				Log w = new Log(l.getHeight(), widthLeft, 0.0);
				boards = Optimization(h, w, lTwo, lThree, boards);
			}

			int lTwoCount = (int) boards[0] * multi;
			int lThreeCount = (int) boards[1] * multi;
			scrap += boards[2] * multi;

			double lOneValue = lOneCount * lOne.getValue();
			double lTwoValue = lTwoCount * lOne.getValue();
			double lThreeValue = lThreeCount * lOne.getValue();
			double scrapValue = scrap * lScrap.getValue();

			ttlOne += lOneCount;
			ttlTwo += lTwoCount;
			ttlThree += lThreeCount;
			ttlScrap += scrap;
			ttlVal += lOneValue + lTwoValue + lThreeValue + scrapValue;

			text += "" + lOne.getHeight() + "x" + lOne.getWidth() + "x" + lOne.getLength() +
					": " + lOneCount + " $" + lOneValue + "\n" + lTwo.getHeight() + "x" + lTwo.getWidth() + "x" + lTwo.getLength() +
					": " + lTwoCount + " $" + lTwoValue + "\n" + lThree.getHeight() + "x" + lThree.getWidth() + "x" + lThree.getLength() +
					": " + lThreeCount + " $" + lThreeValue + "\n" + "Scrap: " + scrap + " $" + scrapValue + "\n\n";
		}
		text += "Total number of " + lOne.getHeight() + "x" + lOne.getWidth() + "x" + lOne.getLength() + ": " + ttlOne + "\n" +
				"Total number of " + lTwo.getHeight() + "x" + lTwo.getWidth() + "x" + lTwo.getLength() + ": " + ttlTwo + "\n" +
				"Total number of " + lThree.getHeight() + "x" + lThree.getWidth() + "x" + lThree.getLength() + ": " + ttlThree + "\n" +
				"Total number of scrap: " + ttlScrap + "\n" + "Total value: $" + ttlVal;
		previewtxa.setText(text);
	}

	private double[] Optimization(Log h, Log w, Lumber one, Lumber two, double[] boards)
	{
		int result;
		if(one.getHeight() < h.getHeight())
		{
			if(one.getWidth() < h.getWidth())
			{
				double maxHeight = h.getHeight() / one.getHeight();
				double maxWidth = h.getWidth() / one.getWidth();
				result = (int) maxHeight * (int) maxWidth;
				boards[0] = (double)result;

				double heightLeft = h.getHeight() - one.getHeight() * maxHeight;
				double widthLeft = h.getWidth() - one.getWidth() * maxWidth;

				if(heightLeft > widthLeft)
				{
					Log hTwo = new Log(heightLeft, h.getWidth(), 0.0);
					Log wTwo = new Log(h.getHeight() - heightLeft, widthLeft, 0.0 );
					boards = Optimization(hTwo, wTwo, two, boards);
				}
				else
				{
					Log hTwo = new Log(heightLeft, h.getWidth() - widthLeft, 0.0);
					Log wTwo = new Log(h.getHeight(), widthLeft, 0.0);
					boards = Optimization(hTwo, wTwo, two, boards);
				}
			}
		}
		else if(two.getHeight() < h.getHeight())
		{
			if(two.getWidth() < h.getWidth())
			{
				double maxHeight = h.getHeight() / two.getHeight();
				double maxWidth = h.getWidth() / two.getWidth();
				boards[0] = 0.0;
				result = (int) maxHeight * (int) maxWidth;
				boards[1] = (double)result;

				double heightLeft = h.getHeight() - two.getHeight() * maxHeight;
				double widthLeft = h.getWidth() - two.getWidth() * maxWidth;

				boards[2] = heightLeft * widthLeft * two.getLength() / 1728;
			}
		}
		if(one.getHeight() < w.getHeight())
		{
			if(one.getWidth() < w.getWidth())
			{
				if(one.getWidth() < w.getWidth())
				{
					double maxHeight = w.getHeight() / one.getHeight();
					double maxWidth = w.getWidth() / one.getWidth();
					result = (int) maxHeight * (int) maxWidth;
					boards[0] = (double)result;

					double heightLeft = w.getHeight() - one.getHeight() * maxHeight;
					double widthLeft = w.getWidth() - one.getWidth() * maxWidth;

					if(heightLeft > widthLeft)
					{
						Log hTwo = new Log(heightLeft, w.getWidth(), 0.0);
						Log wTwo = new Log(w.getHeight() - heightLeft, widthLeft, 0.0 );
						boards = Optimization(hTwo, wTwo, two, boards);
					}
					else
					{
						Log hTwo = new Log(heightLeft, h.getWidth() - widthLeft, 0.0);
						Log wTwo = new Log(h.getHeight(), widthLeft, 0.0);
						boards = Optimization(hTwo, wTwo, two, boards);
					}
				}
			}
		}
		else if(two.getHeight() < w.getHeight())
		{
			if(two.getWidth() < w.getWidth())
			{
				if(two.getWidth() < w.getWidth())
				{
					double maxHeight = w.getHeight() / two.getHeight();
					double maxWidth = w.getWidth() / two.getWidth();
					result = (int) maxHeight * (int) maxWidth;
					boards[0] = 0.0;
					boards[1] = (double)result;

					double heightLeft = w.getHeight() - two.getHeight() * maxHeight;
					double widthLeft = w.getWidth() - two.getWidth() * maxWidth;

					boards[2] = heightLeft * widthLeft * two.getLength() / 1728;
				}
			}
		}
		return boards;
	}

	private double[] Optimization(Log h, Log w, Lumber one, double[] boards)
	{
		if(one.getHeight() < h.getHeight())
		{
			if(one.getWidth() < h.getWidth())
			{
				double maxHeight = h.getHeight() / one.getHeight();
				double maxWidth = h.getWidth() / one.getWidth();

				boards[1] = (int) maxHeight * (int) maxWidth;

				double heightLeft = h.getHeight() - one.getHeight() * maxHeight;
				double widthLeft = h.getWidth() - one.getWidth() * maxWidth;

				boards[2] = heightLeft * widthLeft * one.getLength() / 1728;
			}
			else
			{
				boards[1] = 0.0;
				boards[2] = h.getHeight() * h.getWidth() * one.getLength() / 1728;
			}
		}
		else
		{
			boards[1] = 0.0;
			boards[2] = h.getHeight() * h.getWidth() * one.getLength() / 1728;
		}
		if(one.getHeight() < w.getHeight())
		{
			if(one.getWidth() < w.getWidth())
			{
				double maxHeight = w.getHeight() / one.getHeight();
				double maxWidth = w.getWidth() / one.getWidth();

				boards[1] = (int) maxHeight * (int) maxWidth;

				double heightLeft = w.getHeight() - one.getHeight() * maxHeight;
				double widthLeft = w.getWidth() - one.getWidth() * maxWidth;

				boards[2] = heightLeft * widthLeft * one.getLength() / 12;
			}
			else
			{
				boards[1] = 0.0;
				boards[2] = w.getHeight() * w.getWidth() * one.getLength() / 12;
			}
		}
		else
		{
			boards[1] = 0.0;
			boards[2] = w.getHeight() * w.getWidth() * one.getLength() / 12;
		}
		return boards;
	}
}
