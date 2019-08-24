package application;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import models.*;

public class Move {
	private Pile from;
	private Pile to;
	private List<Card> cards;

	public Move() {
		from = null;
		to = null;
		cards = new ArrayList<Card>();
	}

	public Move(Pile from, Card c) {
		this.from = from;
		to = null;
		cards = new ArrayList<Card>();
		addCard(c);
	}
	
	public Move(Pile from, Card c, Pile to) {
		this.from = from;
		this.to = to;
		cards = new ArrayList<Card>();
		addCard(c);
	}
	
	public Move getReverseMove() {
		Move reverseMove = new Move();
		reverseMove.to = from;
		reverseMove.from = to;
		reverseMove.cards = (List<Card>) ((ArrayList<Card>) cards).clone();
		return reverseMove;
	}

	public Pile getFrom() {
		return from;
	}

	public void setFrom(Pile from) {
		this.from = from;
	}

	public Pile getTo() {
		return to;
	}

	public void setTo(Pile to) {
		this.to = to;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void addCard(Card card) {
		if (from == null)
			throw new NullPointerException();
		boolean found = false;
		for (Node n : from.getChildren()) {
			if (n instanceof Card) {
				Card other = (Card) n;
				if (other.equals(card))
					found = true;
				if (found)
					cards.add(other);
			}
		}
	}

	public boolean isValidMoveFrom() {
		if (cards.isEmpty())
			throw new IllegalStateException();
		if (from == null)
			throw new NullPointerException();
		
		if (!cards.get(0).isFacedUp()) return false;
		
		switch (from.getType()) {
		case STOCK:
			return false;
		case TALON:
		case FOUNDATION:
		case TABLEAU:
		default:
			return true;
		}		
	}

	public boolean isValidMoveTo() {
		if (cards.isEmpty())
			throw new IllegalStateException();
		if (from == null || to == null)
			throw new NullPointerException();
		
		if (from.equals(to)) return false;
		
		Card handTop = cards.get(0);		
		Card pileTop;
		
		switch (to.getType()) {
		case STOCK:
		case TALON:
			return false;
		case FOUNDATION:
			if (cards.size() != 1)
				return false;
			if (to.empty()) {
				if (handTop.getValue() != 1)
					return false;
			} else {
				pileTop = to.peek();
				if (pileTop.getValue() != handTop.getValue() - 1)
					return false;
				if (pileTop.getSuit() != handTop.getSuit())
					return false;
			}
			break;
		case TABLEAU:
			if (to.empty()) {
				if (handTop.getValue() != 13)
					return false;
			} else {
				pileTop = to.peek();
				if (pileTop.getValue() != handTop.getValue() + 1)
					return false;
				if (!(pileTop.getSuit().isRed() ^ handTop.getSuit().isRed()))
					return false;
				if (!pileTop.isFacedUp())
					return false;
			}
			break;
		default:
			break;
		}		
		return true;
	}
}
