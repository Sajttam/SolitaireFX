package models;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Card extends ImageView {
	public enum Suit {
		HEART('h', "heart", true),
		DIAMOND('d', "diamond", true),
		CLOVER('c', "clover", false),
		SPADE('s', "spade", false);
		
		private final char sign;
		private final String name;
		private final boolean isRed;

		Suit(char sign, String name, boolean isRed) {
			this.sign = sign;
			this.name = name;
			this.isRed = isRed;
		}
		
		public char getSign() {
			return sign;
		}

		public String getName() {
			return name;
		}
		
		public boolean isRed() {
			return isRed;
		}
	}

	private int value;
	private Suit suit;
	private boolean facedUp = false;
	private Image imageFacedUp;
	private Image imageFacedDown;
	private String spriteBack = "Resources/Cards/Simple/backBlue.png";
	private String tiledCards = "Resources/SpriteSheets/classic_13x4x560x780.png";

	public Card(int value, Suit suit) throws FileNotFoundException  {
		this.value = value;
		this.suit= suit;
		String sprite = "Resources/Cards/Simple/" + suit.getSign() + value + ".png";
		setFitHeight(200);
		setPreserveRatio(true);
		imageFacedUp = new Image(new FileInputStream(sprite));
		imageFacedDown = new Image(new FileInputStream(spriteBack));
		setImage(imageFacedDown);
	}
	
	public boolean isFacedUp() {
		return facedUp;
	}
	
	public void setFacedUp(boolean facedUp) {
		if (facedUp) setImage(imageFacedUp);
		else setImage(imageFacedDown);
		this.facedUp = facedUp;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		if (value < 1 || value > 13)
			throw new IllegalArgumentException();
		this.value = value;
	}

	public Suit getSuit() {
		return suit;
	}

	public void setSuit(Suit suit) {
		this.suit = suit;
	}
	
	public boolean hasOppositeSuit(Card other) {
		return getSuit().isRed() ^ other.getSuit().isRed();	
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Card) {
			Card other = (Card) obj;
			return suit == other.suit && value == other.value;
		}
		return false;		
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		switch (getValue()) {
		case 1:
			s.append("Ace");
			break;
		case 11:
			s.append("Knight");
			break;
		case 12:
			s.append("Queen");
			break;
		case 13:
			s.append("King");
			break;
		default:
			s.append("" + value);
			break;
		}

		s.append(" of " + suit.getName() + "s");
		
		if (isFacedUp())
			s.append(" U");
		else
			s.append(" D");

		return s.toString();
	}
}
