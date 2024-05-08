package sistema_hotel;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Quarto {
    private int numero;
    private boolean ocupado;
    private boolean chaveNaRecepcao;
    private boolean sendoLimpo;
    private int capacidadeAtual; // Capacidade atual do quarto
    private Lock lock;

    public Quarto(int numero) {
        this.numero = numero;
        this.ocupado = false;
        this.chaveNaRecepcao = true;
        this.sendoLimpo = false;
        this.capacidadeAtual = 0; // Inicialmente o quarto está vazio
        this.lock = new ReentrantLock();
    }

    public int getNumero() {
        return numero;
    }

    public boolean Ocupado() {
        return ocupado;
    }

    public boolean ChaveNaRecepcao() {
        return chaveNaRecepcao;
    }

    public boolean SendoLimpo() {
        return sendoLimpo;
    }

    public int getCapacidadeAtual() {
        return capacidadeAtual;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public void setChaveNaRecepcao(boolean chaveNaRecepcao) {
        this.chaveNaRecepcao = chaveNaRecepcao;
    }

    public void setSendoLimpo(boolean sendoLimpo) {
        this.sendoLimpo = sendoLimpo;
    }

    public void setCapacidadeAtual(int capacidadeAtual) {
        this.capacidadeAtual = capacidadeAtual;
    }

    public Lock getLock() {
        return lock;
    }
}