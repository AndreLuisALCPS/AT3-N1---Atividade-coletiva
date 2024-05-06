package sistema_hotel;

import java.util.Random;

public class Hospede extends Thread {
    private int id;
    private Hotel hotel;
    private Random random;

    public Hospede(int id, Hotel hotel) {
        this.id = id;
        this.hotel = hotel;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            try {
                hotel.reservarQuarto(this);
                Thread.sleep(random.nextInt(5000)); // Tempo de passeio pela cidade
                hotel.sairDoQuarto(this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public long getId() {
        return id;
    }
}