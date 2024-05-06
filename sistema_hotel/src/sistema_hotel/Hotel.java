package sistema_hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Hotel {
    private final List<Quarto> quartos;
    private final List<Recepcionista> recepcionistas;
    private final List<Camareira> camareiras;
    private final List<Hospede> hospedes;
    private final Lock lock;

    public Hotel(int numQuartos, int numRecepcionistas, int numCamareiras, int numHospedes) {
        quartos = new ArrayList<>();
        recepcionistas = new ArrayList<>();
        camareiras = new ArrayList<>();
        hospedes = new ArrayList<>();
        lock = new ReentrantLock();

        // Inicializando os quartos
        for (int i = 0; i < numQuartos; i++) {
            quartos.add(new Quarto(i + 1));
        }

        // Inicializando os recepcionistas
        for (int i = 0; i < numRecepcionistas; i++) {
            recepcionistas.add(new Recepcionista(this));
        }

        // Inicializando as camareiras
        for (int i = 0; i < numCamareiras; i++) {
            camareiras.add(new Camareira(this));
        }

        // Inicializando os hóspedes
        for (int i = 0; i < numHospedes; i++) {
            hospedes.add(new Hospede(i + 1, this));
        }

        // Iniciando as threads dos recepcionistas
        for (Recepcionista recepcionista : recepcionistas) {
            recepcionista.start();
        }

        // Iniciando as threads das camareiras
        for (Camareira camareira : camareiras) {
            camareira.start();
        }

        // Iniciando as threads dos hóspedes
        for (Hospede hospede : hospedes) {
            hospede.start();
        }
    }

    public Quarto Disponivel() {
        lock.lock();
        try {
            for (Quarto quarto : quartos) {
                if (!quarto.Ocupado() && quarto.ChaveNaRecepcao()) {
                    return quarto;
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void reservarQuarto(Hospede hospede) throws InterruptedException {
        Quarto quarto = Disponivel();
        if (quarto != null) {
            quarto.getLock().lock();
            try {
                if (!quarto.Ocupado() && quarto.ChaveNaRecepcao()) {
                    quarto.setOcupado(true);
                    quarto.setChaveNaRecepcao(false);
                    System.out.println("Hóspede " + hospede.getId() + " reservou o quarto " + quarto.getNumero());
                    Thread.sleep(9000); //Para alternar os quartos
                }
            } finally {
                quarto.getLock().unlock();
            }
        } else {
            System.out.println("Hóspede " + hospede.getId() + " não encontrou quartos disponíveis, vai para a fila de espera.");
            for (Recepcionista recepcionista : recepcionistas) {
                recepcionista.adicionarFilaEspera(hospede);
            }
        }
    }
    public void sairDoQuarto(Hospede hospede) throws InterruptedException {
        for (Quarto quarto : quartos) {
            quarto.getLock().lock();
            try {
                if (quarto.Ocupado() && hospedes.contains(hospede)) {
                    quarto.setOcupado(false);
                    quarto.setChaveNaRecepcao(true);
                    System.out.println("Hóspede " + hospede.getId() + " saiu do quarto " + quarto.getNumero() + " para passear.");
                    break;
                }
            } finally {
                quarto.getLock().unlock();
            }
        }
    }

    public void checkIn(Hospede hospede) throws InterruptedException {
        Quarto quarto = Disponivel();
        if (quarto != null) {
            quarto.getLock().lock();
            try {
                if (!quarto.Ocupado() && quarto.ChaveNaRecepcao()) {
                    quarto.setOcupado(true);
                    quarto.setChaveNaRecepcao(false);
                    System.out.println("Hóspede " + hospede.getId() + " fez check-in no quarto " + quarto.getNumero());
                }
            } finally {
                quarto.getLock().unlock();
            }
        } else {
            System.out.println("Hóspede " + hospede.getId() + " não encontrou quartos disponíveis para check-in.");
        }
    }

    public void limparQuartos() {
        lock.lock();
        try {
            for (Quarto quarto : quartos) {
                if (!quarto.Ocupado() && !quarto.ChaveNaRecepcao()) {
                    quarto.setChaveNaRecepcao(true);
                    System.out.println("Camareira limpou o quarto " + quarto.getNumero());
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        Hotel hotel = new Hotel(10, 5, 2, 50); 
    }
}
