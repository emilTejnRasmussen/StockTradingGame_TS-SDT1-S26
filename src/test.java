import entities.OwnedStock;
import entities.Portfolio;
import entities.Stock;
import persistence.fileImplementation.FileOwnedStockDao;
import persistence.fileImplementation.FilePortfolioDao;
import persistence.fileImplementation.FileStockDao;
import persistence.fileImplementation.FileUnitOfWork;
import shared.configuration.AppConfig;

void main() {
    FileUnitOfWork uow = new FileUnitOfWork("data/");
    FileStockDao fileStockDao = new FileStockDao(uow);
    FilePortfolioDao filePortfolioDao = new FilePortfolioDao(uow);
    FileOwnedStockDao fileOwnedStockDao = new FileOwnedStockDao(uow);

    // fileStockDao.create(new Stock("AAPL", "Apple", BigDecimal.valueOf(20000000)));
    // fileStockDao.create(new Stock("ABC", "Another big company", BigDecimal.valueOf(20355000)));
    // fileStockDao.create(new Stock("LAL", "LALAL", BigDecimal.valueOf(35234)));

    //Portfolio p = new Portfolio(UUID.randomUUID(), BigDecimal.valueOf(AppConfig.getInstance().getStartingBalance()));
    //filePortfolioDao.create(p);

    // p.setCurrentBalance(BigDecimal.valueOf(200000));
    // filePortfolioDao.update(p);

    // fileOwnedStockDao.create(new OwnedStock(filePortfolioDao.getAll().getFirst().getId(), fileStockDao.getAll().getFirst().getSymbol(), 10 ));
    OwnedStock os = fileOwnedStockDao.getAll().getFirst();
    os.addShares(20);
    fileOwnedStockDao.update(os);

    System.out.println("--- Stock ---");
    for (Stock stock : fileStockDao.getAll()){
        System.out.println(stock.getSymbol() + " " + stock.getName());
    }
    System.out.println("--- Portfolios ---");
    for (Portfolio portfolio : filePortfolioDao.getAll()){
        System.out.println(portfolio.getId() + " " + portfolio.getCurrentBalance());

    }
    System.out.println("--- OwnedStock ---");
    for (OwnedStock ownedStock : uow.getOwnedStocks()){
        Stock s = fileStockDao.getBySymbol(ownedStock.getStockSymbol()).orElse(null);
        System.out.println(ownedStock.getId() + " " + ownedStock.getStockSymbol() + " " + s.getName() + " "
                + ownedStock.getNumberOfShares() + " " + ownedStock.getPortfolioId());
    }


}