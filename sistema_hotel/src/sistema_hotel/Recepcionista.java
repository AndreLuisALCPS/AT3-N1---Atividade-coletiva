package sistema_hotel;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
public class Recepcionista extends Thread {
    private Hotel hotel;
    private BlockingQueue<Hospede> filaEspera;

    public Recepcionista(Hotel hotel) {
        this.hotel = hotel;
        this.filaEspera = new ArrayBlockingQueue<>(50); // Capacidade da fila de espera
    }

    @Override
    public void run() {
        while (true) {
            try {
                Hospede hospede = filaEspera.take();
                hotel.checkIn(hospede);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void adicionarFilaEspera(Hospede hospede) {
        try {
            filaEspera.add(hospede);
        } catch (IllegalStateException e) {
            System.out.println("A fila de espera está cheia. Hóspede " + hospede.getId() + " não pôde ser adicionado à fila.");
        }
    }
}
