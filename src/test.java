import entities.Stock;
import persistence.fileImplementation.FileUnitOfWork;
import shared.logging.LogLevel;
import shared.logging.LogOutput;
import shared.logging.Logger;

void main() {
    FileUnitOfWork uow = new FileUnitOfWork("data");

    for (Stock s : uow.getStocks()){
        System.out.println(s.getName());
    }
}