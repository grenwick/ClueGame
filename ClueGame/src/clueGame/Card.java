package clueGame;

public class Card {
	private String cardName;
	private CardType cardType;
	public Card() {
		
	}
	public Card(String name, CardType type) {
		this.cardName = name;
		this.cardType = type;
	}
	public boolean equals(Card target) {
		if (target.getCardName().equals(this.cardName) && (target.getCardType() == this.cardType)) {
			return true;
		} else {
			return false;
		}
	}
	public Player getPlayerFromCard() {
		for (Player player : Board.getInstance().getPlayers()) {
			if (cardName.equals(player.getName())) {
				return player;
			}
		}
		return null;
	}
	public String getCardName() {
		return cardName;
	}
	public CardType getCardType() {
		return cardType;
	}
	
}
