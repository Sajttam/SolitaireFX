package models;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Pile extends StackPane {
	public enum PileType {
		STOCK(0, 1, 0, 0), TALON(0, 0, 0, 1), TABLEAU(4, 0, 35, 0), FOUNDATION(0, 0, 0, 1);

		double h1, v1, h2, v2;

		PileType(double h1, double v1, double h2, double v2) {
			this.h1 = h1;
			this.v1 = v1;
			this.h2 = h2;
			this.v2 = v2;
		}

		public double getH1() {
			return h1;
		}

		public double getV1() {
			return v1;
		}

		public double getH2() {
			return h2;
		}

		public double getV2() {
			return v2;
		}

	}

	PileType type = PileType.STOCK;

	public Pile(PileType type) {
		super();
		this.type = type;	
		addGraphicsEmptyType();
	}
	
	public void addGraphicsEmptyType() {		
		switch (type) {
		case FOUNDATION:
		case STOCK:
		case TALON:
			addGraphicsEmpty(155, 210, 6, Color.GOLD);
			break;
		case TABLEAU:
			addGraphicsEmpty(140, 210, 6, Color.GREEN);
			break;		
		default:
			break;
		
		}
	}
	
	public void addGraphicsEmpty(double width, double height, double outline, Color color) {		
		Rectangle rectangle1 = new Rectangle();
		rectangle1.setWidth(width);
		rectangle1.setHeight(height);
		
		//Setting the height and width of the arc 
	    rectangle1.setArcWidth(30.0); 
	    rectangle1.setArcHeight(20.0);
	    
	    Rectangle rectangle2 = new Rectangle();
	    rectangle2.setX(outline/2);
		rectangle2.setY(outline/2);
		rectangle2.setWidth(width - outline);
		rectangle2.setHeight(height - outline);
		
		//Setting the height and width of the arc 
	    rectangle2.setArcWidth(30.0); 
	    rectangle2.setArcHeight(20.0);
	    
	    Shape shape = Shape.subtract(rectangle1, rectangle2);
	    shape.setFill(color);
	      
		getChildren().add(shape);
		StackPane.setMargin(shape, new Insets(8, 8, 8, 8));
	}
	
	public void clear() {
		getChildren().clear();
		addGraphicsEmptyType();
	}

	public PileType getType() {
		return type;
	}

	public void setType(PileType type) {
		this.type = type;
	}

	public boolean empty() {
		return getChildren().size() == 1;
	}

	public Card peek() {
		if (empty())
			throw new EmptyStackException();
		return (Card) getChildren().get(getChildren().size() - 1);
	}

	public Card pop() {
		if (empty())
			throw new EmptyStackException();
		int index = getChildren().size() - 1;
		Card c = (Card) getChildren().get(index);
		getChildren().remove(index);
		return c;
	}

	public int getNumberOfFacedDownCards() {
		int count = 0;
		for (Node n : getChildren()) {
			Card c = (Card) n;
			if (!c.isFacedUp())
				count++;
		}
		return count;
	}

	public void push(Card c) {
		if (c == null)
			throw new NullPointerException();

		getChildren().add(c);
		double valueHorizontal = 0;
		double valueVertical = 0;
		
		
		Card prev = null;
		for (Node n : getChildren()) {
			if (n instanceof Card) {
				Card card = (Card) n;
				if (prev != null) {
					if (card.isFacedUp() && prev.isFacedUp()) {
						valueVertical += type.getH2();
						valueHorizontal += type.getV2();
					}
					else {
						valueVertical += type.getH1();
						valueHorizontal += type.getV1();
					}
				}
				prev = card;
			}
		}
		StackPane.setMargin(c, new Insets(valueVertical, 0, 0, valueHorizontal));
	}
}
