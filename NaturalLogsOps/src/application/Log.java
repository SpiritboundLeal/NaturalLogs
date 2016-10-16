package application;


public class Log {

	private double height, width, length;

	public Log(double height, double width, double length) {
		super();
		this.height = height;
		this.width = width;
		this.length = length;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double calculateArea(){
		double area;
		area = height * width;

		return area;
	}

	public double calculateVol(){
		double volume;
		volume = height* width * length;

		return volume;
	}
}
