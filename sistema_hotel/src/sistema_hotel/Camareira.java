package sistema_hotel;

public class Camareira extends Thread {
    private Hotel hotel;

    public Camareira(Hotel hotel) {
        this.hotel = hotel;
    }

    @Override
    public void run() {
        while (true) {
            hotel.limparQuartos();
        }
    }
}