package sistema_hotel;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Quarto {
    private int numero;
    private boolean ocupado;
    private boolean chaveNaRecepcao;
    private Lock lock;

    public Quarto(int numero) {
        this.numero = numero;
        this.ocupado = false;
        this.chaveNaRecepcao = true;
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

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public void setChaveNaRecepcao(boolean chaveNaRecepcao) {
        this.chaveNaRecepcao = chaveNaRecepcao;
    }

    public Lock getLock() {
        return lock;
    }
}