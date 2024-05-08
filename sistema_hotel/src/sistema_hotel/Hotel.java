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

    private Camareira camareira;

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
            Camareira camareira = new Camareira(this); // Criando uma camareira
            camareiras.add(camareira);
            if (i == 0) { 
                this.camareira = camareira;
            }
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
    public void sairDoQuarto(Hospede hospede) throws InterruptedException {
        Quarto quarto = null;
        for (Quarto q : quartos) {
            if (q.getLock().tryLock()) {
                try {
                    if (q.Ocupado() && hospedes.contains(hospede)) {
                        q.setOcupado(false);
                        q.setChaveNaRecepcao(true);
                        quarto = q;
                        System.out.println("Hóspede " + hospede.getId() + " saiu do quarto " + q.getNumero() + " para passear.");
                        break;
                    }
                } finally {
                    q.getLock().unlock();
                }
            }
        }
        
        if (quarto != null) {
            // Sinaliza para a camareira que o quarto precisa ser limpo
            camareira.limparQuarto(quarto);
        }
    }

    public void reservarQuarto(Hospede hospede) throws InterruptedException {
        Quarto quarto = Disponivel();
        if (quarto != null) {
            quarto.getLock().lock();
            try {
                if (!quarto.Ocupado() && quarto.ChaveNaRecepcao()) {
                    // Verifica se o quarto está sendo limpo
                    if (!quarto.SendoLimpo()) {
                        quarto.setOcupado(true);
                        quarto.setChaveNaRecepcao(false);
                        System.out.println("Hóspede " + hospede.getId() + " reservou o quarto " + quarto.getNumero());
                        Thread.sleep(9000); //Para alternar os quartos
                    } else {
                        System.out.println("Hóspede " + hospede.getId() + " não pode reservar o quarto " + quarto.getNumero() + " pois está sendo limpo.");
                    }
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

    public List<Quarto> reservarQuartos(Hospede hospede, int numQuartos) throws InterruptedException {
        List<Quarto> quartosReservados = new ArrayList<>();
        lock.lock();
        try {
            for (Quarto quarto : quartos) {
                if (!quarto.Ocupado() && quarto.ChaveNaRecepcao() && !quarto.SendoLimpo()) {
                    if (quarto.getCapacidadeAtual() == 0) {
                        quarto.setCapacidadeAtual(1);
                        quarto.setOcupado(true);
                        quarto.setChaveNaRecepcao(false);
                        quartosReservados.add(quarto);
                        System.out.println("Hóspede " + hospede.getId() + " reservou o quarto " + quarto.getNumero());
                        numQuartos--; 
                    } else if (quarto.getCapacidadeAtual() < 4 && numQuartos > 0) {
                        quarto.setCapacidadeAtual(quarto.getCapacidadeAtual() + 1); // Incrementa a capacidade do quarto
                        quartosReservados.add(quarto);
                        System.out.println("Hóspede " + hospede.getId() + " reservou o quarto " + quarto.getNumero());
                        numQuartos--; // Decrementa o número de quartos restantes para reservar
                    }
                    if (numQuartos == 0) {
                        break; // Sai do loop quando todos os quartos necessários forem reservados
                    }
                }
            }
        } finally {
            lock.unlock();
        }
        return quartosReservados;
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
        	Thread.sleep(6000);
            System.out.println("Hóspede " + hospede.getId() + " não encontrou quartos disponíveis para check-in.");
        }
    }
    
    public void CheckOut(List<Quarto> quartos) {
        lock.lock();
        try {
            for (Quarto quarto : quartos) {
                quarto.setOcupado(false);
                quarto.setChaveNaRecepcao(true);
                quarto.setCapacidadeAtual(0); // Reseta a capacidade do quarto
                System.out.println("Quarto " + quarto.getNumero() + " liberado.");
            }
        } finally {
            lock.unlock();
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
