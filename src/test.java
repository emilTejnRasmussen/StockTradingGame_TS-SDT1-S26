import entities.Stock;
import persistence.fileImplementation.FileStockDao;
import persistence.fileImplementation.FileUnitOfWork;

void main() {
    FileStockDao fileStockDao = new FileStockDao(new FileUnitOfWork("data/"));

    fileStockDao.create(new Stock("AAPL", "Apple", BigDecimal.valueOf(20000000)));

}