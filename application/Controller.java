package application;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import models.*;

public class Controller {

	private Move currentMove;
	private List<Move> movesHistory;
	private List<Pile> piles = new ArrayList<Pile>();

	// TODO: THREE CARD TALON

	public Controller() throws FileNotFoundException {
		currentMove = null;
		movesHistory = new ArrayList<Move>();
		setUp();
		deal();
	}
	
	public Pile getStock() {
		return piles.get(0);		
	}
	
	public Pile getTalon() {
		return piles.get(1);		
	}

	public void setUp() throws FileNotFoundException {
		piles.add(getDeck());

		piles.add(new Pile(Pile.PileType.TALON));

		for (int i = 0; i < 7; i++)
			piles.add(new Pile(Pile.PileType.TABLEAU));

		for (int i = 0; i < 4; i++)
			piles.add(new Pile(Pile.PileType.FOUNDATION));
	}

	public void deal() {
		List<Card> cards = getAllCards();
		Collections.shuffle(cards);
		piles.get(0).clear();

		for (Card c : cards) {
			c.setFacedUp(false);
			piles.get(0).push(c);
		}

		for (int i = 2; i < 9; i++) {
			for (int j = 2; j <= i; j++) {
				Card c = piles.get(0).pop();
				piles.get(i).push(c);
				if (j == i)
					c.setFacedUp(true);
			}
		}

	}

	public Pile getDeck() throws FileNotFoundException {
		Stack<Card> deck = new Stack<Card>();

		for (Card.Suit suit : Card.Suit.values()) {
			for (int j = 1; j <= 13; j++) {
				deck.push(new Card(j, suit));
			}
		}

		Collections.shuffle(deck);

		Pile p = new Pile(Pile.PileType.STOCK);

		for (Card c : deck)
			p.push(c);

		return (p);
	}

	public void stockUncover() {
		Pile stock = piles.get(0);
		Pile talon = piles.get(1);

		if (!stock.empty()) {
			Card c = stock.peek();
			c.setFacedUp(true);
			performMove(new Move(stock, c, talon), true);
		} else {
			while (!talon.empty()) {
				Card c = talon.peek();
				c.setFacedUp(false);
				performMove(new Move(talon, c, stock), true);
			}
		}
	}

	public boolean beginMove(Card c) {
		currentMove = new Move(getPile(c), c);
		return currentMove.isValidMoveFrom();
	}

	public boolean completeMove(Pile to) {
		if (currentMove == null)
			throw new NullPointerException();
		currentMove.setTo(to);
		if (currentMove.isValidMoveTo()) {
			performMove(currentMove, true);
			return true;
		} else
			return false;
	}

	public void performMove(Move move, boolean addToHistory) {
		for (Card c : move.getCards())
			move.getTo().push(c);

		if (addToHistory) {
			movesHistory.add(move);
			if (!move.getFrom().empty())
				uncoverMove(move.getFrom().peek());
		}
		
		if (isGameWon()) {
			ButtonType result = CustomAlerts.getVictoryAlert().showAndWait().get();
			if (result.equals(ButtonType.OK)){
			    deal();
			} else {
			    System.exit(0);
			}
		}
	}

	public void undoMove() {
		if (!movesHistory.isEmpty()) {
			Move lastMove = movesHistory.get(movesHistory.size() - 1).getReverseMove();
			movesHistory.remove(movesHistory.size() - 1);

			if (lastMove.getTo().equals(lastMove.getFrom())) {
				lastMove.getCards().get(0).setFacedUp(false);
				undoMove();
			} else {
				performMove(lastMove, false);
				if (lastMove.getFrom().getType() == Pile.PileType.STOCK) {
					lastMove.getCards().get(0).setFacedUp(true);
					if (!movesHistory.isEmpty())
						if (movesHistory.get(movesHistory.size() - 1).getTo().getType() == Pile.PileType.STOCK)
							undoMove();
				}
				if (lastMove.getTo().getType() == Pile.PileType.STOCK) {
					lastMove.getCards().get(0).setFacedUp(false);
				}
			}
		}
	}

	public void quickMove(Card c) {
		Move quickMove = new Move(getPile(c), c);
		if (quickMove.isValidMoveFrom())
			for (int i = 9; i < 13; i++) {
				quickMove.setTo(piles.get(i));
				if (quickMove.isValidMoveTo()) {
					performMove(quickMove, true);
					break;
				}
			}
	}

	public void uncoverMove(Card c) {
		Pile p = getPile(c);
		if (p.getType() == Pile.PileType.TABLEAU && p.peek().equals(c)) {
			c.setFacedUp(true);
			movesHistory.add(new Move(p, c, p));
		}
	}

	public Pile getPile(Card c) {
		Pile pile = null;
		for (Pile p : piles) {
			if (p.getChildren().contains(c)) {
				pile = p;
				break;
			}
		}
		return pile;
	}

	public List<Pile> getPiles() {
		return piles;
	}

	public List<Card> getAllCards() {
		List<Card> l = new ArrayList<Card>();

		for (Pile p : piles) {
			for (Node n : p.getChildren()) {
				if (n instanceof Card)
					l.add((Card) n);
			}
		}

		return l;
	}
	
	public boolean isGameWon() {
		int count = 0;
		for (Pile p : piles) {
			if (!p.empty())
				if (p.getType() == Pile.PileType.FOUNDATION && p.peek().getValue() == 13) count++;
		}
		return count == 4;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();

		for (Pile p : piles) {
			s.append(p.getType());
			s.append(": " + p.getChildren().toString());
			s.append("\n");
		}

		return s.toString();
	}
}
