package domain;

public enum BoookingStatus {
    CONFIRMED(1), IN_PROGRESS(2), COMPLETED(3), CANCELED(4);
    private int number;

    BoookingStatus(int number) {
    }

    public int getNumber() {
        return number;
    }
}
