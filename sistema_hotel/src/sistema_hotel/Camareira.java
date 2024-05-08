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

    public void limparQuarto(Quarto quarto) throws InterruptedException {
        quarto.getLock().lock();
        try {
            if (!quarto.Ocupado() && !quarto.ChaveNaRecepcao()) {
                // Realiza a limpeza do quarto
                System.out.println("Camareira limpando o quarto " + quarto.getNumero());
                Thread.sleep(3000); // Simulando o tempo de limpeza
                quarto.setChaveNaRecepcao(true);
                quarto.setSendoLimpo(false); // Indica que a limpeza terminou
            }
        } finally {
            quarto.getLock().unlock();
        }
    }
}
