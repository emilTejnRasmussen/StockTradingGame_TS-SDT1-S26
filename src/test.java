import entities.Portfolio;
import entities.Stock;
import persistence.fileImplementation.FilePortfolioDao;
import persistence.fileImplementation.FileStockDao;
import persistence.fileImplementation.FileUnitOfWork;
import shared.configuration.AppConfig;

void main() {
    FileUnitOfWork uow = new FileUnitOfWork("data/");
    FileStockDao fileStockDao = new FileStockDao(uow);
    FilePortfolioDao filePortfolioDao = new FilePortfolioDao(uow);

    // fileStockDao.create(new Stock("AAPL", "Apple", BigDecimal.valueOf(20000000)));
    // fileStockDao.create(new Stock("ABC", "Another big company", BigDecimal.valueOf(20355000)));
    // fileStockDao.create(new Stock("LAL", "LALAL", BigDecimal.valueOf(35234)));

    //Portfolio p = new Portfolio(UUID.randomUUID(), BigDecimal.valueOf(AppConfig.getInstance().getStartingBalance()));
    //filePortfolioDao.create(p);

    // p.setCurrentBalance(BigDecimal.valueOf(200000));
    // filePortfolioDao.update(p);

    Portfolio p = filePortfolioDao.getAll().getFirst();
    filePortfolioDao.delete(p.getId());



    for (Stock stock : fileStockDao.getAll()){
        System.out.println(stock.getSymbol() + " " + stock.getName());
    }
    for (Portfolio portfolio : filePortfolioDao.getAll()){
        System.out.println(portfolio.getId() + " " + portfolio.getCurrentBalance());

    }


}