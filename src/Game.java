public class Game {
    int gameId;
    String gameName;
    double pricePerHour;

    public Game(int gameId, String gameName, double pricePerHour) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.pricePerHour = pricePerHour;
    }

    @Override
    public String toString() {
        return "Game ID: " + gameId + ", Name: " + gameName + ", Price per Hour: " + pricePerHour;
    }
}
