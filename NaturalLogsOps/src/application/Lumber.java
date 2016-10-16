package application;


public class Lumber extends Log{

	private double value;

	public Lumber(double height, double width, double length, double value) {
		super(height, width, length);
		// TODO Auto-generated constructor stub

		this.value= value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}







}
