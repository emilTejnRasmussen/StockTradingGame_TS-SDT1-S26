import business.stockmarket.simulation.TransitionManager;
import entities.Stock;

void main() {
//    Stock.State nextState = TransitionManager.nextState(Stock.State.GROWING);
//    System.out.println("SetState(" + nextState + ")");
    Random random = new Random();

    for (int i = 0; i < 100; i++)
    {
        System.out.println((random.nextDouble() * 2 - 1) * 0.1);
    }
}